package ch.epfl.tchu.game;
import java.util.*;

/**
 * @author Ahmed Aziz BEN HAJ (sciper: 310934) HMIDA et Aziz Laadher(sciper: 315196) / le 1 mars 2021
 *représente les couleurs qui servent comme types de wagons etc
 */
public enum Color {
        BLACK ,
        VIOLET ,
        BLUE ,
        GREEN ,
        YELLOW ,
        ORANGE ,
        RED ,
        WHITE ,JOKER;

        /**
         * tableau de couleurs des wagons etc
         */
         static final Color[] co = new Color[]{
                BLACK ,
                VIOLET ,
                BLUE ,
                GREEN ,
                YELLOW ,
                ORANGE ,
                RED ,
                WHITE ,JOKER};
        /**
         * liste de couleurs
         */
        public static final List<Color> ALL = List.of(co);
        /**
         * taille de la liste ALL
         */
        public static final int COUNT = ALL.size();
    }

