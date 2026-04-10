package ch.epfl.tchu.game;
/**
 * StationConnectivity
 * @author OMAR MEZGHANI (311542)
 * @author MOHAMED CHARFI (311171)
 */

public interface StationConnectivity {
    /**
     * returns true if the stations are connected
     * @param s1 first station
     * @param s2 second station
     * @return true if the stations are connected
     */
    boolean connected(Station s1, Station s2);
}
