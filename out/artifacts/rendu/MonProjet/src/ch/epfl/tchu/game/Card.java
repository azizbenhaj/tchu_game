package ch.epfl.tchu.game;

import java.util.*;

/**
 * @author Ahmed Aziz BEN HAJ (sciper: 310934) HMIDA et Aziz Laadher(sciper: 315196) / le 1 mars 2021
 *représente ce que nous avons appelé des cartes
 */
public enum Card {
    BLACK(Color.BLACK),
    VIOLET(Color.VIOLET),
    BLUE(Color.BLUE),
    GREEN(Color.GREEN),
    YELLOW(Color.YELLOW),
    ORANGE(Color.ORANGE),
    RED(Color.RED),
    WHITE(Color.WHITE),
    LOCOMOTIVE(null),
    JOKER(Color.JOKER);
    private final Color co;

    Card(Color co) {
        this.co = co;
    }

    static final Card[] co1 = new Card[]{BLACK,
            VIOLET,
            BLUE,
            GREEN,
            YELLOW,
            ORANGE,
            RED,
            WHITE,
            LOCOMOTIVE,
           JOKER};
    /**
     * liste de cartes
     */
    public static final List<Card> ALL = List.of(co1);
    /**
     * taille de la liste ALL
     */
    public static final int COUNT = ALL.size();

    static final Card[] co2 = new Card[]{BLACK,
            VIOLET,
            BLUE,
            GREEN,
            YELLOW,
            ORANGE,
            RED,
            WHITE,JOKER};
    /**
     * liste de cartes
     */
    public static final List<Card> CARS = List.of(co2);

    /**
     * @param color la couleur donnée
     * @return la couleur donnée
     */
    public static Card of(Color color) {
        if(color==null) return LOCOMOTIVE;
        switch (color) {
            case VIOLET:
                return VIOLET;
            case BLACK:
                return BLACK;
            case GREEN:
                return GREEN;
            case YELLOW:
                return YELLOW;
            case WHITE:
                return WHITE;
            case RED:
                return RED;
            case ORANGE:
                return ORANGE;
            case BLUE:
                return BLUE;
            case JOKER:
                return JOKER;
        }

    return null;
    }

    /**
     * retourne la couleur correspondante
     * @return la couleur correspondante
     */
    public Color color() {
        switch (co) {
            case VIOLET:
                return Color.VIOLET;
            case BLACK:
                return Color.BLACK;
            case GREEN:
                return Color.GREEN;
            case YELLOW:
                return Color.YELLOW;
            case WHITE:
                return Color.WHITE;
            case RED:
                return Color.RED;
            case ORANGE:
                return Color.ORANGE;
            case BLUE:
                return Color.BLUE;
            case JOKER:
                return Color.JOKER;
            default:
                return null;
        }
    }
}
