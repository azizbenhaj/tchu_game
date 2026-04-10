package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * @author Ahmed Aziz BEN HAJ (sciper: 310934) HMIDA et Aziz Laadher(sciper: 315196) / le 29 mars 2021
 *représente la partie publique de l'état d'une partie de tCHu
 */
public class PublicGameState {


    private final int ticketsCount;
    private final PublicCardState cardState;
    private final PlayerId currentPlayerId;
    private final Map<PlayerId, PublicPlayerState> playerState;
    private final PlayerId lastPlayer;

    /**
     * constructeur
     * @param ticketsCount nombre de tickets
     * @param cardState etat public des cartes
     * @param currentPlayerId l'identité du joueur actuel
     * @param playerState les etats publics des joueurs
     * @param lastPlayer le dernier joueur qui a jouer
     */
    public PublicGameState(int ticketsCount, PublicCardState cardState, PlayerId currentPlayerId,
                    Map<PlayerId, PublicPlayerState> playerState, PlayerId lastPlayer){
        Preconditions.checkArgument(ticketsCount>=0 && playerState.size()==PlayerId.COUNT);
        if(cardState==null || currentPlayerId==null ||playerState==null ) throw new NullPointerException();
      this.ticketsCount=ticketsCount;
      this.cardState=cardState;
      this.currentPlayerId=currentPlayerId;
      this.playerState=playerState;
      this.lastPlayer=lastPlayer;
    }

    /**
     *retourne la taille de la pioche de billets
     * @return la taille de la pioche de billets
     */
    public int ticketsCount(){
        return this.ticketsCount;
    }

    /**
     * retourne vrai ssi il est possible de tirer des billets, c-à-d si la pioche n'est pas vide
     * @return vrai ssi il est possible de tirer des billets, c-à-d si la pioche n'est pas vide
     */
    public boolean canDrawTickets() {
       return !(this.ticketsCount==0);
    }

    /**
     *retourne la partie publique de l'état des cartes wagon/locomotive
     * @return la partie publique de l'état des cartes wagon/locomotive
     */
    public PublicCardState cardState(){
        return this.cardState;
    }

    /**
     * retourne vrai ssi il est possible de tirer des cartes
     * @return vrai ssi il est possible de tirer des cartes
     */
    public boolean canDrawCards(){
            return (this.cardState.deckSize()+this.cardState.discardsSize())>=5;//MIN CARDS COUNT IN GAME (5)
    }

    /**
     *retourne l'identité du joueur actuel
     * @return l'identité du joueur actuel
     */
     public PlayerId currentPlayerId(){
        return this.currentPlayerId;
     }

    /**
     *retourne la partie publique de l'état du joueur d'identité donnée,
     * @param playerId joueur d'identité donnée,
     * @return la partie publique de l'état du joueur d'identité donnée,
     */
     public PublicPlayerState playerState(PlayerId playerId){
        return this.playerState.get(playerId);
     }

    /**
     *retourne la partie publique de l'état du joueur courant,
     * @return la partie publique de l'état du joueur courant,
     */
     public PublicPlayerState currentPlayerState(){
        return this.playerState.get(currentPlayerId);
     }

    /**
     *retourne la totalité des routes dont l'un ou l'autre des joueurs s'est emparé,
     * @return la totalité des routes dont l'un ou l'autre des joueurs s'est emparé,
     */
    public List<Route> claimedRoutes(){
        List<Route> list =new ArrayList<>();
        list.addAll(playerState.get(currentPlayerId).routes());
        list.addAll(playerState.get(currentPlayerId.next()).routes());
        return list;
     }

    /**
     *retourne l'identité du dernier joueur, ou null si elle n'est pas encore connue car le dernier tour n'a pas commencé.
     * @return l'identité du dernier joueur
     */
     public PlayerId lastPlayer(){
        return this.lastPlayer;
     }

}
