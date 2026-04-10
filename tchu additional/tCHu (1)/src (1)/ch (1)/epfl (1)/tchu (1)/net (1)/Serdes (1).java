package ch.epfl.tchu.net;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Serdes
 *
 * @author OMAR MEZGHANI (311542)
 * @author MOHAMED CHARFI (311171)
 */
public abstract class Serdes {


    public static final String COMMA = ",";
    public static final String SEMICOLON = ";";
    public static final String TWO_POINTS = ":";
    public static final String QUESTION_MARK= "?";


    public static final Serde<Boolean> booleanSerde = Serde.of(String::valueOf, Boolean::parseBoolean);

    /**
     * the serde for integers
     */
    public static final Serde<Integer> intSerde = Serde.of(String::valueOf, Integer::parseInt);

    /**
     *the serde for strings
     */
    public static final Serde<String> stringSerde = Serde.of(t -> Base64.getEncoder().encodeToString(t.getBytes(StandardCharsets.UTF_8)),
            s -> new String(Base64.getDecoder().decode(s.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8));

    /**
     * the serde for player Ids
     */
    public static final Serde<PlayerId> playerIdSerde = Serde.oneOf(PlayerId.ALL);

    /**
     * the serde for TurnKinds
     */
    public static final Serde<Player.TurnKind> turnKindSerde = Serde.oneOf(Player.TurnKind.ALL);

    /**
     * the serde for cards
     */
    public static final Serde<Card> cardSerde = Serde.oneOf(Card.ALL);

    /**
     * the serde for routes
     */
    public static final Serde<Route> routeSerde = Serde.oneOf(ChMap.routes());

    /**
     * the basic serde for tickets
     */
    public static final Serde<Ticket> ticketSerdeBasic = Serde.oneOf(ChMap.tickets());

    /**
     * the serde for tickets
     */
    public static final Serde<Ticket> ticketSerde = Serde.of(
            ticket -> {
                StringJoiner sj = new StringJoiner(QUESTION_MARK);
                sj.add(ticketSerdeBasic.serialize(ticket))
                        .add(booleanSerde.serialize(ticket.isDone()));
                return sj.toString();
            },
            s -> {
                String[] a = s.split(Pattern.quote(QUESTION_MARK), -1);
                Preconditions.checkArgument(a.length == 2);
                Ticket ticket = ticketSerdeBasic.deserialize(a[0]);
                ticket.setDone(booleanSerde.deserialize(a[1]));
                return ticket;
            }
    );

    /**
     * the serde for string lists
     */
    public static final Serde<List<String>> stringListSerde = Serde.listOf(stringSerde, COMMA);

    /**
     * the serde for cards list
     */
    public static final Serde<List<Card>> cardListSerde = Serde.listOf(cardSerde, COMMA);

    /**
     * the serde for route list
     */
    public static final Serde<List<Route>> routeListSerde = Serde.listOf(routeSerde, COMMA);

    /**
     * the serde for cards sortedBag
     */
    public static final Serde<SortedBag<Card>> cardSortedBagSerde = Serde.bagOf(cardSerde, COMMA);

    /**
     * the serde for tickets sortedBag
     */
    public static final Serde<SortedBag<Ticket>> ticketSortedBagSerde = Serde.bagOf(ticketSerde, COMMA);

    /**
     * the serde for sortedBag of Cards List
     */
    public static final Serde<List<SortedBag<Card>>> sortedBagOfCardsListSerde = Serde.listOf(cardSortedBagSerde, SEMICOLON);

    /**
     * the serde for publicCardState
     */
    public static final Serde<PublicCardState> publicCardStateSerde =
            Serde.of(publicCardState -> {
                        StringJoiner sj = new StringJoiner(SEMICOLON);
                        sj.add(cardListSerde.serialize(publicCardState.faceUpCards()));
                        sj.add(intSerde.serialize(publicCardState.deckSize()));
                        sj.add(intSerde.serialize(publicCardState.discardsSize()));
                        return sj.toString();
                    },
                    s -> {
                        String[] a = s.split(Pattern.quote(SEMICOLON), -1);
                        Preconditions.checkArgument(a.length == 3);
                        return new PublicCardState(cardListSerde.deserialize(a[0]), intSerde.deserialize(a[1]),
                                intSerde.deserialize(a[2]));
                    }
            );

    /**
     * the serde for publicPlayerState
     */
    public static final Serde<PublicPlayerState> publicPlayerStateSerde =
            Serde.of(publicPlayerState -> {
                        StringJoiner sj = new StringJoiner(SEMICOLON);
                        sj.add(intSerde.serialize(publicPlayerState.ticketCount()));
                        sj.add(intSerde.serialize(publicPlayerState.cardCount()));
                        sj.add(routeListSerde.serialize(publicPlayerState.routes()));
                        return sj.toString();
                    },
                    s -> {
                        String[] a = s.split(Pattern.quote(SEMICOLON), -1);
                        Preconditions.checkArgument(a.length == 3);
                        return new PublicPlayerState(intSerde.deserialize(a[0]), intSerde.deserialize(a[1]),
                                routeListSerde.deserialize(a[2]));
                    }
            );

    /**
     * the serde for playerState
     */
    public static final Serde<PlayerState> playerStateSerde =
            Serde.of(playerState -> {
                        StringJoiner sj = new StringJoiner(SEMICOLON);
                        sj.add(ticketSortedBagSerde.serialize(playerState.tickets()));
                        sj.add(cardSortedBagSerde.serialize(playerState.cards()));
                        sj.add(routeListSerde.serialize(playerState.routes()));
                        return sj.toString();
                    },
                    s -> {
                        String[] a = s.split(Pattern.quote(SEMICOLON), -1);
                        Preconditions.checkArgument(a.length == 3);
                        return new PlayerState(ticketSortedBagSerde.deserialize(a[0]), cardSortedBagSerde.deserialize(a[1]),
                                routeListSerde.deserialize(a[2]));
                    }
            );

    /**
     * the serde for publicGameState
     */
    public static final Serde<PublicGameState> publicGameStateSerde =
            Serde.of(publicGameState -> {
                        StringJoiner sj = new StringJoiner(TWO_POINTS);
                        sj.add(intSerde.serialize(publicGameState.ticketsCount()));
                        sj.add(publicCardStateSerde.serialize(publicGameState.cardState()));
                        sj.add(playerIdSerde.serialize(publicGameState.currentPlayerId()));
                        sj.add(publicPlayerStateSerde.serialize(publicGameState.playerState(PlayerId.PLAYER_1)));
                        sj.add(publicPlayerStateSerde.serialize(publicGameState.playerState(PlayerId.PLAYER_2)));
                        sj.add(playerIdSerde.serialize(publicGameState.lastPlayer()));
                        sj.add(booleanSerde.serialize(publicGameState.isTheGameEnded()));
                        return sj.toString();
                    },
                    s -> {
                        String[] a = s.split(Pattern.quote(TWO_POINTS), -1);
                        Preconditions.checkArgument(a.length == 7);
                        Map<PlayerId, PublicPlayerState> playerIdPublicPlayerStateMap = new EnumMap<>(PlayerId.class);
                        playerIdPublicPlayerStateMap.put(PlayerId.PLAYER_1, publicPlayerStateSerde.deserialize(a[3]));
                        playerIdPublicPlayerStateMap.put(PlayerId.PLAYER_2, publicPlayerStateSerde.deserialize(a[4]));
                        PublicGameState publicGameState = new PublicGameState(intSerde.deserialize(a[0]), publicCardStateSerde.deserialize(a[1])
                                , playerIdSerde.deserialize(a[2]), playerIdPublicPlayerStateMap, playerIdSerde.deserialize(a[5]));
                        publicGameState.setTheGameEnded(booleanSerde.deserialize(a[6]));
                        return publicGameState;
                    }
            );
}
