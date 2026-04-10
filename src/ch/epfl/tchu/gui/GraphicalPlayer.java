package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static ch.epfl.tchu.gui.DecksViewCreator.createCardsView;
import static ch.epfl.tchu.gui.DecksViewCreator.createHandView;
import static ch.epfl.tchu.gui.MapViewCreator.createMapView;
import static javafx.application.Platform.isFxApplicationThread;
import static javafx.application.Platform.runLater;

/**
 * @author Ahmed Aziz BEN HAJ (sciper: 310934) HMIDA et Aziz Laadher(sciper: 315196) / le 11 mai 2021
 * représente l'interface graphique d'un joueur de tCHu
 */
public class GraphicalPlayer implements MapViewCreator.CardChooser {
   private final ObjectProperty<ActionHandlers.ClaimRouteHandler> claimHandler;
   private final ObjectProperty<ActionHandlers.DrawCardHandler> cardHandler;
   private final ObjectProperty<ActionHandlers.DrawTicketsHandler> ticketHandler;
   private final ObservableGameState gameState;
   private final ObservableList<Text> list;
   private MediaPlayer mediaPlayerForBackgroundMusic;
   private final ObjectProperty<ActionHandlers.SurrenderHandler> surrenderHandler;
   /**
    * constructeur de la lcasse
    * @param id  l'identité du joueur
    * @param tab liste des identités des joueurs
    */
   public GraphicalPlayer(PlayerId id, Map<PlayerId, String> tab) {
      assert isFxApplicationThread();
      claimHandler = new SimpleObjectProperty<>();
      cardHandler = new SimpleObjectProperty<>();
      ticketHandler = new SimpleObjectProperty<>();
      surrenderHandler = new SimpleObjectProperty<>();
      list = FXCollections.observableList(new ArrayList<>());
      gameState = new ObservableGameState(id);
      Node a = createHandView(gameState);
      Node b = createCardsView(gameState, ticketHandler, cardHandler);
      Node c = createMapView(gameState, claimHandler, this);
      Node d = InfoViewCreator.createInfoView(id, tab, gameState, list, () ->
                 runLater(() -> {
                    if (mediaPlayerForBackgroundMusic.isMute()) {
                       mediaPlayerForBackgroundMusic.setMute(false);
                       mediaPlayerForBackgroundMusic.play();
                    } else {
                       mediaPlayerForBackgroundMusic.setMute(true);
                       mediaPlayerForBackgroundMusic.pause();
                    }
                 })
              ,
              surrenderHandler);
      BorderPane mainPane = new BorderPane(c, null, b, a, d);
      Stage stage = new Stage();
      stage.setTitle("tChu - " +tab.get(id));
      Scene scene = new Scene(mainPane);
      stage.setScene(scene);
      stage.show();
      runLater(() -> {
         File file = new File("/Users/sahbiabderrazak/Desktop/projet BA2/resources/BackgroundMusic.mp3");
         URI uri = file.toURI();
         Media media = new Media(uri.toString());
         mediaPlayerForBackgroundMusic = new MediaPlayer(media);
         //pour que le son commence directement, independament de toute commande
         mediaPlayerForBackgroundMusic.setAutoPlay(true);
         //pour que lorsqu'on arrive a la fin du fichier il se repete
         mediaPlayerForBackgroundMusic.setOnRepeat(() -> {
            mediaPlayerForBackgroundMusic.seek(Duration.ZERO);
            mediaPlayerForBackgroundMusic.play();
         });
         mediaPlayerForBackgroundMusic.play();
      });
   }

   /**
    * construit un graphe de scène représentant la fin du jeu
    * @param text le texte des informations de fin du jeu
    * @param replayHandler le gestionnaire du choix utilisé de type ReplayHandler
    */
    public void gameover(String text, ActionHandlers.ReplayHandler replayHandler) {
      assert isFxApplicationThread();
      Stage stage = new Stage(StageStyle.UTILITY);
      stage.setTitle("Fin du jeu");
      stage.initOwner(stage.getOwner());
      stage.initModality(Modality.WINDOW_MODAL);
      stage.setOnCloseRequest(event -> {
         replayHandler.onRequestReplay(gameState.getOwnId(), GameState.PlayerRestartResponse.DOESNT_WANT_TO_REPLAY);
         stage.close();
      });
      VBox vb = new VBox();
      Scene scene = new Scene(vb);
      stage.setScene(scene);
      scene.getStylesheets().add("chooser.css");
      TextFlow txt = new TextFlow();
      Text tx = new Text(text);
      txt.getChildren().add(tx);
      Button replayButton = new Button("Rejouer");
      replayButton.setOnMouseClicked(event -> {
         replayHandler.onRequestReplay(gameState.getOwnId(), GameState.PlayerRestartResponse.WANTS_TO_REPLAY);
         stage.close();
      });
      Button dontReplayButton = new Button("Fermer");
      dontReplayButton.setOnMouseClicked(event -> {
         replayHandler.onRequestReplay(gameState.getOwnId(), GameState.PlayerRestartResponse.DOESNT_WANT_TO_REPLAY);
         stage.close();
      });
      vb.getChildren().addAll(txt, replayButton, dontReplayButton);
      stage.show();
   }


   /**
    * appelle la methode setState sur l'état observable du joueur
    * @param pgs l'etat public du jeu
    * @param player le joueur concerné
    */
   public void setState(PublicGameState pgs, PlayerState player) {
      assert isFxApplicationThread();
      gameState.setState(pgs, player);
   }

   /**
    * prend un message de type String et l'ajoute au bas des informations sur le déroulement de la partie
    * @param msg message ajouté
    */
   public void receiveInfo(String msg) {
      assert isFxApplicationThread();
      list.add(new Text(msg));
      while (list.size() > 5) {
         list.remove(0, 1);
      }
   }

   /**
    * permet au joueur d'en effectuer une action
    * @param drawTickets gestionnaires d'action de drawTickets
    * @param drawCard    gestionnaires d'action de drawCard
    * @param claimRoute  gestionnaires d'action de claimRoute
    */
   public void startTurn(
           ActionHandlers.DrawTicketsHandler drawTickets,
           ActionHandlers.DrawCardHandler drawCard,
           ActionHandlers.ClaimRouteHandler claimRoute ,
           ActionHandlers.SurrenderHandler surrenderHandler) {
      assert isFxApplicationThread();

      if (gameState.canDrawTickets().get()) {
         ticketHandler.set(drawTickets);
      } else {
         ticketHandler.set(null);
      }
      if (gameState.canDrawCards().get()) {
         cardHandler.set(drawCard);
      } else {
         cardHandler.set(null);
      }
      claimHandler.set(claimRoute);
      this.surrenderHandler.set(surrenderHandler);
   }

   /**
    * permet au joueur de faire son choix
    * @param tic  multiensemble contenant cinq ou trois billets que le joueur peut choisir
    * @param handler gestionnaire de choix de billets
    */
   public void chooseTickets(SortedBag<Ticket> tic,
                             ActionHandlers.ChooseTicketsHandler handler) {
      assert isFxApplicationThread();
      Stage stage = new Stage(StageStyle.UTILITY);
      stage.setTitle(StringsFr.TICKETS_CHOICE);
      stage.initOwner(stage.getOwner());
      stage.setOnCloseRequest(Event::consume);
      stage.initModality(Modality.WINDOW_MODAL);
      VBox vb = new VBox();
      Scene scene = new Scene(vb);
      scene.getStylesheets().add("chooser.css");
      stage.setScene(scene);
      TextFlow txt = new TextFlow();
      Text tx = new Text(String.format(StringsFr.CHOOSE_TICKETS, tic.size() == 5 ? 3 : 1, StringsFr.plural(tic.size())));
      txt.getChildren().add(tx);
      ObservableList<Text> ticketTexts = FXCollections.observableArrayList();
      for (Ticket t : tic) {
         ticketTexts.add(new Text(t.text()));
      }
      ListView<Text> liste = new ListView<>(ticketTexts);
      liste.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
      Button but = new Button(StringsFr.CHOOSE);
      SimpleIntegerProperty sip = new SimpleIntegerProperty(0);
      but.disableProperty().bind(sip.lessThan(tic.size() - 2));
      ListChangeListener<? super Text> listener = change ->
         sip.set(liste.getSelectionModel().getSelectedItems().size())
      ;
      liste.getSelectionModel().getSelectedItems().addListener(listener);
      but.setOnMouseClicked(event -> {
         List<Ticket> tickets = tic.toList();
         List<Ticket> chosenTickets = new ArrayList<>();
         liste.getSelectionModel().getSelectedIndices().forEach(integer ->
            chosenTickets.add(tickets.get(integer))
         );
         handler.onChooseTickets(SortedBag.of(chosenTickets));
         ticketHandler.set(null);
         cardHandler.set(null);
         claimHandler.set(null);
         surrenderHandler.set(null);
         stage.close();
      });
      vb.getChildren().addAll(txt, liste, but);
      stage.show();
   }

   /**
    * autorise le joueur a choisir une carte wagon/locomotive, soit l'une des cinq dont la face est visible, soit celle du sommet de la pioche
    * @param handler gestionnaire de tirage de carte de type DrawCardHandler
    */
   public void drawCard(ActionHandlers.DrawCardHandler handler) {
      assert isFxApplicationThread();
      claimHandler.set(null);
      ticketHandler.set(null);
      surrenderHandler.set(null);
      cardHandler.set(handler);
   }

   /**
    * ouvre une fenêtre permettant au joueur de faire son choix
    * @param cards   liste de multiensembles de cartes qui sont les cartes initiales qu'il peut utiliser pour s'emparer d'une route
    * @param handler gestionnaire de choix de cartes
    */
   public void chooseClaimCards(List<SortedBag<Card>> cards, ActionHandlers.ChooseCardsHandler handler) {
      assert isFxApplicationThread();
      ticketHandler.set(null);
      cardHandler.set(null);
      surrenderHandler.set(null);
      if (cards.size() == 1) {
         handler.onChooseCards(cards.get(0));
         return;
      }
      Stage stage = new Stage(StageStyle.UTILITY);
      stage.setTitle(StringsFr.CARDS_CHOICE);
      stage.initOwner(stage.getOwner());
      stage.setOnCloseRequest(Event::consume);
      stage.initModality(Modality.WINDOW_MODAL);
      VBox vb = new VBox();
      Scene scene = new Scene(vb);
      stage.setScene(scene);
      scene.getStylesheets().add("chooser.css");
      TextFlow txt = new TextFlow();
      Text tx = new Text(StringsFr.CHOOSE_CARDS);
      txt.getChildren().add(tx);
      ObservableList<SortedBag<Card>> cardTexts = FXCollections.observableArrayList(cards);
      ListView<SortedBag<Card>> liste = new ListView<>(cardTexts);
      liste.setCellFactory(v -> new TextFieldListCell<>(new CardBagStringConverter()));
      Button but = new Button(StringsFr.CHOOSE);
      SimpleBooleanProperty sbp = new SimpleBooleanProperty(true);
      but.disableProperty().bind(sbp);
      ListChangeListener<? super SortedBag<Card>> listener = change ->
         sbp.set(liste.getSelectionModel().getSelectedItems().isEmpty())
      ;
      liste.getSelectionModel().getSelectedItems().addListener(listener);
      but.setOnMouseClicked(event -> {
         handler.onChooseCards(liste.getSelectionModel().getSelectedItem());
         stage.close();
      });
      vb.getChildren().addAll(txt, liste, but);
      stage.show();
   }

   /**
    * ouvre une fenêtre permettant au joueur de faire son choix
    * @param cards   liste de multiensembles de cartes qui sont les cartes additionnelles qu'il peut utiliser pour s'emparer d'un tunnel
    * @param handler gestionnaire de choix de cartes
    */
   public void chooseAdditionalCard(List<SortedBag<Card>> cards, ActionHandlers.ChooseCardsHandler handler) {
      assert isFxApplicationThread();
      Stage stage = new Stage(StageStyle.UTILITY);
      stage.setTitle(StringsFr.CARDS_CHOICE);
      stage.initOwner(stage.getOwner());
      stage.setOnCloseRequest(Event::consume);
      stage.initModality(Modality.WINDOW_MODAL);
      VBox vb = new VBox();
      Scene scene = new Scene(vb);
      stage.setScene(scene);
      scene.getStylesheets().add("chooser.css");
      TextFlow txt = new TextFlow();
      Text tx = new Text(StringsFr.CHOOSE_ADDITIONAL_CARDS);
      txt.getChildren().add(tx);
      ObservableList<SortedBag<Card>> cardTexts = FXCollections.observableArrayList(cards);
      ListView<SortedBag<Card>> liste = new ListView<>(cardTexts);
      liste.setCellFactory(v -> new TextFieldListCell<>(new CardBagStringConverter()));
      Button but = new Button(StringsFr.CHOOSE);
      but.setOnMouseClicked(event -> {
         if(liste.getSelectionModel().getSelectedIndex()<0) handler.onChooseCards(SortedBag.of());
         else
         handler.onChooseCards(cards.get(liste.getSelectionModel().getSelectedIndex()));
         stage.close();
      });
      vb.getChildren().addAll(txt, liste, but);
      stage.show();
   }

   /**
    * ouvre une fenêtre permettant au joueur de faire son choix
    * @param options liste de multiensembles de cartes qui sont les cartes initiales qu'il peut utiliser pour s'emparer d'une route
    * @param handler gestionnaire de choix de cartes
    */
 @Override
   public void chooseCards(List<SortedBag<Card>> options, ActionHandlers.ChooseCardsHandler handler) {
      assert isFxApplicationThread();
      chooseClaimCards(options, handler);
   }
}