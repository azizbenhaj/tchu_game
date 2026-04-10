package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import java.util.List;
import static ch.epfl.tchu.game.Constants.INITIAL_CAR_COUNT;

/**
 * @author Ahmed Aziz BEN HAJ (sciper: 310934) HMIDA et Aziz Laadher(sciper: 315196) / le 22 mars 2021
 *représente la partie publique de l'état d'un joueur,
 * à savoir le nombre de billets, de cartes et de wagons qu'il possède,
 * les routes dont il s'est emparé,
 * et le nombre de points de construction qu'il a ainsi obtenu.
 */
public class PublicPlayerState {

     private final int ticketCount;
     private final int cardCount;
     private final List<Route> routes;
     private final int carCount;
     private final int claimPoints;

    /**
     *constructeur parametré
     * @param ticketCount nombre de billets possedés
     * @param cardCount nombre de cartes possédés
     * @param routes routes dont on s'est emparé de
     */
    public PublicPlayerState(int ticketCount, int cardCount, List<Route> routes){
        Preconditions.checkArgument(ticketCount>=0 && cardCount>=0);
        this.cardCount=cardCount;
        this.ticketCount=ticketCount;
        this.routes=List.copyOf(routes);
        int somme=0;
        int totalLength=0;
        if(this.routes!=null) {
            for (Route route : this.routes) {
                somme += route.claimPoints();
                totalLength += route.length();
            }}
            this.claimPoints = somme;
            this.carCount = INITIAL_CAR_COUNT - totalLength;
    }

    /**
     *  retourne le nombre de billets que possède le joueur,
     * @return le nombre de billets que possède le joueur,
     */
    public int ticketCount(){
        return this.ticketCount;
    }

    /**
     *retourne le nombre de cartes que possède le joueur,
     * @return le nombre de cartes que possède le joueur,
     */
    public int cardCount(){
        return this.cardCount;
    }

    /**
     *retourne les routes dont le joueur s'est emparé,
     * @return les routes dont le joueur s'est emparé,
     */
    public List<Route> routes(){
        return this.routes;
    }

    /**
     *retourne le nombre de wagons que possède le joueur,
     * @return le nombre de wagons que possède le joueur,
     */
    public int carCount(){
        return this.carCount;
    }

    /**
     *retourne le nombre de points de construction obtenus par le joueur.
     * @return le nombre de points de construction obtenus par le joueur.
     */
    public int claimPoints(){
        return this.claimPoints;
    }
}
