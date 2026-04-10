package ch.epfl.tchu;

import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.gui.Info;
import javafx.util.StringConverter;

/**
 * CardBag String Converter
 *
 * @author OMAR MEZGHANI (311542)
 * @author MOHAMED CHARFI (311171)
 */
public class CardBagStringConverter extends StringConverter<SortedBag<Card>> {

    /**
     * @param object object
     * @return cards description
     */
    @Override
    public String toString(SortedBag<Card> object) {
        return Info.cardsDescription(object);
    }

    /**
     * @param string cards description
     * @return SortedBag<Card>
     */
    @Override
    public SortedBag<Card> fromString(String string) {
        throw new UnsupportedOperationException();
    }
}
