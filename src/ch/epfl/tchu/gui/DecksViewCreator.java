package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Ticket;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

/**
 * @author Ahmed Aziz BEN HAJ (sciper: 310934) HMIDA et Aziz Laadher(sciper: 315196) / le 4 mai 2021
 * permet de construire un graphe de scène représentant des cartes,
 */
class DecksViewCreator {
    /**
     * retourne la vue de la main
     * @param ogs l'état du jeu observable
     * @return la vue de la main
     */
    public static Pane createHandView(ObservableGameState ogs) {

        //Le graphe de scène de la vue de la main
        HBox hbox = new HBox();
        hbox.getStylesheets().addAll("decks.css", "colors.css");
        ListView<Ticket> lv = new ListView<>(ogs.getTickets());
        lv.setId("tickets");
        hbox.getChildren().add(lv);
        HBox hbox1 = new HBox();
        hbox1.setId("hand-pane");
        hbox.getChildren().add(hbox1);
        for (int i = 0; i < ogs.getCardsCount().size(); i++) {
            StackPane sp = new StackPane();
            sp.getStyleClass().add(Card.ALL.get(i)==Card.LOCOMOTIVE?"NEUTRAL":Card.ALL.get(i).name());
            sp.getStyleClass().add("card");
            sp.visibleProperty().bind(Bindings.greaterThan(ogs.getCardsCount().get(i),0));
            Rectangle rect1 = new Rectangle(60, 90);
            Rectangle rect2 = new Rectangle(40, 70);
            Rectangle rect3 = new Rectangle(40, 70);
            rect1.getStyleClass().add("outside");
            rect2.getStyleClass().addAll("filled", "inside");
            rect3.getStyleClass().add("train-image");
            sp.getChildren().addAll(rect1, rect2, rect3);
            Text txt = new Text();
            txt.visibleProperty().bind(Bindings.greaterThan(ogs.getCardsCount().get(i),1));
            txt.textProperty().bind(Bindings.convert(ogs.getCardsCount().get(i)));
            txt.getStyleClass().add("count");
            sp.getChildren().add(txt);
            hbox1.getChildren().add(sp);
        }
        return hbox;
    }

    /**
     * construit un graphe de scène représentant des cartes
     * @param ogs l'état de jeu observable
     * @param handler1 propriété contenant un gestionnaire d'action
     *    gérant le tirage de billets
     * @param handler2 propriété contenant un gestionnaire d'action
     *    gérant le tirage de cartes
     * @return construit un graphe de scène représentant des cartes
     */
    public static Pane createCardsView(ObservableGameState ogs, ObjectProperty<ActionHandlers.DrawTicketsHandler> handler1,
                                       ObjectProperty<ActionHandlers.DrawCardHandler> handler2) {

        //Le graphe de scène de la vue des pioches
        VBox vb = new VBox();
        vb.getStylesheets().addAll("decks.css", "colors.css");
        vb.setId("card-pane");
        Button but1=new Button(StringsFr.TICKETS);
        but1.getStyleClass().add("gauged");
        Group grp0 = new Group();
        but1.setGraphic(grp0);
        Rectangle bgrd = new Rectangle(50, 5);
        Rectangle fgrd = new Rectangle(50, 5);
        bgrd.getStyleClass().add("background");
        fgrd.getStyleClass().add("foreground");
        grp0.getChildren().addAll(bgrd, fgrd);
        ReadOnlyIntegerProperty pctProperty =ogs.getRemainingTickets();
        fgrd.widthProperty().bind(pctProperty.multiply(50).divide(100));
        but1.disableProperty().bind(handler1.isNull());
        but1.setOnMouseClicked(event ->
            handler1.get().onDrawTickets()
        );
        vb.getChildren().add(but1);
        for (int i = 0; i < ogs.getFaceUpCards().size(); i++) {
            StackPane sp1 = new StackPane();
            sp1.disableProperty().bind(handler2.isNull());
            sp1.getStyleClass().add("card");
            final int index = i;
            ogs.faceUpCard(index).addListener((observable, oldValue, newValue) -> {
                sp1.getStyleClass().clear();
                sp1.getStyleClass().add("card");
                if (ogs.faceUpCard(index).get() == Card.LOCOMOTIVE) {
                    sp1.getStyleClass().add("NEUTRAL");
                } else {
                    sp1.getStyleClass().add(ogs.faceUpCard(index).get().color().name());
                }
            });
            sp1.setOnMouseClicked(event -> {
                handler2.get().onDrawCard(index);
                handler2.set(null);
            });
            vb.getChildren().add(sp1);
            Rectangle rect4 = new Rectangle(60, 90);
            Rectangle rect5 = new Rectangle(40, 70);
            Rectangle rect6 = new Rectangle(40, 70);
            rect4.getStyleClass().add("outside");
            rect5.getStyleClass().addAll("filled", "inside");
            rect6.getStyleClass().add("train-image");
            sp1.getChildren().addAll(rect4, rect5, rect6);
        }
        Button but2=new Button(StringsFr.CARDS);
        but2.getStyleClass().add("gauged");
        Group grp1 = new Group();
        but2.setGraphic(grp1);
        Rectangle bgrd2 = new Rectangle(50, 5);
        Rectangle fgrd2 = new Rectangle(50, 5);
        bgrd2.getStyleClass().add("background");
        fgrd2.getStyleClass().add("foreground");
        grp1.getChildren().addAll(bgrd2, fgrd2);
        ReadOnlyIntegerProperty pctProperty2 =ogs.getRemainingCards();
        fgrd2.widthProperty().bind(pctProperty2.multiply(50).divide(100));
        but2.disableProperty().bind(handler2.isNull());
        but2.setOnMouseClicked(event -> {
            handler2.get().onDrawCard(-1);
            handler2.set(null);
        });
        vb.getChildren().add(but2);
        return vb;
    }
}

