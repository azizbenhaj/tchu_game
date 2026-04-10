package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

/**
 * GameState
 * extends PublicGameState
 *
 * @author OMAR MEZGHANI (311542)
 * @author MOHAMED CHARFI (311171)
 */
public final class GameState extends PublicGameState {
    private final static int MINIMUM_NUMBER_OF_CARS = 2;
    private final Deck<Ticket> deckTickets;
    private final CardState cardState;
    private final Map<PlayerId, PlayerState> playerState;

    /**
     * private constructor
     *
     * @param cardState       cardState
     * @param currentPlayerId currentPlayerId
     * @param playerState     playerState
     * @param lastPlayer      lastPlayer
     * @param deckTickets     deckTickets
     */
    private GameState(CardState cardState, PlayerId currentPlayerId, Map<PlayerId, PlayerState> playerState, PlayerId lastPlayer, Deck<Ticket> deckTickets) {
        super(deckTickets.size(), cardState, currentPlayerId, Map.copyOf(playerState), lastPlayer);
        this.deckTickets = Objects.requireNonNull(deckTickets);
        this.cardState = Objects.requireNonNull(cardState);
        this.playerState = Map.copyOf(playerState);
    }


    /**
     * a static method to initialize the GameState
     *
     * @param tickets tickets
     * @param rng     Random
     * @return a game state
     */
    public static GameState initial(SortedBag<Ticket> tickets, Random rng) {
        Deck<Ticket> deckT = Deck.of(tickets, rng);
        Deck<Card> deckC = Deck.of(Constants.ALL_CARDS, rng);

        Map<PlayerId, PlayerState> m = new EnumMap<>(PlayerId.class);

        for (PlayerId id : PlayerId.ALL) {
            m.put(id, PlayerState.initial(deckC.topCards(Constants.INITIAL_CARDS_COUNT)));
            deckC = deckC.withoutTopCards(Constants.INITIAL_CARDS_COUNT);
        }

        CardState cardState = CardState.of(deckC);

        return new GameState(cardState, PlayerId.ALL.get(rng.nextInt(PlayerId.COUNT)), m, null, deckT);
    }

    /**
     * returns the state of the player playerId
     *
     * @param playerId playerId
     * @return the state of the player playerId
     */
    @Override
    public PlayerState playerState(PlayerId playerId) {
        return playerState.get(playerId);
    }

    /**
     * returns the state of the current player
     *
     * @return the state of the current player
     */
    @Override
    public PlayerState currentPlayerState() {
        return playerState.get(currentPlayerId());

    }

    /**
     * returns the count top tickets
     *
     * @param count count
     * @return the count top tickets
     */
    public SortedBag<Ticket> topTickets(int count) {
        Preconditions.checkArgument(count >= 0 && count <= deckTickets.size());
        return deckTickets.topCards(count);
    }

    /**
     * returns a new game state without the count top tickets
     *
     * @param count count
     * @return a new game state without the count top tickets
     */
    public GameState withoutTopTickets(int count) {
        Preconditions.checkArgument(count >= 0 && count <= deckTickets.size());
        return new GameState(cardState, currentPlayerId(), playerState, lastPlayer(), deckTickets.withoutTopCards(count));
    }

    /**
     * returns the top Card of the deck
     *
     * @return the top Card of the deck
     */
    public Card topCard() {
        Preconditions.checkArgument(!cardState.isDeckEmpty());
        return cardState.topDeckCard();
    }

    /**
     * returns a new game state without the top card
     *
     * @return a new game state without the top card
     */
    public GameState withoutTopCard() {
        Preconditions.checkArgument(!cardState.isDeckEmpty());
        return new GameState(cardState.withoutTopDeckCard(), currentPlayerId(), playerState, lastPlayer(), deckTickets);
    }

    /**
     * returns a new game state with more discarded cards
     *
     * @param discardedCards discardedCards
     * @return a new game state with more discarded cards
     */
    public GameState withMoreDiscardedCards(SortedBag<Card> discardedCards) {
        return new GameState(cardState.withMoreDiscardedCards(discardedCards), currentPlayerId(), playerState, lastPlayer(), deckTickets);
    }

    /**
     * returns a new game state with the card's deck recreated if needed
     *
     * @param rng Random
     * @return a new game state with the card's deck recreated if needed
     */
    public GameState withCardsDeckRecreatedIfNeeded(Random rng) {
        if (cardState.isDeckEmpty()) {
            return new GameState(cardState.withDeckRecreatedFromDiscards(rng), currentPlayerId(), playerState, lastPlayer(), deckTickets);
        } else {
            return this;
        }
    }

    /**
     * returns a new game state with added chosen ticket to the state of the player playerId without changing the deck of tickets
     *
     * @param playerId      playerId
     * @param chosenTickets chosenTickets
     * @return a new game state with added chosen ticket to the state of the player playerId without changing the deck of tickets
     */
    public GameState withInitiallyChosenTickets(PlayerId playerId, SortedBag<Ticket> chosenTickets) {
        PlayerState ps = playerState.get(playerId);
        Preconditions.checkArgument(ps.ticketCount() == 0);
        Map<PlayerId, PlayerState> newPlayerState = new EnumMap<>(playerState);
        newPlayerState.replace(playerId, ps.withAddedTickets(chosenTickets));
        return new GameState(cardState, currentPlayerId(), newPlayerState, lastPlayer(), deckTickets);
    }

    /**
     * returns a new game state with added chosen ticket to the state of the player playerId with changing the deck of tickets
     *
     * @param drawnTickets  drawnTickets
     * @param chosenTickets chosenTickets
     * @return a new game state with added chosen ticket to the state of the player playerId with changing the deck of tickets
     */
    public GameState withChosenAdditionalTickets(SortedBag<Ticket> drawnTickets, SortedBag<Ticket> chosenTickets) {
        Preconditions.checkArgument(drawnTickets.contains(chosenTickets));
        PlayerState ps = playerState.get(currentPlayerId());
        Map<PlayerId, PlayerState> newPlayerState = new EnumMap<>(playerState);
        newPlayerState.replace(currentPlayerId(), ps.withAddedTickets(chosenTickets));
        return new GameState(cardState, currentPlayerId(), newPlayerState, lastPlayer(), deckTickets.withoutTopCards(drawnTickets.size()));
    }

    /**
     * returns a new game state with the slot card of the faceUp cards put in the current player hand and changed with a new card
     *
     * @param slot slot
     * @return a new game state with the slot card of the faceUp cards put in the current player hand and changed with a new card
     */
    public GameState withDrawnFaceUpCard(int slot) {
        PlayerState ps = playerState.get(currentPlayerId());
        Map<PlayerId, PlayerState> newPlayerState = new EnumMap<>(playerState);
        newPlayerState.replace(currentPlayerId(), ps.withAddedCard(cardState.faceUpCard(slot)));
        return new GameState(cardState.withDrawnFaceUpCard(slot), currentPlayerId(), newPlayerState, lastPlayer(), deckTickets);
    }

    /**
     * returns a new game state without the top card which was placed in the hand of the current player
     *
     * @return a new game state without the top card which was placed in the hand of the current player
     */
    public GameState withBlindlyDrawnCard() {
        PlayerState ps = playerState.get(currentPlayerId());
        Map<PlayerId, PlayerState> newPlayerState = new EnumMap<>(playerState);
        newPlayerState.replace(currentPlayerId(), ps.withAddedCard(cardState.topDeckCard()));
        return new GameState(cardState.withoutTopDeckCard(), currentPlayerId(), newPlayerState, lastPlayer(), deckTickets);
    }

    /**
     * returns a new game state without the cards used to claim the route which is added to the claimed routes of the current player
     *
     * @param route route
     * @param cards cards
     * @return a new game state without the cards used to claim the route which is added to the claimed routes of the current player
     */
    public GameState withClaimedRoute(Route route, SortedBag<Card> cards) {
        PlayerState ps = playerState.get(currentPlayerId());
        Map<PlayerId, PlayerState> newPlayerState = new EnumMap<>(playerState);
        newPlayerState.replace(currentPlayerId(), ps.withClaimedRoute(route, cards));
        return new GameState(cardState.withMoreDiscardedCards(cards), currentPlayerId(), newPlayerState, lastPlayer(), deckTickets);
    }

    /**
     * returns true if the last Turn Begins
     *
     * @return true if the last Turn Begins
     */
    public boolean lastTurnBegins() {
        return (lastPlayer() == null && playerState.get(currentPlayerId()).carCount() <= MINIMUM_NUMBER_OF_CARS);
    }

    /**
     * returns a new game state with the current player changed to the next player and if the last turn begins the last player is changed
     *
     * @return a new game state with the current player changed to the next player and if the last turn begins the last player is changed
     */
    public GameState forNextTurn() {
        if (lastTurnBegins()) {
            return new GameState(cardState, currentPlayerId().next(), playerState, currentPlayerId(), deckTickets);
        } else {
            return new GameState(cardState, currentPlayerId().next(), playerState, lastPlayer(), deckTickets);
        }
    }

}
