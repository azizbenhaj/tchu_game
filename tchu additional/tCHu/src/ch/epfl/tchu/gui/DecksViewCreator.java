package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Constants;
import ch.epfl.tchu.game.TicketDecorator;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.shape.*;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;
import ch.epfl.tchu.gui.ActionHandlers.*;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

/**
 * DecksViewCreator
 *
 * @author OMAR MEZGHANI (311542)
 * @author MOHAMED CHARFI (311171)
 */

final class DecksViewCreator {
    private final static int OUTSIDE_RECTANGLE_WIDTH = 60;
    private final static int OUTSIDE_RECTANGLE_HEIGHT = 90;
    private final static int INSIDE_RECTANGLE_WIDTH = 40;
    private final static int INSIDE_RECTANGLE_HEIGHT = 70;
    private final static int TRAIN_IMAGE_RECTANGLE_WIDTH = 40;
    private final static int TRAIN_IMAGE_RECTANGLE_HEIGHT = 70;
    private final static int GAUGE_RECTANGLE_WIDTH = 50;
    private final static int GAUGE_RECTANGLE_HEIGHT = 5;

    /**
     * private empty constructor
     */
    private DecksViewCreator() {
    }

    /**
     * creates the hand view
     *
     * @param gameState gameState
     * @return a node containing the hand view
     */
    public static Node createHandView(ObservableGameState gameState) {
        //HBox creation
        HBox hbox = new HBox();
        hbox.getStylesheets().addAll("decks.css", "colors.css");

        //ListView creation
        ListView<TicketDecorator> ticketListView = new ListView<>(gameState.currentPlayerTicketListProperty());
        ticketListView.setId("tickets");

        //HBox creation
        HBox handPaneHBox = new HBox();
        handPaneHBox.setId("hand-pane");

        hbox.getChildren().addAll(ticketListView, handPaneHBox);

        //Cards creation
        for (Card c : Card.ALL) {

            //StackPane creation
            StackPane stackPane = new StackPane();
            stackPane.getStyleClass().addAll(cardStyleClassName(c), "card");

            //outside rectangle creation
            Rectangle outsideRectangle = new Rectangle(OUTSIDE_RECTANGLE_WIDTH, OUTSIDE_RECTANGLE_HEIGHT);
            outsideRectangle.getStyleClass().add("outside");

            //inside rectangle creation
            Rectangle insideRectangle = new Rectangle(INSIDE_RECTANGLE_WIDTH, INSIDE_RECTANGLE_HEIGHT);
            insideRectangle.getStyleClass().addAll("inside", "filled");

            //train image rectangle creation
            Rectangle trainImageRectangle = new Rectangle(TRAIN_IMAGE_RECTANGLE_WIDTH, TRAIN_IMAGE_RECTANGLE_HEIGHT);
            trainImageRectangle.getStyleClass().add("train-image");

            Text countText = new Text();
            countText.getStyleClass().add("count");

            stackPane.getChildren().addAll(outsideRectangle, insideRectangle, trainImageRectangle, countText);

            //properties handling
            ReadOnlyIntegerProperty count = gameState.currentPlayerCardsTypeCount(c);
            stackPane.visibleProperty().bind(Bindings.greaterThan(count, 0));
            countText.textProperty().bind(Bindings.convert(count));
            countText.visibleProperty().bind(Bindings.greaterThan(count, 1));

            handPaneHBox.getChildren().add(stackPane);
        }
        return hbox;
    }

    /**
     * creates the cards view
     *
     * @param gameState                  gameState
     * @param drawTicketsHandlerProperty drawTicketsHandlerProperty
     * @param drawCardsHandlerProperty   drawCardsHandlerProperty
     * @return a node containing the cards view
     */

    public static Node createCardsView(ObservableGameState gameState, ObjectProperty<DrawTicketsHandler> drawTicketsHandlerProperty,
                                       ObjectProperty<DrawCardHandler> drawCardsHandlerProperty, ObjectProperty<SurrenderHandler> surrenderHandlerProperty,
                                       ObjectProperty<RestartHandler> restartHandlerObjectProperty) {
        //sounds creation
        AudioClip cardDrawn = new AudioClip(ClassLoader.getSystemResource("card.mp3").toString());

        //sounds binding
        cardDrawn.volumeProperty().bind(MediaViewCreator.volumeEffectProperty);

        //VBox creation
        VBox vbox = new VBox();
        vbox.getStylesheets().addAll("decks.css", "colors.css");
        vbox.setId("card-pane");

        //restart button creation
        Button restartButton = new Button("REJOUER");
        restartButton.getStyleClass().add("gauged");

        //restart button properties and action handling
        restartButton.disableProperty().bind(restartHandlerObjectProperty.isNull());
        restartButton.setOnAction(event -> restartHandlerObjectProperty.get().restart());

        vbox.getChildren().add(restartButton);


        //tickets button creation
        Button ticketsButton = new Button("Billets");
        ticketsButton.getStyleClass().add("gauged");

        //gauge creation
        Rectangle backGroundTicketsRectangle = new Rectangle(GAUGE_RECTANGLE_WIDTH, GAUGE_RECTANGLE_HEIGHT);
        backGroundTicketsRectangle.getStyleClass().add("background");
        Rectangle foregroundTicketsRectangle = new Rectangle(GAUGE_RECTANGLE_WIDTH, GAUGE_RECTANGLE_HEIGHT);
        foregroundTicketsRectangle.getStyleClass().add("foreground");
        Group ticketsGroup = new Group(backGroundTicketsRectangle, foregroundTicketsRectangle);
        ticketsButton.setGraphic(ticketsGroup);

        //percentage binding
        ReadOnlyIntegerProperty pctTicketsProperty = gameState.ticketsPercentage();
        foregroundTicketsRectangle.widthProperty().bind(pctTicketsProperty.multiply(GAUGE_RECTANGLE_WIDTH).divide(100));

        //tickets button properties and action handling
        ticketsButton.disableProperty().bind(drawTicketsHandlerProperty.isNull());
        ticketsButton.setOnAction(event -> drawTicketsHandlerProperty.get().onDrawTickets());

        vbox.getChildren().add(ticketsButton);

        for (int i : Constants.FACE_UP_CARD_SLOTS) {

            //stackPane creation
            StackPane stackPane = new StackPane();
            stackPane.getStyleClass().add("card");

            //outside rectangle creation
            Rectangle outsideRectangle = new Rectangle(OUTSIDE_RECTANGLE_WIDTH, OUTSIDE_RECTANGLE_HEIGHT);
            outsideRectangle.getStyleClass().add("outside");

            //inside rectangle creation
            Rectangle insideRectangle = new Rectangle(INSIDE_RECTANGLE_WIDTH, INSIDE_RECTANGLE_HEIGHT);
            insideRectangle.getStyleClass().addAll("inside", "filled");

            //train image rectangle creation
            Rectangle trainImageRectangle = new Rectangle(TRAIN_IMAGE_RECTANGLE_WIDTH, TRAIN_IMAGE_RECTANGLE_HEIGHT);
            trainImageRectangle.getStyleClass().add("train-image");

            stackPane.getChildren().addAll(outsideRectangle, insideRectangle, trainImageRectangle);

            //face up card property listener
            ReadOnlyObjectProperty<Card> faceUpCardProperty = gameState.faceUpCard(i);
            faceUpCardProperty.addListener((o, oldValue, newValue) -> {
                        if (oldValue == null) {
                            stackPane.getStyleClass().add(1, cardStyleClassName(newValue));
                        } else {
                            stackPane.getStyleClass().set(1, cardStyleClassName(newValue));
                        }
                    }
            );

            //properties and action handling
            stackPane.disableProperty().bind(drawCardsHandlerProperty.isNull());
            stackPane.setOnMouseClicked(event -> {
                drawCardsHandlerProperty.get().onDrawCard(i);
                cardDrawn.play();
            });
            vbox.getChildren().add(stackPane);
        }

        //cards button creation
        Button cardsButton = new Button("cartes");
        cardsButton.getStyleClass().add("gauged");

        //gauge creation
        Rectangle backGroundCardsRectangle = new Rectangle(GAUGE_RECTANGLE_WIDTH, GAUGE_RECTANGLE_HEIGHT);
        backGroundCardsRectangle.getStyleClass().add("background");
        Rectangle foregroundCardsRectangle = new Rectangle(GAUGE_RECTANGLE_WIDTH, GAUGE_RECTANGLE_HEIGHT);
        foregroundCardsRectangle.getStyleClass().add("foreground");
        Group cardsGroup = new Group(backGroundCardsRectangle, foregroundCardsRectangle);

        //percentage binding
        ReadOnlyIntegerProperty pctCardsProperty = gameState.cardsPercentage();
        foregroundCardsRectangle.widthProperty().bind(pctCardsProperty.multiply(GAUGE_RECTANGLE_WIDTH).divide(100));

        //cards button properties and action handling
        cardsButton.setGraphic(cardsGroup);
        cardsButton.disableProperty().bind(drawCardsHandlerProperty.isNull());
        cardsButton.setOnAction(event -> {
            drawCardsHandlerProperty.get().onDrawCard(Constants.DECK_SLOT);
            cardDrawn.play();

        });

        vbox.getChildren().add(cardsButton);

        //surrender button creation
        Button surrenderButton = new Button("abandonner la partie");
        surrenderButton.getStyleClass().add("gauged");

        //surrender button properties and action handling
        surrenderButton.disableProperty().bind(surrenderHandlerProperty.isNull());
        surrenderButton.setOnAction(event -> surrenderHandlerProperty.get().surrender());

        vbox.getChildren().add(surrenderButton);
        return vbox;
    }

    /**
     * a private methode to determine the style class name of the new card
     *
     * @param card new card
     * @return the style class name of the new card
     */
    private static String cardStyleClassName(Card card) {

        if (card.color() == null) {
            return "NEUTRAL";
        }

        return card.color().name();

    }

}