package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static ch.epfl.tchu.game.Constants.IN_GAME_TICKETS_COUNT;

/**
 * @author Ahmed Aziz BEN HAJ (sciper: 310934) HMIDA et Aziz Laadher(sciper: 315196) / le 6 avril 2021
 * + etape intermediaire le 13 avril 2021
 * représente une partie de tCHu
 */
public final class Game {
    static private BlockingQueue<GameState.PlayerRestartResponse> threadReplay;
    static GameState game;
    private Game() {
    }
    /**
     methode pour eviter l'appel repetitif de receiveInfo() pour les 2 joueurs
     */
     static void receiveInfo(Map<PlayerId, Player> players, String info) {
        players.forEach((k, v) ->
            v.receiveInfo(info)
        );
    }

    /**
     methode pour eviter l'appel repetitif de updateState() pour les 2 joueurs
     */
     static void updateState(Map<PlayerId, Player> players, GameState game) {
        players.get(PlayerId.PLAYER_1).updateState(game, game.playerState(PlayerId.PLAYER_1));
        players.get(PlayerId.PLAYER_2).updateState(game, game.playerState(PlayerId.PLAYER_2));
    }

    /**
     * fait jouer une partie de tCHu aux joueurs
     * @param players     joueurs donnés du jeu
     * @param playerNames noms des joueurs donnés
     * @param tickets     les tickets du jeu
     * @param rng         instance aliatoire de type random
     * lève IllegalArgumentException si l'une des deux tables associatives a une taille différente de 2
     */
    public static void play(Map<PlayerId, Player> players, Map<PlayerId, String> playerNames, SortedBag<Ticket> tickets, Random rng) {
        int m = 0;
        if (players.size() != 2 || playerNames.size() != 2) throw new IllegalArgumentException();
        threadReplay = new ArrayBlockingQueue<>(2);
        players.get(PlayerId.PLAYER_1).initPlayers(PlayerId.PLAYER_1, playerNames);
        players.get(PlayerId.PLAYER_2).initPlayers(PlayerId.PLAYER_2, playerNames);
        Game.game = GameState.initial(tickets, rng);
        Info info1 = new Info(playerNames.get(Game.game.currentPlayerId()));
        Info info2 = new Info(playerNames.get(Game.game.currentPlayerId().next()));
        receiveInfo(players, info1.willPlayFirst());
        players.get(PlayerId.PLAYER_1).setInitialTicketChoice(Game.game.topTickets(Constants.INITIAL_TICKETS_COUNT));
        Game.game = Game.game.withoutTopTickets(Constants.INITIAL_TICKETS_COUNT);
        players.get(PlayerId.PLAYER_2).setInitialTicketChoice(Game.game.topTickets(Constants.INITIAL_TICKETS_COUNT));
        Game.game = Game.game.withoutTopTickets(Constants.INITIAL_TICKETS_COUNT);
        updateState(players, game);
        game = game.withInitiallyChosenTickets(PlayerId.PLAYER_1, players.get(PlayerId.PLAYER_1).chooseInitialTickets());
        game = game.withInitiallyChosenTickets(PlayerId.PLAYER_2, players.get(PlayerId.PLAYER_2).chooseInitialTickets());
        receiveInfo(players, info1.keptTickets(Game.game.currentPlayerState().ticketCount()));
        receiveInfo(players, info2.keptTickets(Game.game.playerState(Game.game.currentPlayerId().next()).ticketCount()));

        boolean gameHasNotEnded = true;
        int j = -1;
        while (gameHasNotEnded && game.aPlayerHasSurrendered() == null) {
            updateState(players, game);
            if (game.lastTurnBegins() && j < 0) {
                j = m + 2;
            }
            if (m >= 1) {
                game = game.forNextTurn();
            }
            m++;
            if (j > 0 && m == j)
                gameHasNotEnded = false;
            PlayerId pId =Game.game.currentPlayerId();
            Info info3 = new Info(playerNames.get(pId));
            updateState(players, game);
            receiveInfo(players, info3.canPlay());
            Player.TurnKind aux = players.get(pId).nextTurn();
            switch (aux) {
                case SURRENDER: {
                    game.withPlayerSurrendered();
                    receiveInfo(players, new Info(playerNames.get(game.currentPlayerId())).surrender());
                    break;
                }
                case DRAW_TICKETS: {

                    SortedBag<Ticket> ticketss = game.topTickets(IN_GAME_TICKETS_COUNT);
                    SortedBag<Ticket> tic = players.get(pId).chooseTickets(ticketss);
                    game=game.withChosenAdditionalTickets(ticketss, tic);
                    receiveInfo(players, info3.drewTickets(IN_GAME_TICKETS_COUNT));
                    receiveInfo(players, info3.keptTickets(tic.size()));
                    updateState(players, game);
                    break;
                }
                case DRAW_CARDS: {
                    game = game.withCardsDeckRecreatedIfNeeded(rng);
                    int w = players.get(Game.game.currentPlayerId()).drawSlot();
                    if (w == Constants.DECK_SLOT) {
                        game = game.withBlindlyDrawnCard();
                        receiveInfo(players, info3.drewBlindCard());
                    } else {
                        receiveInfo(players, info3.drewVisibleCard(game.cardState().faceUpCard(w)));
                        game = game.withDrawnFaceUpCard(w);
                    }
                    updateState(players, game);
                    game = game.withCardsDeckRecreatedIfNeeded(rng);
                    int x = players.get(pId).drawSlot();
                    if (x == Constants.DECK_SLOT) {
                        game = game.withBlindlyDrawnCard();
                        receiveInfo(players, info3.drewBlindCard());
                    } else {
                        receiveInfo(players, info3.drewVisibleCard(game.cardState().faceUpCard(x)));
                        game = game.withDrawnFaceUpCard(x);
                    }
                    break;
                }
                case CLAIM_ROUTE: {
                    Route route = players.get(pId).claimedRoute();
                    SortedBag<Card> cards = players.get(Game.game.currentPlayerId()).initialClaimCards();
                    if (route.level() == Route.Level.UNDERGROUND) {
                        SortedBag<Card> sb = SortedBag.of();
                        game = game.withCardsDeckRecreatedIfNeeded(rng);
                        sb = sb.union(SortedBag.of(Game.game.topCard()));
                        Game.game = Game.game.withoutTopCard();
                        game = game.withCardsDeckRecreatedIfNeeded(rng);
                        sb = sb.union(SortedBag.of(Game.game.topCard()));
                        Game.game = Game.game.withoutTopCard();
                        game = game.withCardsDeckRecreatedIfNeeded(rng);
                        sb = sb.union(SortedBag.of(Game.game.topCard()));
                        Game.game = Game.game.withoutTopCard();
                        int k = route.additionalClaimCardsCount(cards, sb);
                        receiveInfo(players, info3.attemptsTunnelClaim(route, cards));
                        receiveInfo(players, info3.drewAdditionalCards(sb, k));
                        if (k >= 1) {

                            List<SortedBag<Card>> sb1 = Game.game.currentPlayerState().possibleAdditionalCards(k, cards);
                            if (!sb1.isEmpty()) {

                                SortedBag<Card> crd = players.get(Game.game.currentPlayerId()).chooseAdditionalCards(sb1);
                                if (crd.isEmpty()) {
                                    game = game.withMoreDiscardedCards(sb);
                                    receiveInfo(players, info3.didNotClaimRoute(route));
                                } else {
                                    game = game.withClaimedRoute(route, cards.union(crd));
                                    game = game.withMoreDiscardedCards(sb);
                                    receiveInfo(players, info3.claimedRoute(route, cards));
                                }
                            } else {
                                game = game.withMoreDiscardedCards(sb);
                                receiveInfo(players, info3.didNotClaimRoute(route));
                            }
                        } else {
                            game = game.withClaimedRoute(route, cards);
                            game = game.withMoreDiscardedCards(sb);
                            receiveInfo(players, info3.claimedRoute(route, cards));
                        }
                    } else {
                        game = game.withClaimedRoute(route, cards);
                        receiveInfo(players, info3.claimedRoute(route, cards));
                    }
                    break;
                }

            }
            if (game.lastTurnBegins() && j < 0) {
                receiveInfo(players, info3.lastTurnBegins(game.currentPlayerState().carCount()));
            }
        }
        updateState(players, game);
        Info info5 = new Info(playerNames.get(Game.game.currentPlayerId()));
        Info info6 = new Info(playerNames.get(Game.game.currentPlayerId().next()));

        Trail trail1 = Trail.longest(game.currentPlayerState().routes());
        Trail trail2 = Trail.longest(game.playerState(game.currentPlayerId().next()).routes());

        int points1 = game.playerState(game.currentPlayerId()).finalPoints();
        int points2 = game.playerState(game.currentPlayerId().next()).finalPoints();
        if (trail1.length() > trail2.length()) {
            receiveInfo(players, info5.getsLongestTrailBonus(trail1));
            points1 += 10;
        }
        if (trail1.length() < trail2.length()) {
            receiveInfo(players, info6.getsLongestTrailBonus(trail2));
            points2 += 10;
        }
        if (trail1.length() == trail2.length()) {
            receiveInfo(players, info5.getsLongestTrailBonus(trail1));
            receiveInfo(players, info6.getsLongestTrailBonus(trail2));
            points1 += Constants.LONGEST_TRAIL_BONUS_POINTS;
            points2 += Constants.LONGEST_TRAIL_BONUS_POINTS;
        }
        final int a = points1;
        final int b = points2;
        updateState(players, game);
        if (points1 > points2) {
            receiveInfo(players, info5.won(points1, points2));
            new Thread(() -> {
                try {
                    threadReplay.put(players.get(PlayerId.PLAYER_1).gameOver(info5.won(a, b)));
                } catch (InterruptedException e) {
                    throw new Error();
                }
            }).start();
            new Thread(() -> {
                try {
                    threadReplay.put(players.get(PlayerId.PLAYER_2).gameOver(info5.won(a, b)));
                } catch (InterruptedException e) {
                    throw new Error();
                }
            }).start();
        }
        if (points2 > points1) {
            receiveInfo(players, info6.won(points2, points1));
            new Thread(() -> {
                try {
                    threadReplay.put(players.get(PlayerId.PLAYER_1).gameOver(info5.won(b, a)));
                } catch (InterruptedException e) {
                    throw new Error();
                }
            }).start();

            new Thread(() -> {
                try {
                    threadReplay.put(players.get(PlayerId.PLAYER_2).gameOver(info5.won(b, a)));
                } catch (InterruptedException e) {
                    throw new Error();
                }
            }).start();
        }
        if (points1 == points2) {
            receiveInfo(players, Info.draw(List.of(playerNames.get(PlayerId.PLAYER_1), playerNames.get(PlayerId.PLAYER_2)), points1));
            new Thread(() -> {
                try {
                    threadReplay.put(players.get(PlayerId.PLAYER_1).gameOver(Info.draw(List.of(PlayerId.PLAYER_1.name(),PlayerId.PLAYER_2.name()),a)));
                } catch (InterruptedException e) {
                    throw new Error();
                }
            }).start();

            new Thread(() -> {
                try {
                    threadReplay.put(players.get(PlayerId.PLAYER_2).gameOver(Info.draw(List.of(PlayerId.PLAYER_1.name(),PlayerId.PLAYER_2.name()),a)));
                } catch (InterruptedException e) {
                    throw new Error();
                }
            }).start();
        }
        try {
            GameState.PlayerRestartResponse playerRestartResponse1 = threadReplay.take();
            GameState.PlayerRestartResponse playerRestartResponse2 = threadReplay.take();
            if (playerRestartResponse1 == GameState.PlayerRestartResponse.WANTS_TO_REPLAY && playerRestartResponse2 == GameState.PlayerRestartResponse.WANTS_TO_REPLAY) {
                Game.play(players, playerNames, tickets, rng);
            }
        } catch (InterruptedException e) {
            throw new Error();
        }
    }
}