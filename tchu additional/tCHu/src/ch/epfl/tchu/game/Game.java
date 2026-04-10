package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;

import java.util.*;

/**
 * Game
 *
 * @author OMAR MEZGHANI (311542)
 * @author MOHAMED CHARFI (311171)
 */
public final class Game {
    private final static int NUMBER_OF_DRAWN_CARDS_PER_TURN = 2;

    /**
     * empty private constructor
     */
    private Game() {
    }

    /**
     * gives informations to both players
     *
     * @param players a map of players
     * @param info    a string
     */
    private static void receiveInfo(Map<PlayerId, Player> players, String info) {
        players.forEach((k, v) -> v.receiveInfo(info));
    }

    /**
     * updates the state of both players
     *
     * @param players   a map of players
     * @param gameState a game state
     */
    private static void updateState(Map<PlayerId, Player> players, GameState gameState) {
        players.forEach((k, v) -> v.updateState(gameState, gameState.playerState(k)));
    }


    private static void longest(Map<PlayerId, Player> players, List<Route> trailroutes, PlayerId playerId) {
        players.forEach((k, v) -> v.longest(trailroutes, playerId));
    }

    /**
     * How the game unfolds
     *
     * @param players     a map of players
     * @param playerNames a map of names of players
     * @param tickets     the tickets
     * @param rng         a Random
     * @throws IllegalArgumentException
     * @throws NullPointerException
     */
    public static void play(Map<PlayerId, Player> players, Map<PlayerId, String> playerNames, SortedBag<Ticket> tickets, Random rng) {
        Preconditions.checkArgument(playerNames.size() == PlayerId.COUNT && players.size() == PlayerId.COUNT);

        boolean restart;

        do {
            for (Ticket t:tickets) {
                t.setDone(false);
            }
            restart = false;

            boolean isSurrender = false;
            PlayerId theWinnerIfOtherPlayerSurrendered = null;

            //initPlayers
            players.forEach((k, v) -> v.initPlayers(k, playerNames));

            //initGameState
            GameState gameState = GameState.initial(tickets, rng);

            //updateState
            updateState(players, gameState);

            Map<PlayerId, Info> playerInfos = new EnumMap<>(PlayerId.class);
            playerInfos.put(PlayerId.PLAYER_1, new Info(playerNames.get(PlayerId.PLAYER_1)));
            playerInfos.put(PlayerId.PLAYER_2, new Info(playerNames.get(PlayerId.PLAYER_2)));

            ////receiveInfo
            receiveInfo(players, playerInfos.get(gameState.currentPlayerId()).willPlayFirst());

            //setInitialTicketChoice
            for (Player player : players.values()) {
                player.setInitialTicketChoice(gameState.topTickets(Constants.INITIAL_TICKETS_COUNT));
                gameState = gameState.withoutTopTickets(Constants.INITIAL_TICKETS_COUNT);
            }

            Map<PlayerId, Integer> chosenTicketsCount = new EnumMap<>(PlayerId.class);

            //updateState
            updateState(players, gameState);

            //chooseInitialTickets
            for (Map.Entry<PlayerId, Player> e : players.entrySet()) {
                PlayerId id = e.getKey();
                Player p = e.getValue();
                SortedBag<Ticket> chosenTickets = p.chooseInitialTickets();
                chosenTicketsCount.put(id, chosenTickets.size());
                gameState = gameState.withInitiallyChosenTickets(id, chosenTickets);
            }

            //receive info
            for (Map.Entry<PlayerId, Player> e : players.entrySet()) {
                PlayerId id = e.getKey();
                Player p = e.getValue();
                receiveInfo(players, playerInfos.get(id).keptTickets(chosenTicketsCount.get(id)));
            }

            loop:
            while (true) {
                //update state
                updateState(players, gameState);

                //current player
                Info currentPlayerInfo = playerInfos.get(gameState.currentPlayerId());
                Player currentPlayer = players.get(gameState.currentPlayerId());

                //receive info
                receiveInfo(players, currentPlayerInfo.canPlay());

                //updateState
                updateState(players, gameState);

                //turn kind
                Player.TurnKind turnKind = currentPlayer.nextTurn();

                switch (turnKind) {
                    case DRAW_TICKETS:
                        //receive info
                        receiveInfo(players, currentPlayerInfo.drewTickets(Constants.IN_GAME_TICKETS_COUNT));

                        SortedBag<Ticket> drawnTickets = gameState.topTickets(Constants.IN_GAME_TICKETS_COUNT);
                        SortedBag<Ticket> keptTickets = currentPlayer.chooseTickets(drawnTickets);
                        gameState = gameState.withChosenAdditionalTickets(drawnTickets, keptTickets);

                        //receive info
                        receiveInfo(players, currentPlayerInfo.keptTickets(keptTickets.size()));

                        //updateState
                        updateState(players, gameState);

                        break;

                    case DRAW_CARDS:
                        //updateState
                        updateState(players, gameState);
                        for (int i = 0; i < NUMBER_OF_DRAWN_CARDS_PER_TURN; i++) {
                            int slot = currentPlayer.drawSlot();
                            if (slot == Constants.DECK_SLOT) {
                                //receive info
                                receiveInfo(players, currentPlayerInfo.drewBlindCard());

                                gameState = gameState.withCardsDeckRecreatedIfNeeded(rng).withBlindlyDrawnCard();

                            } else {
                                //receive info
                                receiveInfo(players, currentPlayerInfo.drewVisibleCard(gameState.cardState().faceUpCard(slot)));

                                gameState = gameState.withCardsDeckRecreatedIfNeeded(rng).withDrawnFaceUpCard(slot);
                            }

                            //updateState
                            updateState(players, gameState);

                        }
                        break;

                    case CLAIM_ROUTE:
                        Route claimedRoute = currentPlayer.claimedRoute();
                        SortedBag<Card> initialClaimCards = currentPlayer.initialClaimCards();

                        if (claimedRoute.level() == Route.Level.OVERGROUND) {
                            //receive info
                            receiveInfo(players, currentPlayerInfo.claimedRoute(claimedRoute, initialClaimCards));

                            gameState = gameState.withClaimedRoute(claimedRoute, initialClaimCards);
                        } else {
                            //receive info
                            receiveInfo(players, currentPlayerInfo.attemptsTunnelClaim(claimedRoute, initialClaimCards));

                            SortedBag.Builder<Card> b = new SortedBag.Builder<>();

                            for (int i = 0; i < Constants.ADDITIONAL_TUNNEL_CARDS; i++) {
                                gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
                                b.add(gameState.topCard());
                                gameState = gameState.withoutTopCard();
                            }
                            SortedBag<Card> drawnCards = b.build();
                            int additionalClaimCardsCount = claimedRoute.additionalClaimCardsCount(initialClaimCards, drawnCards);

                            //receive info
                            receiveInfo(players, currentPlayerInfo.drewAdditionalCards(drawnCards, additionalClaimCardsCount));
                            SortedBag<Card> additionalCards;
                            if (additionalClaimCardsCount == 0) {
                                additionalCards = SortedBag.of();
                            } else {
                                List<SortedBag<Card>> possibleAdditionalCards = gameState.currentPlayerState()
                                        .possibleAdditionalCards(additionalClaimCardsCount, initialClaimCards);
                                if (possibleAdditionalCards.isEmpty()) {
                                    additionalCards = SortedBag.of();
                                } else {
                                    additionalCards = currentPlayer
                                            .chooseAdditionalCards(possibleAdditionalCards);
                                }
                            }
                            if (additionalCards.isEmpty() && (additionalClaimCardsCount != 0)) {
                                //receive info
                                receiveInfo(players, currentPlayerInfo.didNotClaimRoute(claimedRoute));
                            } else {
                                //receive info
                                receiveInfo(players, currentPlayerInfo.claimedRoute(claimedRoute, initialClaimCards.union(additionalCards)));

                                gameState = gameState.withClaimedRoute(claimedRoute, initialClaimCards.union(additionalCards));
                            }
                            gameState = gameState.withMoreDiscardedCards(drawnCards);
                        }
                        break;
                    case SURRENDER:
                        //receive info
                        receiveInfo(players, currentPlayerInfo.surrender());
                        isSurrender = true;
                        theWinnerIfOtherPlayerSurrendered = gameState.currentPlayerId().next();
                        gameState.setTheGameEnded(true);
                        break loop;
                }

                if ((gameState.lastPlayer() != null) && (gameState.lastPlayer().equals(gameState.currentPlayerId()))) {
                    gameState.setTheGameEnded(true);
                    break;
                }

                //receive info
                if (gameState.lastTurnBegins()) {
                    receiveInfo(players, currentPlayerInfo.lastTurnBegins(gameState.currentPlayerState().carCount()));
                }

                gameState = gameState.forNextTurn();
            }

            //updateState
            updateState(players, gameState);

            if (isSurrender) {
                receiveInfo(players, playerInfos.get(theWinnerIfOtherPlayerSurrendered).surrenderWin());
                players.get(theWinnerIfOtherPlayerSurrendered).music(true);
                players.get(theWinnerIfOtherPlayerSurrendered.next()).music(false);

            } else {

                Trail player1LongestTrail = Trail.longest(gameState.playerState(PlayerId.PLAYER_1).routes());
                Trail player2LongestTrail = Trail.longest(gameState.playerState(PlayerId.PLAYER_2).routes());
                int player1Points = gameState.playerState(PlayerId.PLAYER_1).finalPoints();
                int player2Points = gameState.playerState(PlayerId.PLAYER_2).finalPoints();

                if (player1LongestTrail.length() > player2LongestTrail.length()) {
                    //receive info
                    receiveInfo(players, playerInfos.get(PlayerId.PLAYER_1).getsLongestTrailBonus(player1LongestTrail));
                    longest(players, player1LongestTrail.getTrailroutes(), PlayerId.PLAYER_1);

                    player1Points += Constants.LONGEST_TRAIL_BONUS_POINTS;
                } else if (player1LongestTrail.length() < player2LongestTrail.length()) {
                    //receive info
                    receiveInfo(players, playerInfos.get(PlayerId.PLAYER_2).getsLongestTrailBonus(player2LongestTrail));
                    longest(players, player2LongestTrail.getTrailroutes(), PlayerId.PLAYER_2);

                    player2Points += Constants.LONGEST_TRAIL_BONUS_POINTS;
                } else {
                    //receive info
                    receiveInfo(players, playerInfos.get(PlayerId.PLAYER_1).getsLongestTrailBonus(player1LongestTrail));
                    receiveInfo(players, playerInfos.get(PlayerId.PLAYER_2).getsLongestTrailBonus(player2LongestTrail));
                    longest(players, player1LongestTrail.getTrailroutes(), PlayerId.PLAYER_1);
                    longest(players, player2LongestTrail.getTrailroutes(), PlayerId.PLAYER_2);

                    player1Points += Constants.LONGEST_TRAIL_BONUS_POINTS;
                    player2Points += Constants.LONGEST_TRAIL_BONUS_POINTS;
                }

                if (player1Points > player2Points) {
                    //receive info
                    receiveInfo(players, playerInfos.get(PlayerId.PLAYER_1).won(player1Points, player2Points));

                    players.get(PlayerId.PLAYER_1).music(true);
                    players.get(PlayerId.PLAYER_2).music(false);

                } else if (player2Points > player1Points) {
                    //receive info
                    receiveInfo(players, playerInfos.get(PlayerId.PLAYER_2).won(player2Points, player1Points));

                    players.get(PlayerId.PLAYER_1).music(false);
                    players.get(PlayerId.PLAYER_2).music(true);
                } else {
                    //receive info
                    receiveInfo(players, Info.draw(List.copyOf(playerNames.values()), player1Points));
                    players.get(PlayerId.PLAYER_1).music(true);
                    players.get(PlayerId.PLAYER_2).music(true);
                }
            }

            if (players.get(PlayerId.PLAYER_1).nextTurn() == Player.TurnKind.RESTART && players.get(PlayerId.PLAYER_2).nextTurn() == Player.TurnKind.RESTART) {
                restart = true;

            }

        } while (restart);
    }
}
