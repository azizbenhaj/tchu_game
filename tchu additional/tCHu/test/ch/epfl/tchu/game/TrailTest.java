package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TrailTest {

    @Test
    void trailLongestReturnsLongest() {
        List<Route> liste = new ArrayList<>();
        liste.add(new Route("A", new Station(1, "Yverdon"), new Station(2, "Neuchatel"), 2, Route.Level.OVERGROUND, Color.BLUE));
        liste.add(new Route("E", new Station(3, "Berne"), new Station(6, "Lucerne"), 4, Route.Level.OVERGROUND, Color.BLUE));
        liste.add(new Route("C", new Station(2, "Neuchatel"), new Station(3, "Berne"), 2, Route.Level.OVERGROUND, Color.BLUE));
        liste.add(new Route("B", new Station(2, "Neuchatel"), new Station(4, "Soleure"), 4, Route.Level.OVERGROUND, Color.BLUE));
        liste.add(new Route("D", new Station(4, "Soleure"), new Station(3, "Berne"), 2, Route.Level.OVERGROUND, Color.BLUE));
        liste.add(new Route("F", new Station(3, "Berne"), new Station(5, "Fribourg"), 1, Route.Level.OVERGROUND, Color.BLUE));
        assertEquals(13, Trail.longest(liste).length());
    }

    @Test
    void trailLongestWorksWithEmptyList() {
        List<Route> liste = new ArrayList<>();
        assertEquals(0, Trail.longest(liste).length());
        assertEquals(null, Trail.longest(liste).station1());

    }


}
