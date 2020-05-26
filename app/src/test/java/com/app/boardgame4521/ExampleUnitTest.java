package com.app.boardgame4521;

import com.app.boardgame4521.enumm.Position;
import com.app.boardgame4521.enumm.Suit;

import org.junit.Test;

import java.util.Queue;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Test
    public void player_name() {
        Player player = new Player("test", Position.N);
        assertEquals("test", player.getName());
    }

    @Test
    public void player_score() {
        Player player = new Player("test", Position.N);
        player.addScore(10);
        player.addScore(15);
        assertEquals(25, player.getScore());
    }

    @Test
    public void player_target() {
        Player player = new Player("test", Position.N);
        assertNull(player.getTarget());
        player.setTarget(3);
        player.setTarget(4);
        assertEquals(4, player.getTarget());
    }

    @Test
    public void player_stack() {
        Player player = new Player("test", Position.N);
        player.setStack(3);
        player.setStack(5);
        assertEquals(5, player.getStack());
    }

    @Test
    public void player_position() {
        Player player = new Player("test", Position.N);
        assertEquals(Position.N, player.getPosition());
    }

    @Test
    public void player_cards() {
        Player player = new Player("test", Position.N);
        Card card1 = new Card(3, Suit.Club);
        Card card2 = new Card(9, Suit.Diamond);
        Card card3 = new Card(2, Suit.Heart);
        player.addCard(card1);
        player.addCard(card2);
        player.addCard(card3);
        assertEquals(card1, player.getCards().get(0));
        assertEquals(card2, player.getCards().get(1));
        assertEquals(card3, player.getCards().get(2));
    }

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }



}