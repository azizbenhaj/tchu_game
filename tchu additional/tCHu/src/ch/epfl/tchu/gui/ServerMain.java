package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import ch.epfl.tchu.net.RemotePlayerProxy;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.game.PlayerId.PLAYER_2;

/**
 * ServerMain
 * extends Application
 *
 * @author OMAR MEZGHANI (311542)
 * @author MOHAMED CHARFI (311171)
 */
public class ServerMain extends Application {

    private final BlockingQueue<String> player1BlockingQueue = new ArrayBlockingQueue<>(1);
    private final BlockingQueue<String> player2BlockingQueue = new ArrayBlockingQueue<>(1);
    private static final String DEFAULT_FIRST_PLAYER = "Ada";
    private static final String DEFAULT_SECOND_PLAYER = "Charles";

    /**
     * private method that puts T in the BlockingQueue
     *
     * @param blockingQueue blocking queue
     * @param element       element
     * @param <T>           generic parameter specifying the type of element in the blocking queue
     */
    private <T> void putInBlockingQueue(BlockingQueue<T> blockingQueue, T element) {
        try {
            blockingQueue.put(element);
        } catch (InterruptedException interruptedException) {
            throw new Error();
        }
    }

    /**
     * a private method that takes an element from the blocking queue
     *
     * @param blockingQueue blocking queue
     * @param <T>           generic parameter specifying the type of element in the blocking queue
     * @return T
     */
    private <T> T takeFromBlockingQueue(BlockingQueue<T> blockingQueue) {
        try {
            return blockingQueue.take();
        } catch (InterruptedException interruptedException) {
            throw new Error();
        }

    }

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * starts the game for the client
     *
     * @param primaryStage primaryStage
     * @throws IOException IOException
     */
    @Override
    public void start(Stage primaryStage) throws IOException {

        Platform.setImplicitExit(false);

        //Stage creation
        Stage stage = new Stage(StageStyle.UTILITY);
        stage.initModality(Modality.WINDOW_MODAL);

        //Text creation
        Text text = new Text("CHOISIR LE NOM DES DEUX JOUEURS");
        Text textServer = new Text("votre nom ");
        Text textClient = new Text("le nom de l'autre joueur");

        //TextField creation
        TextField serverField = new TextField();
        TextField clientField = new TextField();

        //Separator creation
        Separator separator = new Separator(Orientation.HORIZONTAL);

        //Button creation
        Button button = new Button("CHOISIR");
        button.setOnAction(event -> {
                    stage.hide();
                    String firstPlayerName = DEFAULT_FIRST_PLAYER;
                    String secondPlayerName = DEFAULT_SECOND_PLAYER;

                    if (!serverField.getText().equals(""))
                        firstPlayerName = serverField.getText();

                    if (!clientField.getText().equals(""))
                        secondPlayerName = clientField.getText();

                    putInBlockingQueue(player1BlockingQueue, firstPlayerName);
                    putInBlockingQueue(player2BlockingQueue, secondPlayerName);
                }

        );

        //VBox creation
        VBox vBox = new VBox();
        vBox.getChildren().addAll(text, separator, textServer, serverField, textClient, clientField, button);

        // Scene creation
        Scene scene = new Scene(vBox);
        scene.getStylesheets().add("chooser.css");
        stage.setScene(scene);
        stage.setTitle("CHOIX DES NOMS DES JOUEURS");
        stage.show();

        Player playerProxy;

        try (ServerSocket serverSocket = new ServerSocket(5108)) {
            Socket socket = serverSocket.accept();
            playerProxy = new RemotePlayerProxy(socket);
        }

        new Thread(() -> {
            Random rng = new Random();
            SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets());
            Map<PlayerId, Player> players = Map.of(PLAYER_1, new GraphicalPlayerAdapter(), PLAYER_2, playerProxy);
            Map<PlayerId, String> playerNames = Map.of(PLAYER_1, takeFromBlockingQueue(player1BlockingQueue),
                    PLAYER_2, takeFromBlockingQueue(player2BlockingQueue));
            Game.play(players, playerNames, tickets, rng);
        }).start();
    }
}
