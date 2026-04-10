package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Ticket;

/**
 * ActionHandlers
 *
 * @author OMAR MEZGHANI (311542)
 * @author MOHAMED CHARFI (311171)
 */
public interface ActionHandlers {

    /**
     * DrawTicketsHandler
     */
    @FunctionalInterface
    interface DrawTicketsHandler {
        /**
         * a method that handles ticket drawing
         */
        void onDrawTickets();
    }

    /**
     * DrawCardHandler
     */
    @FunctionalInterface
    interface DrawCardHandler {
        /**
         * a method that handles card drawing
         *
         * @param slot slot
         */
        void onDrawCard(int slot);
    }

    /**
     * ClaimRouteHandler
     */
    @FunctionalInterface
    interface ClaimRouteHandler {
        /**
         * a method that handles route claiming
         *
         * @param r     route to claim
         * @param cards used cards
         */
        void onClaimRoute(Route r, SortedBag<Card> cards);
    }

    /**
     * ChooseTicketsHandler
     */
    @FunctionalInterface
    interface ChooseTicketsHandler {
        /**
         * a method that handles ticket choosing
         *
         * @param tickets tickets
         */
        void onChooseTickets(SortedBag<Ticket> tickets);
    }

    /**
     * ChooseCardsHandler
     */
    @FunctionalInterface
    interface ChooseCardsHandler {
        /**
         * a method that handles cards choosing
         *
         * @param c cards
         */
        void onChooseCards(SortedBag<Card> c);

    }

    /**
     * Surrender
     */
    @FunctionalInterface
    interface SurrenderHandler {
        /**
         * a method that handles surrendering
         */
        void surrender();

    }

    /**
     * Restart
     */
    @FunctionalInterface
    interface RestartHandler {
        /**
         * a method that handles restarting
         */
        void restart();
    }
}