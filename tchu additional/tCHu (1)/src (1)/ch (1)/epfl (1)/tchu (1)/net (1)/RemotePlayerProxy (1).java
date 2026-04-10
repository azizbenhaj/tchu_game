package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import static ch.epfl.tchu.net.MessageId.*;
import static ch.epfl.tchu.net.Serdes.*;
import static java.nio.charset.StandardCharsets.US_ASCII;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

/**
 * RemotePlayerProxy
 * implements Player
 *
 * @author OMAR MEZGHANI (311542)
 * @author MOHAMED CHARFI (311171)
 */
public class RemotePlayerProxy implements Player {
    private static final String SPACE = " ";
    private final BufferedWriter w;
    private final BufferedReader r;

    /**
     * public constructor
     *
     * @param socket socket
     * @throws IOException IOException
     */
    public RemotePlayerProxy(Socket socket) throws IOException {
        w = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), US_ASCII));
        r = new BufferedReader(new InputStreamReader(socket.getInputStream(), US_ASCII));
    }

    /**
     * sends the string to the client
     *
     * @param s string to send
     */
    private void send(String s) {
        try {
            w.write(s);
            w.write('\n');
            w.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * receives the string from the client
     *
     * @return the received message
     */
    private String receive() {
        try {
            return r.readLine();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

    }

    /**
     * sends the arguments and the message ID to the client and initialise the players
     *
     * @param ownId       ownId
     * @param playerNames playerNames
     */
    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        StringJoiner sj = new StringJoiner(SPACE);
        sj.add(INIT_PLAYERS.name())
                .add(playerIdSerde.serialize(ownId))
                .add(stringListSerde.serialize(List.of(playerNames.get(PlayerId.PLAYER_1), playerNames.get(PlayerId.PLAYER_2))));
        send(sj.toString());

    }

    /**
     * receive the Information and sends it to the client
     *
     * @param info info
     */
    @Override
    public void receiveInfo(String info) {
        StringJoiner sj = new StringJoiner(SPACE);
        sj.add(RECEIVE_INFO.name())
                .add(stringSerde.serialize(info));
        send(sj.toString());
    }

    /**
     * sends the message ID and the arguments to the client and updates the state of the player
     *
     * @param newState newState the gameState
     * @param ownState ownState the playerState
     */
    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        StringJoiner sj = new StringJoiner(SPACE);
        sj.add(UPDATE_STATE.name())
                .add(publicGameStateSerde.serialize(newState))
                .add(playerStateSerde.serialize(ownState));
        send(sj.toString());
    }

    /**
     * sends the message ID and the arguments to the client and sets the initial ticket choice
     *
     * @param tickets tickets
     */
    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        StringJoiner sj = new StringJoiner(SPACE);
        sj.add(SET_INITIAL_TICKETS.name())
                .add(ticketSortedBagSerde.serialize(tickets));
        send(sj.toString());

    }

    /**
     * sends the message ID to the client and choose the initial tickets
     *
     * @return the sortedBag of chosen tickets
     */
    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        send(CHOOSE_INITIAL_TICKETS.name());
        return ticketSortedBagSerde.deserialize(receive());
    }

    /**
     * sends the message ID to the client and return the TurnKind of the nextTurn
     *
     * @return the TurnKind of the nextTurn
     */
    @Override
    public TurnKind nextTurn() {
        send(NEXT_TURN.name());
        return turnKindSerde.deserialize(receive());
    }

    /**
     * sends the message ID and the arguments  to the client and choose tickets
     *
     * @param options options
     * @return the SortedBag of chosen tickets
     */
    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        StringJoiner sj = new StringJoiner(SPACE);
        sj.add(CHOOSE_TICKETS.name())
                .add(ticketSortedBagSerde.serialize(options));
        send(sj.toString());
        return ticketSortedBagSerde.deserialize(receive());
    }

    /**
     * sends the message ID to the client and returns the slot from where the player draws the cards
     *
     * @return the slot from where the player draws the cards
     */
    @Override
    public int drawSlot() {
        send(DRAW_SLOT.name());
        return intSerde.deserialize(receive());
    }

    /**
     * sends the message ID to the client and returns the claimed route
     *
     * @return the claimed route
     */
    @Override
    public Route claimedRoute() {
        send(ROUTE.name());
        return routeSerde.deserialize(receive());
    }

    /**
     * sends the message ID to the client and returns the initial claimed cards
     *
     * @return the initial claimed cards
     */
    @Override
    public SortedBag<Card> initialClaimCards() {
        send(CARDS.name());
        return cardSortedBagSerde.deserialize(receive());
    }

    /**
     * sends the message ID and the arguments to the client and returns the cards that the player will use as additional cards to claim an underground route
     *
     * @param options options
     * @return the cards that the player will use as additional cards to claim an underground route
     */
    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        StringJoiner sj = new StringJoiner(SPACE);
        sj.add(CHOOSE_ADDITIONAL_CARDS.name())
                .add(sortedBagOfCardsListSerde.serialize(options));
        send(sj.toString());
        return cardSortedBagSerde.deserialize(receive());
    }

    /**
     * sends the message ID and the arguments to the client
     *
     * @param trailRoutes the list of the roads of the longest trail
     * @param playerId the player ID of the player owning the longest trail
     */
    @Override
    public void longest(List<Route> trailRoutes, PlayerId playerId) {
        StringJoiner sj = new StringJoiner(SPACE);
        sj.add(LONGEST.name())
                .add(routeListSerde.serialize(trailRoutes))
                .add(playerIdSerde.serialize(playerId));
        send(sj.toString());
    }

    /**
     * sends the message ID and the argument to the client
     *
     * @param win boolean parameter
     */
    @Override
    public void music(boolean win) {
        StringJoiner sj = new StringJoiner(SPACE);
        sj.add(MUSIC.name())
                .add(booleanSerde.serialize(win));
        send(sj.toString());
    }
}