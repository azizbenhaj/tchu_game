package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Trail;
import java.util.ArrayList;
import java.util.List;
import static ch.epfl.tchu.gui.StringsFr.plural;


/**
 * @author Ahmed Aziz BEN HAJ (sciper: 310934) HMIDA et Aziz Laadher(sciper: 315196) / le 15 mars 2021
 * classe qui permet de générer les textes décrivant le déroulement de la partie
 */
public final class Info {

    private final String playerName;

    public Info(String playerName) {
        this.playerName = playerName;
    }

    /**
     * retourne le nom (français) de la carte donnée
     * @param card  carte donnée
     * @param count nombre de cette carte donnée
     * @return le nom (français) de la carte donnée
     */
    public static String cardName(Card card, int count) {
        String s = "";
        switch (card) {
            case RED: {
                s += StringsFr.RED_CARD;
                break;
            }
            case BLACK: {
                s += StringsFr.BLACK_CARD;
                break;
            }
            case BLUE: {
                s += StringsFr.BLUE_CARD;
                break;
            }
            case ORANGE: {
                s += StringsFr.ORANGE_CARD;
                break;
            }
            case GREEN: {
                s += StringsFr.GREEN_CARD;
                break;
            }
            case YELLOW: {
                s += StringsFr.YELLOW_CARD;
                break;
            }
            case WHITE: {
                s += StringsFr.WHITE_CARD;
                break;
            }
            case VIOLET: {
                s += StringsFr.VIOLET_CARD;
                break;
            }
            case LOCOMOTIVE:{
                s += StringsFr.LOCOMOTIVE_CARD;
                break;
            }
            case JOKER: {
                s += StringsFr.JOKER_CARD;
                break;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
        s += plural(count);
        return s;
    }

    /**
     * retourne le message déclarant que les joueurs, dont les noms sont ceux donnés,
     * ont terminé la partie ex æqo en ayant chacun remporté les points donnés
     * @param playerNames liste des joueurs
     * @param points      nombre de points remportés
     * @return le message déclarant que les joueurs, dont les noms sont ceux donnés,
     * ont terminé la partie ex æqo en ayant chacun remporté les points donnés
     */
    public static String draw(List<String> playerNames, int points) {
        List<String> playerNames1 = playerNames.subList(0, playerNames.size() - 1);
        String d = String.join(", ", playerNames1);
        d += StringsFr.AND_SEPARATOR;
        d += playerNames.get(playerNames.size() - 1);
        return String.format(StringsFr.DRAW, d, points);
    }

    /**
     * retourne le message déclarant que le joueur jouera en premier
     * @return le message déclarant que le joueur jouera en premier
     */
    public String willPlayFirst() {
        return String.format(StringsFr.WILL_PLAY_FIRST, this.playerName);
    }

    /**
     * retourne le message déclarant que le joueur a gardé le nombre de billets donné
     * @param count nombre de billets donné
     * @return le message déclarant que le joueur a gardé le nombre de billets donné
     */
    public String keptTickets(int count) {
        return String.format(StringsFr.KEPT_N_TICKETS, this.playerName, count, plural(count));
    }

    /**
     * retourne le message déclarant que le joueur peut jouer
     * @return le message déclarant que le joueur peut jouer
     */
    public String canPlay() {
        return String.format(StringsFr.CAN_PLAY, this.playerName);
    }

    /**
     * retourne le message déclarant que le joueur a tiré le nombre donné de billets
     * @param count nombre de billets
     * @return le message déclarant que le joueur a tiré le nombre donné de billets
     */
    public String drewTickets(int count) {
        return String.format(StringsFr.DREW_TICKETS, this.playerName, count, plural(count));
    }

    /**
     * retourne le message déclarant que le joueur a tiré une carte «à l'aveugle», c-à-d du sommet de la pioche
     * @return le message déclarant que le joueur a tiré une carte «à l'aveugle», c-à-d du sommet de la pioche
     */
    public String drewBlindCard() {
        return String.format(StringsFr.DREW_BLIND_CARD, this.playerName);
    }

    public String surrender() {
        return String.format(StringsFr.SURRENDER, this.playerName);
    }

    /**
     * retourne le message déclarant que le joueur a tiré la carte disposée face visible donnée
     * @param card la carte disposée face visible tirée donnée
     * @return le message déclarant que le joueur a tiré la carte disposée face visible donnée
     */
    public String drewVisibleCard(Card card) {
        return String.format(StringsFr.DREW_VISIBLE_CARD, this.playerName, cardName(card, 1));
    }

    /**
     * retourne le message déclarant que le joueur s'est emparé de la route donnée au moyen des cartes données
     * @param route route prise
     * @param cards cartes permettant de s'emparer de la route
     * @return le message déclarant que le joueur s'est emparé de la route donnée au moyen des cartes données
     */
    public String claimedRoute(Route route, SortedBag<Card> cards) {
        String a = routeText(route.station1().toString(), route.station2().toString());
        return String.format(StringsFr.CLAIMED_ROUTE, this.playerName, a, listCardText(cards));
    }

    /**
     * retourne le message déclarant que le joueur désire s'emparer de la route en tunnel donnée en utilisant initialement les cartes données
     * @param route route prise
     * @param initialCards cartes permettant initialement de s'emparer de la route
     * @return le message déclarant que le joueur désire s'emparer de la route en tunnel donnée en utilisant initialement les cartes données
     */
    public String attemptsTunnelClaim(Route route, SortedBag<Card> initialCards) {
        String a = routeText(route.station1().toString(), route.station2().toString());
        return String.format(StringsFr.ATTEMPTS_TUNNEL_CLAIM, this.playerName, a, listCardText(initialCards));
    }

    /**
     * retourne le message déclarant que le joueur a tiré les trois cartes additionnelles données,
     * et qu'elles impliquent un coût additionel du nombre de cartes donné
     * @param drawnCards     les trois cartes additionnelles données
     * @param additionalCost un coût additionel du nombre de cartes donné
     * @return le message déclarant que le joueur a tiré les trois cartes additionnelles données,
     * et qu'elles impliquent un coût additionel du nombre de cartes donné
     */
    public String drewAdditionalCards(SortedBag<Card> drawnCards, int additionalCost) {
        String a = String.format(StringsFr.ADDITIONAL_CARDS_ARE, listCardText(drawnCards));
        if (additionalCost > 0) {
            String b = String.format(StringsFr.SOME_ADDITIONAL_COST, additionalCost, StringsFr.plural(additionalCost));
            return a + b;
        }
        String c = StringsFr.NO_ADDITIONAL_COST;
        return a + c;
    }

    /**
     * retourne le message déclarant que le joueur n'a pas pu (ou voulu) s'emparer du tunnel donné
     * @param route route dont le joueur n'a pas pu (ou voulu) s'emparer de
     * @return le message déclarant que le joueur n'a pas pu (ou voulu) s'emparer du tunnel donné
     */
    public String didNotClaimRoute(Route route) {
        String a = routeText(route.station1().toString(), route.station2().toString());
        return String.format(StringsFr.DID_NOT_CLAIM_ROUTE, this.playerName, a);
    }

    /**
     * retourne le message déclarant que le joueur n'a plus que le nombre donné (et inférieur ou égale à 2) de wagons,
     * et que le dernier tour commence donc
     * @param carCount nombre restants de wagons
     * @return le message déclarant que le joueur n'a plus que le nombre donné (et inférieur ou égale à 2) de wagons,
     * et que le dernier tour commence donc
     */
    public String lastTurnBegins(int carCount) {
        return String.format(StringsFr.LAST_TURN_BEGINS, this.playerName, carCount, StringsFr.plural(carCount));
    }

    /**
     * retourne le message déclarant que le joueur obtient le bonus de fin de partie grâce au chemin donné,
     * qui est le plus long, ou l'un des plus longs
     * @param longestTrail le chemin le plus long
     * @return le message déclarant que le joueur obtient le bonus de fin de partie grâce au chemin donné,
     * qui est le plus long, ou l'un des plus longs
     */
    public String getsLongestTrailBonus(Trail longestTrail) {
        if(longestTrail.station1() == null || longestTrail.station2() == null) return "";
        return String.format(StringsFr.GETS_BONUS, this.playerName, routeText(longestTrail.station1().toString(), longestTrail.station2().toString()));
    }

    /**
     * retourne le message déclarant que le joueur remporte la partie avec le nombre de points donnés,
     * son adversaire n'en ayant obtenu que loserPoints.
     * @param points      nombre des points du joueur
     * @param loserPoints nombre des points de l'adversaire
     * @return le message déclarant que le joueur remporte la partie avec le nombre de points donnés,
     * son adversaire n'en ayant obtenu que loserPoints.
     */
    public String won(int points, int loserPoints) {
        return String.format(StringsFr.WINS, this.playerName, points, plural(points), loserPoints, plural(loserPoints));
    }


    static private String routeText(String s1, String s2) {
        return s1 + StringsFr.EN_DASH_SEPARATOR + s2;

    }

    static private String listCardText(SortedBag<Card> cards) {
        List<String> liste = new ArrayList<>();
        for (Card c : cards.toSet()) {
            int n = cards.countOf(c);
            String a = n + " " + cardName(c, n);
            liste.add(a);
        }
        String b ;
        if (liste.size() > 1) { b = String.join(", ", liste.subList(0, liste.size() - 1)) + StringsFr.AND_SEPARATOR +
                    liste.get(liste.size() - 1);
        } else {
            b = liste.get(0);
        }
        return b;
    }
}
