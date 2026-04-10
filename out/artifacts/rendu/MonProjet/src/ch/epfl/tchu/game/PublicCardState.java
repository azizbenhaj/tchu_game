package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import java.util.List;
import java.util.Objects;
import static ch.epfl.tchu.game.Constants.FACE_UP_CARDS_COUNT;

/**
 * @author Ahmed Aziz BEN HAJ (sciper: 310934) HMIDA et Aziz Laadher(sciper: 315196) / le 15 mars 2021
 *représente (une partie de) l'état des cartes wagon/locomotive qui ne sont pas en main des joueurs, à savoir:
 * les 5 cartes disposées face visible à côté du plateau,
 * la pioche,
 * la défausse.
 */
public class PublicCardState {

    private final List<Card> faceUpCards;
    private final int deckSize;
    private final int discardsSize;

    /**
     *constructeur parametré
     * @param faceUpCards les 5 cartes disposées face visible à côté du plateau,
     * @param deckSize taille de la pioche
     * @param discardsSize taille de la defausse
     * lève IllegalArgumentException si faceUpCards ne contient pas le bon nombre d'éléments (5),
     * ou si la taille de la pioche ou de la défausse sont négatives (< 0).
     */
    public PublicCardState(List<Card> faceUpCards, int deckSize, int discardsSize){
        Preconditions.checkArgument(faceUpCards.size()==FACE_UP_CARDS_COUNT && deckSize>=0&& discardsSize>=0);
    this.deckSize=deckSize;
    this.discardsSize=discardsSize;
    this.faceUpCards=List.copyOf(faceUpCards);
    }

    /**
     * retourne les 5 cartes disposées face visible à côté du plateau,
     * @return les 5 cartes disposées face visible à côté du plateau,
     */
    public List<Card> faceUpCards(){
        return this.faceUpCards;
    }

    /**
     * retourne la carte face visible à l'index donné
     * @param slot indice de la carte retournée
     * @return la carte face visible à l'index donné
     * lève IndexOutOfBoundsException (!) si cet index n'est pas compris entre 0 (inclus) et 5 (exclus),
     * qui est geré par la methode checkIndex()
     */
    public Card faceUpCard(int slot){
        return  this.faceUpCards.get(Objects.checkIndex(slot,this.faceUpCards.size()));
    }

    /**
     *retourne la taille de la pioche
     * @return la taille de la pioche
     */
    public int deckSize(){
        return this.deckSize;
    }

    /**
     *retourne vrai ssi la pioche est vide
     * @return vrai ssi la pioche est vide
     */
    public boolean isDeckEmpty(){
        return this.deckSize==0;
    }

    /**
     *retourne la taille de la defausse
     * @return la taille de la defausse
     */
    public int discardsSize(){
        return this.discardsSize;
    }

}
