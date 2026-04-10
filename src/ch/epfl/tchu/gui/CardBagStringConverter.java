package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import javafx.util.StringConverter;

/**
 * @author Ahmed Aziz BEN HAJ (sciper: 310934) HMIDA et Aziz Laadher(sciper: 315196) / le 11 mai 2021
 * classe qui hérite de StringConverter<SortedBag<Card>> et définit des versions concrètes de ses deux méthodes abstraites
 */
public class CardBagStringConverter extends StringConverter<SortedBag<Card>> {
    /**
     *  transforme le multiensemble de cartes en chaînes
     * @param object le multiensemble de cartes à transformer
     * @return le multiensemble de cartes transformé
     */
    @Override
    public String toString(SortedBag<Card> object) {
        String s = "";
        for (Card card : Card.ALL) {
            int k = object.countOf(card);
            if (k > 0) {
                s += k + " " + Info.cardName(card, k) + ", ";
            }
        }
        if(s.length()>0){
        s=s.substring(0,s.length()-2);
        }
            int l = s.lastIndexOf(",");
        if(l<0){return s;}
            String w = s.substring(0, l);
            w += s.substring(l).replace(",", " et");
            return w;

    }

    /**
     * lève simplement une exception de type UnsupportedOperationException car elle n'est jamais utilisée dans ce contexte
     * @param string non mentionnée
     * lève simplement une exception de type UnsupportedOperationException
     */
    @Override
    public SortedBag<Card> fromString(String string) {
        throw new UnsupportedOperationException();
    }


}
