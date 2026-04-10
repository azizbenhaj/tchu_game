package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static ch.epfl.tchu.game.Constants.*;

/**
 * CardState extends PublicCardState
 *
 * @author OMAR MEZGHANI (311542)
 * @author MOHAMED CHARFI (311171)
 */
public final class CardState extends PublicCardState {
    private final Deck<Card> deckCards;
    private final SortedBag<Card> discardedCards;

    /**
     * a private constructor
     *
     * @param faceUpCards    the face-up cards
     * @param deckSize       the deck size
     * @param discardsSize   the discard size
     * @param deckCards      the deck cards
     * @param discardedCards the discarded cards
     */
    private CardState(List<Card> faceUpCards, int deckSize, int discardsSize, Deck<Card> deckCards, SortedBag<Card> discardedCards) {
        super(faceUpCards, deckSize, discardsSize);
        this.deckCards = deckCards;
        this.discardedCards = discardedCards;
    }

    /**
     * a static method to construct a new CardState
     *
     * @param deck the deck
     * @return a new CardState
     * @throws IllegalArgumentException
     */
    public static CardState of(Deck<Card> deck) {
        Preconditions.checkArgument(deck.size() >= FACE_UP_CARDS_COUNT);
        return new CardState(deck.topCards(FACE_UP_CARDS_COUNT).toList(),
                deck.size() - FACE_UP_CARDS_COUNT, 0,
                deck.withoutTopCards(FACE_UP_CARDS_COUNT), SortedBag.of());
    }

    /**
     * returns a new cardState where one of the face-up cards is changed
     *
     * @param slot the position of the face-up card
     * @return a new cardState where one of the face-up cards is changed
     * @throws IllegalArgumentException
     * @throws IndexOutOfBoundsException
     */
    public CardState withDrawnFaceUpCard(int slot) {
        Preconditions.checkArgument(!isDeckEmpty());
        List<Card> newFaceUpCards = new ArrayList<>(faceUpCards());
        newFaceUpCards.set(Objects.checkIndex(slot, Constants.FACE_UP_CARDS_COUNT),deckCards.topCard());
        return new CardState(newFaceUpCards, deckSize() - 1, discardsSize(), deckCards.withoutTopCard(), discardedCards);

    }

    /**
     * returns the top card of the deck
     *
     * @return the top card of the deck
     * @throws IllegalArgumentException
     */
    public Card topDeckCard() {
        return deckCards.topCard();
    }

    /**
     * returns a CardState without the top deck card
     *
     * @return a CardState without the top deck card
     * @throws IllegalArgumentException
     */
    public CardState withoutTopDeckCard() {
        return new CardState(faceUpCards(), deckSize() - 1, discardsSize(), deckCards.withoutTopCard(), discardedCards);
    }

    /**
     * returns a CardState with a new deck randomly generated for discards
     *
     * @param rng Random
     * @return a CardState with a new deck randomly generated for discards
     * @throws IllegalArgumentException
     */
    public CardState withDeckRecreatedFromDiscards(Random rng) {
        Preconditions.checkArgument(isDeckEmpty());
        return new CardState(faceUpCards(), discardsSize(), 0, Deck.of(discardedCards, rng), SortedBag.of());

    }

    /**
     * returns a new CardState adding additionalDiscards to discards
     *
     * @param additionalDiscards the additional discards
     * @return a new CardState adding additionalDiscards to discards
     */
    public CardState withMoreDiscardedCards(SortedBag<Card> additionalDiscards) {
        return new CardState(faceUpCards(), deckSize(), discardsSize() + additionalDiscards.size(),
                deckCards, discardedCards.union(additionalDiscards));

    }

}
