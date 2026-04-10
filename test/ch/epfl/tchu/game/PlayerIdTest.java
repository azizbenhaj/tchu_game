/*package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;

import java.util.List;

import static ch.epfl.tchu.game.Color.*;
import static ch.epfl.tchu.game.Color.WHITE;
import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.game.PlayerId.PLAYER_2;
import static org.junit.jupiter.api.Assertions.*;

class PlayerIdTest {

    @Test
    void next() {
        assertEquals(PLAYER_1, PLAYER_2.next());
    }

    @Test
    void values() {
        var expectedValues = new PlayerId[]{PLAYER_1,PLAYER_2};
        assertArrayEquals(expectedValues,PlayerId.values());
    }

    /*@Test
    void valueOf() {


        assertEquals("PLAYER_1 ",PlayerId.valueOf(PlayerId.PLAYER_1.toString()));
    }*/
   /* @Test
    void all() {
        assertEquals(List.of(PlayerId.values()), PlayerId.ALL);
    }

    @Test
    void count() {
        assertEquals(2, PlayerId.COUNT);
    }
}*/
package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlayerIdTest {
    @Test
    void playerIdAllIsDefinedCorrectly() {
        assertEquals(List.of(PlayerId.PLAYER_1, PlayerId.PLAYER_2), PlayerId.ALL);
    }

    @Test
    void playerIdNextWorks() {
        assertEquals(PlayerId.PLAYER_2, PlayerId.PLAYER_1.next());
        assertEquals(PlayerId.PLAYER_1, PlayerId.PLAYER_2.next());
    }
}