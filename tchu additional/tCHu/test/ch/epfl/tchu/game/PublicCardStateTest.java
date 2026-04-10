package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static ch.epfl.tchu.game.Color.BLACK;
import static org.junit.jupiter.api.Assertions.*;

public class PublicCardStateTest {
    @Test
    void PublicCardStateWorks() {

        assertThrows(IllegalArgumentException.class, () -> {
            new PublicCardState(List.of( Card.BLUE, Card.BLUE, Card.LOCOMOTIVE, Card.BLACK), 4, 3);
        });

    }

    @Test
    void faceUpCardsWork() {
        PublicCardState p = new PublicCardState(List.of(Card.BLUE, Card.BLUE, Card.BLUE, Card.LOCOMOTIVE, Card.BLACK), 6, 3);
        assertEquals(List.of(Card.BLUE, Card.BLUE, Card.BLUE, Card.LOCOMOTIVE, Card.BLACK), p.faceUpCards());
    }

    @Test
    void faceUpCardWorks() {
        PublicCardState p = new PublicCardState(List.of(Card.BLUE, Card.BLUE, Card.BLUE, Card.LOCOMOTIVE, Card.BLACK), 6, 3);
        assertEquals(Card.BLACK, p.faceUpCard(4));
    }

    @Test
    void isDeckEmpty() {
        PublicCardState p = new PublicCardState(List.of(Card.BLUE, Card.BLUE, Card.BLUE, Card.LOCOMOTIVE, Card.BLACK), 0, 3);
        assertTrue( p.isDeckEmpty());
    }
    @Test
    void faceUpCardsThrows() {
        PublicCardState p = new PublicCardState(List.of(Card.BLUE, Card.BLUE, Card.BLUE, Card.LOCOMOTIVE, Card.BLACK), 0, 3);
        assertThrows(IndexOutOfBoundsException.class,() -> {
            p.faceUpCard(7) ;
        }) ;
    }

}
