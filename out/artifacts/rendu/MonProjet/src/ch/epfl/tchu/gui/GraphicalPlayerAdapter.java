package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import static javafx.application.Platform.runLater;

/**
 * @author Ahmed Aziz BEN HAJ (sciper: 310934) HMIDA et Aziz Laadher(sciper: 315196) / le 18 mai 2021
 * adapte (au sens du patron Adapter) une instance de
 * GraphicalPlayer en une valeur de type Player
 */
public class GraphicalPlayerAdapter implements Player {

    private GraphicalPlayer graphicalPlayer;
    private final BlockingQueue<SortedBag<Ticket>> threadChooseTickets;
    private final BlockingQueue<Integer> threadDrawSlot;
    private final BlockingQueue<Route> threadRoute;
    private final BlockingQueue<SortedBag<Card>> threadCardsRoute;
    private final BlockingQueue<SortedBag<Card>> threadCards;
    private final BlockingQueue<TurnKind> threadTurnKind;
    private final ActionHandlers.ChooseTicketsHandler chooseTicketsHandler;
    private final BlockingQueue<GameState.PlayerRestartResponse> threadReplay;
    private final MediaPlayer mediaPlayerForStartingTurn;
    private final MediaPlayer mediaPlayerForCardsAndTickets;
    private final MediaPlayer mediaPlayerForRoute;
    /**
     * simple constructeur de la classe
     */
    public GraphicalPlayerAdapter() {
        threadChooseTickets = new ArrayBlockingQueue<>(1);
        threadTurnKind = new ArrayBlockingQueue<>(1);
        threadDrawSlot = new ArrayBlockingQueue<>(1);
        threadRoute = new ArrayBlockingQueue<>(1);
        threadCardsRoute = new ArrayBlockingQueue<>(1);
        threadCards = new ArrayBlockingQueue<>(1);
        chooseTicketsHandler =
                s -> {
                    try {
                        threadChooseTickets.put(s);
                    } catch (InterruptedException e) {
                        throw new Error();
                    }
                };
        threadReplay = new ArrayBlockingQueue<>(1);
        Media media = new Media(new File("/Users/sahbiabderrazak/Desktop/projet BA2/resources/StartingTurn.mp3").toURI().toString());
        mediaPlayerForStartingTurn = new MediaPlayer(media);
        Media media2 = new Media(new File("/Users/sahbiabderrazak/Desktop/projet BA2/resources/DrawCard.mp3").toURI().toString());
        mediaPlayerForCardsAndTickets = new MediaPlayer(media2);
        Media media3 = new Media(new File("/Users/sahbiabderrazak/Desktop/projet BA2/resources/RouteSound.mp3").toURI().toString());
        mediaPlayerForRoute = new MediaPlayer(media3);
    }

    /**
     *  construit, sur le fil JavaFX, l'instance du joueur
     *  graphique GraphicalPlayer qu'elle adapte
     * @param ownId id du joueur
     * @param playerNames les noms des différents joueurs
     */
    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        runLater(() -> graphicalPlayer = new GraphicalPlayer(ownId, playerNames));
    }

    /**
     *vappelle, sur le fil JavaFX,
     * la méthode du même nom du joueur graphique,
     * @param info l'information donnée
     */
    @Override
    public void receiveInfo(String info) {
        runLater(() -> graphicalPlayer.receiveInfo(info));
    }

    /**
     *appelle, sur le fil JavaFX,
     * la méthode setState du joueur graphique
     * @param newState  la composante publiqu de l'état du joueur
     * @param ownState propre etat du joueur
     */
    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        runLater(() -> graphicalPlayer.setState(newState, ownState));
    }

    /**
     * appelle, sur le fil JavaFX, la méthode chooseTickets du joueur graphique,
     * pour lui demander de choisir ses billets initiaux
     * @param tickets 5billets distribués
     */
    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        runLater(() ->
            graphicalPlayer.chooseTickets(tickets, chooseTicketsHandler)
        );
    }

    /**
     *bloque en attendant que la file utilisée également par
     * setInitialTicketChoice contienne une valeur, puis la retourne
     * @return la valeur de la file
     */
    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        try {
            SortedBag<Ticket> tickets = threadChooseTickets.take();
            //nous fait retourner au debut de l'audio
            mediaPlayerForCardsAndTickets.seek(Duration.ZERO);
            mediaPlayerForCardsAndTickets.play();
            return tickets;
        } catch (InterruptedException e) {
            throw new Error();
        }
    }

    /**
     *appelle, sur le fil JavaFX, la méthode startTurn du joueur graphique
     * @return le type de tour
     */
    @Override
    public TurnKind nextTurn() {
        //nous fait retourner au debut de l'audio
        mediaPlayerForStartingTurn.seek(Duration.ZERO);
        mediaPlayerForStartingTurn.play();
        ActionHandlers.DrawTicketsHandler drawTicketsHandler =
                () -> {
                    try {
                        threadTurnKind.put(TurnKind.DRAW_TICKETS);
                    } catch (InterruptedException e) {
                        throw new Error();
                    }
                };
        ActionHandlers.DrawCardHandler drawCardsHandler =
                (i) -> {
                    try {
                        threadDrawSlot.put(i);
                        threadTurnKind.put(TurnKind.DRAW_CARDS);
                    } catch (InterruptedException e) {
                        throw new Error();
                    }
                };
        ActionHandlers.ClaimRouteHandler claimRouteHandler =
                (route, claimCards) -> {
                    try {
                        threadRoute.put(route);
                        threadCardsRoute.put(claimCards);
                        threadTurnKind.put(TurnKind.CLAIM_ROUTE);
                    } catch (InterruptedException e) {
                        throw new Error();
                    }
                };
        ActionHandlers.SurrenderHandler surrenderHandler =
                () -> {
                    try {
                        threadTurnKind.put(TurnKind.SURRENDER);
                    } catch (InterruptedException e) {
                        throw new Error();
                    }
                };
        runLater(() -> graphicalPlayer.startTurn(drawTicketsHandler, drawCardsHandler, claimRouteHandler,surrenderHandler));
        try {
            return threadTurnKind.take();
        } catch (InterruptedException e) {
            throw new Error();
        }
    }

    /**
     *  enchaîne les actions effectuées par setInitialTicketChoice et chooseInitialTickets,
     * @param ts tikets donnés
     * @return le choix de tickets
     */
    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> ts) {
        setInitialTicketChoice(ts);
        return chooseInitialTickets();
    }

    /**
     * teste le numero d'appel de drawSlot
     * @return le numero d'appel de drawSlot
     */
    @Override
    public int drawSlot() {

        ActionHandlers.DrawCardHandler drawCardsHandler = threadDrawSlot::add;
        if (!threadDrawSlot.isEmpty()) {
            int slot = threadDrawSlot.remove();
            //nous fait retourner au debut de l'audio
            mediaPlayerForCardsAndTickets.seek(Duration.ZERO);
            mediaPlayerForCardsAndTickets.play();
            return slot;
        } else {
            runLater(() -> graphicalPlayer.drawCard(drawCardsHandler));
            try {
                int slot = threadDrawSlot.take();
                mediaPlayerForCardsAndTickets.seek(Duration.ZERO);
                mediaPlayerForCardsAndTickets.play();
                return slot;
            } catch (InterruptedException e) {
                throw new Error();
            }
        }
    }

    /**
     * retourne le premier élément de la file contenant les routes,
     * @return le premier élément de la file contenant les routes,
     */
    @Override
    public Route claimedRoute() {
        try {
            Route route = threadRoute.take();
            //nous fait retourner au debut de l'audio
            mediaPlayerForRoute.seek(Duration.ZERO);
            mediaPlayerForRoute.play();
            return route;
        } catch (InterruptedException e) {
            throw new Error();
        }
    }

    /**
     * retourne le premier multiensemble de cartes
     * @return le premier multiensemble de cartes
     */
    @Override
    public SortedBag<Card> initialClaimCards() {
        try {
            return threadCardsRoute.take();
        } catch (InterruptedException e) {
            throw new Error();
        }
    }

    /**
     * appelle, sur le fil JavaFX,
     * la méthode du même nom du joueur graphique
     * @param options les possibilitées des cartes
     * @return le premier multiensemble de cartes
     */
    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        ActionHandlers.ChooseCardsHandler chooseCardsHandler = (cards -> {
            try {
                threadCards.put(cards);
            } catch (InterruptedException e) {
                throw new Error();
            }
        });

        runLater(() -> graphicalPlayer.chooseAdditionalCard(options, chooseCardsHandler));
        try {
            return threadCards.take();
        } catch (InterruptedException e) {
            throw new Error();
        }
    }

    /**
     * appelle, sur le fil JavaFX,
     * la méthode du même nom du joueur graphique
     * @param info les information du fin du jeu
     * @return la decision prise ( premiere valeur du type enum PlayerRestartResponse
     */
    @Override
    public GameState.PlayerRestartResponse gameOver(String info) {
        ActionHandlers.ReplayHandler replayHandler1 = (playerId, playerRestartResponse) -> {
            try {
                threadReplay.put(playerRestartResponse);
            } catch (InterruptedException e) {
                throw new Error();
            }
        };
        runLater(() -> graphicalPlayer.gameover(info, replayHandler1));
        try {
            return threadReplay.take();
        } catch (InterruptedException e) {
            throw new Error();
        }
    }
}


