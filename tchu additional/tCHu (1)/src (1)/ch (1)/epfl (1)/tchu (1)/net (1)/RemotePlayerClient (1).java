package ch.epfl.tchu.net;


import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.game.PlayerId;

import java.io.*;
import java.net.Socket;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.US_ASCII;
import static ch.epfl.tchu.net.Serdes.*;

/**
 * RemotePlayerClient
 *
 * @author OMAR MEZGHANI (311542)
 * @author MOHAMED CHARFI (311171)
 */

public class RemotePlayerClient {

    private static final String SPACE = " ";
    private final Player player;
    private final String name;
    private final int port;

    /**
     * public constructor
     *
     * @param player player
     * @param name   name
     * @param port   port
     */

    public RemotePlayerClient(Player player, String name, int port) {
        this.player = player;
        this.name = name;
        this.port = port;
    }

    /**
     * sends the string to the client
     *
     * @param w buffered writer
     * @param s string to send
     */
    private void send(BufferedWriter w, String s) throws IOException {
        w.write(s);
        w.write('\n');
        w.flush();
    }

    /**
     * sends the message to the proxy
     */
    public void run() {
        try (Socket socket = new Socket(name, port);
             BufferedReader r = new BufferedReader(new InputStreamReader(socket.getInputStream(), US_ASCII));
             BufferedWriter w = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), US_ASCII))) {

            String s;

            while ((s = r.readLine()) != null) {
                String[] a = s.split(Pattern.quote(SPACE), -1);
                switch (MessageId.valueOf(a[0])) {
                    case INIT_PLAYERS:
                        Map<PlayerId, String> playerNames = new EnumMap<>(PlayerId.class);
                        List<String> names = stringListSerde.deserialize(a[2]);
                        playerNames.put(PlayerId.PLAYER_1, names.get(0));
                        playerNames.put(PlayerId.PLAYER_2, names.get(1));
                        player.initPlayers(playerIdSerde.deserialize(a[1]), playerNames);
                        break;
                    case RECEIVE_INFO:
                        player.receiveInfo(stringSerde.deserialize(a[1]));
                        break;
                    case UPDATE_STATE:
                        player.updateState(publicGameStateSerde.deserialize(a[1]), playerStateSerde.deserialize(a[2]));
                        break;
                    case SET_INITIAL_TICKETS:
                        player.setInitialTicketChoice(ticketSortedBagSerde.deserialize(a[1]));
                        break;
                    case CHOOSE_INITIAL_TICKETS:
                        send(w, ticketSortedBagSerde.serialize(player.chooseInitialTickets()));
                        break;
                    case NEXT_TURN:
                        send(w, turnKindSerde.serialize(player.nextTurn()));
                        break;
                    case CHOOSE_TICKETS:
                        send(w, ticketSortedBagSerde.serialize(player.chooseTickets(ticketSortedBagSerde.deserialize(a[1]))));
                        break;
                    case DRAW_SLOT:
                        send(w, intSerde.serialize(player.drawSlot()));
                        break;
                    case ROUTE:
                        send(w, routeSerde.serialize(player.claimedRoute()));
                        break;
                    case CARDS:
                        send(w, cardSortedBagSerde.serialize(player.initialClaimCards()));
                        break;
                    case CHOOSE_ADDITIONAL_CARDS:
                        send(w, cardSortedBagSerde.serialize(player.chooseAdditionalCards(sortedBagOfCardsListSerde.deserialize(a[1]))));
                        break;
                    case LONGEST:
                        player.longest(routeListSerde.deserialize(a[1]), playerIdSerde.deserialize(a[2]));
                        break;
                    case MUSIC:
                        player.music(booleanSerde.deserialize(a[1]));
                        break;
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}