package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import java.util.Collections;
import java.util.List;
import java.util.Random;


/**
 * @author Ahmed Aziz BEN HAJ (sciper: 310934) HMIDA et Aziz Laadher(sciper: 315196) / le 15 mars 2021
 * représente un tas de cartes
 * @param <C> représente le type des cartes du tas
 */
public final class Deck<C extends Comparable<C>> {

    private final List<C> cards;

    private Deck(List<C> cards) {
        this.cards = cards;
    }


    /**
     * retourne un tas de cartes ayant les mêmes cartes que le multiensemble cards, mélangées au moyen du générateur de nombres aléatoires rng
     * @param cards cartes de la pioche
     * @param rng   variable Random pour melanger
     * @param <C>   le type des cartes du tas
     * @return un tas de cartes ayant les mêmes cartes que le multiensemble cards, mélangées au moyen du générateur de nombres aléatoires rng
     */
    public static <C extends Comparable<C>> Deck<C> of(SortedBag<C> cards, Random rng) {
        List<C> var = cards.toList();
        Collections.shuffle(var, rng);
        return new Deck<>(var);
    }

    /**
     * retourne nombre de cartes
     * @return nombre de cartes
     */
    public int size() {
        return this.cards.size();
    }

    /**
     * retourne si le tas est vide
     * @return si le tas est vide
     */
    public boolean isEmpty() {
        return this.cards.isEmpty();
    }

    /**
     * retourne la premiere carte du tas
     * @return la premiere carte du tas
     * lève IllegalArgumentException si le tas est vide,
     */
    public C topCard() {
        Preconditions.checkArgument(!this.isEmpty());
        return this.cards.get(0);
    }

    /**
     * retourne le tas sans la premiere carte
     * @return le tas sans la premiere carte
     * lève IllegalArgumentException si le tas est vide,
     */
    public Deck<C> withoutTopCard() {
        Preconditions.checkArgument(!this.isEmpty());
        return new Deck<>(this.cards.subList(1,this.cards.size()));
    }

    /**
     * retourne les count premieres cartes
     * @param count nombre de cartes retournées
     * @return les count premieres cartes
     * lève IllegalArgumentException si count n'est pas compris entre 0 (inclus) et la taille du tas (incluse),
     */
    public SortedBag<C> topCards(int count) {
        Preconditions.checkArgument(count >= 0 && count <= this.cards.size());
        SortedBag.Builder<C> ens = new SortedBag.Builder<>();
        for (int i = 0; (i < count); i++) {
            ens.add(this.cards.get(i)); }
        return ens.build();
    }

    /**
     * retourne le tas sans les count premieres cartes
     * @param count nombre de cartes non retournées
     * @return le tas sans les count premieres cartes
     * lève IllegalArgumentException si count n'est pas compris entre 0 (inclus) et la taille du tas (incluse),
     */
    public Deck<C> withoutTopCards(int count) {
        Preconditions.checkArgument(count >= 0 && count <= this.cards.size());
        List<C> cards1 = this.cards.subList(count, this.cards.size());
        return new Deck<>(cards1);
    }

}
