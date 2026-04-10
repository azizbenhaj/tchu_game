package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public final class PlayerTest implements Player {
    private static final int TURN_LIMIT = 1000;

    private final Random rng;
    // Toutes les routes de la carte
    private final List<Route> allRoutes;

    private int turnCounter;
    private PlayerState ownState;
    private PublicGameState gameState;

    private PlayerId playerId;
    private Map<PlayerId, String> playerNamess;

    // Lorsque nextTurn retourne CLAIM_ROUTE
    private Route routeToClaim;
    private SortedBag<Card> initialClaimCards;
    private SortedBag<Ticket> initialTickets;

    public PlayerTest(long randomSeed, List<Route> allRoutes) {
        this.rng = new Random(randomSeed);
        this.allRoutes = List.copyOf(allRoutes);
        this.turnCounter = 0;
        // this.ownState=new PlayerState(SortedBag.of(),SortedBag.of(),List.of());
    }

    public PlayerState getState() {
        return ownState;
    }

    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        this.gameState = newState;
        this.ownState = ownState;
    }

    @Override
    public TurnKind nextTurn() {
        turnCounter += 1;
        if (turnCounter > TURN_LIMIT)
            throw new Error("Trop de tours joués !");

        // Détermine les routes dont ce joueur peut s'emparer
        List<Route> claimableRoutes = new ArrayList<>();
        List<Route> availableRoutes = new ArrayList<>(ChMap.routes());
        availableRoutes.removeAll(gameState.claimedRoutes());


        for (Route r : availableRoutes) {
            if (ownState.canClaimRoute(r)) {
                claimableRoutes.add(r);
            }
        }
        System.out.println("claimable routes count " + claimableRoutes.size());
        System.out.println("all routes " + ChMap.routes().size() + ", all claimed routes " + gameState.claimedRoutes().size() + " ,available routes " + availableRoutes.size());
        if (claimableRoutes.isEmpty()) {
            return TurnKind.DRAW_CARDS;
        } else {
            return TurnKind.CLAIM_ROUTE;
        }
    }

    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        playerId = ownId;
        playerNamess = playerNames;
    }

    @Override
    public void receiveInfo(String info) {
        System.out.println(info);
    }

    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        initialTickets = tickets;
    }

    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        Random rng = new Random();
        int i = rng.nextInt(3);
        SortedBag<Ticket> sb = SortedBag.of();
        for (int j = 0; j < i + 3; j++) {
            sb = sb.union(SortedBag.of(initialTickets.get(j)));
        }
        ownState = new PlayerState(sb, ownState.cards(), ownState.routes());
        return sb;
    }

    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        Random rng = new Random();
        int i = rng.nextInt(3);
        SortedBag<Ticket> sb = SortedBag.of();
        for (int j = 0; j < i + 1; j++) {
            sb = sb.union(SortedBag.of(options.get(j)));
        }
        ownState = new PlayerState(sb.union(ownState.tickets()), ownState.cards(), ownState.routes());
        return sb;
    }
    /////////
    @Override
    public GameState.PlayerRestartResponse gameOver(String info) {
        return null;
    }

    @Override
    public int drawSlot() {
        Random rng = new Random();
        int i = rng.nextInt(10);
        if (i > 4) return -1;
        return i;
    }

    @Override
    public Route claimedRoute() {
        List<Route> claimableRoutes = new ArrayList<>();
        List<Route> availableRoutes = new ArrayList<>(ChMap.routes());
        availableRoutes.removeAll(gameState.claimedRoutes());
        for (Route r : availableRoutes) {
            if (ownState.canClaimRoute(r)) {
                claimableRoutes.add(r);
            }
        }
        int routeIndex = rng.nextInt(claimableRoutes.size());
        Route route = claimableRoutes.get(routeIndex);
        List<SortedBag<Card>> cards = ownState.possibleClaimCards(route);

        routeToClaim = route;
        initialClaimCards = cards.get(0);
        return route;
    }

    @Override
    public SortedBag<Card> initialClaimCards() {
        return initialClaimCards;
    }

    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        if (options.isEmpty()) return null;
        else {
            Random rng = new Random();
            int i = rng.nextInt(options.size());
            return options.get(i);
        }
    }

}