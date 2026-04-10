package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Trip
 *
 * @author OMAR MEZGHANI (311542)
 * @author MOHAMED CHARFI (311171)
 */

public final class Trip {
    private final Station from;
    private final Station to;
    private final int points;

    /**
     * constructs a trip with two stations and the corresponding points
     *
     * @param from the station of the beginning of the trip
     * @param to   the station of the ending of the trip
     * @throws IllegalArgumentException if the points are negative
     */

    public Trip(Station from, Station to, int points) {
        Preconditions.checkArgument(points > 0);
        this.from = Objects.requireNonNull(from);
        this.to = Objects.requireNonNull(to);
        this.points = points;
    }

    /**
     * give all the trips from a station to a station
     *
     * @param from   a list of all  from stations
     * @param to     a list of all  to stations
     * @param points a list of all   points
     * @return a list of all trips
     * @throws IllegalArgumentException if the points are negative
     *                                  if the lists are empty
     */

    public static List<Trip> all(List<Station> from, List<Station> to, int points) {
        Preconditions.checkArgument((!from.isEmpty()) && (!to.isEmpty()) && (points > 0));
        List<Trip> result = new ArrayList<>();
        for (Station de : from) {
            for (Station a : to) {
                result.add(new Trip(de, a, points));
            }
        }
        return result;
    }

    /**
     * returns the from station
     *
     * @return the from station
     */

    public Station from() {
        return from;
    }

    /**
     * returns the to station
     *
     * @return the to station
     */

    public Station to() {
        return to;
    }

    /**
     * returns the points
     *
     * @return the points
     */

    public int points() {
        return points;
    }

    /**
     * a method used to compute the points in function of the connectivity
     *
     * @param connectivity connectivity of two stations
     * @return the points
     */

    public int points(StationConnectivity connectivity) {
        if (connectivity.connected(from, to)) {
            return points();
        } else {
            return -points();
        }
    }

}
