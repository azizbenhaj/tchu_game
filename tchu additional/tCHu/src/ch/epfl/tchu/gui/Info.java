package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.util.ArrayList;
import java.util.List;

import static ch.epfl.tchu.gui.StringsFr.*;

/**
 * Info
 *
 * @author OMAR MEZGHANI (311542)
 * @author MOHAMED CHARFI (311171)
 */
public final class Info {
    private final String playerName;

    public Info(String playerName) {
        this.playerName = playerName;
    }

    /**
     * returns the name of the card in french
     *
     * @param card  the card
     * @param count the number of cards
     * @return the name of the card in french
     */
    public static String cardName(Card card, int count) {
        switch (card) {
            case BLACK:
                return BLACK_CARD + StringsFr.plural(count);
            case VIOLET:
                return VIOLET_CARD + StringsFr.plural(count);
            case BLUE:
                return BLUE_CARD + StringsFr.plural(count);
            case GREEN:
                return GREEN_CARD + StringsFr.plural(count);
            case YELLOW:
                return YELLOW_CARD + StringsFr.plural(count);
            case ORANGE:
                return ORANGE_CARD + StringsFr.plural(count);
            case RED:
                return RED_CARD + StringsFr.plural(count);
            case WHITE:
                return WHITE_CARD + StringsFr.plural(count);
            case LOCOMOTIVE:
                return LOCOMOTIVE_CARD + StringsFr.plural(count);
            default:
                throw new Error();
        }
    }

    /**
     * returns a message describing a draw
     *
     * @param playerNames the name of players
     * @param points      the points of the players
     * @return a message describing a draw
     */
    public static String draw(List<String> playerNames, int points) {
        String names = playerNames.get(0) + AND_SEPARATOR + playerNames.get(1);
        return String.format(DRAW, names, points);
    }

    /**
     * returns a message describing the name of the road
     *
     * @param route the route
     * @return a message describing the name of the road
     */
    private static String routeName(Route route) {
        return route.station1().name() + EN_DASH_SEPARATOR + route.station2().name();
    }

    /**
     * returns a message describing the cards
     *
     * @param cards the cards
     * @return a message describing the cards
     */
    public static String cardsDescription(SortedBag<Card> cards) {
        if (cards.isEmpty()){
            return "";
        }

        List<String> l = new ArrayList<>();
        String result;

        for (Card c : cards.toSet()) {
            int n = cards.countOf(c);
            l.add(n + " " + cardName(c, n));
        }

        if (l.size() > 1) {
            result = String.join(", ", l.subList(0, l.size() - 1));
            result += AND_SEPARATOR + l.get(l.size() - 1);
        } else {
            result = l.get(0);
        }

        return result;
    }

    /**
     * returns a message declaring the player who begins to play
     *
     * @return a message declaring the player who begins to play
     */
    public String willPlayFirst() {
        return String.format(WILL_PLAY_FIRST, playerName);
    }

    /**
     * returns a message stating that the person kept the count tickets
     *
     * @param count count
     * @return a message stating that the person kept the count cards
     */
    public String keptTickets(int count) {
        return String.format(KEPT_N_TICKETS, playerName, count, plural(count));
    }

    /**
     * returns a message declaring that the player can play
     *
     * @return a message declaring that the player can play
     */
    public String canPlay() {
        return String.format(CAN_PLAY, playerName);
    }

    /**
     * returns a message stating that the player has drawn the count cards
     *
     * @param count count
     * @return a message stating that the player has drawn the count cards
     */
    public String drewTickets(int count) {
        return String.format(DREW_TICKETS, playerName, count, plural(count));
    }

    /**
     * returns a message stating that the player has drawn a card blindly
     *
     * @return the message stating that the player has drawn a card blindly
     */
    public String drewBlindCard() {
        return String.format(DREW_BLIND_CARD, playerName);
    }

    /**
     * returns a message stating that the player has drawn a visible card
     *
     * @param card card
     * @return a message stating that the player has drawn a visible card
     */
    public String drewVisibleCard(Card card) {
        return String.format(DREW_VISIBLE_CARD, playerName, cardName(card, 1));
    }

    /**
     * returns a message stating that the player has seized the given route using the given cards
     *
     * @param route route
     * @param cards cards
     * @return a message stating that the player has seized the given route using the given cards
     */
    public String claimedRoute(Route route, SortedBag<Card> cards) {
        return String.format(CLAIMED_ROUTE, playerName, routeName(route), cardsDescription(cards));
    }

    /**
     * returns a message stating that the player wishes to seize the given tunnel route using initially the given cards
     *
     * @param route route
     * @param initialCards initialCards
     * @return a message stating that the player wishes to seize the given tunnel route using initially the given cards
     */
    public String attemptsTunnelClaim(Route route, SortedBag<Card> initialCards) {
        return String.format(ATTEMPTS_TUNNEL_CLAIM, playerName, routeName(route), cardsDescription(initialCards));
    }

    /**
     * returns a message stating that the player has drawn the three additional cards,
     * and that they involve an additional cost of the given number of cards
     *
     * @param drawnCards drawnCards
     * @param additionalCost additional Cost
     * @return message stating that the player has drawn the three additional cards,
     * and that they involve an additional cost of the given number of cards
     */
    public String drewAdditionalCards(SortedBag<Card> drawnCards, int additionalCost) {
        if (additionalCost > 0) {
            return String.format(ADDITIONAL_CARDS_ARE, cardsDescription(drawnCards))
                    + String.format(SOME_ADDITIONAL_COST, additionalCost, plural(additionalCost));
        } else {
            return String.format(ADDITIONAL_CARDS_ARE, cardsDescription(drawnCards)) + NO_ADDITIONAL_COST;
        }
    }

    /**
     * returns a message stating that the player could not (or wanted) to seize the given tunnel
     *
     * @param route route
     * @return a message stating that the player could not (or wanted) to seize the given tunnel
     */
    public String didNotClaimRoute(Route route) {
        return String.format(DID_NOT_CLAIM_ROUTE, playerName, routeName(route));
    }

    /**
     * returns a message declaring that the player has only the given number (and less than or equal to 2) of cars left, and that the last turn therefore begins
     *
     * @param carCount car Count
     * @return a message declaring that the player has only the given number (and less than or equal to 2) of cars left, and that the last turn therefore begins
     */
    public String lastTurnBegins(int carCount) {
        return String.format(LAST_TURN_BEGINS, playerName, carCount, plural(carCount));
    }

    /**
     * returns a message stating that the player gets the end-of-game bonus thanks to the given path, which is the longest, or one of the longest
     *
     * @param longestTrail longest Trail
     * @return a message stating that the player gets the end-of-game bonus thanks to the given path, which is the longest, or one of the longest
     */
    public String getsLongestTrailBonus(Trail longestTrail) {
        String s = longestTrail.station1().name() + EN_DASH_SEPARATOR + longestTrail.station2().name();
        return String.format(GETS_BONUS, playerName, s);
    }

    /**
     * returns a message declaring that the player wins the game with the number of points points and that his opponent having only obtained loserPoints
     *
     * @param points points
     * @param loserPoints loser Points
     * @return a message declaring that the player wins the game with the number of points points and that his opponent having only obtained loserPoints
     */
    public String won(int points, int loserPoints) {
        return String.format(WINS, playerName, points, plural(points), loserPoints, plural(loserPoints));
    }
    /**
     * returns a message declaring that the player surrendered
     *
     * @return a message declaring that the player surrendered
     */
    public String surrender() {
        return String.format(SURRENDER, playerName);
    }

    /**
     * returns a message declaring that the other player won
     * @return a message declaring that the other player won
     */
    public String surrenderWin() {
        return String.format(SURRENDER_WIN, playerName);
    }

}
