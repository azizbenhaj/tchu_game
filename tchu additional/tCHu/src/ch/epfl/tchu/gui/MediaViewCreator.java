package ch.epfl.tchu.gui;

import javafx.beans.property.*;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.shape.Rectangle;

import java.net.URL;

/**
 * MediaViewCreator
 *
 * @author OMAR MEZGHANI (311542)
 * @author MOHAMED CHARFI (311171)
 */
public class MediaViewCreator {

    private final static int IMAGE_RECTANGLE_WIDTH = 60;
    private final static int IMAGE_RECTANGLE_HEIGHT = 60;
    private final static int GAUGE_RECTANGLE_WIDTH = 80;
    private final static int GAUGE_RECTANGLE_HEIGHT = 5;
    public static DoubleProperty volumeEffectProperty = new SimpleDoubleProperty(1);

    /**
     * private empty constructor
     */
    private MediaViewCreator() {
    }

    /**
     * creates the media view
     *
     * @return a node containing the media view
     */
    public static Node createMediaView(ObservableGameState observableGameState) {

        DoubleProperty volumeProperty = new SimpleDoubleProperty(0.0);
        final URL resource = ClassLoader.getSystemResource("background.mp3");
        Media media = new Media(resource.toString());
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setAutoPlay(true);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.volumeProperty().bind(volumeProperty);

        ReadOnlyBooleanProperty booleanProperty = observableGameState.theGameEnded();
        booleanProperty.addListener((o, oldValue, newValue) -> {
            volumeProperty.set(0);
            mediaPlayer.stop();
        });

        MediaView mediaView = new MediaView(mediaPlayer);
        HBox hbox = new HBox();
        hbox.getStylesheets().add("decks.css");
        hbox.setId("media-pane");

        hbox.getChildren().add(mediaView);


        // ***************** BACKGROUND MUSIC *****************

        //_______________ mute volume ________________________

        //StackPane creation
        StackPane muteStackPane = new StackPane();
        muteStackPane.getStyleClass().add("media");
        muteStackPane.setTranslateX(200);

        //mute image rectangle creation
        Rectangle muteImageRectangle = new Rectangle(IMAGE_RECTANGLE_WIDTH, IMAGE_RECTANGLE_HEIGHT);
        muteImageRectangle.getStyleClass().addAll("mute-image", "track");

        muteStackPane.getChildren().addAll(muteImageRectangle);
        muteStackPane.setOnMouseClicked(event -> volumeProperty.set(0));

        //_______________ reduce volume ________________________

        //StackPane creation
        StackPane reduceVolumeStackPane = new StackPane();
        reduceVolumeStackPane.getStyleClass().add("media");
        reduceVolumeStackPane.setTranslateX(200);

        //reduce volume image rectangle creation
        Rectangle reduceVolumeImageRectangle = new Rectangle(IMAGE_RECTANGLE_WIDTH, IMAGE_RECTANGLE_HEIGHT);
        reduceVolumeImageRectangle.getStyleClass().addAll("reduceVolume-image", "track");

        reduceVolumeStackPane.getChildren().addAll(reduceVolumeImageRectangle);
        reduceVolumeStackPane.setOnMouseClicked(event -> {
            if (volumeProperty.getValue() > 0.0) {
                volumeProperty.set(volumeProperty.getValue() - 0.1);
            }
        });


        //_______________ add volume ________________________

        //StackPane creation
        StackPane addVolumeStackPane = new StackPane();
        addVolumeStackPane.getStyleClass().add("media");
        addVolumeStackPane.setTranslateX(200);

        //add volume image rectangle creation
        Rectangle addVolumeImageRectangle = new Rectangle(IMAGE_RECTANGLE_WIDTH, IMAGE_RECTANGLE_HEIGHT);
        addVolumeImageRectangle.getStyleClass().addAll("addVolume-image", "track");

        addVolumeStackPane.getChildren().addAll(addVolumeImageRectangle);
        addVolumeStackPane.setOnMouseClicked(event -> {
            if (volumeProperty.getValue() < 0.99) {
                volumeProperty.set(volumeProperty.getValue() + 0.1);
            }
        });

        //gauge creation
        Label backgroundGaugeLabel = new Label("volume musique de fond");
        backgroundGaugeLabel.getStyleClass().addAll("gauged");

        Rectangle backGroundCardsRectangleMusicVolume = new Rectangle(GAUGE_RECTANGLE_WIDTH, GAUGE_RECTANGLE_HEIGHT);
        backGroundCardsRectangleMusicVolume.getStyleClass().add("background");
        Rectangle foregroundCardsRectangleMusicVolume = new Rectangle(GAUGE_RECTANGLE_WIDTH, GAUGE_RECTANGLE_HEIGHT);
        foregroundCardsRectangleMusicVolume.getStyleClass().add("foreground");
        Group musicVolumeGroup = new Group(backGroundCardsRectangleMusicVolume, foregroundCardsRectangleMusicVolume);

        foregroundCardsRectangleMusicVolume.widthProperty().bind(volumeProperty.multiply(GAUGE_RECTANGLE_WIDTH));
        backgroundGaugeLabel.setGraphic(musicVolumeGroup);
        backgroundGaugeLabel.setTranslateX(200);


        // ***************** EFFECTS *****************


        //gauge creation
        Label effectGaugeLabel = new Label("volume des effets");
        effectGaugeLabel.getStyleClass().addAll("gauged");

        Rectangle backGroundCardsRectangleEffectVolume = new Rectangle(GAUGE_RECTANGLE_WIDTH, GAUGE_RECTANGLE_HEIGHT);
        backGroundCardsRectangleEffectVolume.getStyleClass().add("background");
        Rectangle foregroundCardsRectangleEffectVolume = new Rectangle(GAUGE_RECTANGLE_WIDTH, GAUGE_RECTANGLE_HEIGHT);
        foregroundCardsRectangleEffectVolume.getStyleClass().add("foreground");
        Group EffectVolumeGroup = new Group(backGroundCardsRectangleEffectVolume, foregroundCardsRectangleEffectVolume);

        foregroundCardsRectangleEffectVolume.widthProperty().bind(volumeEffectProperty.multiply(GAUGE_RECTANGLE_WIDTH));
        effectGaugeLabel.setGraphic(EffectVolumeGroup);
        effectGaugeLabel.setTranslateX(660);

        //_______________ reduce volume for effects  ________________________

        //StackPane creation
        StackPane reduceVolumeEffectStackPane = new StackPane();
        reduceVolumeEffectStackPane.getStyleClass().add("media");
        reduceVolumeEffectStackPane.setTranslateX(660);


        //reduce volume image rectangle creation
        Rectangle reduceVolumeEffectImageRectangle = new Rectangle(IMAGE_RECTANGLE_WIDTH, IMAGE_RECTANGLE_HEIGHT);
        reduceVolumeEffectImageRectangle.getStyleClass().addAll("reduceVolume-image", "track");
        reduceVolumeEffectStackPane.getChildren().addAll(reduceVolumeEffectImageRectangle);
        reduceVolumeEffectStackPane.setOnMouseClicked(event -> {
            if (MediaViewCreator.volumeEffectProperty.getValue() > 0) {
                MediaViewCreator.volumeEffectProperty.set(MediaViewCreator.volumeEffectProperty.getValue() - 0.1);
            }
        });


        //_______________ add volume for effects  ________________________

        //StackPane creation
        StackPane addVolumeEffectStackPane = new StackPane();
        addVolumeEffectStackPane.getStyleClass().add("media");
        addVolumeEffectStackPane.setTranslateX(660);

        //reduce volume image rectangle creation
        Rectangle addVolumeEffectImageRectangle = new Rectangle(IMAGE_RECTANGLE_WIDTH, IMAGE_RECTANGLE_HEIGHT);
        addVolumeEffectImageRectangle.getStyleClass().addAll("addVolume-image", "track");
        addVolumeEffectStackPane.getChildren().addAll(addVolumeEffectImageRectangle);
        addVolumeEffectStackPane.setOnMouseClicked(event -> {
            if (MediaViewCreator.volumeEffectProperty.getValue() < 0.99) {
                MediaViewCreator.volumeEffectProperty.set(MediaViewCreator.volumeEffectProperty.getValue() + 0.1);
            }
        });

        //_______________ mute volume for effects  ________________________

        //StackPane creation
        StackPane muteEffectStackPane = new StackPane();
        muteEffectStackPane.getStyleClass().add("media");
        muteEffectStackPane.setTranslateX(660);

        //reduce volume image rectangle creation
        Rectangle muteEffectImageRectangle = new Rectangle(IMAGE_RECTANGLE_WIDTH, IMAGE_RECTANGLE_HEIGHT);
        muteEffectImageRectangle.getStyleClass().addAll("mute-image", "track");
        muteEffectStackPane.getChildren().addAll(muteEffectImageRectangle);
        muteEffectStackPane.setOnMouseClicked(event -> MediaViewCreator.volumeEffectProperty.set(0));

        hbox.getChildren().addAll(muteStackPane, reduceVolumeStackPane, addVolumeStackPane, backgroundGaugeLabel, effectGaugeLabel, muteEffectStackPane, reduceVolumeEffectStackPane, addVolumeEffectStackPane);

        return hbox;
    }
}
