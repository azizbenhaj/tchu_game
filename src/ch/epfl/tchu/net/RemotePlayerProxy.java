
package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.US_ASCII;

/**
 * @author Ahmed Aziz BEN HAJ (sciper: 310934) HMIDA et Aziz Laadher(sciper: 315196) / le 27 avril 2021
 * représente un mandataire de joueur distant, joue le rôle d'un joueur
 */
public class RemotePlayerProxy implements Player {
    Socket playerSocket;
    BufferedWriter bufferedWriter;
    BufferedReader bufferedReader;

    /**
     * constructeur du proxy
     * @param socket  la prise (socket) que le mandataire utilise pour communiquer
     * à travers le réseau avec le client par échange de messages textuels
     * @throws IOException vu le  bufferedWriter et bufferedReader
     */
    public RemotePlayerProxy(Socket socket) throws IOException{
        this.playerSocket=socket;
        bufferedWriter =
                new BufferedWriter(
                        new OutputStreamWriter( playerSocket.getOutputStream(),
                                US_ASCII));
        bufferedReader = new BufferedReader(new InputStreamReader( playerSocket.getInputStream()));
    }

    /**
     * methode auxiliaire qui permet d'envoyer un message étant donné
     * son identité et les chaînes de caractères correspondants
     * à la sérialisation de ses arguments,
     */
    void send(MessageId message, List<String> liste) {
        try {
            String k = message + " " + String.join(" ", liste);
            bufferedWriter.write(k);
            bufferedWriter.write('\n');
            bufferedWriter.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     *methode auxiliaire qui permet de recevoir un message.
     */
    String receive() {
        try {
            return bufferedReader.readLine();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     *les methodes qui suivent sont les mises en œuvre concrètes
     * des méthodes de l'interface Player dans la classe proxy
     */
    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        String s1 = Serdes.PLAYERID.serialize(ownId);
        String s2 = Serdes.LISTSTRING.serialize(List.of(playerNames.get(PlayerId.PLAYER_1), playerNames.get(PlayerId.PLAYER_2)));
        send(MessageId.INIT_PLAYERS, List.of(s1, s2));
    }

    /**
     * definition globale juste en haut qui definit toutes ces methodes
     */
    @Override
    public void receiveInfo(String info) {
        String s1 = Serdes.STRING.serialize(info);
        send(MessageId.RECEIVE_INFO, List.of(s1));
    }

    /**
     * definition globale juste en haut qui definit toutes ces methodes
     */
    @Override
    public GameState.PlayerRestartResponse gameOver(String info) {
        send(MessageId.GAMEOVER, List.of(Serdes.STRING.serialize(info)));
        return Serdes.PLAYERRESTARTRESPONSE.deserialize(receive());
    }

    /**
     * definition globale juste en haut qui definit toutes ces methodes
     */
    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        String s1 = Serdes.PUBLICGAMESTATE.serialize(newState);
        String s2 = Serdes.PLAYERSTATE.serialize(ownState);
        send(MessageId.UPDATE_STATE, List.of(s1, s2));
    }

    /**
     * definition globale juste en haut qui definit toutes ces methodes
     */
    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        String s1 = Serdes.SORTEDBAGTICKET.serialize(tickets);
        System.out.println(s1);
        send(MessageId.SET_INITIAL_TICKETS, List.of(s1));
    }

    /**
     * definition globale juste en haut qui definit toutes ces methodes
     */
    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        send(MessageId.CHOOSE_INITIAL_TICKETS, List.of());
        return Serdes.SORTEDBAGTICKET.deserialize(receive());
    }

    /**
     * definition globale juste en haut qui definit toutes ces methodes
     */
    @Override
    public TurnKind nextTurn() {
        send(MessageId.NEXT_TURN, List.of());
        return Serdes.TURNKIND.deserialize(receive());
    }

    /**
     * definition globale juste en haut qui definit toutes ces methodes
     */
    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        send(MessageId.CHOOSE_TICKETS, List.of(Serdes.SORTEDBAGTICKET.serialize(options)));
        return Serdes.SORTEDBAGTICKET.deserialize(receive());
    }

    /**
     * definition globale juste en haut qui definit toutes ces methodes
     */
    @Override
    public int drawSlot() {
        send(MessageId.DRAW_SLOT, List.of());
        return Serdes.INTEGER.deserialize(receive());
    }

    /**
     * definition globale juste en haut qui definit toutes ces methodes
     */
    @Override
    public Route claimedRoute() {
        send(MessageId.ROUTE, List.of());
        return Serdes.ROUTES.deserialize(receive());
    }

    /**
     * definition globale juste en haut qui definit toutes ces methodes
     */
    @Override
    public SortedBag<Card> initialClaimCards() {
        send(MessageId.CARDS, List.of());
        return Serdes.SORTEDBAGCARD.deserialize(receive());
    }

    /**
     * definition globale juste en haut qui definit toutes ces methodes
     */
    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        send(MessageId.CHOOSE_ADDITIONAL_CARDS, List.of(Serdes.LISTSORTEDBAGCARD.serialize(options)));
        return Serdes.SORTEDBAGCARD.deserialize(receive());
    }
}
