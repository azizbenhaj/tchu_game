package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Route
 *
 * @author OMAR MEZGHANI (311542)
 * @author MOHAMED CHARFI (311171)
 */
public final class Route {
    /**
     * Level
     * define the level of the route
     */
    public enum Level {
        OVERGROUND,
        UNDERGROUND
    }

    private final String id;
    private final Station station1;
    private final Station station2;
    private final int length;
    private final Level level;
    private final Color color;

    /**
     * constructs a Route
     *
     * @param id       the id of the route
     * @param station1 the first station
     * @param station2 the second station
     * @param length   the length of the route
     * @param level    the level of the route
     * @param color    the color of the route
     */
    public Route(String id, Station station1, Station station2, int length, Level level, Color color) {
        Preconditions.checkArgument(!(station1.equals(station2))
                && (length <= Constants.MAX_ROUTE_LENGTH)
                && (length >= Constants.MIN_ROUTE_LENGTH));

        this.id = Objects.requireNonNull(id);
        this.station1 = Objects.requireNonNull(station1);
        this.station2 = Objects.requireNonNull(station2);
        this.length = length;
        this.level = Objects.requireNonNull(level);
        this.color = color;
    }

    /**
     * returns the id of the route
     *
     * @return the id of the route
     */
    public String id() {
        return id;
    }

    /**
     * returns the first station of the route
     *
     * @return the first station of the route
     */
    public Station station1() {
        return station1;
    }

    /**
     * returns the second station of the route
     *
     * @return the second station of the route
     */
    public Station station2() {
        return station2;
    }

    /**
     * returns the length of the route
     *
     * @return the length of the route
     */
    public int length() {
        return length;
    }

    /**
     * returns the level of the route
     *
     * @return the level of the route
     */
    public Level level() {
        return level;
    }

    /**
     * returns the color route
     *
     * @return the color of the route
     */
    public Color color() {
        return color;
    }

    /**
     * returns a list of station 1 and station 2
     *
     * @return a list of station 1 and station 2
     */
    public List<Station> stations() {
        return List.of(station1, station2);
    }

    /**
     * returns the opposite station
     *
     * @return the opposite station
     * @throws IllegalArgumentException if the given station does not correspond to any of the 2 stations of the road
     */
    public Station stationOpposite(Station station) {
        Preconditions.checkArgument(station == station1 || station == station2);
        if (station == station1) {
            return station2;
        }
        return station1;
    }

    /**
     * returns a list of all possible cards to obtain the road
     *
     * @return a list of all possible cards to obtain the road
     */
    public List<SortedBag<Card>> possibleClaimCards() {
        List<SortedBag<Card>> result = new ArrayList<>();
        if (level == Level.OVERGROUND) {
            if (color == null) {
                for (Color color : Color.values()) {
                    result.add(SortedBag.of(length, Card.of(color)));
                }
            } else {
                result.add(SortedBag.of(length, Card.of(color)));
            }
        } else {
            if (color == null) {
                for (int i = length; i > 0; i--) {
                    for (Color color : Color.values()) {
                        result.add(SortedBag.of(i, Card.of(color), length - i, Card.LOCOMOTIVE));
                    }
                }
                result.add(SortedBag.of(length, Card.LOCOMOTIVE));
            } else {
                for (int i = length; i >= 0; i--) {
                    result.add(SortedBag.of(i, Card.of(color), length - i, Card.LOCOMOTIVE));
                }
            }
        }
        return result;
    }

    /**
     * returns the number of additional card needed to obtain an underground road
     *
     * @param claimCards the cards you chose to play
     * @param drawnCards the cards drawn from the deck
     * @return the number of additional card needed to obtain an underground road
     */
    public int additionalClaimCardsCount(SortedBag<Card> claimCards, SortedBag<Card> drawnCards) {
        Preconditions.checkArgument((level == Level.UNDERGROUND) && (drawnCards.size() == Constants.ADDITIONAL_TUNNEL_CARDS));
        int result = 0;
        for (Card c : drawnCards) {
            if (claimCards.countOf(c) > 0 || c == Card.LOCOMOTIVE) {
                result++;
            }
        }
        return result;
    }

    /**
     * returns the points depending on the length of the road
     *
     * @return the points depending on the length of the road
     */
    public int claimPoints() {
        return Constants.ROUTE_CLAIM_POINTS.get(length);
    }

}

