
package ch.epfl.tchu.net;

import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.game.PlayerId;
import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.US_ASCII;

/**
 * @author Ahmed Aziz BEN HAJ (sciper: 310934) HMIDA et Aziz Laadher(sciper: 315196) / le 27 avril 2021
 * représente un client de joueur distant.
 */
public class RemotePlayerClient {

    private final Player player;
    private final BufferedReader bufferedReader;
    private final BufferedWriter bufferedWriter;

    /**
     * costructeur du client
     * @param player le joueur (de type Player) auquel la methode doit fournir un accès distant
     * @param nom  le nom (de type String)
     * @param port le port (de type int) à utiliser pour se connecter au mandataire.
     */
    public RemotePlayerClient(Player player, String nom, int port) {
        this.player = player;
        try{
            Socket socket = new Socket(nom, port);
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), US_ASCII));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), US_ASCII));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     *cette methode: attend un message en provenance du mandataire,
     * le découpe, détermine le type du message, désérialise les arguments, appelle la méthode correspondante du;
     * si cette méthode retourne un résultat, le sérialise pour le renvoyer au mandataire en réponse.
     * @throws IOException vu le  bufferedWriter et bufferedReader
     */
    public void run() throws IOException {
        String k = bufferedReader.readLine();
        while (k!=null && !k.isEmpty()) {
            String[] data = k.split(Pattern.quote(" "), -1);
            MessageId mi = MessageId.valueOf(data[0]);
            System.out.println("message ID "+mi);
            System.out.println("reading stream");
            switch (mi) {
                case INIT_PLAYERS: {
                    this.player.initPlayers(Serdes.PLAYERID.deserialize(data[1]),
                            Map.of(PlayerId.PLAYER_1, Serdes.LISTSTRING.deserialize(data[2]).get(0),
                                    PlayerId.PLAYER_2,
                                    Serdes.LISTSTRING.deserialize(data[2]).get(1)));
                    break;
                }
                case RECEIVE_INFO: {
                    this.player.receiveInfo(Serdes.STRING.deserialize(data[1]));
                    break;
                }
                case UPDATE_STATE: {
                    this.player.updateState(Serdes.PUBLICGAMESTATE.deserialize(data[1]),
                            Serdes.PLAYERSTATE.deserialize(data[2]));
                    break;
                }
                case SET_INITIAL_TICKETS: {
                    this.player.setInitialTicketChoice(Serdes.SORTEDBAGTICKET.deserialize(data[1]));
                    break;
                }
                case GAMEOVER: {
                    String q = Serdes.PLAYERRESTARTRESPONSE.serialize(this.player.gameOver(Serdes.STRING.deserialize(data[1])));
                    bufferedWriter.write(q);
                    bufferedWriter.write('\n');
                    bufferedWriter.flush();
                    break;
                }
                case CHOOSE_INITIAL_TICKETS: {
                    String q = Serdes.SORTEDBAGTICKET.serialize(this.player.chooseInitialTickets());
                    bufferedWriter.write(q);
                    bufferedWriter.write('\n');
                    bufferedWriter.flush();
                    break;
                }
                case NEXT_TURN: {
                    String q = Serdes.TURNKIND.serialize(this.player.nextTurn());
                    bufferedWriter.write(q);
                    bufferedWriter.write('\n');
                    bufferedWriter.flush();
                    break;
                }
                case CHOOSE_TICKETS: {
                    String q = Serdes.SORTEDBAGTICKET.serialize(
                            this.player.chooseTickets(Serdes.SORTEDBAGTICKET.deserialize(data[1])));
                    bufferedWriter.write(q);
                    bufferedWriter.write('\n');
                    bufferedWriter.flush();
                    break;
                }
                case DRAW_SLOT: {
                    String q = Serdes.INTEGER.serialize(this.player.drawSlot());
                    bufferedWriter.write(q);
                    bufferedWriter.write('\n');
                    bufferedWriter.flush();
                    break;
                }
                case ROUTE: {
                    String q = Serdes.ROUTES.serialize(this.player.claimedRoute());
                    bufferedWriter.write(q);
                    bufferedWriter.write('\n');
                    bufferedWriter.flush();
                    break;
                }
                case CARDS: {
                    String q = Serdes.SORTEDBAGCARD.serialize(this.player.initialClaimCards());
                    bufferedWriter.write(q);
                    bufferedWriter.write('\n');
                    bufferedWriter.flush();
                    break;
                }
                case CHOOSE_ADDITIONAL_CARDS: {
                    String q = Serdes.SORTEDBAGCARD.serialize(this.player.chooseAdditionalCards(
                            Serdes.LISTSORTEDBAGCARD.deserialize(data[1])));
                    bufferedWriter.write(q);
                    bufferedWriter.write('\n');
                    bufferedWriter.flush();
                    break;
                }
            }
            k=bufferedReader.readLine();
        }
    }
}