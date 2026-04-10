package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerStateTest {
    @Test
    void initialFails() {
        assertThrows(IllegalArgumentException.class, () -> {
            PlayerState.initial(SortedBag.of(5, Card.BLUE));
        });
    }

    @Test
    void withAddedTicketWorks() {
        Ticket t1 = new Ticket(List.of(new Trip(new Station(0, "Lausanne"), new Station(1, "Bern"), 5)));
        Ticket t2 = new Ticket(List.of(new Trip(new Station(3, "zoug"), new Station(1, "bern"), 6)));
        SortedBag<Ticket> srt = SortedBag.of(t1);
        PlayerState p = new PlayerState(srt, SortedBag.of(5, Card.ORANGE), List.of(new Route("A", new Station(0, "Berne"), new Station(2, "Fribourg"), 4, Route.Level.OVERGROUND, Color.BLACK)));
        PlayerState np = p.withAddedTickets(SortedBag.of(t2));
        SortedBag<Ticket> result = SortedBag.of(1, t1, 1, t2);
        assertEquals(result, np.tickets());
    }

    @Test
    void CanClaimRouteWorks() {
        Ticket t1 = new Ticket(List.of(new Trip(new Station(0, "Lausanne"), new Station(1, "Bern"), 5)));
        SortedBag<Ticket> srt = SortedBag.of(t1);
        PlayerState p = new PlayerState(srt, SortedBag.of(5, Card.ORANGE), List.of(new Route("A", new Station(0, "Berne"), new Station(2, "Fribourg"), 4, Route.Level.OVERGROUND, Color.ORANGE)));
        assertTrue(p.canClaimRoute(new Route("A", new Station(0, "Berne"), new Station(2, "Fribourg"), 4, Route.Level.OVERGROUND, Color.ORANGE)));
    }

    @Test
    void ticketPointsWorks() {
        Station berne = new Station(0, "Berne");
        Station fribourg = new Station(2, "Fribourg");
        Station interlaken = new Station(3, "interlaken");
        Station lausanne = new Station(5, "Lausanne");
        Station neuchatel = new Station(7, "Neuchatel");
        Ticket t1 = new Ticket(List.of(new Trip(lausanne, interlaken, 7)));
        Ticket t2 = new Ticket(List.of(new Trip(neuchatel, berne, 6)));
        SortedBag<Ticket> srt = SortedBag.of(1, t1, 1, t2);
        Route r1 = new Route("A", lausanne, fribourg, 3, Route.Level.OVERGROUND, Color.BLUE);
        Route r2 = new Route("B", fribourg, berne, 1, Route.Level.OVERGROUND, Color.ORANGE);
        Route r3 = new Route("C", berne, interlaken, 3, Route.Level.OVERGROUND, Color.RED);
        PlayerState p = new PlayerState(srt, SortedBag.of(5, Card.ORANGE), List.of(r1, r2, r3));
        assertEquals(1, p.ticketPoints());
    }
    @Test
    void totalPointsWorks() {
        Station berne = new Station(0, "Berne");
        Station fribourg = new Station(2, "Fribourg");
        Station interlaken = new Station(3, "interlaken");
        Station lausanne = new Station(5, "Lausanne");
        Station neuchatel = new Station(7, "Neuchatel");
        Ticket t1 = new Ticket(List.of(new Trip(lausanne, interlaken, 7)));
        Ticket t2 = new Ticket(List.of(new Trip(neuchatel, berne, 6)));
        SortedBag<Ticket> srt = SortedBag.of(1, t1, 1, t2);
        Route r1 = new Route("A", lausanne, fribourg, 3, Route.Level.OVERGROUND, Color.BLUE);
        Route r2 = new Route("B", fribourg, berne, 1, Route.Level.OVERGROUND, Color.ORANGE);
        Route r3 = new Route("C", berne, interlaken, 3, Route.Level.OVERGROUND, Color.RED);
        PlayerState p = new PlayerState(srt, SortedBag.of(5, Card.ORANGE), List.of(r1, r2, r3));
        assertEquals(10, p.finalPoints());
    }
}
