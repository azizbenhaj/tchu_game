
/*package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static ch.epfl.tchu.game.Card.*;
import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.game.PlayerId.PLAYER_2;

class RemotePlayerProxyTest {
    public static void main(String[] args) throws IOException {
        System.out.println("Starting server!");
        try (ServerSocket serverSocket = new ServerSocket(5108);
             Socket socket = serverSocket.accept()) {
            Player playerProxy = new RemotePlayerProxy(socket);

            var rng = new Random(100);
            var routes = ChMap.routes();
            var tickets = ChMap.tickets();
            GameState gameState = GameState.initial(SortedBag.of(tickets), rng);


            List<Card> fu = List.of(RED, WHITE, BLUE, BLACK, RED);
            PublicCardState cs = new PublicCardState(fu, 30, 31);
            List<Route> rs1 = ChMap.routes().subList(0, 2);
            Map<PlayerId, PublicPlayerState> ps = Map.of(
                    PLAYER_1, new PublicPlayerState(10, 11, rs1),
                    PLAYER_2, new PublicPlayerState(20, 21, List.of()));

            var playerNames = Map.of(PLAYER_1, "Ada",
                    PLAYER_2, "Charles");
            playerProxy.initPlayers(PLAYER_1, playerNames);

            var info = "aziz";
            playerProxy.receiveInfo(info);
            System.out.println(SortedBag.of(tickets.subList(0, 5)));
            playerProxy.setInitialTicketChoice(SortedBag.of(tickets.subList(0, 5)));
            playerProxy.updateState(gameState, gameState.playerState(PlayerId.PLAYER_1));
            System.out.println(playerProxy.chooseInitialTickets());
            System.out.println(playerProxy.nextTurn());
            System.out.println(playerProxy.chooseTickets(SortedBag.of(tickets.subList(0, 3))));
            System.out.println(playerProxy.drawSlot());
            System.out.println(playerProxy.claimedRoute());
            System.out.println(playerProxy.initialClaimCards());
            System.out.println(playerProxy.chooseAdditionalCards(List.of(SortedBag.of(2, BLUE), SortedBag.of(2, LOCOMOTIVE), SortedBag.of(1, BLUE, 1, LOCOMOTIVE))));
            //serverSocket.close();
        }
        System.out.println("Server done!");
    }
}*/
package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static ch.epfl.tchu.game.Card.*;
import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.game.PlayerId.PLAYER_2;

class RemotePlayerProxyTest {
    public static void main(String[] args) throws IOException {
        System.out.println("Starting server!");
        try (ServerSocket serverSocket = new ServerSocket(5108);
        ) {
            Socket socket1 = serverSocket.accept();
            Socket socket2 = serverSocket.accept();

            Player playerProxy1 = new RemotePlayerProxy(socket1);
            Player playerProxy2 = new RemotePlayerProxy(socket2);

            var rng = new Random(100);
            var routes = ChMap.routes();
            var tickets = ChMap.tickets();
            var players = Map.of(
                    PlayerId.PLAYER_1, (Player) playerProxy1,
                    PlayerId.PLAYER_2, (Player) playerProxy2);
            var playerNames = Map.of(
                    PlayerId.PLAYER_1, "Ada",
                    PlayerId.PLAYER_2, "Charles");
            Game.play(players, playerNames, SortedBag.of(tickets), rng);


//      List<Card> fu = List.of(RED, WHITE, BLUE, BLACK, RED);
//      PublicCardState cs = new PublicCardState(fu, 30, 31);
//      List<Route> rs1 = ChMap.routes().subList(0, 2);
//      Map<PlayerId, PublicPlayerState> ps = Map.of(
//          PLAYER_1, new PublicPlayerState(10, 11, rs1),
//          PLAYER_2, new PublicPlayerState(20, 21, List.of()));
//playerProxy1.initPlayers(PLAYER_1, playerNames);
//      playerProxy2.initPlayers(PLAYER_2, playerNames);
//
//      var info = "aziz";
//      playerProxy1.receiveInfo(info);
//      playerProxy2.receiveInfo(info);
//      playerProxy1.setInitialTicketChoice(SortedBag.of(tickets.subList(0, 5)));
//      playerProxy2.setInitialTicketChoice(SortedBag.of(tickets.subList(0, 5)));
//      playerProxy1.updateState(gameState, gameState.playerState(PlayerId.PLAYER_1));
//      playerProxy2.updateState(gameState, gameState.playerState(PlayerId.PLAYER_1));
//      System.out.println(playerProxy1.chooseInitialTickets());
//      System.out.println(playerProxy2.chooseInitialTickets());
//      System.out.println(playerProxy1.nextTurn());
//      System.out.println(playerProxy2.nextTurn());
//      System.out.println(playerProxy1.chooseTickets(SortedBag.of(tickets.subList(0, 3))));
//      System.out.println(playerProxy2.chooseTickets(SortedBag.of(tickets.subList(0, 3))));
//      System.out.println(playerProxy1.drawSlot());
//      System.out.println(playerProxy2.drawSlot());
//      System.out.println(playerProxy1.claimedRoute());
//      System.out.println(playerProxy2.claimedRoute());
//      System.out.println(playerProxy1.initialClaimCards());
//      System.out.println(playerProxy2.initialClaimCards());
//      System.out.println(playerProxy1.chooseAdditionalCards(List.of(SortedBag.of(2, BLUE), SortedBag.of(2, LOCOMOTIVE), SortedBag.of(1, BLUE, 1, LOCOMOTIVE))));
//      System.out.println(playerProxy2.chooseAdditionalCards(List.of(SortedBag.of(2, BLUE), SortedBag.of(2, LOCOMOTIVE), SortedBag.of(1, BLUE, 1, LOCOMOTIVE))));
            serverSocket.close();
            System.out.println("Server done!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}