
package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

/**
 * @author Ahmed Aziz BEN HAJ (sciper: 310934) HMIDA et Aziz Laadher(sciper: 315196) / le 22 mars 2021
 * représente une partition (aplatie) de gares
 */
public final class StationPartition implements StationConnectivity {

    /**
     * implementation de connected de StationConnecctivity
     * @param s1 station une
     * @param s2 station 2
     * @return s'il existe un lien entre les 2 stations
     */
    @Override
    public boolean connected(Station s1, Station s2) {
        if (s1.id() > this.liens.length - 1 || s2.id() > this.liens.length - 1) {
            return s1.id() == s2.id();
        }
        return liens[s1.id()] == liens[s2.id()];
    }

    /**
     * redefinition de toString()
     */
    @Override
    public String toString() {
        String str = "";
        for (int i : liens) {
            str = str + i + ",";
        }
        return str;
    }

    private final int[] liens;

    private StationPartition(int[] liens) {
        this.liens = liens.clone();
    }

    /**
     * représente un bâtisseur de partition de gare.
     */
    public final static class Builder {
        int stationCount;
        int[] liens;

        /**
         * constructeur du builder
         * @param stationCount le nombre de station
         */
        public Builder(int stationCount) {
            Preconditions.checkArgument(stationCount>=0);
            this.stationCount = stationCount;
            liens = new int[stationCount];
            for (int i = 0; i < stationCount; i++) {
                liens[i] = i;
            }
        }

        /**
         * joint les sous-ensembles contenant les deux gares passées en argument
         * @param s1 station 1
         * @param s2 station 2
         * @return le bâtisseur (this),
         */
        public Builder connect(Station s1, Station s2) {
                int rep1 = this.representative(s1.id());
                int rep2 = this.representative(s2.id());
                if (rep1 != s1.id() && rep2 != s2.id()) {
                    liens[s2.id()] = rep1;
                    liens[rep2] = rep1;
                } else if (rep1 != s1.id())
                    liens[s2.id()] = rep1;
                else if (rep2 != s2.id())
                    liens[s1.id()] = rep2;
                else {
                    liens[s1.id()] = rep2;
                    liens[s2.id()] = rep2;
                }
                return this;
        }

        private int representative(int id) {
            if (id == liens[id])
                return id;
            return representative(liens[id]);
        }

        /**
         * retourne la partition aplatie des gares
         * @return la partition aplatie des gares
         */
        public StationPartition build() {
            for (int i = 0; i < liens.length; i++) {
                liens[i] = representative(i); }
            return new StationPartition(this.liens);
        }

    }
}


