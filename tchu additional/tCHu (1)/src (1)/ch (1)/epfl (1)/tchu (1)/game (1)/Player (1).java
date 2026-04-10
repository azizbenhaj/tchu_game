package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import javafx.scene.media.AudioClip;

import java.util.*;

/**
 * Player
 *
 * @author OMAR MEZGHANI (311542)
 * @author MOHAMED CHARFI (311171)
 */
public interface Player {
    /**
     * enum TurnKind
     */
    public enum TurnKind {
        DRAW_TICKETS,
        DRAW_CARDS,
        CLAIM_ROUTE,
        RESTART,
        SURRENDER;

        public static final List<TurnKind> ALL = List.of(TurnKind.values());
    }

    /**
     * initialises the players
     *
     * @param ownId       ownId
     * @param playerNames playerNames
     */
    void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames);

    /**
     * receives informations about the game
     *
     * @param info info
     */
    void receiveInfo(String info);

    /**
     * updates the public game state and the player state
     *
     * @param newState newState
     * @param ownState ownState
     */
    void updateState(PublicGameState newState, PlayerState ownState);

    /**
     * sets the initial ticket choice
     *
     * @param tickets tickets
     */
    void setInitialTicketChoice(SortedBag<Ticket> tickets);

    /**
     * sets the initial ticket choice
     *
     * @return the chosen tickets
     */
    SortedBag<Ticket> chooseInitialTickets();

    /**
     * defines witch type of action will the player do
     *
     * @return the type of action the player will do
     */
    TurnKind nextTurn();

    /**
     * returns the chosen tickets
     *
     * @param options options
     * @return the chosen tickets
     */
    SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options);

    /**
     * returns the place (integer) from where the player draws the cards
     *
     * @return the place (integer) from where the player draws the cards
     */
    int drawSlot();

    /**
     * returns the claimed route
     *
     * @return the claimed route
     */
    Route claimedRoute();

    /**
     * returns the initial claimed cards
     *
     * @return the initial claimed cards
     */
    SortedBag<Card> initialClaimCards();

    /**
     * returns the cards that the player will use as additional cards to claim an underground route
     *
     * @param options options
     * @return the cards that the player will use as additional cards to claim an underground route
     */
    SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options);

    /**
     * sets the property of each road
     *
     * @param trailRoutes the list of the raods of the longest trail
     * @param playerId the id of the player
     */
    void longest(List<Route> trailRoutes, PlayerId playerId);

    /**
     * plays a music depending on the value of the parameter win
     *
     * @param win boolean parameter true if the player won
     */
    void music(boolean win);

}

