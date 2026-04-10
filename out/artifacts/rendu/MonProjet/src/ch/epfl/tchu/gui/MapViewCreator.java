package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.animation.FadeTransition;
import javafx.animation.Timeline;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.*;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.List;

/**
 * @author Ahmed Aziz BEN HAJ (sciper: 310934) HMIDA et Aziz Laadher(sciper: 315196) / le 4 mai 2021
 * crée la vue de la carte
 */
class MapViewCreator {
    /**
     * interface fonctionnelle imbriquée dans la classe MapViewCreator
     */
    @FunctionalInterface
    interface CardChooser {
        void chooseCards(List<SortedBag<Card>> options, ActionHandlers.ChooseCardsHandler handler);
    }

    /**
     * permettant de créer la vue de la carte
     * @param ogs  l'état du jeu observable,
     * @param claimHandler propriété contenant le gestionnaire d'action à utiliser
     *   lorsque le joueur désire s'emparer d'une route
     * @param cardChooser  sélectionneur de cartes
     * @return a vue de la carte
     */
    public static Pane createMapView(ObservableGameState ogs, ObjectProperty<ActionHandlers.ClaimRouteHandler> claimHandler,
                                     CardChooser cardChooser) {

        Pane pane = new Pane();
        pane.getStylesheets().addAll("map.css", "colors.css");
        Node imageView = new ImageView();
        pane.getChildren().add(imageView);

        for (Route route : ChMap.routes()) {
            Group grp = new Group();
            grp.setId(route.id());
            grp.getStyleClass().add("route");
            grp.getStyleClass().add(route.level().toString());
            ogs.getRoutesOwning().get(route).addListener((observable, oldValue, newValue) ->
                grp.getStyleClass().add(newValue.name())
            );
            grp.setOnMouseClicked(event ->
                cardChooser.chooseCards(ogs.possibleClaimCards(route).get(), (cards -> {
                    claimHandler.get().onClaimRoute(route, cards);
                    claimHandler.set(null);
                }))
            );
            if (route.color() == null) {
                grp.getStyleClass().add("NEUTRAL");
            } else {
                grp.getStyleClass().add(route.color().name());
            }
            for (int i = 1; i < (route.length() + 1); i++) {
                Group grp1 = new Group();
                grp1.setId(route.id() + "_" + i);
                grp.getChildren().add(grp1);
                Rectangle rect = new Rectangle(36, 12);
                rect.getStyleClass().addAll("track", "filled");
                grp1.getChildren().add(rect);
                dumpTree(grp);
                Group grp2 = new Group();
                grp2.getStyleClass().add("car");
                ogs.getRoutesOwning().get(route).addListener((observable, oldValue, newValue) ->
                        { grp2.getStyleClass().add(newValue.name());
                            //transition responsable de l'animation qui dure 0.5 seconde
                            FadeTransition ft = new FadeTransition(Duration.millis(500), grp2);
                            //visibilité 100%
                            ft.setFromValue(1.0);
                            //puis visibilité 10%
                            ft.setToValue(0.1);
                            //puis on recommence à l'infini
                            ft.setCycleCount(Timeline.INDEFINITE);
                            //la visibilité entre 10% et 100% change d'une facon fluide
                            ft.setAutoReverse(true);
                            // la transition commence
                            ft.play();
                            new Thread(() -> {
                                try {
                                    //thread qui joue l'animation pendant 2 secondes avant de s'arrèter
                                    Thread.sleep(2000);
                                    ft.stop();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }).start();
                        });
                Rectangle rect1 = new Rectangle(36, 12);
                rect1.getStyleClass().add("filled");
                grp2.getChildren().add(rect1);
                Circle circ1 = new Circle(12, 6, 3);
                Circle circ2 = new Circle(24, 6, 3);
                grp2.getChildren().add(circ1);
                grp2.getChildren().add(circ2);
                grp1.getChildren().add(grp2);
            }
            grp.disableProperty().bind(ogs.claimable(route).not().or(claimHandler.isNull()));
            pane.getChildren().add(grp);
        }
        return pane;
    }

    /**
     * simple appel à la méthode de vérification de la scene de graphe
     */
    public static void dumpTree(Node root) {
        dumpTree(0, root);
    }
    /**
     * methode de verification de la scene de graphe
     * @param root n'importe quel composant de la scene de graphe
     * @param indent toujours zero
     */
    public static void dumpTree(int indent, Node root) {
        System.out.printf("%s%s (id: %s, classes: [%s])%n",
                " ".repeat(indent),
                root.getTypeSelector(),
                root.getId(),
                String.join(", ", root.getStyleClass()));
        if (root instanceof Parent) {
            Parent parent = ((Parent) root);
            for (Node child : parent.getChildrenUnmodifiable())
                dumpTree(indent + 2, child);
        }
    }


}