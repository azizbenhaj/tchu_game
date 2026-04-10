package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Deck
 *
 * @author OMAR MEZGHANI (311542)
 * @author MOHAMED CHARFI (311171)
 */
public final class Deck<C extends Comparable<C>> {
    private final List<C> cards;

    /**
     * a private constructor of the deck
     *
     * @param cards the cards in the deck
     */
    private Deck(List<C> cards) {
        this.cards = cards;
    }

    /**
     * static method to construct a new deck  randomly
     *
     * @param cards the cards of the deck
     * @param rng   Random
     * @return a deck
     */
    public static <C extends Comparable<C>> Deck<C> of(SortedBag<C> cards, Random rng) {
        List<C> l = new ArrayList<>(cards.toList());
        Collections.shuffle(l, rng);
        return new Deck<>(l);
    }

    /**
     * return the size of the deck
     *
     * @return the size of the deck
     */
    public int size() {
        return cards.size();
    }

    /**
     * returns true if the deck is empty
     *
     * @return true if the deck is empty
     */
    public boolean isEmpty() {
        return cards.isEmpty();
    }

    /**
     * returns the top card of the deck
     *
     * @return the top card of the deck
     * @throws IllegalArgumentException
     */
    public C topCard() {
        Preconditions.checkArgument(!cards.isEmpty());
        return cards.get(cards.size() - 1);
    }

    /**
     * returns a deck without the top card
     *
     * @return a deck without the top card
     * @throws IllegalArgumentException
     */
    public Deck<C> withoutTopCard() {
        Preconditions.checkArgument(!cards.isEmpty());
        return withoutTopCards(1);

    }

    /**
     * returns a deck with the top count cards
     *
     * @param count the number of top cards
     * @return a deck with the top count cards
     * @throws IllegalArgumentException
     */
    public SortedBag<C> topCards(int count) {
        Preconditions.checkArgument(count >= 0 && count <= cards.size());
        List<C> l = cards.subList(cards.size() - count, cards.size());
        SortedBag.Builder<C> countCards = new SortedBag.Builder<>();
        for (C c : l) {
            countCards.add(c);
        }
        return countCards.build();
    }

    /**
     * returns a deck without the top count cards
     *
     * @param count the number of top cards
     * @return a deck without the top count cards
     * @throws IllegalArgumentException
     */
    public Deck<C> withoutTopCards(int count) {
        Preconditions.checkArgument(count >= 0 && count <= cards.size());
        List<C> l = cards.subList(0, cards.size() - count);
        return new Deck<>(l);
    }

}
