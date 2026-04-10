package ch.epfl.tchu.gui;


import ch.epfl.tchu.game.PlayerId;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.Map;

/**
 * InfoViewCreator
 *
 * @author OMAR MEZGHANI (311542)
 * @author MOHAMED CHARFI (311171)
 */

final class InfoViewCreator {
    private final static int RADIUS = 5;

    /**
     * private empty constructor
     */
    private InfoViewCreator() {
    }

    /**
     * creates info view
     *
     * @param playerId            playerId
     * @param playerIdStringMap   playerIdStringMap
     * @param observableGameState observableGameState
     * @param textObservableList  textObservableList
     * @return a node containing the info view
     */
    public static Node createInfoView(PlayerId playerId, Map<PlayerId, String> playerIdStringMap
            , ObservableGameState observableGameState, ObservableList<Text> textObservableList) {

        //vBox creation
        VBox vBox = new VBox();
        vBox.getStylesheets().addAll("info.css", "colors.css");

        //infoTextFlow creation
        TextFlow infoTextFlow = new TextFlow();
        infoTextFlow.setId("game-info");
        Bindings.bindContent(infoTextFlow.getChildren(), textObservableList);

        //playerInfoVBox creation
        VBox playerInfoVBox = new VBox();
        playerInfoVBox.setId("player-stats");

        //currentPlayerTextFlow creation
        TextFlow currentPlayerTextFlow = new TextFlow();
        currentPlayerTextFlow.getStyleClass().add(playerId.name());

        //currentPlayerStatsText creation
        Text currentPlayerStatsText = new Text();
        currentPlayerStatsText.textProperty().bind(Bindings.format(StringsFr.PLAYER_STATS, playerIdStringMap.get(playerId)
                , observableGameState.playerTicketCount(playerId), observableGameState.playerCardCount(playerId),
                observableGameState.playerCarCount(playerId), observableGameState.playerConstructionPoints(playerId)));

        //currentPlayerCircle creation
        Circle currentPlayerCircle = new Circle(RADIUS);
        currentPlayerCircle.getStyleClass().add("filled");

        currentPlayerTextFlow.getChildren().addAll(currentPlayerCircle, currentPlayerStatsText);

        //otherPlayerTextFlow creation
        TextFlow otherPlayerTextFlow = new TextFlow();
        otherPlayerTextFlow.getStyleClass().add(playerId.next().name());

        //otherPlayerStatsText creation
        Text otherPlayerStatsText = new Text();
        otherPlayerStatsText.textProperty().bind(Bindings.format(StringsFr.PLAYER_STATS, playerIdStringMap.get(playerId.next())
                , observableGameState.playerTicketCount(playerId.next()), observableGameState.playerCardCount(playerId.next()),
                observableGameState.playerCarCount(playerId.next()), observableGameState.playerConstructionPoints(playerId.next())));

        //otherPlayerCircle creation
        Circle otherPlayerCircle = new Circle(RADIUS);
        otherPlayerCircle.getStyleClass().add("filled");

        otherPlayerTextFlow.getChildren().addAll(otherPlayerCircle, otherPlayerStatsText);

        playerInfoVBox.getChildren().addAll(currentPlayerTextFlow, otherPlayerTextFlow);


        Separator separator = new Separator(Orientation.HORIZONTAL);
        vBox.getChildren().addAll(playerInfoVBox, separator, infoTextFlow);

        return vBox;
    }
}
