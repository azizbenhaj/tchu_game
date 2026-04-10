package ch.epfl.tchu.game;

/**
 * TicketDecorator
 *
 * @author OMAR MEZGHANI (311542)
 * @author MOHAMED CHARFI (311171)
 */
public final class TicketDecorator {
    private final Ticket ticket;

    /**
     * public constructor
     * @param ticket the ticket
     */
    public TicketDecorator(Ticket ticket) {
        this.ticket = ticket;
    }

    /**
     * overrides the methode to string adding a string stating that the ticket is done or not yet
     * @return the string representation of the string withe the added string stating that the ticket is done or not yet
     */
    @Override
    public String toString() {
        return ticket.isDone() ? ticket.toString() + " - FINI !" : ticket.toString() + " - PAS FINI !";
    }
}

