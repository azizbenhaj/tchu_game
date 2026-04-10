package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PublicPlayerStateTest {
    @Test
    void constructorFails() {
        assertThrows(IllegalArgumentException.class, () -> {
            new PublicPlayerState(2, -2, List.of());
        });
    }

    @Test
    void carCountWorks(){
        List<Route>l =new ArrayList<>();
        l.add(new Route("A", new Station(0, "Berne"),new Station(2, "Fribourg"),4, Route.Level.OVERGROUND,Color.BLACK));
        l.add(new Route("B", new Station(0, "Berne"),new Station(3, "interlaken"),5, Route.Level.OVERGROUND,Color.BLACK));
        PublicPlayerState pps = new PublicPlayerState(3,20,l);
        assertEquals(31,pps.carCount());
    }

    @Test
    void claimPointsWorks(){
        List<Route>l =new ArrayList<>();
        l.add(new Route("A", new Station(0, "Berne"),new Station(2, "Fribourg"),4, Route.Level.OVERGROUND,Color.BLACK));
        l.add(new Route("B", new Station(0, "Berne"),new Station(3, "interlaken"),5, Route.Level.OVERGROUND,Color.BLACK));
        PublicPlayerState pps = new PublicPlayerState(3,20,l);
        assertEquals(17,pps.claimPoints());

    }
}
