package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.scene.media.AudioClip;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static javafx.application.Platform.runLater;

/**
 * GraphicalPlayerAdapter
 * implements Player
 *
 * @author OMAR MEZGHANI (311542)
 * @author MOHAMED CHARFI (311171)
 */
public class GraphicalPlayerAdapter implements Player {

    private GraphicalPlayer graphicalPlayer;
    private final BlockingQueue<SortedBag<Ticket>> ticketsSortedBagBlockingQueue;
    private final BlockingQueue<Integer> slotBlockingQueue;
    private final BlockingQueue<SortedBag<Card>> cardsSortedBagBlockingQueue;
    private final BlockingQueue<Route> routeBlockingQueue;

    /**
     * public constructor
     */
    public GraphicalPlayerAdapter() {
        ticketsSortedBagBlockingQueue = new ArrayBlockingQueue<>(1);
        slotBlockingQueue = new ArrayBlockingQueue<>(1);
        routeBlockingQueue = new ArrayBlockingQueue<>(1);
        cardsSortedBagBlockingQueue = new ArrayBlockingQueue<>(1);
    }

    /**
     * private method that puts T in the BlockingQueue
     *
     * @param blockingQueue blocking queue
     * @param element       element
     * @param <T>           generic parameter specifying the type of element in the blocking queue
     */
    private <T> void putInBlockingQueue(BlockingQueue<T> blockingQueue, T element) {
        try {
            blockingQueue.put(element);
        } catch (InterruptedException interruptedException) {
            throw new Error();
        }
    }

    /**
     * a private method that takes an element from the blocking queue
     *
     * @param blockingQueue blocking queue
     * @param <T>           generic parameter specifying the type of element in the blocking queue
     * @return T
     */
    private <T> T takeFromBlockingQueue(BlockingQueue<T> blockingQueue) {
        try {
            return blockingQueue.take();
        } catch (InterruptedException interruptedException) {
            throw new Error();
        }

    }

    /**
     * a method that initialises the players
     *
     * @param ownId       ownId
     * @param playerNames playerNames
     */
    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        runLater(() -> graphicalPlayer = new GraphicalPlayer(ownId, playerNames));

    }

    /**
     * a method that receives the player informations
     *
     * @param info info
     */
    @Override
    public void receiveInfo(String info) {
        runLater(() -> graphicalPlayer.receiveInfo(info));
    }

    /**
     * a method that updates the state of the game
     *
     * @param newState newState
     * @param ownState ownState
     */
    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        runLater(() -> graphicalPlayer.setState(newState, ownState));
    }

    /**
     * a method that sets the initial ticket choice
     *
     * @param tickets tickets
     */
    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        runLater(() -> graphicalPlayer.chooseTickets(tickets, chosenTickets -> putInBlockingQueue(ticketsSortedBagBlockingQueue, chosenTickets)));
    }

    /**
     * a method that returns the initial ticket choice
     *
     * @return the sorted bag of chosen tickets
     */
    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        return takeFromBlockingQueue(ticketsSortedBagBlockingQueue);
    }

    /**
     * a method that returns the next TurnKind
     *
     * @return the next TurnKind
     */
    @Override
    public TurnKind nextTurn() {
        BlockingQueue<TurnKind> turnKindBlockingQueue = new ArrayBlockingQueue<>(1);

        runLater(() -> graphicalPlayer.startTurn(() -> putInBlockingQueue(turnKindBlockingQueue, TurnKind.DRAW_TICKETS)
                , i -> {
                    putInBlockingQueue(turnKindBlockingQueue, TurnKind.DRAW_CARDS);
                    putInBlockingQueue(slotBlockingQueue, i);
                }
                , (r, cards) -> {
                    putInBlockingQueue(turnKindBlockingQueue, TurnKind.CLAIM_ROUTE);
                    putInBlockingQueue(routeBlockingQueue, r);
                    putInBlockingQueue(cardsSortedBagBlockingQueue, cards);

                }
                , () -> putInBlockingQueue(turnKindBlockingQueue, TurnKind.SURRENDER)
                , () -> putInBlockingQueue(turnKindBlockingQueue, TurnKind.RESTART)
        ));

        return takeFromBlockingQueue(turnKindBlockingQueue);
    }

    /**
     * a method that returns the ticket choice
     *
     * @param options options
     * @return the sorted bag of chosen tickets
     */
    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        setInitialTicketChoice(options);
        return chooseInitialTickets();
    }

    /**
     * a method that returns the slot of the drawn card
     *
     * @return the slot of the drawn card
     */
    @Override
    public int drawSlot() {
        if (slotBlockingQueue.isEmpty()) {
            runLater(() -> graphicalPlayer.drawCard(i -> putInBlockingQueue(slotBlockingQueue, i)));
        }
        return takeFromBlockingQueue(slotBlockingQueue);
    }

    /**
     * a method that returns the claimed route
     *
     * @return the claimed route
     */
    @Override
    public Route claimedRoute() {
        return takeFromBlockingQueue(routeBlockingQueue);
    }

    /**
     * returns the initial claim cards
     *
     * @return the sorted bag of the initial cards used to claim the route
     */
    @Override
    public SortedBag<Card> initialClaimCards() {
        return takeFromBlockingQueue(cardsSortedBagBlockingQueue);
    }

    /**
     * returns the chosen additional cards used to claim an underground route
     *
     * @param options options
     * @return the chosen additional cards used to claim an underground route
     */
    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        runLater(() -> graphicalPlayer.chooseAdditionalCards(options, chosenCards -> putInBlockingQueue(cardsSortedBagBlockingQueue, chosenCards)));
        return takeFromBlockingQueue(cardsSortedBagBlockingQueue);
    }

    /**
     * sets the property of each road
     *
     * @param trailRoutes the list of the raods of the longest trail
     * @param playerId the id of the player
     */
    @Override
    public void longest(List<Route> trailRoutes, PlayerId playerId) {
        runLater(() -> graphicalPlayer.longest(trailRoutes, playerId));
    }

    /**
     * plays a music depending on the value of the parameter win
     *
     * @param win boolean parameter true if the player won
     */
    @Override
    public void music(boolean win) {
        runLater(() -> {
            AudioClip surrender;
            if (win) {
                surrender = new AudioClip(ClassLoader.getSystemResource("win.mp3").toString());
            } else {
                surrender = new AudioClip(ClassLoader.getSystemResource("loose.mp3").toString());
            }
            surrender.play();
        });
    }

}
