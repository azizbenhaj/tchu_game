package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static ch.epfl.tchu.game.Card.*;
import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.game.PlayerId.PLAYER_2;
import static org.junit.jupiter.api.Assertions.*;

class SerdesTest {

    List<Card> fu = List.of(RED, WHITE, BLUE, BLACK, RED);
    PublicCardState cs = new PublicCardState(fu, 30, 31);
    List<Route> rs1 = ChMap.routes().subList(0, 2);
    Map<PlayerId, PublicPlayerState> ps = Map.of(
            PLAYER_1, new PublicPlayerState(10, 11, rs1),
            PLAYER_2, new PublicPlayerState(20, 21, List.of()));
    PublicGameState gs =
            new PublicGameState(40, cs, PLAYER_2, ps, null);
    //publicGameState
    @Test
    void tests1() {
        String actual1 = Serdes.PUBLICGAMESTATE.serialize(gs);
        assertEquals("40:6,7,2,0,6;30;31:1:10;11;0,1:20;21;:", actual1);
        PublicGameState actual2 = Serdes.PUBLICGAMESTATE.deserialize("40:6,7,2,0,6;30;31:1:10;11;0,1:20;21;:");
        assertEquals(gs.ticketsCount(), actual2.ticketsCount());

        assertEquals(gs.cardState().deckSize(),actual2.cardState().deckSize());
        assertEquals(gs.cardState().discardsSize(),actual2.cardState().discardsSize());

        assertEquals(gs.currentPlayerId(),actual2.currentPlayerId());

        assertEquals(gs.playerState(PLAYER_1).ticketCount(),actual2.playerState(PLAYER_1).ticketCount());
        assertEquals(gs.playerState(PLAYER_1).cardCount(),actual2.playerState(PLAYER_1).cardCount());
        assertEquals(gs.playerState(PLAYER_1).routes().toString(),actual2.playerState(PLAYER_1).routes().toString());

        assertEquals(gs.playerState(PLAYER_2).ticketCount(),actual2.playerState(PLAYER_2).ticketCount());
        assertEquals(gs.playerState(PLAYER_2).cardCount(),actual2.playerState(PLAYER_2).cardCount());
        assertEquals(gs.playerState(PLAYER_2).routes().toString(),actual2.playerState(PLAYER_2).routes().toString());
    }

    //liste de cardes
    @Test
    void tests2(){
        String actual1=Serdes.LISTCARD.serialize(fu);
        List<Card> actual2=Serdes.LISTCARD.deserialize("6,7,2,0,6");
        assertEquals("6,7,2,0,6",actual1);
        assertEquals(fu,actual2);
    }
    //string
    @Test
    void tests3(){
        String actual1=Serdes.STRING.serialize("Charles");
        String actual2=Serdes.STRING.deserialize("Q2hhcmxlcw==");
        assertEquals("Q2hhcmxlcw==",actual1);
        assertEquals("Charles",actual2);
    }
    //integer
    @Test
    void tests4(){
        String actual1=Serdes.INTEGER.serialize(1234567);
        Integer actual2=Serdes.INTEGER.deserialize("1234567");
        assertEquals("1234567",actual1);
        assertEquals(1234567,actual2);
    }
    //turnkind
    @Test
    void tests5(){
        String actual1=Serdes.TURNKIND.serialize(Player.TurnKind.DRAW_CARDS);
        Player.TurnKind actual2=Serdes.TURNKIND.deserialize("1");
        assertEquals("1",actual1);
        assertEquals(Player.TurnKind.DRAW_CARDS,actual2);
    }
    //routes
    @Test
    void tests6(){
        String actual1=Serdes.ROUTES.serialize(ChMap.routes().get(15));
        Route actual2=Serdes.ROUTES.deserialize("15");
       // assertEquals("15",actual1);
        assertEquals(ChMap.routes().get(15).toString(),actual2.toString());
    }
    //plyaerid
    @Test
    void tests7(){
        String actual1=Serdes.PLAYERID.serialize(PLAYER_1);
        PlayerId actual2=Serdes.PLAYERID.deserialize("0");
        assertEquals("0",actual1);
        assertEquals(PLAYER_1,actual2);
    }
    //card
    @Test
    void tests8(){
        String actual1=Serdes.CARD.serialize(BLUE);
        Card actual2=Serdes.CARD.deserialize("2");
        assertEquals("2",actual1);
        assertEquals(BLUE,actual2);
    }
    //ticket
    @Test
    void tests9(){
        String actual1=Serdes.TICKETS.serialize(ChMap.tickets().get(17));
        Ticket actual2=Serdes.TICKETS.deserialize("17");
        assertEquals("17",actual1);
        assertEquals(ChMap.tickets().get(17),actual2);
    }
    //liste de string
    @Test
    void tests10(){
        List<String>str=List.of("aziz","ben","ha+/=");
        String actual1=Serdes.LISTSTRING.serialize(str);
        List<String> actual2=Serdes.LISTSTRING.deserialize("YXppeg==,YmVu,aGErLz0=");
        assertEquals("YXppeg==,YmVu,aGErLz0=",actual1);
        assertEquals(str,actual2);
    }
    //liste de routes
    @Test
    void tests11(){
        List<Route>rte=List.of(ChMap.routes().get(8),ChMap.routes().get(18),ChMap.routes().get(28));
        String actual1=Serdes.LISTROUTE.serialize(rte);
        List<Route> actual2=Serdes.LISTROUTE.deserialize("8,18,28");
        assertEquals("8,18,28",actual1);
        assertEquals(rte,actual2);
    }
    //sortedbag de cartes
    @Test
    void tests12(){
        SortedBag<Card>crd=SortedBag.of(2, BLACK,4, BLUE);
        String actual1=Serdes.SORTEDBAGCARD.serialize(crd);
        SortedBag<Card> actual2=Serdes.SORTEDBAGCARD.deserialize("0,0,2,2,2,2");
        assertEquals("0,0,2,2,2,2",actual1);
        assertEquals(crd,actual2);
    }
    //sortedbag de tickets
    @Test
    void tests13(){
        SortedBag<Ticket>tck=SortedBag.of(4, ChMap.tickets().get(15),4,  ChMap.tickets().get(10));
        String actual1=Serdes.SORTEDBAGTICKET.serialize(tck);
        SortedBag<Ticket> actual2=Serdes.SORTEDBAGTICKET.deserialize("10,10,10,10,15,15,15,15");
        assertEquals("10,10,10,10,15,15,15,15",actual1);
        assertEquals(tck,actual2);
    }
    //liste de sortedbag de cartes
    @Test
    void tests14(){
        List<SortedBag<Card>>crd=List.of(SortedBag.of(1, BLACK),SortedBag.of(2, VIOLET),SortedBag.of(3, BLUE));
        String actual1=Serdes.LISTSORTEDBAGCARD.serialize(crd);
        List<SortedBag<Card>> actual2=Serdes.LISTSORTEDBAGCARD.deserialize("0;1,1;2,2,2");
        assertEquals("0;1,1;2,2,2",actual1.toString());
        assertEquals(crd,actual2);
    }
    //publicCardState
    @Test
    void tests15(){
        PublicCardState pcs=new PublicCardState(List.of(BLACK,BLUE,BLACK,BLUE,BLACK),2,2);
        String actual1=Serdes.PUBLICCARDSTATE.serialize(pcs);
        PublicCardState actual2=Serdes.PUBLICCARDSTATE.deserialize("0,2,0,2,0;2;2");
        assertEquals("0,2,0,2,0;2;2",actual1.toString());
        assertEquals(pcs.faceUpCards().toString(),actual2.faceUpCards().toString());
        assertEquals(pcs.deckSize(),actual2.deckSize());
        assertEquals(pcs.discardsSize(),actual2.discardsSize());
    }
    //publicPlayerState
    @Test
    void tests16(){
        PublicPlayerState pps=new PublicPlayerState(2,2,List.of());
        String actual1=Serdes.PUBLICPLAYERSTATE.serialize(pps);
        PublicPlayerState actual2=Serdes.PUBLICPLAYERSTATE.deserialize("2;2;");
        assertEquals("2;2;",actual1.toString());
        assertEquals(pps.ticketCount(),actual2.ticketCount());
        assertEquals(pps.cardCount(),actual2.cardCount());
        assertEquals(pps.routes().size(),actual2.routes().size());
    }
    //playerState
    @Test
    void tests17(){
        PlayerState ps=new PlayerState(SortedBag.of(),SortedBag.of(),List.of());
        String actual1=Serdes.PLAYERSTATE.serialize(ps);
        PlayerState actual2=Serdes.PLAYERSTATE.deserialize(";;");
        assertEquals(";;",actual1.toString());
        assertEquals(ps.tickets().size(),actual2.tickets().size());
        assertEquals(ps.cards().size(),actual2.cards().size());
        assertEquals(ps.routes().size(),actual2.routes().size());
    }

}