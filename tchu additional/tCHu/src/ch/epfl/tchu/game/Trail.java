package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.List;

/**
 * Trail
 *
 * @author OMAR MEZGHANI (311542)
 * @author MOHAMED CHARFI (311171)
 */

public final class Trail {

    private final Station station1;
    private final Station station2;
    private final List<Route> trailroutes;
    private final int length ;
    private final static Trail EMPTY_TRAIL = new Trail(null, null, List.of());

    /**
     * a private constructor of a trail
     *
     * @param station1    the first station
     * @param station2    the second station
     * @param Trailroutes a list of  roads
     */
    private Trail(Station station1, Station station2, List<Route> Trailroutes) {
        this.station1 = station1;
        this.station2 = station2;
        this.trailroutes = Trailroutes;
        if (trailroutes.isEmpty()) {
            length= 0;
        }
        else {
            int i = 0;
            for (Route r : trailroutes) {
                i += r.length();
            }
            length = i;
        }
    }

    /**
     * returns the longest trail
     *
     * @param routes a list of all the roads in possession of the player
     * @return the longest trail
     */
    public static Trail longest(List<Route> routes) {

        if (routes.isEmpty()) {
            return EMPTY_TRAIL;
        }

        Trail longestTrail ;
        List<Trail> trails = new ArrayList<>();

        //creation of simple trails
        for (Route r : routes) {
            trails.add(new Trail(r.station1(), r.station2(), List.of(r)));
            trails.add(new Trail(r.station2(), r.station1(), List.of(r)));
        }

        longestTrail=trails.get(0);

        while (!trails.isEmpty()) {

            List<Trail> extendedTrails = new ArrayList<>();

            //creation of extended trails
            for (Trail trail : trails) {

                //calculation of the longest trail
                if (trail.length() > longestTrail.length()) {
                    longestTrail = trail;
                }

                List<Route> newRoutes = new ArrayList<>();

                for (Route r : routes) {
                    if (!(trail.trailroutes.contains(r)) && (r.station1().id() == trail.station2.id() || r.station2().id() == trail.station2.id())) {
                        newRoutes.add(r);
                    }
                }

                for (Route r : newRoutes) {
                    List<Route> newTrailRoutes = new ArrayList<>(trail.trailroutes);
                    newTrailRoutes.add(r);
                    Station s2;

                    if (trail.station2.id() == r.station1().id()) {
                        s2 = r.station2();
                    } else {
                        s2 = r.station1();
                    }

                    extendedTrails.add(new Trail(trail.station1(), s2, newTrailRoutes));
                }
            }
            trails = extendedTrails;
        }
        return longestTrail;
    }

    /**
     * returns the length of the trail
     *
     * @return the length of the trail
     */
    public int length() {
        return length;
    }

    /**
     * returns the first station
     *
     * @return the first station
     */
    public Station station1() {
        if (length() == 0) {
            return null;
        }
        return station1;
    }

    /**
     * returns the second station
     *
     * @return the second station
     */
    public Station station2() {
        if (length() == 0) {
            return null;
        }
        return station2;
    }

    /**
     * overrides the toString method
     *
     * @return a text
     */
    @Override
    public String toString() {
        if (length() == 0) {
            return "The trail is empty.";
        }
        StringBuilder result;
        result = new StringBuilder(station1.name() + " - ");
        for (Route r : trailroutes) {
            result.append(r.id());
        }
        result.append(" - ").append(station2.name()).append(" (").append(length()).append(")");
        return result.toString();
    }

    public List<Route> getTrailroutes() {
        return trailroutes;
    }
}
