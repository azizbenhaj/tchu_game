package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import static ch.epfl.tchu.game.Constants.ADDITIONAL_TUNNEL_CARDS;
import static ch.epfl.tchu.game.Constants.INITIAL_CARDS_COUNT;

/**
 * @author Ahmed Aziz BEN HAJ (sciper: 310934) HMIDA et Aziz Laadher(sciper: 315196) / le 22 mars 2021
 * represente l'etat privé d'un joueur
 */
public final class PlayerState extends PublicPlayerState {

    private final SortedBag<Ticket> tickets;
    private final SortedBag<Card> cards;

    /**
     * constructeur parametré
     * @param tickets billets possedés
     * @param cards   cartes possédés
     * @param routes  routes dont on s'est emparé de
     */
    public PlayerState(SortedBag<Ticket> tickets, SortedBag<Card> cards, List<Route> routes) {
        super(tickets.size(), cards.size(), routes);
        this.cards = cards;
        this.tickets = tickets;
    }

    /**
     * retourne l'état initial d'un joueur
     * @param initialCards cartes initiales  distribuées
     * @return l'état initial d'un joueur
     */
    public static PlayerState initial(SortedBag<Card> initialCards) {
        Preconditions.checkArgument(initialCards.size() == INITIAL_CARDS_COUNT);
        return new PlayerState(SortedBag.of(), initialCards, List.of());
    }

    /**
     * retourne les billets du joueur
     * @return les billets du joueur
     */
    public SortedBag<Ticket> tickets() {
        return this.tickets;
    }

    /**
     * retourne un état identique au récepteur,
     * si ce n'est que le joueur possède en plus les billets donnés
     * @param newTickets les billets additionnés
     * @return un état identique au récepteur,
     * si ce n'est que le joueur possède en plus les billets donnés
     */
    public PlayerState withAddedTickets(SortedBag<Ticket> newTickets) {
        return new PlayerState(this.tickets.union(newTickets), this.cards, this.routes());
    }

    /**
     * retourne les cartes wagon/locomotive du joueur,
     * @return les cartes wagon/locomotive du joueur,
     */
    public SortedBag<Card> cards() {
        return this.cards;
    }

    /**
     * retourne un état identique au récepteur,
     * si ce n'est que le joueur possède en plus la carte ajoutée
     * @param card la carte ajoutée
     * @return un état identique au récepteur,
     * si ce n'est que le joueur possède en plus la carte ajoutée
     */
    public PlayerState withAddedCard(Card card) {
        SortedBag<Card> cards1 = SortedBag.of(card);
        return new PlayerState(this.tickets, this.cards.union(cards1), this.routes());
    }

    /**
     * retourne vrai ssi le joueur peut s'emparer de la route donnée
     * @param route la route donnée
     * @return vrai ssi le joueur peut s'emparer de la route donnée
     */
    public boolean canClaimRoute(Route route) {

        boolean bo = false;
        for (SortedBag<Card> cards1 : route.possibleClaimCards()) {
            if (this.cards.contains(cards1)) {
                bo = true;
                break;
            }
        }
      return (this.carCount() >= route.length() && bo);
    }

    /**
     * retourne la liste de tous les ensembles de cartes
     * que le joueur pourrait utiliser pour prendre possession de la route donnée
     * @param route route dont le joueur veut prendre possession
     * @return la liste de tous les ensembles de cartes
     * que le joueur pourrait utiliser pour prendre possession de la route donnée
     * lève IllegalArgumentException si le joueur n'a pas assez de wagons
     * pour s'emparer de la route,
     */
    public List<SortedBag<Card>> possibleClaimCards(Route route) {
        if (this.carCount() < route.length()) throw new IllegalArgumentException();
        List<SortedBag<Card>> liste = route.possibleClaimCards();
        List<SortedBag<Card>> liste2 = new ArrayList<>();
        for (SortedBag<Card> cards1 : liste) {
            if (this.cards.contains(cards1)) {
                liste2.add(cards1); } }
        return liste2;
    }

    /**
     * retourne la liste de tous les ensembles de cartes
     * que le joueur pourrait utiliser pour s'emparer d'un tunnel
     * @param additionalCardsCount cartes a poser encore
     * @param initialCards    cartes initialement posées
    // * @param drawnCards  les 3 cartes tirées du sommet de la pioche(inutile)
     * @return la liste de tous les ensembles de cartes
     * que le joueur pourrait utiliser pour s'emparer d'un tunnel
     */
    public List<SortedBag<Card>> possibleAdditionalCards(int additionalCardsCount, SortedBag<Card> initialCards) {
        Preconditions.checkArgument(!initialCards.isEmpty());
        Card usedCard = initialCards.get(0);
        for (Card initialCard : initialCards) {
            if (initialCard != usedCard) {
                usedCard = initialCard;
                break; }
        }
        boolean bo = false;
        for (Card initialCardq : initialCards) {
            if (!initialCardq.equals(usedCard) && !initialCardq.equals(initialCards.get(0))) {
                bo = true;
                break; }
        }
       Preconditions.checkArgument (!(additionalCardsCount < 1 || additionalCardsCount > ADDITIONAL_TUNNEL_CARDS || bo));
        var temp = this.cards.difference(initialCards);
        Card usedCard2 = Card.LOCOMOTIVE;
        for (Card initialCard : initialCards) {
            if (initialCard != usedCard2) {
                usedCard2 = initialCard;
                break; }
        }
        if (temp.size() < additionalCardsCount)
            return new ArrayList<>();
        Set<SortedBag<Card>> subset = temp.subsetsOfSize(additionalCardsCount);
        List<SortedBag<Card>> result = new ArrayList<>();
        for (SortedBag<Card> z : subset) {
            boolean possible = true;
            for (Card card : z) {
                if (card != Card.LOCOMOTIVE && card != usedCard2) {
                    possible = false;
                break; }
            }
            if (!possible) {
                continue; }
            result.add(z);
        }
        result.sort(
                Comparator.comparingInt(cs -> cs.countOf(Card.LOCOMOTIVE)));
        return result;

    }

    /**
     * retourne un état identique au récepteur,
     * si ce n'est que le joueur s'est de plus emparé de la route donnée au moyen des cartes données,
     * @param route route ajoutée
     * @param claimCards cartes utilisées
     * @return un état identique au récepteur,
     * si ce n'est que le joueur s'est de plus emparé de la route donnée au moyen des cartes données,
     */
    public PlayerState withClaimedRoute(Route route, SortedBag<Card> claimCards) {
        List<Route> routes1 = new ArrayList<>(this.routes());
        routes1.add(route);
        return new PlayerState(this.tickets, this.cards.difference(claimCards), routes1);

    }

    /**
     * retourne le nombre de points éventuellement négatif obtenus par le joueur grâce à ses billet
     * @return le nombre de points éventuellement négatif obtenus par le joueur grâce à ses billet
     */
    public int ticketPoints() {
        int id = 0;
        for (Route r : routes()) {
            if (r.station1().id() > id) {
                id = r.station1().id();
            }
            if (r.station2().id() > id) {
                id = r.station2().id();
            }
        }
        StationPartition.Builder builder = new StationPartition.Builder(id + 1);
        for (Route r : this.routes()) {
            builder.connect(r.station1(), r.station2());
        }
        StationPartition sp = builder.build();
        int points = 0;
        for (Ticket t : this.tickets) {
            points += t.points(sp);
        }
        return points;
    }

    /**
     * retourne la totalité des points obtenus par le joueur à la fin de la partie
     * @return la totalité des points obtenus par le joueur à la fin de la partie
     */
    public int finalPoints() {
        return this.ticketPoints() + this.claimPoints();
    }

}
