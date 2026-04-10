package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import java.util.List;
import java.util.Map;

/**
 * @author Ahmed Aziz BEN HAJ (sciper: 310934) HMIDA et Aziz Laadher(sciper: 315196) / le 29 mars 2021
 * représente un joueur de tCHu
 */
public interface Player {
    /**
     *appelée au début de la partie pour communiquer au joueur sa propre identité ownId, ainsi
     * que les noms des différents joueurs, le sien inclus, qui se trouvent dans playerNames
     * @param ownId id du joueur
     * @param playerNames les noms des différents joueurs
     */
     void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames);

    /**
     *appelée chaque fois qu'une information doit être communiquée au joueur au cours de la partie
     * @param info l'information donnée
     */
     void receiveInfo(String info);

    /**
     *appelée chaque fois que l'état du jeu a changé, pour informer le joueur de la composante publique
     * de ce nouvel état, newState, ainsi que de son propre état, ownState,
     * @param newState  la composante publiqu de l'état du joueur
     * @param ownState propre etat du joueur
     */
     void updateState(PublicGameState newState, PlayerState ownState);

    /**
     *appelée au début de la partie pour communiquer au joueur les cinq billets qui lui ont été distribués
     * @param tickets 5billets distribués
     */
     void setInitialTicketChoice(SortedBag<Ticket> tickets) ;

    /**
     *appelée au début de la partie pour demander au joueur lesquels des billets qu'on lui a distribué initialement
     *  (via la méthode précédente) il garde
     * @return les billets qu'il garde
     */
     SortedBag<Ticket> chooseInitialTickets() ;

    /**
     *appelée au début du tour d'un joueur, pour savoir quel type d'action il désire effectuer durant ce tour,
     * @return type d'action desiré
     */
     TurnKind nextTurn();

    /**
     *appelée lorsque le joueur a décidé de tirer des billets supplémentaires en cours de partie,
     *  afin de lui communiquer les billets tirés et de savoir lesquels il garde,
     * @param options les billets supplementaires
     * @return les billets gardés
     */
     SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) ;

    /**
     * appelée lorsque le joueur decide de rejouer ou fermer a la fin du jeu
     * @param info string du fin de jeu
     * @return la valeur du type enum PlayerRestartResponse
     */
     GameState.PlayerRestartResponse gameOver(String info);
    /**
     *appelée lorsque le joueur a décidé de tirer des cartes wagon/locomotive, afin de savoir d'où il désire les tirer
     * @return la carte tirée
     */
     int drawSlot();

    /**
     *appelée lorsque le joueur a décidé de (tenter de) s'emparer d'une route,
     * afin de savoir de quelle route il s'agit,
     * @return la route mentionnée
     */
     Route claimedRoute();

    /**
     * appelée lorsque le joueur a décidé de (tenter de) s'emparer d'une route,
     * afin de savoir quelle(s) carte(s) il désire initialement utiliser pour cela,
     * @return cartes initialement utilisées
     */
     SortedBag<Card> initialClaimCards();

    /**
     *appelée lorsque le joueur a décidé de tenter de s'emparer d'un tunnel et
     * que des cartes additionnelles sont nécessaires,
     * afin de savoir quelle(s) carte(s) il désire utiliser pour cela, les possibilités lui étant passées en argument
     * @param options les possibilitées des cartes
     * @return cartes additionnelles
     */
     SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options);

    /**
     *représente les trois types d'actions qu'un joueur de tCHu peut effectuer durant un tour
     */
     enum TurnKind{
        SURRENDER,DRAW_TICKETS,DRAW_CARDS,CLAIM_ROUTE;
        /**
         * tableau des membres de l'enum
         */
        public static final TurnKind[] co = new TurnKind[]{ SURRENDER,DRAW_TICKETS,DRAW_CARDS,CLAIM_ROUTE};
        /**
         *liste des membres de l'enum
         */
        public static final List<TurnKind> ALL = List.of(co);
    }
}