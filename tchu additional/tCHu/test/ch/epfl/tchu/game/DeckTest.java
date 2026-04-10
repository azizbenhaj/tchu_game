package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static ch.epfl.tchu.game.Deck.*;
import static org.junit.jupiter.api.Assertions.*;


public class DeckTest {
    @Test
    void sizeWorkswithEmptyDeck() {
        SortedBag.Builder<Card> b = new SortedBag.Builder<>();
        Deck<Card> d = of(b.build(), new Random(34));
        assertEquals(0, d.size());
    }

    @Test
    void isEmptyWorks() {
        SortedBag.Builder<Card> b = new SortedBag.Builder<>();
        Deck<Card> d = of(b.build(), new Random(34));
        assertTrue(d.isEmpty());
    }

    @Test
    void topCardFailsWithEmptyDeck() {
        SortedBag.Builder<Card> b = new SortedBag.Builder<>();
        Deck<Card> d = of(b.build(), new Random(34));
        assertThrows(IllegalArgumentException.class, () -> {
            d.topCard();
        });
    }

    @Test
    void withoutTopCardFailsWithEmptyDeck() {
        SortedBag.Builder<Card> b = new SortedBag.Builder<>();
        Deck<Card> d = of(b.build(), new Random(34));
        assertThrows(IllegalArgumentException.class, () -> {
            d.withoutTopCard();
        });
    }

    @Test
    void topCardsFailsWithInvalidCount() {
        SortedBag.Builder<Card> b = new SortedBag.Builder<>();
        b.add(2, Card.BLACK);
        b.add(3, Card.BLUE);
        Deck<Card> d = of(b.build(), new Random(34));
        assertThrows(IllegalArgumentException.class, () -> {
            d.topCards(6);
        });
    }

    @Test
    void withoutTopCardsFailsWithInvalidCount(){
        SortedBag.Builder<Card> b = new SortedBag.Builder<>();
        b.add(2, Card.BLACK);
        b.add(3, Card.BLUE);
        Deck<Card> d = of(b.build(), new Random(34));
        assertThrows(IllegalArgumentException.class, () -> {
            d.withoutTopCards(6);
        });
    }

}
