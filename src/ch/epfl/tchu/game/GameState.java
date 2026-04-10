package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @author Ahmed Aziz BEN HAJ (sciper: 310934) HMIDA et Aziz Laadher(sciper: 315196) / le 29 mars 2021
 * représente l'état d'une partie de tCHu
 */
public final class GameState extends PublicGameState {

    private final Deck<Ticket> tickets;
    private final CardState cardState;
    private final PlayerId currentPlayerId;
    private final PlayerId lastPlayer;
    private final Map<PlayerId, PlayerState> playerState;
    private PlayerId surrenderedPlayerId = null;
    /**
     * type enuméré des decisions qui peuvent etre prises a la fin du jeu
     */
    public enum PlayerRestartResponse {
         UNDECIDED,WANTS_TO_REPLAY, DOESNT_WANT_TO_REPLAY;
        /**
         * liste qui englobe toutes les decisions possibles
         */
        public static final List<PlayerRestartResponse> ALL = List.of(UNDECIDED,WANTS_TO_REPLAY,DOESNT_WANT_TO_REPLAY);
    }

    private GameState(Deck<Ticket> tickets, CardState cardState, PlayerId currentPlayerId, Map<PlayerId, PlayerState> playerState, PlayerId lastPlayer) {
        super(tickets.size(), cardState, currentPlayerId, makePublic(playerState), lastPlayer);
        this.tickets = tickets;
        this.cardState = cardState;
        this.currentPlayerId = currentPlayerId;
        this.lastPlayer = lastPlayer;
        this.playerState = playerState;
    }

    private static Map<PlayerId, PublicPlayerState> makePublic(Map<PlayerId, PlayerState> playerState) {
        return new HashMap<>(playerState);
    }

    /**
     * retourne l'état initial d'une partie de tCHu
     * @param tickets la pioche des billets contient les billets donnés
     * @param rng     variable random
     * @return l'état initial d'une partie de tCHu
     */

    public static GameState initial(SortedBag<Ticket> tickets, Random rng) {
        Deck<Card> cardDeck = Deck.of(Constants.ALL_CARDS, rng);
        int ran = rng.nextInt(PlayerId.COUNT);
        PlayerId player = PlayerId.ALL.get(ran);
        SortedBag<Card> Cartes1 = cardDeck.topCards(Constants.INITIAL_CARDS_COUNT);
        SortedBag<Card> Cartes2 = cardDeck.withoutTopCards(Constants.INITIAL_CARDS_COUNT).topCards(Constants.INITIAL_CARDS_COUNT);
        return new GameState(Deck.of(tickets, rng),
                CardState.of(cardDeck.withoutTopCards(PlayerId.COUNT * Constants.INITIAL_CARDS_COUNT)), player,
                Map.of(player, new PlayerState(SortedBag.of(), Cartes1, List.of()), player.next(), new PlayerState(SortedBag.of(), Cartes2, List.of())),
                null);
    }

    /**
     * redéfinit la méthode de même nom de PublicGameState pour retourner l'état complet du joueur d'identité donnée
     * @param playerId joueur d'identité donnée
     * @return l'état complet du joueur d'identité donnée
     */
    public PlayerState playerState(PlayerId playerId) {
        return this.playerState.get(playerId);
    }

    /**
     * redéfinit la méthode de même nom de PublicGameState pour retourner l'état complet du joueur courant
     * @return l'état complet du joueur courant
     */
    public PlayerState currentPlayerState() {
        return this.playerState.get(currentPlayerId);
    }

    /**
     * retourne les count billets du sommet de la pioche
     * @param count nombre de billets retournés
     * @return les count billets du sommet de la pioche
     * lève IllegalArgumentException si count n'est pas compris entre 0 et la taille de la pioche (inclus),
     */
    public SortedBag<Ticket> topTickets(int count) {
        Preconditions.checkArgument(count>=0 && count<=this.tickets.size());
        return this.tickets.topCards(count);
    }

    /**
     * retourne un état identique au récepteur, mais sans les count billets du sommet de la pioche,
     * @param count nombre de billets supprimés
     * @return un état identique au récepteur, mais sans les count billets du sommet de la pioche,
     * lève IllegalArgumentException si count n'est pas compris entre 0 et la taille de la pioche (inclus),
     */
    public GameState withoutTopTickets(int count) {
        Preconditions.checkArgument(count>=0 && count<=this.tickets.size());
        return new GameState(this.tickets.withoutTopCards(count), this.cardState, this.currentPlayerId, this.playerState, this.lastPlayer);
    }

    /**
     * retourne la carte au sommet de la pioche,
     * @return la carte au sommet de la pioche,
     * lève IllegalArgumentException si la pioche est vide,
     */
    public Card topCard() {
        Preconditions.checkArgument(!this.cardState.isDeckEmpty());
        return this.cardState.topDeckCard();
    }

    /**
     * retourne un état identique au récepteur mais sans la carte au sommet de la pioche,
     * @return un état identique au récepteur mais sans la carte au sommet de la pioche
     * lève IllegalArgumentException si la pioche est vide,
     */
    public GameState withoutTopCard() {
        Preconditions.checkArgument(!this.cardState.isDeckEmpty());
        return new GameState(this.tickets, this.cardState.withoutTopDeckCard(), this.currentPlayerId, this.playerState, this.lastPlayer);
    }

    /**
     * retourne un état identique au récepteur mais avec les cartes données ajoutées à la défausse,
     * @param discardedCards cartes ajoutées a la defausse
     * @return un état identique au récepteur mais avec les cartes données ajoutées à la défausse,
     */
    public GameState withMoreDiscardedCards(SortedBag<Card> discardedCards) {
        return new GameState(this.tickets, this.cardState.withMoreDiscardedCards(discardedCards), this.currentPlayerId, this.playerState, this.lastPlayer);
    }

    /**
     * retourne un état identique au récepteur sauf si la pioche de cartes est vide,
     * auquel cas elle est recréée à partir de la défausse, mélangée au moyen du générateur aléatoire donné.
     * @param rng générateur aléatoire donné.
     * @return un état identique au récepteur sauf si la pioche de cartes est vide,
     * auquel cas elle est recréée à partir de la défausse, mélangée au moyen du générateur aléatoire donné.
     */
    public GameState withCardsDeckRecreatedIfNeeded(Random rng) {
        if (this.cardState.isDeckEmpty()) {
            return new GameState(tickets, cardState.withDeckRecreatedFromDiscards(rng), currentPlayerId, playerState, lastPlayer);
        }
        return this;
    }

    /**
     * retourne un état identique au récepteur mais dans lequel les billets
     * donnés ont été ajoutés à la main du joueur donné;
     * @param playerId      joueur donné
     * @param chosenTickets billets ajoutés
     * @return un état identique au récepteur mais dans lequel les billets
     * donnés ont été ajoutés à la main du joueur donné;
     * lève IllegalArgumentException si le joueur en question possède déjà au moins un billet.
     */
    public GameState withInitiallyChosenTickets(PlayerId playerId, SortedBag<Ticket> chosenTickets) {
        Preconditions.checkArgument(playerState.get(playerId).tickets().isEmpty());
        HashMap<PlayerId, PlayerState> map = new HashMap<>(playerState);
        map.replace(playerId, playerState.get(playerId).withAddedTickets(chosenTickets));
        return new GameState(this.tickets, this.cardState, this.currentPlayerId, map, this.lastPlayer);

    }

    /**
     * retourne un état identique au récepteur, mais dans lequel le joueur courant a tiré les billets drawnTickets
     * du sommet de la pioche,
     * et choisi de garder ceux contenus dans chosenTicket
     * @param drawnTickets  billets tirés
     * @param chosenTickets billets choisis de garder
     * @return un état identique au récepteur, mais dans lequel le joueur courant a tiré les billets
     * drawnTickets du sommet de la pioche, et choisi de garder ceux contenus dans chosenTicket
     * lève IllegalArgumentException si l'ensemble des billets gardés n'est pas inclus dans celui des billets tirés,
     */
    public GameState withChosenAdditionalTickets(SortedBag<Ticket> drawnTickets, SortedBag<Ticket> chosenTickets) {
        Preconditions.checkArgument(drawnTickets.contains(chosenTickets));
        HashMap<PlayerId, PlayerState> map = new HashMap<>(playerState);
        map.replace(currentPlayerId, playerState.get(currentPlayerId).withAddedTickets(chosenTickets));
        Deck<Ticket> tickets1 = tickets.withoutTopCards(drawnTickets.size());
        return new GameState(tickets1, this.cardState, this.currentPlayerId, map, this.lastPlayer);
    }

    /**
     * retourne un état identique au récepteur si ce n'est que la carte face retournée
     * à l'emplacement donné a été placée dans la main du joueur courant,
     * et remplacée par celle au sommet de la pioche
     * @param slot emplacement de la carte retournée
     * @return un état identique au récepteur si ce n'est que la carte face retournée
     * à l'emplacement donné a été placée dans la main du joueur courant
     * , et remplacée par celle au sommet de la pioche
     * lève IllegalArgumentException s'il n'est pas possible de tirer des cartes, c-à-d si canDrawCards retourne faux
     */
    public GameState withDrawnFaceUpCard(int slot) {
        HashMap<PlayerId, PlayerState> map = new HashMap<>(playerState);
        map.replace(currentPlayerId, playerState.get(currentPlayerId).withAddedCard(this.cardState.faceUpCards().get(slot)));
        return new GameState(this.tickets, this.cardState.withDrawnFaceUpCard(slot), this.currentPlayerId, map, this.lastPlayer);
    }

    /**
     * retourne un état identique au récepteur si ce n'est que la carte du sommet
     * de la pioche a été placée dans la main du joueur courant;
     * @return un état identique au récepteur si ce n'est que la carte du sommet
     * de la pioche a été placée dans la main du joueur courant;
     * lève IllegalArgumentException s'il n'est pas possible de tirer des cartes,
     * c-à-d si canDrawCards retourne faux,
     */
    public GameState withBlindlyDrawnCard() {
        HashMap<PlayerId, PlayerState> map = new HashMap<>(playerState);
        map.replace(currentPlayerId, playerState.get(currentPlayerId).withAddedCard(cardState.topDeckCard()));
        return new GameState(tickets, cardState.withoutTopDeckCard(), currentPlayerId, map, lastPlayer);
    }

    /**
     * retourne un état identique au récepteur mais dans lequel le joueur
     * courant s'est emparé de la route donnée au moyen des cartes données.
     * @param route route dont le joueur s'est emparé
     * @param cards cartes utilisées
     * @return un état identique au récepteur mais dans lequel le joueur
     * courant s'est emparé de la route donnée au moyen des cartes données.
     */
    public GameState withClaimedRoute(Route route, SortedBag<Card> cards) {
        HashMap<PlayerId, PlayerState> map = new HashMap<>(playerState);
        map.replace(currentPlayerId, playerState.get(currentPlayerId).withClaimedRoute(route, cards));
        CardState cardState1 = cardState.withMoreDiscardedCards(cards);
        return new GameState(tickets, cardState1, currentPlayerId, map, lastPlayer);
    }


    /**
     * retourne vrai ssi le dernier tour commence
     * @return vrai ssi le dernier tour commence
     */
    public boolean lastTurnBegins() {
        return (this.lastPlayer == null && playerState.get(currentPlayerId).carCount() < 3);
    }

    /**
     * retourne un état identique au récepteur si ce n'est que
     * le joueur courant est celui qui suit le joueur courant actuel;
     * de plus, si lastTurnBegins retourne vrai, le joueur courant actuel devient le dernier joueur.
     * @return un état identique au récepteur si ce n'est que le joueur
     * courant est celui qui suit le joueur courant actuel; de plus,
     * si lastTurnBegins retourne vrai, le joueur courant actuel devient le dernier joueur.
     */
    public GameState forNextTurn() {
        if (lastTurnBegins())
            return new GameState(this.tickets, this.cardState, this.currentPlayerId.next(), this.playerState, this.currentPlayerId);
        return new GameState(this.tickets, this.cardState, this.currentPlayerId.next(), this.playerState, this.lastPlayer);
    }

    /**
     * affecte le joueur actuel à l'attribut surrenderedPlayerId qui represente l'id du
     * joueur qui a abandonné
     */
    public void withPlayerSurrendered() {
        surrenderedPlayerId = currentPlayerId();
    }

    /**
     * getter du l'id du joueur qui a abandonné
     * @return l'id du joueur qui a abandonné
     */
    public PlayerId aPlayerHasSurrendered() {
        return surrenderedPlayerId;
    }

}