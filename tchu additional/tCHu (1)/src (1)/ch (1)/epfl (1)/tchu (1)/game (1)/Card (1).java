package ch.epfl.tchu.game;

import java.util.List;
/**
 * Card
 * @author OMAR MEZGHANI (311542)
 * @author MOHAMED CHARFI (311171)
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
    LOCOMOTIVE(null);

    private final Color color;
    /**
     * Construct a card
     *
     * @param color
     *           the color of the card
     */
    Card(Color color) {
        this.color = color;
    }

    /**
     * a list of all cards
     */
    public static final List<Card> ALL = List.of(Card.values());

    /**
     * the number of all cards
     */
    public static final int COUNT = ALL.size();

    /**
     *a list of cars cards
     */
    public static final List<Card> CARS = ALL.subList(0, Color.COUNT);

    /**
     * returns a card given a color
     *
     * @param color
     *           the color of the card
     * @return a card given a color
     */
    public static Card of(Color color) {
        for (Card c : ALL) {
            if (c.color == color) {
                return c;
            }
        }
        return null;
    }
    /**
     * returns the color of the card
     * @return the color of the card
     */
    public Color color() {
        return color;
    }


}
