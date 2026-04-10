package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Ahmed Aziz BEN HAJ (sciper: 310934) HMIDA et Aziz Laadher(sciper: 315196) / le 4 mai 2021
 * représente l'état observable d'une partie de tCH
 */
public class ObservableGameState {
    private  PlayerState playerState;
    private  final PlayerId ownId;
    private final BooleanProperty canDrawTickets;
    private final BooleanProperty canDrawCards;
    private final ObjectProperty<List<SortedBag<Card>>> possibleClaimCards;
    private final IntegerProperty remainingTickets;
    private final IntegerProperty remainingCards;
    private final List<ObjectProperty<Card>> faceUpCards;
    private final ObservableMap<Route, SimpleObjectProperty<PlayerId>> routesOwning;

    private final IntegerProperty ticketsInHand1;
    private final IntegerProperty cardsInHand1;
    private final IntegerProperty carsInHand1;
    private final IntegerProperty points1;

    private final IntegerProperty ticketsInHand2;
    private final IntegerProperty cardsInHand2;
    private final IntegerProperty carsInHand2;
    private final IntegerProperty points2;

    private final ObservableList<Ticket> tickets;
    private final ObservableList<IntegerProperty> cardsCount;
    private final ObservableMap<Route, BooleanProperty> routesAvailability;

    /**
     * constructeur d'ObservableGameState qui prend un seul argument
     * @param playerId l'identité du joueur
     */
    public ObservableGameState(PlayerId playerId) {
        this.ownId = playerId;
        this.playerState = null;

        // initializing properties
        canDrawTickets = new SimpleBooleanProperty(false);
        canDrawCards = new SimpleBooleanProperty(false);
        possibleClaimCards = new SimpleObjectProperty<>(null);
        remainingTickets = new SimpleIntegerProperty(0);
        remainingCards = new SimpleIntegerProperty(0);
        faceUpCards = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            faceUpCards.add(0, new SimpleObjectProperty<>(null));
        }
        routesOwning = FXCollections.observableMap(new HashMap<>());

        ticketsInHand1 = new SimpleIntegerProperty(0);
        cardsInHand1 = new SimpleIntegerProperty(0);
        carsInHand1 = new SimpleIntegerProperty(0);
        points1 = new SimpleIntegerProperty(0);

        ticketsInHand2 = new SimpleIntegerProperty(0);
        cardsInHand2 = new SimpleIntegerProperty(0);
        carsInHand2 = new SimpleIntegerProperty(0);
        points2 = new SimpleIntegerProperty(0);

        tickets = FXCollections.observableList(new ArrayList<>());
        cardsCount = FXCollections.observableArrayList();
        for (int i = 0; i < Card.ALL.size(); i++) {
            cardsCount.add(new SimpleIntegerProperty(0));
        }
        routesAvailability = FXCollections.observableMap(new HashMap<>());
        for (Route route : ChMap.routes()) {
            routesAvailability.put(route, new SimpleBooleanProperty(false));
            routesOwning.put(route, new SimpleObjectProperty<>(null));
        }
    }

    /**
     * permet d'obtenir la propriété booléenne associée à la route et ne contenant vrai
     * que si le joueur peut actuellement s'en emparer
     * @param route route associée
     * @return la propriété booléenne associée à la route
     */
    public BooleanProperty claimable(Route route) {
        return routesAvailability.get(route);
    }

    /**
     * met à jour la totalité des propriétés décrites ci-dessous en fonction de ces deux états
     * @param gameState   la partie publique du jeu
     * @param playerState l'état complet du joueur auquel elle correspond
     */
    public void setState(PublicGameState gameState, PlayerState playerState) {
        this.playerState = playerState;

        // tickets & cards update
        canDrawTickets.set(gameState.canDrawTickets());
        canDrawCards.set(gameState.canDrawCards());
        remainingTickets.set(gameState.ticketsCount()*100/46);
        remainingCards.set(gameState.cardState().deckSize()*100/110);

        // Updating face up cards
        for (int i = 0; i < gameState.cardState().faceUpCards().size(); i++) {
            faceUpCards.get(i).set(gameState.cardState().faceUpCard(i));
        }
        for (int i = 0; i < gameState.claimedRoutes().size(); i++) {
            routesOwning.get(gameState.claimedRoutes().get(i)).set(gameState.playerState(PlayerId.PLAYER_1).routes()
                    .contains(gameState.claimedRoutes().get(i)) ? PlayerId.PLAYER_1 : PlayerId.PLAYER_2); }

        // tickets count, cards count, cars count and points
        ticketsInHand1.set(gameState.playerState(PlayerId.PLAYER_1).ticketCount());
        cardsInHand1.set(gameState.playerState(PlayerId.PLAYER_1).cardCount());
        carsInHand1.set(gameState.playerState(PlayerId.PLAYER_1).carCount());
        points1.set(gameState.playerState(PlayerId.PLAYER_1).claimPoints());
        ticketsInHand2.set(gameState.playerState(PlayerId.PLAYER_2).ticketCount());
        cardsInHand2.set(gameState.playerState(PlayerId.PLAYER_2).cardCount());
        carsInHand2.set(gameState.playerState(PlayerId.PLAYER_2).carCount());
        points2.set(gameState.playerState(PlayerId.PLAYER_2).claimPoints());
        tickets.clear();
        tickets.addAll(playerState.tickets().toList());
        for (int i = 0; i < Card.ALL.size(); i++) {
            cardsCount.get(i).set(playerState.cards().countOf(Card.ALL.get(i)));
        }
        if (gameState.currentPlayerId() != ownId) {
            routesAvailability.forEach((route, isAvailable) ->
                routesAvailability.get(route).setValue(false)
            );
        } else {
            ArrayList<Route> availableRoutes = new ArrayList<>(ChMap.routes());
            availableRoutes.removeAll(gameState.claimedRoutes());
            for (Route rc : gameState.claimedRoutes()) {
                availableRoutes.removeIf(av -> (av.station1().id() == rc.station2().id() && av.station2().id() == rc.station1().id())
                        ||(av.station1().id() == rc.station1().id() && av.station2().id() == rc.station2().id())); }
            availableRoutes.forEach(route ->
                routesAvailability.get(route).set(playerState.canClaimRoute(route))
            );
        }
    }

    /**
     * getter du ownId
     * @return ownId
     */
    public PlayerId getOwnId() {
        return ownId;
    }

    /**
     * getter de remainingTickets
     * @return remainingTickets
     */
    public ReadOnlyIntegerProperty getRemainingTickets() {
        return remainingTickets;
    }

    /**
     * getter de remainingCards
     * @return remainingCards
     */
    public ReadOnlyIntegerProperty getRemainingCards() {
        return remainingCards;
    }

    /**
     * getter de faceUpCards
     * @return faceUpCards
     */
    public List<ObjectProperty<Card>> getFaceUpCards() {
        return faceUpCards;
    }

    /**
     * getter de routesOwning
     * @return routesOwning
     */
    public ObservableMap<Route, SimpleObjectProperty<PlayerId>> getRoutesOwning() {
        return routesOwning;
    }

    /**
     * getter de la carte d'indice slot de faceUpCards
     * @param slot indice de la carte
     * @return la carte d'indice slot de faceUpCards
     */
    public ReadOnlyObjectProperty<Card> faceUpCard(int slot) {
        return faceUpCards.get(slot);
    }

    /**
     * getter de ticketsInHand du premier joueur
     * @return ticketsInHand1
     */
    public ReadOnlyIntegerProperty getTicketsInHand1() {
        return ticketsInHand1;
    }

    /**
     * getter de cardsInHand du premier joueur
     * @return cardsInHand1
     */
    public ReadOnlyIntegerProperty getCardsInHand1() {
        return cardsInHand1;
    }

    /**
     * getter de carsInHand du premier joueur
     * @return carsInHand1
     */
    public ReadOnlyIntegerProperty getCarsInHand1() {
        return carsInHand1;
    }

    /**
     * getter de points du premier joueur
     * @return points1
     */
    public ReadOnlyIntegerProperty getPoints1() {
        return points1;
    }

    /**
     * getter de ticketsInHand du second joueur
     * @return ticketsInHand2
     */
    public ReadOnlyIntegerProperty getTicketsInHand2() {
        return ticketsInHand2;
    }

    /**
     * getter de cardsInHand du second joueur
     * @return cardsInHand2
     */
    public ReadOnlyIntegerProperty getCardsInHand2() {
        return cardsInHand2;
    }

    /**
     * getter de carsInHand du second joueur
     * @return carsInHand2
     */
    public ReadOnlyIntegerProperty getCarsInHand2() {
        return carsInHand2;
    }

    /**
     * getter de points du second joueur
     * @return points2
     */
    public ReadOnlyIntegerProperty getPoints2() {
        return points2;
    }

    /**
     * getter de canDrawTickets
     * @return canDrawTickets
     */
    public ReadOnlyBooleanProperty canDrawTickets() {
        return canDrawTickets;
    }

    /**
     * getter de canDrawCards
     * @return canDrawCards
     */
    public ReadOnlyBooleanProperty canDrawCards() {
        return canDrawCards;
    }

    /** getter de possibleClaimCards()
     * @param route route à vouloir prendre
     * @return les listes de cartes possibles de l'obtention de la route
     */
    public ReadOnlyObjectProperty<List<SortedBag<Card>>> possibleClaimCards(Route route){
       possibleClaimCards.set(this.playerState.possibleClaimCards(route));
        return possibleClaimCards ;
    }

    /**
     * getter de cardsCount
     * @return cardsCount
     */
    public ObservableList<IntegerProperty> getCardsCount() {
        return cardsCount;
    }

    /**
     * getter de tickets
     * @return tickets
     */
    public ObservableList<Ticket> getTickets() {
        return tickets;
    }

}