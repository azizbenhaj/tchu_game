package ch.epfl.tchu.gui;

import ch.epfl.tchu.CardBagStringConverter;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.List;
import java.util.Map;

import static ch.epfl.tchu.gui.ActionHandlers.*;
import static javafx.application.Platform.isFxApplicationThread;

/**
 * GraphicalPlayer
 *
 * @author OMAR MEZGHANI (311542)
 * @author MOHAMED CHARFI (311171)
 */

public final class GraphicalPlayer {

    private static final int ONE_CHOICE = 1;
    private static final int MAX_MESSAGES_COUNT = 5;

    private final ObservableGameState observableGameState;
    private final ObservableList<Text> textObservableList;
    private final Stage mainStage;
    private final ObjectProperty<ClaimRouteHandler> claimRouteHandlerObjectProperty;
    private final ObjectProperty<ActionHandlers.DrawTicketsHandler> drawTicketsHandlerObjectProperty;
    private final ObjectProperty<ActionHandlers.DrawCardHandler> drawCardHandlerObjectProperty;
    private final ObjectProperty<ActionHandlers.SurrenderHandler> surrenderHandlerObjectProperty;
    private final ObjectProperty<ActionHandlers.RestartHandler> restartHandlerObjectProperty;


    /**
     * public constructor
     *
     * @param playerId          playerId
     * @param playerIdStringMap playerIdStringMap
     */
    public GraphicalPlayer(PlayerId playerId, Map<PlayerId, String> playerIdStringMap) {
        assert isFxApplicationThread();

        observableGameState = new ObservableGameState(playerId);
        textObservableList = FXCollections.observableArrayList();

        claimRouteHandlerObjectProperty = new SimpleObjectProperty<>(null);
        drawCardHandlerObjectProperty = new SimpleObjectProperty<>(null);
        drawTicketsHandlerObjectProperty = new SimpleObjectProperty<>(null);
        surrenderHandlerObjectProperty = new SimpleObjectProperty<>(null);
        restartHandlerObjectProperty = new SimpleObjectProperty<>(null);

        Node mapView = MapViewCreator.createMapView(observableGameState, claimRouteHandlerObjectProperty, this::chooseClaimCards);
        Node cardsView = DecksViewCreator.createCardsView(observableGameState, drawTicketsHandlerObjectProperty, drawCardHandlerObjectProperty,
                surrenderHandlerObjectProperty, restartHandlerObjectProperty);
        Node handView = DecksViewCreator.createHandView(observableGameState);
        Node infoView = InfoViewCreator.createInfoView(playerId, playerIdStringMap, observableGameState, textObservableList);
        Node mediaView = MediaViewCreator.createMediaView(observableGameState);

        BorderPane mainPane = new BorderPane(mapView, mediaView, cardsView, handView, infoView);

        mainStage = new Stage();
        mainStage.setScene(new Scene(mainPane));
        mainStage.setTitle("tchu \u2014 " + playerIdStringMap.get(playerId));
        mainStage.show();
    }

    /**
     * private method to create the selection window
     *
     * @param list   list
     * @param button button
     * @param s      text
     * @param title  title
     * @param <T>    type of the listView
     * @return the created stage created
     */
    private <T> Stage createStage(ListView<T> list, Button button, String s, String title) {
        //Stage creation
        Stage stage = new Stage(StageStyle.UTILITY);
        stage.initOwner(mainStage);
        stage.initModality(Modality.WINDOW_MODAL);

        //Text creation
        Text text = new Text(s);
        TextFlow textFlow = new TextFlow(text);

        //VBox creation
        VBox vBox = new VBox();
        vBox.getChildren().addAll(textFlow, list, button);

        // Scene creation
        Scene scene = new Scene(vBox);
        scene.getStylesheets().add("chooser.css");

        stage.setScene(scene);
        stage.setTitle(title);
        stage.setOnCloseRequest(Event::consume);

        return stage;

    }

    /**
     * sets all the properties to null
     */
    private void setHandlerPropertiesToNull() {
        drawCardHandlerObjectProperty.set(null);
        claimRouteHandlerObjectProperty.set(null);
        drawTicketsHandlerObjectProperty.set(null);
        surrenderHandlerObjectProperty.set(null);
    }

    /**
     * sets the observableGameState state
     *
     * @param newGameState new Game State
     * @param playerState  playerState
     */
    public void setState(PublicGameState newGameState, PlayerState playerState) {
        assert isFxApplicationThread();
        observableGameState.setState(newGameState, playerState);
    }

    /**
     * adds the message to the textObservableList
     *
     * @param message message
     */
    public void receiveInfo(String message) {
        assert isFxApplicationThread();
        if (textObservableList.size() == MAX_MESSAGES_COUNT) {
            textObservableList.remove(0);
        }
        textObservableList.add(new Text(message));
    }

    /**
     * allows the player to do only one action
     *
     * @param ticketsHandler    ticketsHandler
     * @param cardHandler       cardHandler
     * @param claimRouteHandler claimRouteHandler
     */

    public void startTurn(DrawTicketsHandler ticketsHandler, DrawCardHandler cardHandler, ClaimRouteHandler claimRouteHandler,
                          SurrenderHandler surrenderHandler, RestartHandler restartHandler) {
        assert isFxApplicationThread();

        if (!observableGameState.theGameEnded().get()) {

            if (observableGameState.canDrawTickets()) {
                drawTicketsHandlerObjectProperty.set(() -> {
                    ticketsHandler.onDrawTickets();
                    setHandlerPropertiesToNull();
                });
            }

            if (observableGameState.canDrawCards()) {
                drawCardHandlerObjectProperty.set(i -> {
                    cardHandler.onDrawCard(i);
                    setHandlerPropertiesToNull();
                });
            }

            claimRouteHandlerObjectProperty.set((r, cards) -> {
                claimRouteHandler.onClaimRoute(r, cards);
                setHandlerPropertiesToNull();
            });

            surrenderHandlerObjectProperty.set(() -> {
                surrenderHandler.surrender();
                setHandlerPropertiesToNull();
            });

        } else {
            restartHandlerObjectProperty.set(() -> {
                        mainStage.close();
                        Platform.setImplicitExit(false);
                        restartHandler.restart();
                    }
            );
        }

    }

    /**
     * opens a selection window tho choose the tickets
     *
     * @param tickets              tickets
     * @param chooseTicketsHandler chooseTicketsHandler
     */
    public void chooseTickets(SortedBag<Ticket> tickets, ChooseTicketsHandler chooseTicketsHandler) {
        assert isFxApplicationThread();

        int minimumNumberOfTickets = tickets.size() - Constants.DISCARDABLE_TICKETS_COUNT;

        //ListView creation
        ListView<Ticket> list = new ListView<>(FXCollections.observableList(tickets.toList()));
        list.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Button creation
        Button button = new Button("Choisir");

        //Selection Window Creation
        Stage stage = createStage(list, button, String.format(StringsFr.CHOOSE_TICKETS, String.valueOf(minimumNumberOfTickets), StringsFr.plural(minimumNumberOfTickets)), StringsFr.TICKETS_CHOICE);

        //Button properties and event handling
        button.disableProperty().bind(Bindings.size(list.getSelectionModel().getSelectedItems()).lessThan(minimumNumberOfTickets));
        button.setOnAction(event -> {
            stage.hide();
            chooseTicketsHandler.onChooseTickets(SortedBag.of(list.getSelectionModel().getSelectedItems()));
        });

        stage.show();
    }

    /**
     * allows the player to only draw a card
     *
     * @param drawCardHandler drawCardHandler
     */
    public void drawCard(DrawCardHandler drawCardHandler) {
        assert isFxApplicationThread();

        drawCardHandlerObjectProperty.set(i -> {
            drawCardHandler.onDrawCard(i);
            setHandlerPropertiesToNull();
        });
    }

    /**
     * opens a selection window to choose th claim cards
     *
     * @param cardsChoices       cards Choices
     * @param chooseCardsHandler chooseCardsHandler
     */
    public void chooseClaimCards(List<SortedBag<Card>> cardsChoices, ChooseCardsHandler chooseCardsHandler) {
        assert isFxApplicationThread();

        //ListView creation
        ListView<SortedBag<Card>> list = new ListView<>(FXCollections.observableList(cardsChoices));
        list.setCellFactory(v -> new TextFieldListCell<>(new CardBagStringConverter()));

        // Button creation
        Button button = new Button("Choisir");

        //Selection Window Creation
        Stage stage = createStage(list, button, StringsFr.CHOOSE_CARDS, StringsFr.CARDS_CHOICE);

        //Button properties and event handling
        button.disableProperty().bind(Bindings.size(list.getSelectionModel().getSelectedItems()).isNotEqualTo(ONE_CHOICE));
        button.setOnAction(event -> {
            stage.hide();
            chooseCardsHandler.onChooseCards(list.getSelectionModel().getSelectedItem());
        });

        stage.show();
    }

    /**
     * opens a selection window to choose the additional cards
     *
     * @param cardsChoices       cards Choices
     * @param chooseCardsHandler chooseCardsHandler
     */
    public void chooseAdditionalCards(List<SortedBag<Card>> cardsChoices, ChooseCardsHandler chooseCardsHandler) {
        assert isFxApplicationThread();

        //ListView creation
        ListView<SortedBag<Card>> list = new ListView<>(FXCollections.observableList(cardsChoices));
        list.setCellFactory(v -> new TextFieldListCell<>(new CardBagStringConverter()));

        // Button creation
        Button button = new Button("Choisir");

        //Selection Window Creation
        Stage stage = createStage(list, button, StringsFr.CHOOSE_ADDITIONAL_CARDS, StringsFr.CARDS_CHOICE);

        //Button event handling
        button.setOnAction(event -> {
            stage.hide();
            if (list.getSelectionModel().getSelectedItems().size() == 0) {
                chooseCardsHandler.onChooseCards(SortedBag.of());
            } else {
                chooseCardsHandler.onChooseCards(list.getSelectionModel().getSelectedItem());
            }
        });

        stage.show();
    }

    /**
     * sets the property of each road
     *
     * @param trailRoutes the list of the roads of the longest trail
     * @param playerId the id of the player
     */
    public void longest(List<Route> trailRoutes, PlayerId playerId) {
        for (Route r : trailRoutes) {
            observableGameState.setLongestRouteProperty(r, playerId);
        }
    }

}
