package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import java.util.*;

/**
 * @author Ahmed Aziz BEN HAJ (sciper: 310934) HMIDA et Aziz Laadher(sciper: 315196) / le 1 mars 2021
 * représente ce que nous avons appelé un trajet
 */
public final class Trip {
    private final Station from ;
    private final Station to;
    private final int points;

    /**
     * constructeur avec 3 parametres de la classe
     * @param from : presente la station de depart
     * @param to   : presente la station d'arrivée
     * @param points : presente le nombre de points de ce trajet
     */
    public Trip(Station from, Station to, int points){
        Preconditions.checkArgument(points>0);
        this.from = Objects.requireNonNull(from);
        this.to = Objects.requireNonNull(to);
        this.points=points;
    }

    /**
     * @param from : presente la liste des stations de depart
     * @param to  : presente la liste des stations d'arrivée
     * @param points : presente la liste des points des trajets
     * @return la liste des trajets de chaque station de depart et d'arrivée avec le nombre des points correspondant
     */
    public static List<Trip> all(List<Station> from, List<Station> to, int points){
        Preconditions.checkArgument(!from.isEmpty() && !to.isEmpty() && (points>0));
        List<Trip> lt=new ArrayList<>();
        for(Station s : from) {
            for (Station c : to) {
             Trip a=new Trip(s,c,points);
             lt.add(a); }
        }
         return lt;
    }

    /**
     * getter de l'attribut from
     * @return l'attribut from de l'objet
     */
    public Station from() { return from;}

    /**
     *getter de l'attribut to
     * @return l'attribut to de l'objet
     */
    public Station to(){ return to;}

    /**
     *getter de l'attribut points
     * @return l'attribut points de l'objet
     */
    public int points() {return points;}

    /**
     * @param connectivity instance de l'interface StationConnectivity qui sert a verifier la connection entre les Gates
     * @return points ou -points selon la connection entre les Gates
     */
    public int points(StationConnectivity connectivity) {
        if(connectivity.connected(from,to)) return points;
        else return(-points);
    }
}
