package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StationPartitionTest {


    @Test
    void conectedWorks() {
        StationPartition.Builder b = new StationPartition.Builder(15);
        Station berne = new Station(0, "Berne");
        Station delemont = new Station(1, "Delemont");
        Station fribourg = new Station(2, "Fribourg");
        Station interlaken = new Station(3, "interlaken");
        Station chaux = new Station(4, "Chaux");
        Station lausanne = new Station(5, "Lausanne");
        Station lucerne = new Station(6, "lucerne");
        Station neuchatel = new Station(7, "Neuchatel");
        Station olten = new Station(8, "olten");
        Station schwyz = new Station(9, "schwyz");
        Station soleure = new Station(10, "soleure");
        Station wasswen = new Station(11, "wasswen");
        Station yverdon = new Station(12, "yverdon");
        Station zoug = new Station(13, "zoug");
        Station zurich = new Station(14, "zurich");
        b.connect(lausanne, fribourg);
        b.connect(fribourg, berne);
        b.connect(berne, interlaken);
        b.connect(soleure, olten);
        b.connect(lucerne, zoug);
        b.connect(zoug, schwyz);
        b.connect(lucerne, schwyz);
        b.connect(wasswen, schwyz);
        b.connect(neuchatel, soleure);
        StationPartition sp = b.build();
        assertTrue(sp.connected(wasswen, zoug));

    }


}
