package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import java.util.*;

/**
 * @author Ahmed Aziz BEN HAJ (sciper: 310934) HMIDA et Aziz Laadher(sciper: 315196) / le 1 mars 2021
 * représente ce que nous avons appelé un billet
 */
public final class Ticket implements Comparable<Ticket>{
   private final List<Trip> trips;
   private final String text;

    /**
     * constructeur avec parametre de Ticket
     * @param trips liste de trips (liste non nulle)
     */
   public Ticket(List<Trip> trips){
       boolean test=true;
       for (int i=1;i<trips.size();i++) {
           if(trips.get(i-1)==trips.get(i)){
               test=false;
               break;}
       }
       Preconditions.checkArgument(!trips.isEmpty() && test);
       this.trips=List.copyOf(trips);
       this.text=computeText(trips);
       }

    /**
     * constructeur
     * @param from station depart
     * @param to station arrivée
     * @param points nombre de points
     */
   public Ticket(Station from, Station to, int points){
       this(List.of(new Trip(from,to,points)));
   }

    /**
     *  retourne le texte du billet
     * @param tripss liste des trips
     * @return le texte du billet
     */
    private static String computeText(List<Trip>tripss){
        String s;
        if(tripss.size()==1) {s=String.format("%s - %s (%s)", tripss.get(0).from(),tripss.get(0).to(),tripss.get(0).points() );
        return s;}
        else if(tripss.size()>1) {
            TreeSet<String> sT= new TreeSet<>();
            for (Trip t: tripss ) {
                String topoints = String.format("%s (%s)", t.to(), t.points());
                sT.add(topoints); }
            String ls =String.join(", ",sT);
                s=String.format("%s - {%s}", tripss.get(0).from(),ls) ;
                return s;
        }
        else return null;
    }

    /**
     * getter de text
     * @return text
     */
   public String text(){
       return text;
   }

    /**
     * calcule le nombre de points selon la connectivity
     * @param connectivity instance de l'interface StationConnectivity qui sert comme arg a la methode points(arg) de Trip
     * @return nombre de points maximal
     */
   public int points(StationConnectivity connectivity){
       int max=trips.get(0).points(connectivity);
       for(int i=1;i<trips.size();i++){
           if(trips.get(i).points(connectivity)>max){
               max=trips.get(i).points(connectivity);}
       }
       return max;
       }

    /**
     * comparer deux textes des tickets
     * @param that deuxieme ticket
     * @return si le text du 1er ticket precede celui du 2eme
     */
    @Override
    public int compareTo(Ticket that) {
    return this.text().compareTo(that.text());
    }

    /**
     * redef de toString
     * @return text
     */
    @Override
    public String toString(){
       return this.text();
    }

}
