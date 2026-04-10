package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author Ahmed Aziz BEN HAJ (sciper: 310934) HMIDA et Aziz Laadher(sciper: 315196) / le 20 avril 2021
 *contient la totalité des serdes utiles au projet.
 */
public class Serdes {

    private Serdes(){}

    /**
     * serde d'entier defini comme attribut publique, statique et final de la classe
     */
    public static final Serde<Integer> INTEGER = Serde.of(i -> Integer.toString(i), Integer::parseInt);
    /**
     * serde de chaines de caracteres defini comme attribut publique, statique et final de la classe
     */
    public static final Serde<String> STRING = Serde.of(
            i-> Base64.getEncoder().encodeToString(i.getBytes(StandardCharsets.UTF_8)),
            s->new String(Base64.getDecoder().decode(s.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8));
    /**
     * serde de PlayerId defini comme attribut publique, statique et final de la classe
     */
    public static final Serde<PlayerId> PLAYERID = Serde.oneOf(PlayerId.ALL);
    /**
     * serde de TurnKind defini comme attribut publique, statique et final de la classe
     */
    public static final Serde<Player.TurnKind> TURNKIND = Serde.oneOf(Player.TurnKind.ALL);
    /**
     * serde de PlayerRestartResponse defini comme attribut publique, statique et final de la classe
     */
    public static final Serde<GameState.PlayerRestartResponse> PLAYERRESTARTRESPONSE = Serde.oneOf(GameState.PlayerRestartResponse.ALL);
    /**
     * serde de cartes defini comme attribut publique, statique et final de la classe
     */
    public static final Serde<Card> CARD = Serde.oneOf(Card.ALL);
    /**
     * serde de routes defini comme attribut publique, statique et final de la classe
     */
    public static final Serde<Route> ROUTES = Serde.oneOf(ChMap.routes());
    /**
     * serde de tickets defini comme attribut publique, statique et final de la classe
     */
    public static final Serde<Ticket> TICKETS = Serde.oneOf(ChMap.tickets());
    /**
     * serde de liste de chaines de caracteres defini comme attribut publique, statique et final de la classe
     */
    public static final Serde<List<String>> LISTSTRING = Serde.listOf(STRING,",");
    /**
     * serde de liste de cartes defini comme attribut publique, statique et final de la classe
     */
    public static final Serde<List<Card>> LISTCARD=Serde.listOf(CARD,",");
    /**
     * serde de listes de routes defini comme attribut publique, statique et final de la classe
     */
    public static final Serde< List<Route>> LISTROUTE=Serde.listOf(ROUTES,",");
    /**
     * serde de multiensemble de cartes defini comme attribut publique, statique et final de la classe
     */
    public static final Serde<SortedBag<Card>> SORTEDBAGCARD=Serde.bagOf(CARD,",");
    /**
     * serde de multiensemble de tickets defini comme attribut publique, statique et final de la classe
     */
    public static final Serde<SortedBag<Ticket>> SORTEDBAGTICKET=Serde.bagOf(TICKETS,",");
    /**
     * serde de listes de multiensembles de cartes defini comme attribut publique, statique et final de la classe
     */
    public static final Serde<List<SortedBag<Card>>> LISTSORTEDBAGCARD =Serde.listOf(SORTEDBAGCARD,";");
    /**
     * serde de PublicCardState defini comme attribut publique, statique et final de la classe
     */
    public static final Serde<PublicCardState> PUBLICCARDSTATE =Serde.of(
             i-> LISTCARD.serialize(i.faceUpCards())+";"+
                INTEGER.serialize(i.deckSize())+";"+
                 INTEGER.serialize(i.discardsSize())
             ,
            s->{String[] elements = s.split(Pattern.quote(";"), -1);
             return new PublicCardState(LISTCARD.deserialize(elements[0]),
                     INTEGER.deserialize(elements[1]),INTEGER.deserialize(elements[2]));}
           );
    /**
     * serde de PublicPlayerState defini comme attribut publique, statique et final de la classe
     */
    public static final Serde<PublicPlayerState> PUBLICPLAYERSTATE =Serde.of(
            i-> INTEGER.serialize(i.ticketCount())+";"+
                    INTEGER.serialize(i.cardCount())+";"+
                    LISTROUTE.serialize(i.routes())
            ,
            s->{String[] elements = s.split(Pattern.quote(";"), -1);
                System.out.println(elements.length);
                return new PublicPlayerState(INTEGER.deserialize(elements[0]),INTEGER.deserialize(elements[1]),
                        LISTROUTE.deserialize(elements[2]));}
           );
    /**
     * serde de PlayerState defini comme attribut publique, statique et final de la classe
     */
    public static final Serde<PlayerState> PLAYERSTATE =Serde.of(
            i->  SORTEDBAGTICKET.serialize(i.tickets()) + ";" +
                        SORTEDBAGCARD.serialize(i.cards()) + ";" +
                        LISTROUTE.serialize(i.routes())
            ,
            s->{ String[] elements = s.split(Pattern.quote(";"), -1);
                return new PlayerState(SORTEDBAGTICKET.deserialize(elements[0]),SORTEDBAGCARD.deserialize(elements[1]),
                        LISTROUTE.deserialize(elements[2]));}
            );
    /**
     * serde de PublicGameState defini comme attribut publique, statique et final de la classe
     */
    public static final Serde<PublicGameState> PUBLICGAMESTATE =Serde.of(
            i->{ if(i.lastPlayer()==null) return INTEGER.serialize(i.ticketsCount())+ ":" +
                    PUBLICCARDSTATE.serialize(i.cardState())+ ":" +
                    PLAYERID.serialize(i.currentPlayerId())+ ":" +
                    PUBLICPLAYERSTATE.serialize(i.playerState(PlayerId.PLAYER_1))+ ":" +
                    PUBLICPLAYERSTATE.serialize(i.playerState(PlayerId.PLAYER_2))+ ":" +"";
                return INTEGER.serialize(i.ticketsCount())+ ":" +
                        PUBLICCARDSTATE.serialize(i.cardState())+ ":" +
                        PLAYERID.serialize(i.currentPlayerId())+ ":" +
                        PUBLICPLAYERSTATE.serialize(i.playerState(PlayerId.PLAYER_1))+ ":" +
                        PUBLICPLAYERSTATE.serialize(i.playerState(PlayerId.PLAYER_2))+ ":" +
                        PLAYERID.serialize(i.lastPlayer());
            },
            s-> { String[] elements = s.split(Pattern.quote(":"), -1);
                Map<PlayerId, PublicPlayerState>map=new HashMap<>();
                map.put(PlayerId.PLAYER_1,PUBLICPLAYERSTATE.deserialize(elements[3]));
                map.put(PlayerId.PLAYER_2,PUBLICPLAYERSTATE.deserialize(elements[4]));
                return new PublicGameState(INTEGER.deserialize(elements[0]),PUBLICCARDSTATE.deserialize(elements[1]),
                        PLAYERID.deserialize(elements[2]),map,
                        PLAYERID.deserialize(elements[5]));});

}
