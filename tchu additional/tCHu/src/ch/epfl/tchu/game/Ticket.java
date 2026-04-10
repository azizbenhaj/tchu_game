package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 * Ticket
 *
 * @author OMAR MEZGHANI (311542)
 * @author MOHAMED CHARFI (311171)
 */

public final class Ticket implements Comparable<Ticket> {
    private final List<Trip> trips;
    private final String text;
    private boolean done;

    /**
     * constructs a ticket
     *
     * @param trips a list of all trips
     */
    public Ticket(List<Trip> trips) {
        Preconditions.checkArgument((!trips.isEmpty()) && ticketCheck(trips));
        this.trips = List.copyOf(trips);
        text = computeText();
        done = false;
    }

    /**
     * constructs a ticket
     *
     * @param from   the from station
     * @param to     the to station
     * @param points the points
     */
    public Ticket(Station from, Station to, int points) {
        this(List.of(new Trip(from, to, points)));
    }

    /**
     * check that the ticket is valid
     *
     * @param trips a list of all trips
     * @return a boolean result depending on the validity of the ticket
     */
    private static boolean ticketCheck(List<Trip> trips) {
        for (Trip t : trips) {
            if (!((trips.get(0).from().name()).equals(t.from().name()))) {
                return false;
            }
        }
        return true;
    }

    /**
     * compares a ticket to another one
     *
     * @param that the ticket
     * @return an integer depending on the comparison
     */
    @Override
    public int compareTo(Ticket that) {

        return text().compareTo(that.text());
    }

    /**
     * compute the text of the ticket
     *
     * @return the text of the ticket
     */
    private String computeText() {
        String result = trips.get(0).from().name() + " - %s";
        String arrival;
        TreeSet<String> s = new TreeSet<>();

        if (trips.size() == 1) {
            arrival = trips.get(0).to().name() + " (" + trips.get(0).points() + ")";
        } else {
            for (Trip t : trips) {
                s.add(t.to().name() + " (" + t.points() + ")");
            }
            arrival = "{" + String.join(", ", s) + "}";
        }
        return String.format(result, arrival);
    }

    /**
     * return the text of the ticket
     *
     * @return the text of the ticket
     */
    public String text() {
        return text;
    }

    /**
     * return the text of the ticket
     *
     * @return the text of the ticket
     */
    @Override
    public String toString() {
        return text();
    }

    /**
     * returns the max of an integer list
     *
     * @param l the list of integers
     * @return the max of an integer list
     */
    private int max(List<Integer> l) {
        int max = l.get(0);
        for (int m : l) {
            if (m > max) {
                max = m;
            }
        }
        return max;
    }

    /**
     * returns the points of the ticket
     *
     * @param connectivity the connectivity of the stations
     * @return an integer depending on the connectivity
     */
    public int points(StationConnectivity connectivity) {
        List<Integer> r = new ArrayList<>();
        for (Trip t : trips) {
            r.add(t.points(connectivity));
        }
        return max(r);
    }

    /**
     * sets the value of done depending of the connectivity of the stations
     * @param connectivity the connectivity of the stations
     */
    public void setDone(StationConnectivity connectivity) {

        for (Trip t : trips) {
            if (connectivity.connected(t.from(), t.to())) {
                done = true;
            }
        }
    }

    /**
     * sets the value of done
     * @param done the new value
     */
    public void setDone(boolean done) {
        this.done = done;
    }

    /**
     * returns the value of done
     * @return the value of done
     */
    public boolean isDone() {
        return done;
    }
}
