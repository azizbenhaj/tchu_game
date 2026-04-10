package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import java.util.ArrayList;
import java.util.List;
import static ch.epfl.tchu.game.Route.Level.UNDERGROUND;

/**
 * @author Ahmed Aziz BEN HAJ (sciper: 310934) HMIDA et Aziz Laadher(sciper: 315196) / le 8 mars 2021
 represente une route
 */
public final class Route {
          private final String id;
          private final Station station1;
          private final Station station2;
          private final int length;
          private final Level level;
          private final Color color;
          public enum Level{OVERGROUND,UNDERGROUND}

    /**
     *constructeur...
     * @param id identificateur de la route
     * @param station1 station de depart
     * @param station2 station d'arrivée
     * @param length longueur de la route
     * @param level route tunnel ou OVERGROUND
     * @param color couleur de la route(de la carte wagon neccessaire a cette route)
     */
        public Route(String id, Station station1, Station station2, int length, Level level, Color color){
            Preconditions.checkArgument(!station1.equals(station2)&& length>=Constants.MIN_ROUTE_LENGTH && length<=Constants.MAX_ROUTE_LENGTH);
             if(id==null || station1==null || station2==null || level==null) throw new  NullPointerException();
             this.length=length;
             this.station1=station1;
             this.station2=station2;
             this.level=level;
             this.color=color;
             this.id=id;
        }

    /**
     * getter de id
     * @return identificateur de la route
     */
    public String id(){return this.id; }

    /**
     * getter de station1
     * @return la station de depart
     */
        public Station station1(){return this.station1; }

    /**
     * getter de station2
     * @return la station de d'arrivée
     */
    public Station station2(){return this.station2; }

    /**
     * getter de length
     * @return la longueur de la route
     */
        public int length(){return this.length;}

    /**
     *getter de level
     * @return le niveau de la route(OVERGROUND ou UNDERGROUND)
     */
    public Level level(){return this.level;}

    /**
     * getter de color
     * @return la couleur de la route
     */
        public Color color(){ return this.color;}

    /**
     * @return la liste des deux gares de la route,
     * dans l'ordre dans lequel elles ont été passées au constructeur
     */

        public List<Station> stations(){
              return List.of(station1,station2);

        }

    /**
     * @param station l'une des stations depart ou arrivée
     * @return  la gare de la route qui n'est pas celle donnée
     * lève IllegalArgumentException si la gare donnée
     * n'est ni la première ni la seconde gare de la route
     */
    public Station stationOpposite(Station station){
              if(station.equals(station1))return station2;
              if(station.equals(station2))return station1;
              throw new IllegalArgumentException();
        }

    /**
     * @return la liste de tous les ensembles de cartes qui pourraient être joués
     * trié par ordre croissant de nombre de cartes locomotive, puis par couleur
     */
    public List<SortedBag<Card>> possibleClaimCards() {
        Card[] listOfCards = Card.CARS.toArray(Card[]::new);
        Card[]listCards;
        if (this.color!=null){
            listCards = new Card[]{Card.of(this.color)};
        }else {
            listCards = listOfCards; }
        List<SortedBag<Card>> claims = new ArrayList<>();
        if (level==UNDERGROUND) {
            for (int i = 0; i < length; i++) {
                for (Card card : listCards) {
                    claims.add(SortedBag.of(length - i, card, i, Card.LOCOMOTIVE)); }
            }
            claims.add(SortedBag.of(length, Card.LOCOMOTIVE)); }
        else{
            for (Card card : listCards) {
                //si la valeur est de type JOKER on passe pour avoir toutes les collections de cartes de couleurs
                //uniques qui ne sont pas de type JOKER
                if(card== Card.JOKER) continue;
                claims.add(SortedBag.of(length ,card ));
                //puis on ajoute les collections de cartes ou une seule carte joker peut etre utilisee a la fois
                claims.add(SortedBag.of(length-1 ,card,1,Card.JOKER ));
            }
        }

        return claims;
    }

    /**
     * @param claimCards cartes initialement posées
     * @param drawnCards les trois cartes tirées du sommet de la pioche
     * @return le nombre de cartes additionnelles à jouer pour s'emparer de la route (en tunnel)
     * lève l'exception IllegalArgumentException si la route à laquelle on l'applique n'est pas un tunnel,
     * ou si drawnCards ne contient pas exactement 3 cartes
     */
        public int additionalClaimCardsCount(SortedBag<Card> claimCards, SortedBag<Card> drawnCards) {
            int count = 0;
            Preconditions.checkArgument(level == UNDERGROUND && drawnCards.size() == Constants.ADDITIONAL_TUNNEL_CARDS);
            for (int i = 0; i < 3; i++) {
                if (claimCards.countOf(drawnCards.get(i)) > 0 || drawnCards.get(i)==Card.LOCOMOTIVE)
                    count++; }
            return count;
        }
    /**
     * @return le nombre de points de construction qu'un joueur obtient lorsqu'il s'empare de la route
     */

    public int claimPoints() {
                return Constants.ROUTE_CLAIM_POINTS.get(length);
    }

}
