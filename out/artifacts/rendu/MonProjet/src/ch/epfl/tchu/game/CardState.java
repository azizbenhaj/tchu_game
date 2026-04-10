package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static ch.epfl.tchu.game.Constants.FACE_UP_CARDS_COUNT;

/**
 * @author Ahmed Aziz BEN HAJ (sciper: 310934) HMIDA et Aziz Laadher(sciper: 315196) / le 15 mars 2021
 * représente l'état des cartes wagon/locomotive qui ne sont pas en main des joueurs
 */
public final class CardState extends PublicCardState {


    private final Deck<Card> deckCards;
    private final SortedBag<Card> discardCards;

    private CardState(List<Card> faceUpCards, Deck<Card> deckCards, SortedBag<Card> discardCards) {
        super(faceUpCards, deckCards.size(), discardCards.size());
        this.deckCards = deckCards;
        this.discardCards = discardCards;
    }

    /**
     * retourne un état dans lequel les 5 cartes disposées faces visibles sont les 5 premières du tas donné,
     * la pioche est constituée des cartes du tas restantes, et la défausse est vide
     * @param deck tas des cartes
     * @return un état des cartes deck
     * <p>
     * lève IllegalArgumentException si le tas donné contient moins de 5 cartes
     */
    public static CardState of(Deck<Card> deck) {
        Preconditions.checkArgument(deck.size() >= 5);
        List<Card> abc = deck.topCards(FACE_UP_CARDS_COUNT).toList();
        Deck<Card> abcd = deck.withoutTopCards(FACE_UP_CARDS_COUNT);
        return new CardState(abc, abcd, SortedBag.of());
    }

    /**
     * retourne un ensemble de cartes identique au récepteur (this), si ce n'est que la carte face visible d'index slot a été remplacée
     * par celle se trouvant au sommet de la pioche, qui en est du même coup retirée
     * @param slot indice de la carte remplacée
     * @return un ensemble de cartes identique au récepteur (this), si ce n'est que la carte face visible d'index slot a été remplacée
     * par celle se trouvant au sommet de la pioche, qui en est du même coup retirée
     * lève IndexOutOfBoundsException (!) si l'index donné n'est pas compris entre 0 (inclus) et 5 (exclus),
     * ou IllegalArgumentException si la pioche est vide,
     */
    public CardState withDrawnFaceUpCard(int slot) {
        Preconditions.checkArgument(this.deckCards.size() != 0);
        List<Card> car = new ArrayList<>(this.faceUpCards());
        car.set(slot, this.deckCards.topCard());
        return new CardState(car, this.deckCards.withoutTopCard(), this.discardCards);
    }

    /**
     * retourne la carte se trouvant au sommet de la pioche,
     * @return la carte se trouvant au sommet de la pioche,
     * ou lève IllegalArgumentException si la pioche est vide,
     */
    public Card topDeckCard() {
        Preconditions.checkArgument(!this.deckCards.isEmpty());
        return this.deckCards.topCard();
    }

    /**
     * retourne un ensemble de cartes identique au récepteur (this), mais sans la carte se trouvant au sommet de la pioche
     * @return un ensemble de cartes identique au récepteur (this), mais sans la carte se trouvant au sommet de la pioche
     * lève IllegalArgumentException si la pioche est vide,
     */
    public CardState withoutTopDeckCard() {
        Preconditions.checkArgument(!this.deckCards.isEmpty());
        return new CardState(this.faceUpCards(), this.deckCards.withoutTopCard(), this.discardCards);
    }

    /**
     * retourne un ensemble de cartes identique au récepteur (this), si ce n'est que les cartes de la défausse ont été mélangées au
     * moyen du générateur aléatoire donné
     * afin de constituer la nouvelle pioche;
     * @param rng variable Random pour melanger les cartes
     * @return un ensemble de cartes identique au récepteur (this), si ce n'est que les cartes de la défausse ont été mélangées au
     * moyen du générateur aléatoire donné
     * afin de constituer la nouvelle pioche;
     * lève IllegalArgumentException si la pioche du récepteur n'est pas vide,
     */
    public CardState withDeckRecreatedFromDiscards(Random rng) {
        Preconditions.checkArgument(this.deckCards.isEmpty());
        return new CardState(this.faceUpCards(), Deck.of(discardCards, rng), SortedBag.of());
    }

    /**
     * retourne un ensemble de cartes identique au récepteur (this), mais avec les cartes données ajoutées à la défausse.
     * @param additionalDiscards cartes ajoutées a la defausse
     * @return un ensemble de cartes identique au récepteur (this), mais avec les cartes données ajoutées à la défausse.
     */
    public CardState withMoreDiscardedCards(SortedBag<Card> additionalDiscards) {
        return new CardState(this.faceUpCards(), this.deckCards,this.discardCards.union(additionalDiscards));
    }


}