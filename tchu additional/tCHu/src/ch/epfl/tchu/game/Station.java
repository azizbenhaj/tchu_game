package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

/**
 * Station
 *
 * @author OMAR MEZGHANI (311542)
 * @author MOHAMED CHARFI (311171)
 */

public final class Station {
    private final int id;
    private final String name;

    /**
     * constructs a station
     *
     * @param id   the id of the station
     * @param name the name of the station
     */

    public Station(int id, String name) {
        Preconditions.checkArgument(id >= 0 && name != null);
        this.id = id;
        this.name = name;
    }

    /**
     * returns the id of the station
     *
     * @return the id of the station
     */

    public int id() {
        return id;
    }

    /**
     * returns the name of the station
     *
     * @return the name of the station
     */

    public String name() {
        return name;
    }

    /**
     * override to the toString method
     * returns the name of the station
     */

    @Override
    public String toString() {
        return name;
    }
}
