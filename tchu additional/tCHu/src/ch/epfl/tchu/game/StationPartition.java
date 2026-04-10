package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

/**
 * StationPartition
 *
 * @author OMAR MEZGHANI (311542)
 * @author MOHAMED CHARFI (311171)
 */
public final class StationPartition implements StationConnectivity {
    private final int[] bond;

    /**
     * private constructor
     *
     * @param bond bond
     */
    private StationPartition(int[] bond) {
        this.bond = bond.clone();
    }

    /**
     * overrides the method inherited from StationConnectivity
     *
     * @param s1 first station
     * @param s2 second station
     * @return
     */
    @Override
    public boolean connected(Station s1, Station s2) {
        if (s1.id() >= bond.length || s2.id() >= bond.length) {
            return s1.id() == s2.id();
        } else
            return (bond[s1.id()] == bond[s2.id()]);
    }

    /**
     * Inner Class Builder
     */
    public static final class Builder {
        private final int[] bondBuilder;

        /**
         * public constructor of the builder
         *
         * @param stationCount the number of stations
         * @throws IllegalArgumentException
         */
        public Builder(int stationCount) {
            Preconditions.checkArgument(stationCount >= 0);
            bondBuilder = new int[stationCount];
            for (int i = 0; i < stationCount; i++) {
                bondBuilder[i] = i;
            }
        }

        /**
         * connects the two stations
         *
         * @param s1 first station
         * @param s2 second station
         * @return the Builder
         */
        public Builder connect(Station s1, Station s2) {
            int repS1 = representative(s1.id());
            int repS2 = representative(s2.id());
            bondBuilder[repS1] = repS2;
            return this;
        }

        /**
         * returns the station Partition
         *
         * @return the station Partition
         */
        public StationPartition build() {
            for (int i = 0; i < bondBuilder.length; i++) {
                bondBuilder[i] = representative(i);
            }

            return new StationPartition(bondBuilder);
        }

        /**
         * returns the id of representative
         *
         * @param i the id of the station
         * @return the id of representative
         */
        private int representative(int i) {

            while (bondBuilder[i] != i) {
                i = bondBuilder[i];
            }
            return i;
        }


    }


}
