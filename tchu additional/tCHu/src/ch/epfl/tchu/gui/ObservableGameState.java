package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.*;

import static ch.epfl.tchu.game.Constants.*;

/**
 * ObservableGameState
 *
 * @author OMAR MEZGHANI (311542)
 * @author MOHAMED CHARFI (311171)
 */

public final class ObservableGameState {

    private final PlayerId playerId;
    private PublicGameState publicGameState;
    private PlayerState playerState;

    //first group
    private final List<ObjectProperty<Card>> faceUpCards;
    private final IntegerProperty ticketsPercentage;
    private final IntegerProperty cardsPercentage;
    private final Map<Route, ObjectProperty<PlayerId>> routesPossession;
    private final Map<Route, ObjectProperty<PlayerId>> longestRouteProperty;
    private final BooleanProperty theGameEnded;

    //second group
    private final Map<PlayerId, IntegerProperty> playersTicketCount;
    private final Map<PlayerId, IntegerProperty> playersCardCount;
    private final Map<PlayerId, IntegerProperty> playersCarCount;
    private final Map<PlayerId, IntegerProperty> playersConstructionPoints;

    //third group
    private final ObservableList<TicketDecorator> currentPlayerTicketList;
    private final Map<Card, IntegerProperty> currentPlayerCardsTypeCount;
    private final Map<Route, BooleanProperty> currentPlayerCanClaimRoute;

    /**
     * a private method to create a map with key of type Route and value of type ObjectProperty<PlayerId>
     *
     * @return a map with key of type Route and value of type ObjectProperty<PlayerId>
     */
    private static Map<Route, ObjectProperty<PlayerId>> createPlayersObjectProperty() {
        Map<Route, ObjectProperty<PlayerId>> result = new HashMap<>();
        for (Route r : ChMap.routes()) {
            result.put(r, new SimpleObjectProperty<>(null));
        }
        return result;
    }

    /**
     * a private method to create a List of ObjectProperty<Card>
     *
     * @return a List of ObjectProperty<Card>
     */
    private static List<ObjectProperty<Card>> createFaceUpCards() {
        List<ObjectProperty<Card>> result = new ArrayList<>();
        for (int slot : FACE_UP_CARD_SLOTS) {
            result.add(new SimpleObjectProperty<>(null));
        }
        return result;
    }

    /**
     * a private method to create a map with key of type Route and value of type BooleanProperty
     *
     * @return a map with key of type Route and value of type BooleanProperty
     */
    private static Map<Route, BooleanProperty> createPlayersBooleanProperty() {
        Map<Route, BooleanProperty> result = new HashMap<>();
        for (Route r : ChMap.routes()) {
            result.put(r, new SimpleBooleanProperty(false));
        }
        return result;
    }

    /**
     * a private method to create a map with key of type Card and value of type IntegerProperty
     *
     * @return a map with key of type Card and value of type IntegerProperty
     */
    private static Map<Card, IntegerProperty> createCurrentPlayerCardsTypeCount() {
        Map<Card, IntegerProperty> result = new HashMap<>();
        for (Card c : Card.ALL) {
            result.put(c, new SimpleIntegerProperty(0));
        }
        return result;
    }

    /**
     * a private method to create a map with key of type PlayerId and value of type IntegerProperty
     *
     * @return a map with key of type PlayerId and value of type IntegerProperty
     */
    private static Map<PlayerId, IntegerProperty> createPlayersIntegerProperty() {
        Map<PlayerId, IntegerProperty> result = new EnumMap<>(PlayerId.class);
        result.put(PlayerId.PLAYER_1, new SimpleIntegerProperty(0));
        result.put(PlayerId.PLAYER_2, new SimpleIntegerProperty(0));
        return result;
    }

    /**
     * public constructor
     *
     * @param playerId player Id
     */
    public ObservableGameState(PlayerId playerId) {
        this.playerId = playerId;
        publicGameState = null;
        playerState = null;

        //first group
        faceUpCards = createFaceUpCards();
        ticketsPercentage = new SimpleIntegerProperty(0);
        cardsPercentage = new SimpleIntegerProperty(0);
        routesPossession = createPlayersObjectProperty();
        longestRouteProperty = createPlayersObjectProperty();
        theGameEnded = new SimpleBooleanProperty(false);

        //second group
        playersTicketCount = createPlayersIntegerProperty();
        playersCardCount = createPlayersIntegerProperty();
        playersCarCount = createPlayersIntegerProperty();
        playersConstructionPoints = createPlayersIntegerProperty();

        //third group
        currentPlayerTicketList = FXCollections.observableArrayList();
        currentPlayerCardsTypeCount = createCurrentPlayerCardsTypeCount();
        currentPlayerCanClaimRoute = createPlayersBooleanProperty();

    }

    /**
     * sets the observable game state
     *
     * @param newGameState new Game State
     * @param playerState  player State
     */
    public void setState(PublicGameState newGameState, PlayerState playerState) {

        this.publicGameState = newGameState;
        this.playerState = playerState;

        //first group
        ticketsPercentage.set((int) (newGameState.ticketsCount() * 100.0 / ChMap.tickets().size()));
        cardsPercentage.set((int) (newGameState.cardState().deckSize() * 100.0 / ALL_CARDS.size()));

        for (int slot : FACE_UP_CARD_SLOTS) {
            Card newCard = newGameState.cardState().faceUpCard(slot);
            faceUpCards.get(slot).set(newCard);
        }

        for (Route r : ChMap.routes()) {
            for (PlayerId p : PlayerId.ALL) {
                if (newGameState.playerState(p).routes().contains(r))
                    routesPossession.get(r).set(p);
            }
        }

        theGameEnded.set(publicGameState.isTheGameEnded());

        //second group
        for (PlayerId p : PlayerId.ALL) {
            playersTicketCount.get(p).set(newGameState.playerState(p).ticketCount());
            playersCardCount.get(p).set(newGameState.playerState(p).cardCount());
            playersCarCount.get(p).set(newGameState.playerState(p).carCount());
            playersConstructionPoints.get(p).set(newGameState.playerState(p).claimPoints());
        }

        //third group
        List<TicketDecorator> ticketDecorators = new ArrayList<>();
        for (Ticket t : playerState.tickets()) {
            ticketDecorators.add(new TicketDecorator(t));
        }
        currentPlayerTicketList.setAll(ticketDecorators);

        for (Card c : Card.ALL) {
            currentPlayerCardsTypeCount.get(c).set(playerState.cards().countOf(c));
        }

        Set<List<Station>> stations = new HashSet<>();

        for (Route r : ChMap.routes()) {
            if (routesPossession.get(r).get() != null) {
                stations.add(r.stations());
            }
        }

        for (Route r : ChMap.routes()) {
            currentPlayerCanClaimRoute.get(r)
                    .set((playerState.canClaimRoute(r)) &&
                            (newGameState.currentPlayerId() == playerId) &&
                            !(stations.contains(r.stations())));
        }

    }

    /**
     * returns the ReadOnlyObjectProperty<Card> faceUpCard depending on the slot
     *
     * @param slot slot
     * @return the ReadOnlyObjectProperty<Card> faceUpCard depending on the slot
     */
    public ReadOnlyObjectProperty<Card> faceUpCard(int slot) {
        return faceUpCards.get(slot);
    }

    /**
     * returns the ReadOnlyIntegerProperty ticketsPercentage
     *
     * @return the ReadOnlyIntegerProperty ticketsPercentage
     */
    public ReadOnlyIntegerProperty ticketsPercentage() {
        return ticketsPercentage;
    }

    /**
     * returns the ReadOnlyIntegerProperty cardsPercentage
     *
     * @return the ReadOnlyIntegerProperty cardsPercentage
     */
    public ReadOnlyIntegerProperty cardsPercentage() {
        return cardsPercentage;
    }

    /**
     * returns the ReadOnlyObjectProperty<PlayerId> depending on the route
     *
     * @param r the route
     * @return the ReadOnlyObjectProperty<PlayerId> depending on the route
     */
    public ReadOnlyObjectProperty<PlayerId> routesPossession(Route r) {
        return routesPossession.get(r);
    }

    /**
     * returns the ReadOnlyIntegerProperty playerTicketCount depending on the PlayerId
     *
     * @param id PlayerId
     * @return the ReadOnlyIntegerProperty playerTicketCount depending on the PlayerId
     */
    public ReadOnlyIntegerProperty playerTicketCount(PlayerId id) {
        return playersTicketCount.get(id);
    }

    /**
     * returns the ReadOnlyIntegerProperty playerCardCount depending on the PlayerId
     *
     * @param id PlayerId
     * @return the ReadOnlyIntegerProperty playerCardCount depending on the PlayerId
     */
    public ReadOnlyIntegerProperty playerCardCount(PlayerId id) {
        return playersCardCount.get(id);
    }

    /**
     * returns the ReadOnlyIntegerProperty playerCarCount depending on the PlayerId
     *
     * @param id PlayerId
     * @return theReadOnlyIntegerProperty playerCarCount depending on the PlayerId
     */
    public ReadOnlyIntegerProperty playerCarCount(PlayerId id) {
        return playersCarCount.get(id);
    }

    /**
     * returns the ReadOnlyIntegerProperty playerConstructionPoints depending on the PlayerId
     *
     * @param id PlayerId
     * @return theReadOnlyIntegerProperty playerConstructionPoints depending on the PlayerId
     */
    public ReadOnlyIntegerProperty playerConstructionPoints(PlayerId id) {
        return playersConstructionPoints.get(id);
    }

    /**
     * returns the ObservableList<Ticket> currentPlayerTicketListProperty
     *
     * @return the ObservableList<Ticket> currentPlayerTicketListProperty
     */
    public ObservableList<TicketDecorator> currentPlayerTicketListProperty() {
        return FXCollections.unmodifiableObservableList(currentPlayerTicketList);
    }

    /**
     * returns the ReadOnlyIntegerProperty currentPlayerCardsTypeCount depending on the card
     *
     * @param c card
     * @return the ReadOnlyIntegerProperty currentPlayerCardsTypeCount depending on the card
     */
    public ReadOnlyIntegerProperty currentPlayerCardsTypeCount(Card c) {
        return currentPlayerCardsTypeCount.get(c);
    }

    /**
     * returns the ReadOnlyBooleanProperty CurrentPlayerCanClaimRoute depending on the route
     *
     * @param r route
     * @return the ReadOnlyBooleanProperty CurrentPlayerCanClaimRoute depending on the route
     */
    public ReadOnlyBooleanProperty currentPlayerCanClaimRoute(Route r) {
        return currentPlayerCanClaimRoute.get(r);
    }

    /**
     * returns true if the player can draw tickets
     *
     * @return true if the player can draw tickets
     */
    public boolean canDrawTickets() {
        return publicGameState.canDrawTickets();
    }

    /**
     * returns true if the player can draw cards
     *
     * @return true if the player can draw cards
     */
    public boolean canDrawCards() {
        return publicGameState.canDrawCards();
    }

    /**
     * returns all possible claim cards  depending on the route
     *
     * @param route route
     * @return all possible claim cards  depending on the route
     */
    public List<SortedBag<Card>> possibleClaimCards(Route route) {
        return playerState.possibleClaimCards(route);
    }

    /**
     * returns the readOnlyProperty<PlayerId> depending on the road r
     *
     * @param r the road
     * @return the readOnlyProperty<PlayerId> depending on the road r
     */
    public ReadOnlyObjectProperty<PlayerId> longestRouteProperty(Route r) {
        return longestRouteProperty.get(r);
    }

    /**
     * sets the objectProperty<PlayerId> corresponding to the road r
     *
     * @param r the road
     * @param playerId the id of the player
     */
    public void setLongestRouteProperty(Route r, PlayerId playerId) {
        longestRouteProperty.get(r).set(playerId);
    }

    /**
     * the ReadOnlyBooleanProperty that is true only if the game ended
     *
     * @return ReadOnlyBooleanProperty that is true only if the game ended
     */
    public ReadOnlyBooleanProperty theGameEnded() {
        return theGameEnded;
    }

}
