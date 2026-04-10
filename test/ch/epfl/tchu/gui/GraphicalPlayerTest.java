/*package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.List;
import java.util.Map;
import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.game.PlayerId.PLAYER_2;

public final class GraphicalPlayerTest extends Application {
    PlayerState p1State =
            new PlayerState(SortedBag.of(ChMap.tickets().subList(0, 3)),
                    SortedBag.of(5, Card.LOCOMOTIVE, 4, Card.BLUE),
                    ChMap.routes().subList(0, 3));

    public static void main(String[] args) {
        launch(args);
    }

    private void setState(GraphicalPlayer player) {
        PublicPlayerState p2State =
                new PublicPlayerState(0, 0, ChMap.routes().subList(3, 6));

        Map<PlayerId, PublicPlayerState> pubPlayerStates =
                Map.of(PLAYER_1, p1State, PLAYER_2, p2State);
        PublicCardState cardState =
                new PublicCardState(Card.ALL.subList(0, 5), 110 - 2 * 4 - 5, 0);
//    GameState gameState2 = GameState.initial(SortedBag.of(ChMap.tickets()), new Random());
//    if (gameState2.currentPlayerId() == PLAYER_2) {
//      gameState2 = gameState2.forNextTurn();
//    }
        PublicGameState publicGameState =
                new PublicGameState(36, cardState, PLAYER_1, pubPlayerStates, null);
        player.setState(publicGameState, p1State);
    }

    @Override
    public void start(Stage primaryStage) {
        Map<PlayerId, String> playerNames =
                Map.of(PLAYER_1, "Ada", PLAYER_2, "Charles");
        GraphicalPlayer p = new GraphicalPlayer(PLAYER_1, playerNames);
        setState(p);
        Scene scene = new Scene((Parent) p.getNode());
        primaryStage.setScene(scene);
        primaryStage.setTitle("tChu - " + playerNames.get(PLAYER_1));
        primaryStage.show();
        ActionHandlers.ChooseTicketsHandler chooseTicket =
                (tickets) -> {
                    System.out.println("chose tickets !");
                };
        ActionHandlers.DrawTicketsHandler drawTicketsH =
                () -> {
                    p.receiveInfo("Je tire des billets !");
                    p.chooseTickets(SortedBag.of(ChMap.tickets().subList(0, 3)), chooseTicket);
                };

        ActionHandlers.DrawCardHandler drawCardH2 =
                s -> {
                    p.receiveInfo(String.format("Je tire une carte de %s !", s));
                    System.out.println("drew second card");
                };
        ActionHandlers.DrawCardHandler drawCardH =
                s -> {
                    p.receiveInfo(String.format("Je tire une carte de %s !", s));
                    System.out.println("drew first card");
                    p.drawCard(drawCardH2);
                };
        ActionHandlers.ChooseCardsHandler claimRouteH2 =
                (cards) -> {
//          String rn = r.station1() + " - " + r.station2();
                    p.receiveInfo(String.format("Je m'empare de %s avec %s", cards, cards));
                };
        ActionHandlers.ClaimRouteHandler claimRouteH =
                (r, cs) -> {
                    String rn = r.station1() + " - " + r.station2();
//          p.receiveInfo(String.format("Je m'empare de %s avec %s", rn, cs));
                    p.chooseClaimCards(p1State.possibleClaimCards(r), claimRouteH2);
//          if(r.level() == Route.Level.UNDERGROUND){
//            p.chooseAdditionalCard();
//          }
                };

        ActionHandlers.ChooseCardsHandler chooseCardsHandler =
                cards -> {
                    p.receiveInfo(String.format("J'ai choisi les cartes additionnelles %s", cards.toString()));
                };

        p.chooseAdditionalCard(
                List.of(SortedBag.of(1, Card.BLUE, 2, Card.LOCOMOTIVE), SortedBag.of(1, Card.RED, 2, Card.BLUE), SortedBag.of(1, Card.LOCOMOTIVE, 2, Card.YELLOW)),
                chooseCardsHandler
        );

        p.startTurn(drawTicketsH, drawCardH, claimRouteH);
    }
}*/
