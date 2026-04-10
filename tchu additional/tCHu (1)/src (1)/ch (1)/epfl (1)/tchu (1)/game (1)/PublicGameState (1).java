package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * PublicGameState
 *
 * @author OMAR MEZGHANI (311542)
 * @author MOHAMED CHARFI (311171)
 */
public class PublicGameState {

    private static final int MINIMUM_DECK_AND_DISCARD_SIZE = 5;
    private final int ticketsCount;
    private final PublicCardState cardState;
    private final PlayerId currentPlayerId;
    private final Map<PlayerId, PublicPlayerState> playerState;
    private final PlayerId lastPlayer;
    private boolean theGameEnded;


    /**
     * public constructor
     *
     * @param ticketsCount    ticketsCount
     * @param cardState       cardState
     * @param currentPlayerId currentPlayerId
     * @param playerState     playerState
     * @param lastPlayer      lastPlayer
     */
    public PublicGameState(int ticketsCount, PublicCardState cardState, PlayerId currentPlayerId,
                           Map<PlayerId, PublicPlayerState> playerState, PlayerId lastPlayer) {
        Preconditions.checkArgument(ticketsCount >= 0);
        Preconditions.checkArgument(playerState.size() == PlayerId.COUNT);

        this.ticketsCount = ticketsCount;
        this.cardState = Objects.requireNonNull(cardState);
        this.currentPlayerId = Objects.requireNonNull(currentPlayerId);
        this.playerState = Objects.requireNonNull(Map.copyOf(playerState));
        this.lastPlayer = lastPlayer;
        theGameEnded = false;
    }

    /**
     * returns the number of tickets
     *
     * @return the number of tickets
     */
    public int ticketsCount() {
        return ticketsCount;
    }

    /**
     * returns true if it is possible to draw a ticket
     *
     * @return true if it is possible to draw a ticket
     */
    public boolean canDrawTickets() {
        return ticketsCount > 0;
    }

    /**
     * returns the public State of the cards
     *
     * @return the public State of the cards
     */
    public PublicCardState cardState() {
        return cardState;
    }

    /**
     * returns true if it is possible to daw a card
     *
     * @return true if it is possible to daw a card
     */
    public boolean canDrawCards() {
        return (cardState.deckSize() + cardState.discardsSize()) >= MINIMUM_DECK_AND_DISCARD_SIZE;
    }

    /**
     * returns the current player Id
     *
     * @return the current player Id
     */
    public PlayerId currentPlayerId() {
        return currentPlayerId;
    }

    /**
     * returns the public  state of the player playerId
     *
     * @param playerId playerId
     * @return the public player state of the player playerId
     */
    public PublicPlayerState playerState(PlayerId playerId) {
        return playerState.get(playerId);
    }

    /**
     * returns the public state of the current player
     *
     * @return the public state of the current player
     */
    public PublicPlayerState currentPlayerState() {
        return playerState.get(currentPlayerId);
    }

    /**
     * returns a list of claimed routes of both players
     *
     * @return a list of claimed routes of both players
     */
    public List<Route> claimedRoutes() {
        List<Route> result = new ArrayList<>();
        result.addAll(playerState.get(PlayerId.PLAYER_1).routes());
        result.addAll(playerState.get(PlayerId.PLAYER_2).routes());
        return result;
    }

    /**
     * returns the id of the last player
     *
     * @return the id of the last player
     */
    public PlayerId lastPlayer() {
        return lastPlayer;
    }

    /**
     * returns the value of isTheGameEnded
     * @return
     */
    public boolean isTheGameEnded() {
        return theGameEnded;
    }

    /**
     * sets the value of isTheGameEnded
     * @param test the new value
     */
    public void setTheGameEnded(boolean test) {
        theGameEnded = test;
    }
}
