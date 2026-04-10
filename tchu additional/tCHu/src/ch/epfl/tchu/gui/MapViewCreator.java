package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.Route;
import javafx.beans.property.ObjectProperty;
import ch.epfl.tchu.gui.ActionHandlers.*;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.media.AudioClip;
import javafx.scene.shape.*;

import java.util.List;

/**
 * MapViewCreator
 *
 * @author OMAR MEZGHANI (311542)
 * @author MOHAMED CHARFI (311171)
 */

final class MapViewCreator {
    private final static int RECTANGLE_WIDTH = 36;
    private final static int RECTANGLE_HEIGHT = 12;
    private final static int FIRST_CIRCLE_CENTER_X = 12;
    private final static int SECOND_CIRCLE_CENTER_X = 24;
    private final static int CIRCLE_CENTER_Y = 6;
    private final static int CIRCLE_RADIUS = 3;


    /**
     * CardChooser interface
     */
    @FunctionalInterface
    public interface CardChooser {
        /**
         * chooses the cards
         *
         * @param options cards options
         * @param handler ChooseCardsHandler
         */
        void chooseCards(List<SortedBag<Card>> options, ChooseCardsHandler handler);
    }

    /**
     * private empty constructor
     */
    private MapViewCreator() {
    }

    /**
     * creates the map view
     *
     * @param gameState                 the game state
     * @param claimRouteHandlerProperty the claimRouteHandlerProperty
     * @param cardChooser               the cardChooser
     * @return a node containing the map view
     */
    public static Node createMapView(ObservableGameState gameState, ObjectProperty<ClaimRouteHandler> claimRouteHandlerProperty, CardChooser cardChooser) {
        AudioClip claimRoute = new AudioClip(ClassLoader.getSystemResource("train.mp3").toString());
        claimRoute.volumeProperty().bind(MediaViewCreator.volumeEffectProperty);
        //Pane creation
        Pane pane = new Pane();
        pane.getStylesheets().addAll("map.css", "colors.css");
        pane.getChildren().add(new ImageView());

        //Route creation
        for (Route r : ChMap.routes()) {

            //route group creation
            Group routeGroup = new Group();
            routeGroup.setId(r.id());
            routeGroup.getStyleClass().addAll("route", routeStyleClassName(r));

            if (r.level() == Route.Level.UNDERGROUND) {
                routeGroup.getStyleClass().add("UNDERGROUND");
            }


            //route rectangles creation
            for (int i = 1; i <= r.length(); i++) {

                Group boxGroup = new Group();
                boxGroup.setId(String.format(r.id() + "_%d", i));
                routeGroup.getChildren().add(boxGroup);

                Rectangle wayRectangle = new Rectangle(RECTANGLE_WIDTH, RECTANGLE_HEIGHT);
                wayRectangle.getStyleClass().addAll("track", "filled");

                boxGroup.getChildren().add(wayRectangle);

                Group carGroup = new Group();
                carGroup.getStyleClass().add("car");
                boxGroup.getChildren().add(carGroup);

                Rectangle carRectangle = new Rectangle(RECTANGLE_WIDTH, RECTANGLE_HEIGHT);
                carRectangle.getStyleClass().add("filled");
                Circle carCircle1 = new Circle(FIRST_CIRCLE_CENTER_X, CIRCLE_CENTER_Y, CIRCLE_RADIUS);
                Circle carCircle2 = new Circle(SECOND_CIRCLE_CENTER_X, CIRCLE_CENTER_Y, CIRCLE_RADIUS);

                carGroup.getChildren().addAll(carRectangle, carCircle1, carCircle2);

            }

            //player id property listener
            ReadOnlyObjectProperty<PlayerId> possessionPlayerIdProperty = gameState.routesPossession(r);
            possessionPlayerIdProperty.addListener((o, oldValue, newValue) -> {
                if (oldValue == null && newValue != null) {
                    routeGroup.getStyleClass().add(newValue.name());
                    claimRoute.play();
                }
            });

            //longest property listener
            ReadOnlyObjectProperty<PlayerId> longestPlayerIdProperty = gameState.longestRouteProperty(r);
            longestPlayerIdProperty.addListener((o, oldValue, newValue) -> {
                if (oldValue == null && newValue != null) {
                    routeGroup.getStyleClass().add("LONGEST_" + newValue.name());
                }
            });

            //route group properties and action handling
            routeGroup.disableProperty().bind(claimRouteHandlerProperty.isNull().or(gameState.currentPlayerCanClaimRoute(r).not()));
            routeGroup.setOnMouseClicked(event -> {
                List<SortedBag<Card>> possibleClaimCards = gameState.possibleClaimCards(r);
                ClaimRouteHandler claimRouteH = claimRouteHandlerProperty.get();

                if (possibleClaimCards.size() == 1) {
                    claimRouteH.onClaimRoute(r, possibleClaimCards.get(0));
                } else {
                    ChooseCardsHandler chooseCardsH = chosenCards -> claimRouteH.onClaimRoute(r, chosenCards);
                    cardChooser.chooseCards(possibleClaimCards, chooseCardsH);
                }
            });

            pane.getChildren().add(routeGroup);
        }

        return pane;
    }

    /**
     * a private methode to determine the style class name of the road
     *
     * @param route the road
     * @return the style class name of the road
     */
    private static String routeStyleClassName(Route route) {

        if (route.color() == null) {
            return "NEUTRAL";
        }

        return route.color().name();

    }
}
