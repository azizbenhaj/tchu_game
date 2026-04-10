package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * PlayerState
 * extends PublicPlayerState
 *
 * @author OMAR MEZGHANI (311542)
 * @author MOHAMED CHARFI (311171)
 */
public final class PlayerState extends PublicPlayerState {
    private final SortedBag<Ticket> tickets;
    private final SortedBag<Card> cards;

    /**
     * public constructor
     *
     * @param tickets tickets
     * @param cards   cards
     * @param routes  routes
     * @throws IllegalArgumentException
     */
    public PlayerState(SortedBag<Ticket> tickets, SortedBag<Card> cards, List<Route> routes) {
        super(tickets.size(), cards.size(), routes);
        this.tickets = tickets;
        this.cards = cards;
        updatesTickets();
    }

    /**
     * a static method to construct an initial PlayerState
     *
     * @param initialCards initial cards
     * @return a player state
     * @throws IllegalArgumentException
     */
    public static PlayerState initial(SortedBag<Card> initialCards) {
        Preconditions.checkArgument(initialCards.size() == Constants.INITIAL_CARDS_COUNT);
        return new PlayerState(SortedBag.of(), initialCards, List.of());
    }

    /**
     * returns the tickets
     *
     * @return the tickets
     */
    public SortedBag<Ticket> tickets() {
        return tickets;
    }

    /**
     * returns a player state with the added tickets
     *
     * @param newTickets the additional tickets
     * @return a player state with the added tickets
     */
    public PlayerState withAddedTickets(SortedBag<Ticket> newTickets) {
        return new PlayerState(tickets.union(newTickets), cards, routes());
    }

    /**
     * returns the cards
     *
     * @return the cards
     */
    public SortedBag<Card> cards() {
        return cards;
    }

    /**
     * returns a player state with the added card
     *
     * @param card the additional card
     * @return a player state with the added card
     */
    public PlayerState withAddedCard(Card card) {
        return new PlayerState(tickets, cards.union(SortedBag.of(card)), routes());
    }

    /**
     * returns true if the player can claim the route
     *
     * @param route a route
     * @return true if the player can claim the route
     */
    public boolean canClaimRoute(Route route) {
        return (carCount() >= route.length() && !possibleClaimCards(route).isEmpty());
    }

    /**
     * returns a list of all possible cards
     *
     * @param route a route
     * @return a list of all possible cards
     * @throws IllegalArgumentException
     */
    public List<SortedBag<Card>> possibleClaimCards(Route route) {
        Preconditions.checkArgument(carCount() >= route.length());
        List<SortedBag<Card>> allPossibleClaimCards = route.possibleClaimCards();
        List<SortedBag<Card>> result = new ArrayList<>();
        for (SortedBag<Card> s : allPossibleClaimCards) {
            if (cards.contains(s)) {
                result.add(s);
            }
        }
        return result;
    }

    /**
     * returns a list of all possible additional cards
     *
     * @param additionalCardsCount additional Cards Count
     * @param initialCards         initial Cards
     * @return a list of all possible additional cards
     * @throws IllegalArgumentException
     */
    public List<SortedBag<Card>> possibleAdditionalCards(int additionalCardsCount, SortedBag<Card> initialCards) {
        Preconditions.checkArgument(additionalCardsCount > 0 && additionalCardsCount <= Constants.ADDITIONAL_TUNNEL_CARDS
                && initialCards.toSet().size() <= 2 && initialCards.size() > 0);

        SortedBag.Builder<Card> b = new SortedBag.Builder<>();
        for (Card c : cards) {
            if (initialCards.contains(c) || c == Card.LOCOMOTIVE) {
                b.add(c);
            }
        }
        SortedBag<Card> possibleCards = b.build().difference(initialCards);
        Set<SortedBag<Card>> s;
        if (possibleCards.size() >= additionalCardsCount) {
            s = possibleCards.subsetsOfSize(additionalCardsCount);
        } else {
            s = Set.of();
        }
        List<SortedBag<Card>> options = new ArrayList<>(s);
        options.sort(Comparator.comparingInt(cs -> cs.countOf(Card.LOCOMOTIVE)));
        return options;
    }

    /**
     * returns a new player state with the claimed route
     *
     * @param route      the claimed route
     * @param claimCards the claimed cards
     * @return a new player state with the claimed route
     */
    public PlayerState withClaimedRoute(Route route, SortedBag<Card> claimCards) {
        List<Route> l = new ArrayList<>(routes());
        l.add(route);
        return new PlayerState(tickets, cards.difference(claimCards), l);
    }


    /**
     * returns the points obtained by the player
     *
     * @return the points obtained by the player
     */
    public int ticketPoints() {
        int maxId = 0;
        for (Route r : routes()) {

            int maxR = Integer.max(r.station1().id(), r.station2().id());
            maxId = Integer.max(maxR, maxId);

        }
        StationPartition.Builder b = new StationPartition.Builder(maxId + 1);
        for (Route r : routes()) {
            b.connect(r.station1(), r.station2());
        }
        StationPartition sp = b.build();
        int resultat = 0;
        for (Ticket t : tickets) {
            resultat += t.points(sp);
        }
        return resultat;
    }

    /**
     * returns the total of points
     *
     * @return the total of points
     */
    public int finalPoints() {
        return ticketPoints() + claimPoints();
    }

    /**
     * updates the value of the variable isDone of each ticket depending on the connectivity of the stations
     */
    private void updatesTickets() {
        int maxId = 0;
        for (Route r : routes()) {

            int maxR = Integer.max(r.station1().id(), r.station2().id());
            maxId = Integer.max(maxR, maxId);

        }
        StationPartition.Builder b = new StationPartition.Builder(maxId + 1);
        for (Route r : routes()) {
            b.connect(r.station1(), r.station2());
        }
        StationPartition sp = b.build();

        for (Ticket t : tickets) {
            t.setDone(sp);
        }
    }

}
