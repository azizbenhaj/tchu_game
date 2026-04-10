package ch.epfl.tchu.gui;

import ch.epfl.tchu.net.RemotePlayerClient;
import javafx.application.Application;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * @author Ahmed Aziz BEN HAJ (sciper: 310934) HMIDA et Aziz Laadher(sciper: 315196) / le 18 mai 2021
 * contient le programme principal du client tCHu
 */
public class ClientMain extends Application {
    /**
     *appelle seulement launch
     * @param args les arguments passés au programme
     */
    public static void main(String[] args) { launch(args); }

    /**
     *se charge de démarrer le client
     * @param primaryStage argument non utilisé
     */
    @Override
    public void start(Stage primaryStage) {
        String host= getParameters().getRaw().get(0);
        int port = Integer.parseInt(getParameters().getRaw().get(1));
        final RemotePlayerClient remotePlayerClient = new RemotePlayerClient(new GraphicalPlayerAdapter(), host, port);
        new Thread(() -> {
            try {
                remotePlayerClient.run();
            } catch (IOException e) {
                throw new Error("erreur de connexion");
            }
        }).start();
    }
}
