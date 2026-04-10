package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Game;
import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.net.RemotePlayerProxy;
import javafx.application.Application;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Random;

/**
 * @author Ahmed Aziz BEN HAJ (sciper: 310934) HMIDA et Aziz Laadher(sciper: 315196) / le 18 mai 2021
 * contient le programme principal du serveur tCHu
 */
public class ServerMain extends Application {
    /**
     *appelle seulement launch
     * @param args les arguments passés au programme
     */
    public static void main(String[] args) { launch(args); }

    /**
     *se charge de démarrer le serveur
     * @param primaryStage argument ignoré
     */
    @Override
    public void start(Stage primaryStage) {
        String playerOneName;
        String playerTwoName;
        if(getParameters().getRaw().isEmpty()) {
            playerOneName= "Ada";
            playerTwoName= "Charles";
        }else{
            playerOneName= getParameters().getRaw().get(0);
            playerTwoName= getParameters().getRaw().get(1);}

        try (ServerSocket serverSocket = new ServerSocket(5108)
        ) {
            Socket socket1 = serverSocket.accept();
            GraphicalPlayerAdapter player1= new GraphicalPlayerAdapter();
            Player playerProxy1 = new RemotePlayerProxy(socket1);


            Map<PlayerId,Player> map1 = Map.of(PlayerId.PLAYER_1,player1,PlayerId.PLAYER_2,playerProxy1);
            Map<PlayerId,String> map2 = Map.of(PlayerId.PLAYER_1,playerOneName,PlayerId.PLAYER_2,playerTwoName);

            new Thread(() ->
                Game.play(map1,map2,SortedBag.of(ChMap.tickets()),new Random())
            ).start();

        } catch (IOException e) {
            throw new Error("erreur de connexion");
        }


    }
}
