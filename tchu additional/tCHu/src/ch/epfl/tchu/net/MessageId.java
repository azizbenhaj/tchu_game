package ch.epfl.tchu.net;


import java.util.List;
/**
 * enum MessageId
 *
 * @author OMAR MEZGHANI (311542)
 * @author MOHAMED CHARFI (311171)
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
    LONGEST,
    MUSIC,
    CHOOSE_ADDITIONAL_CARDS;
    /**
     * a list of all messages
     */
    public static final List<MessageId> ALL = List.of(MessageId.values());
    /**
     * the count of all messages
     */
    public static final int COUNT = ALL.size();
}
