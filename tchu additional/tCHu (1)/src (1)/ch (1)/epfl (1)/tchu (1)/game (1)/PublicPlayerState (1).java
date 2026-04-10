package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;

/**
 * PublicPlayerState
 *
 * @author OMAR MEZGHANI (311542)
 * @author MOHAMED CHARFI (311171)
 */
public class PublicPlayerState {
    private final int ticketCount;
    private final int cardCount;
    private final List<Route> routes;
    private final int carCount;
    private final int claimPoints;

    /**
     * public constructor
     *
     * @param ticketCount ticket Count
     * @param cardCount   card Count
     * @param routes      the claimed routes
     * @throws IllegalArgumentException
     */
    public PublicPlayerState(int ticketCount, int cardCount, List<Route> routes) {
        Preconditions.checkArgument(cardCount >= 0 && ticketCount >= 0);
        this.ticketCount = ticketCount;
        this.cardCount = cardCount;
        this.routes = List.copyOf(routes);
        int totalLength = 0;
        int points = 0;
        for (Route r : routes) {
            totalLength += r.length();
            points += r.claimPoints();
        }
        carCount = Constants.INITIAL_CAR_COUNT - totalLength;
        claimPoints = points;
    }

    /**
     * returns the ticket count
     *
     * @return the ticket count
     */
    public int ticketCount() {
        return ticketCount;
    }

    /**
     * returns the cardCount
     *
     * @return the cardCount
     */
    public int cardCount() {
        return cardCount;
    }

    /**
     * returns a list of the claimed  routes
     *
     * @return a list of the claimed routes
     */
    public List<Route> routes() {
        return routes;
    }

    /**
     * return the car Count
     *
     * @return the car Count
     */
    public int carCount() {
        return carCount;
    }

    /**
     * return the claim Points
     *
     * @return the claim Points
     */
    public int claimPoints() {
        return claimPoints;
    }
}
