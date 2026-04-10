package ch.epfl.tchu.net;

/**
 * @author Ahmed Aziz BEN HAJ (sciper: 310934) HMIDA et Aziz Laadher(sciper: 315196) / le 20 avril 2021
 *énumère les types de messages que le serveur peut envoyer aux clients
 */
public enum MessageId {
    INIT_PLAYERS,
    RECEIVE_INFO,
    UPDATE_STATE,
    SET_INITIAL_TICKETS,
    CHOOSE_INITIAL_TICKETS,
    NEXT_TURN,
    CHOOSE_TICKETS,
    DRAW_SLOT,
    ROUTE,
    CARDS,
    CHOOSE_ADDITIONAL_CARDS,
    GAMEOVER

}
