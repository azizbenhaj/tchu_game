package ch.epfl.tchu.game;

import java.util.List;

/**
 * @author Ahmed Aziz BEN HAJ (sciper: 310934) HMIDA et Aziz Laadher(sciper: 315196) / le 22 mars 2021
 *représente l'identité d'un joueur.
 */
public enum PlayerId {
    PLAYER_1,PLAYER_2;

    /**
     * liste des identités des joueurs
     */
    public static final List<PlayerId> ALL = List.of(PLAYER_1,PLAYER_2);
    /**
     * taille de la liste precedente
     */
    public static final int COUNT = ALL.size();

    /**
     *retourne l'autre joueur que le joueur actuel
     * @return l'autre joueur que le joueur actuel
     */
    public PlayerId next(){
        if (this==PLAYER_1) return PLAYER_2;
      return PLAYER_1;
    }
}
