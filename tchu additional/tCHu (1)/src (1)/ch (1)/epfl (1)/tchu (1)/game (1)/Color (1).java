package ch.epfl.tchu.game;

import java.util.List;
/**
 * Color
 * @author OMAR MEZGHANI (311542)
 * @author MOHAMED CHARFI (311171)
 */
public enum Color {
    BLACK, VIOLET, BLUE, GREEN, YELLOW, ORANGE, RED, WHITE;

    /**
     * a list of all colors
     */
    public static final List<Color> ALL = List.of(Color.values());

    /**
     * the number of all colors
     */
    public static final int COUNT = ALL.size();

}
