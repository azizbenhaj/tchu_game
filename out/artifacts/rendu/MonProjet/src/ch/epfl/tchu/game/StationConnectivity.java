package ch.epfl.tchu.game;

/**
 * @author Ahmed Aziz BEN HAJ (sciper: 310934) HMIDA et Aziz Laadher(sciper: 315196) / le 1 mars 2021
 * représente ce que nous appellerons la «connectivité» du réseau d'un joueur,
 * c-à-d le fait que deux gares du réseau de tCHu soient reliées ou non par le réseau du joueur en question
 */
public interface StationConnectivity {

     boolean connected(Station s1, Station s2);

}
