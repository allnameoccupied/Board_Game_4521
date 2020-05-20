package com.app.boardgame4521;

import com.app.boardgame4521.enumm.Position;
import com.app.boardgame4521.enumm.Suit;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game {
    private List<Player> players;
    private Suit trump;
    private int[] scores = new int[2];
    private final List<Card> cardPile = new ArrayList<>();
    private int round = 0;

    public Game() {
        for (Suit s : Suit.values()) {
            Card cardA = new Card('A', s);
            cardPile.add(cardA);
            for (int i = 2; i < 11; ++i) { //2-10
                Card card = new Card((char) i, s);
                cardPile.add(card);
            }
            Card cardJ = new Card('J', s);
            Card cardQ = new Card('Q', s);
            Card cardK = new Card('K', s);
            cardPile.add(cardJ);
            cardPile.add(cardQ);
            cardPile.add(cardK);
        }
        Player player1 = new Player("A", Position.E);
        Player player2 = new Player("B", Position.S);
        Player player3 = new Player("C", Position.W);
        Player player4 = new Player("D", Position.N);
        players.add(player1);
        players.add(player2);
        players.add(player3);
        players.add(player4);
    }

    public void initGame() {
        for(int i = 0; i < 52; ++i){ //shuffle
            int rnd = new Random().nextInt(52);
            Card tmp = cardPile.get(rnd);
            cardPile.set(rnd, cardPile.get(i));
            cardPile.set(i, tmp);
        }

    }
    public void deal(){

    }


}
