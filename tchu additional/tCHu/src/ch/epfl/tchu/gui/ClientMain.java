package ch.epfl.tchu.gui;

import ch.epfl.tchu.net.RemotePlayerClient;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
/**
 * ClientMain
 * extends Application
 * @author OMAR MEZGHANI (311542)
 * @author MOHAMED CHARFI (311171)
 */
public class ClientMain extends Application {
    private final static int ARGUMENT_SIZE = 2;
    private final static String DEFAULT_PORT_NUMBER = "5108";
    private static final String DEFAULT_HOST_NAME = "localhost";

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * starts the game for the client
     * @param primaryStage primary Stage
     */
    @Override
    public void start(Stage primaryStage) {

        List<String> arguments = new ArrayList<>(this.getParameters().getRaw());
        if (arguments.size() == 0) {
            arguments.add(DEFAULT_HOST_NAME);
            arguments.add(DEFAULT_PORT_NUMBER);
        } else if (arguments.size() == ARGUMENT_SIZE - 1) {
            arguments.add(DEFAULT_PORT_NUMBER);
        }
        RemotePlayerClient remotePlayerClient = new RemotePlayerClient(new GraphicalPlayerAdapter(), arguments.get(0), Integer.parseInt(arguments.get(1)));
        new Thread(remotePlayerClient::run).start();

    }
}