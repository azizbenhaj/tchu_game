package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;
import java.util.Objects;

import static ch.epfl.tchu.game.Constants.FACE_UP_CARDS_COUNT;

/**
 * PublicCardState
 *  @author OMAR MEZGHANI (311542)
 *   @author MOHAMED CHARFI (311171)
 */
public class PublicCardState {
    private final List<Card> faceUpCards;
    private final int deckSize;
    private final int discardsSize;

    /**
     * public constructor
     * @param faceUpCards the faceUpCards
     * @param deckSize the deckSize
     * @param discardsSize the discardsSize
     * @throws IllegalArgumentException
     */
    public PublicCardState(List<Card> faceUpCards, int deckSize, int discardsSize) {
        Preconditions.checkArgument(faceUpCards.size() == FACE_UP_CARDS_COUNT
                && deckSize >= 0 && discardsSize >= 0);
        this.deckSize = deckSize;
        this.discardsSize = discardsSize;
        this.faceUpCards = List.copyOf(faceUpCards);
    }

    /**
     * returns the five face-up cards
     * @return the five face-up cards
     */
    public List<Card> faceUpCards() {
        return faceUpCards;
    }

    /**
     * returns the face-up card of index slot
     * @param slot the index
     * @throws IndexOutOfBoundsException
     * @return the face-up card of index slot
     */
    public Card faceUpCard(int slot) {
        return faceUpCards.get(Objects.checkIndex(slot, FACE_UP_CARDS_COUNT));
    }

    /**
     * returns the deck size
     * @return the deck size
     */
    public int deckSize() {
        return deckSize;
    }

    /**
     * returns true if the deck is empty
     * @return true if the deck is empty
     */
    public boolean isDeckEmpty() {
        return deckSize == 0;
    }

    /**
     * returns the discards size
     * @return the discards size
     */
    public int discardsSize() {
        return discardsSize;
    }

}
