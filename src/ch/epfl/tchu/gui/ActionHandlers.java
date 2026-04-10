package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

/**
 * @author Ahmed Aziz BEN HAJ (sciper: 310934) HMIDA et Aziz Laadher(sciper: 315196) / le 4 mai 2021
 contient cinq interfaces fonctionnelles imbriquées représentant différents gestionnaires d'actions
 */
public interface ActionHandlers {

    @FunctionalInterface
    interface BackgroundSoundStateHandler{
        /**
         * appelée lorsqque le joueur a décidé de couper ou activer le son
         */
        void onToggleSoundState();
    }

    @FunctionalInterface
    interface SurrenderHandler {
        /**
         * appelée lorsque le joueur a décidé d'abandonner
         */
        void onSurrender();
    }
    @FunctionalInterface
    interface ReplayHandler{
        /**
         * appelée lorsque le joueur a terminé la partie et qu'il veut rejouer ou pas
         * @param playerId l'identité du joueur (on a constaté que cet argument est inutile et peut etre retiré)
         * @param playerRestartResponse la décision prise
         */
        void onRequestReplay(PlayerId playerId, GameState.PlayerRestartResponse playerRestartResponse);
    }

    @FunctionalInterface
    interface DrawTicketsHandler{
        /**
         * appelée lorsque le joueur désire tirer des billets,
         */
         void onDrawTickets();
    }

    @FunctionalInterface
    interface DrawCardHandler{
        /**
         * appelée lorsque le joueur désire tirer une carte de l'emplacement donné,
         * @param i un numéro d'emplacement (0 à 4, ou -1 pour la pioche)
         */
         void onDrawCard(int i);
    }

    @FunctionalInterface
    interface ClaimRouteHandler{
        /**
         * appelée lorsque le joueur désire s'emparer de la route donnée au moyen des cartes (initiales) données
         * @param route une route
         * @param cards un multiensemble de cartes
         */
         void onClaimRoute(Route route , SortedBag<Card> cards);
    }

    @FunctionalInterface
    interface ChooseTicketsHandler{
        /**
         * appelée lorsque le joueur a choisi de garder les billets donnés suite à un tirage de billets,
         * @param tickets un multiensemble de billets
         */
         void onChooseTickets(SortedBag<Ticket> tickets);
    }

    @FunctionalInterface
    interface ChooseCardsHandler{
        /**
         * appelée lorsque le joueur a choisi d'utiliser les cartes données comme cartes initiales
         * ou additionnelles lors de la prise de possession d'une route; s'il s'agit de cartes additionnelles,
         * alors le multiensemble peut être vide, ce qui signifie que le joueur renonce à s'emparer du tunnel
         * @param cards un multiensemble de cartes
         */
         void onChooseCards(SortedBag<Card> cards);
    }

}
