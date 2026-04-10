package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import ch.epfl.test.TestRandomizer;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class RouteTest {

    @Test
    void routeConstructorFailsForNullId() {
        assertThrows( NullPointerException.class, () -> {
            new Route(null, new Station(1, "Yverdon"), new Station(2, "Neuchatel"), 2, Route.Level.OVERGROUND, Color.BLUE);
        });
    }

    @Test
    void routeConstructorFailsForNullLevel() {
        assertThrows( NullPointerException.class, () -> {
            new Route("A", new Station(1, "Yverdon"), new Station(2, "Neuchatel"), 2, null, Color.BLUE);
        });
    }

    @Test
    void routeConstructorFailsForEqualStations() {
        Station s = new Station(1, "Yverdon");
        assertThrows( IllegalArgumentException.class, () -> {
            new Route("A", s, s, 2, Route.Level.OVERGROUND, Color.BLUE);
        });
    }

    @Test
    void routeConstructorFailsForIllegalLength() {

        assertThrows( IllegalArgumentException.class, () -> {
            new Route("A", new Station(1, "Yverdon"), new Station(2, "Neuchatel"), 7, Route.Level.OVERGROUND, Color.BLUE);
        });
    }

    private static final String alphabet = "abcdefghijklmnopqrstuvwxyz";
    private static String randomName(Random rng, int length) {
        var sb = new StringBuilder();
        for (int i = 0; i < length; i++)
            sb.append(alphabet.charAt(rng.nextInt(alphabet.length())));
        return sb.toString();
    }

    @Test
    void idAccessorWorks() {
        var rng = TestRandomizer.newRandom();
        for (int i = 0; i < TestRandomizer.RANDOM_ITERATIONS; i++) {
            var id = randomName(rng, 1 + rng.nextInt(10));
            var route = new Route(id, new Station(1, "Yverdon"), new Station(2, "Neuchatel"), 3, Route.Level.OVERGROUND, Color.BLUE);
            assertEquals(id, route.id());
        }
    }

    @Test
    void colorAccessorWorks() {
        Route r = new Route("A", new Station(1, "Yverdon"), new Station(2, "Neuchatel"), 4, Route.Level.UNDERGROUND, null) ;
        assertEquals(null, r.color());
    }

    @Test
    void additionalClaimCardsCountWorks() {
        Route r= new Route("A", new Station(1, "Yverdon"), new Station(2, "Neuchatel"), 5, Route.Level.UNDERGROUND, Color.BLUE);
        SortedBag claimCards =  SortedBag.of(5,Card.LOCOMOTIVE) ;
        SortedBag drawnCards = SortedBag.of(1,Card.BLUE,2,Card.LOCOMOTIVE) ;
        int expectedValue= 2 ;
        assertEquals(expectedValue,r.additionalClaimCardsCount(claimCards,drawnCards));
    }

    @Test
    void additionalPossibleClaimCardsWorks() {
        Route r= new Route("A", new Station(1, "Yverdon"), new Station(2, "Neuchatel"), 2, Route.Level.UNDERGROUND, Color.BLUE);
        List<SortedBag<Card>> result = r.possibleClaimCards();
        List<SortedBag<Card>> expected = new ArrayList<>();
        expected.add(SortedBag.of(2,Card.BLUE));
        expected.add(SortedBag.of(1,Card.BLUE,1,Card.LOCOMOTIVE));
        expected.add(SortedBag.of(2,Card.LOCOMOTIVE));

        for (int i = 0; i < expected.size() ; i++) {
            assertTrue(expected.get(i).equals(result.get(i)));
        }


    }








}
