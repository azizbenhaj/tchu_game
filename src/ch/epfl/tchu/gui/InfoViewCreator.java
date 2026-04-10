package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.PlayerId;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import java.util.Map;

/**
 * @author Ahmed Aziz BEN HAJ (sciper: 310934) HMIDA et Aziz Laadher(sciper: 315196) / le 11 mai 2021
 * permet de créer la vue des informations
 */
class InfoViewCreator {
    /**
     * permet de créer la vue des informations
     * @param playerId l'identité du joueur auquel l'interface correspond
     * @param tab la table associative des noms des joueurs
     * @param ogs l'état de jeu observable
     * @param liste une liste (observable) contenant les informations sur le déroulement de la partie
     * @return la vue des informations
     */
    public static Pane createInfoView(PlayerId playerId, Map<PlayerId, String> tab,
                                      ObservableGameState ogs, ObservableList<Text> liste,
                                      ActionHandlers.BackgroundSoundStateHandler backgroundSoundStateHandler,
                                      ObjectProperty<ActionHandlers.SurrenderHandler> surrenderHandler) {

        VBox vb = new VBox();
        vb.getStylesheets().addAll("info.css", "colors.css");
        Button muteButton = new Button("Couper le son de fond");
        muteButton.setOnMouseClicked(event -> backgroundSoundStateHandler.onToggleSoundState());
        vb.getChildren().add(muteButton);
        Button surrenderButton = new Button("Abandonner");
        surrenderButton.setOnMouseClicked(event -> surrenderHandler.get().onSurrender());
        surrenderButton.disableProperty().bind(surrenderHandler.isNull());
        vb.getChildren().add(surrenderButton);
        Separator sep = new Separator();
        sep.setOrientation(Orientation.HORIZONTAL);
        VBox vb1 = new VBox();
        vb1.setId("player-stats");
        vb.getChildren().add(vb1);
        TextFlow txt1 = new TextFlow();
        txt1.getStyleClass().add(playerId.name());
        vb1.getChildren().add(txt1);
        Circle circ1 = new Circle(5);
        circ1.getStyleClass().add("filled");
        Text t1 = new Text();
        StringExpression monExpression1 = Bindings.format(StringsFr.PLAYER_STATS, tab.get(playerId), playerId == PlayerId.PLAYER_1 ?
                ogs.getTicketsInHand1():ogs.getTicketsInHand2(), playerId == PlayerId.PLAYER_1 ?ogs.getCardsInHand1():ogs.getCardsInHand2(),
                playerId == PlayerId.PLAYER_1 ?ogs.getCarsInHand1():ogs.getCarsInHand2(), playerId == PlayerId.PLAYER_1 ?ogs.getPoints1():ogs.getPoints2());
        t1.textProperty().bind(monExpression1);
        txt1.getChildren().addAll(circ1, t1);

        TextFlow txt2 = new TextFlow();
        txt2.getStyleClass().add(playerId.next().name());
        vb1.getChildren().add(txt2);
        Circle circ2 = new Circle(5);
        circ2.getStyleClass().add("filled");
        Text t2 = new Text();
        StringExpression monExpression2 = Bindings.format(StringsFr.PLAYER_STATS, tab.get(playerId.next()),  playerId.next() == PlayerId.PLAYER_1 ?
                ogs.getTicketsInHand1():ogs.getTicketsInHand2(), playerId.next() == PlayerId.PLAYER_1 ?ogs.getCardsInHand1():ogs.getCardsInHand2(),
                playerId.next() == PlayerId.PLAYER_1 ?ogs.getCarsInHand1():ogs.getCarsInHand2(), playerId.next() == PlayerId.PLAYER_1 ?ogs.getPoints1():ogs.getPoints2());
        t2.textProperty().bind(monExpression2);
        txt2.getChildren().addAll(circ2, t2);
        vb.getChildren().add(sep);
        TextFlow tf = new TextFlow();
        tf.setId("game-info");
        vb.getChildren().add(tf);

        Bindings.bindContent(tf.getChildren(),liste);
        return vb;
    }
}

