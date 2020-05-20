package com.app.boardgame4521;

import com.app.boardgame4521.enumm.Position;
import com.app.boardgame4521.enumm.Suit;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game {
    private List<Player> players;
    private Suit trump;
    private int[] scores = {0, 0, 0, 0};
    private final List<Card> cardPile = new ArrayList<>();
    private int round = 1;

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

        for (int i = 0; i < round; i += 4) {
            for (int j = 0; j < 4; j++) {
                players.get(j).addCard(cardPile.get(i + j));
            }
        }

        if (round * 4 < 52)
            setTrump(cardPile.get(round * 4).getSuit());
        else
            setTrump(cardPile.get(round * 4 - 1).getSuit());

        // need to ask user input
        for (int i = 0; i < 4; i++) {
            players.get((((round - 1) % 4) + i) % 4).setTarget(1);
        }
    }

    public void startGame() {

        firstTrial();
        for (int i = 1; i < round; i++) {
            nextTrial();
        }

    }

    private Player firstTrial() {

        List<Card> pile = new ArrayList<>();

        Card card1 = players.get((round - 1) % 4).selectCard(null);
        pile.add(card1);
        Suit start = card1.getSuit();
        // need to ask user input
        for (int i = 1; i < 4; i++) {
            pile.add(players.get((((round - 1) % 4) + i) % 4).selectCard(start));
        }

        Card winner = pile.get(0);
        for (Card card : pile) {
            if (winner.getSuit() == card.getSuit()) {
                winner = getWinner(winner, card);
            }
            else {
                if (card.getSuit() == trump)
                    winner = card;
            }
        }

        return players.get((((round - 1) % 4) + pile.indexOf(winner)) % 4);
    }

    private void nextTrial() {

    }

    private Card getWinner(Card card1, Card card2) {

        char[] ranks = {'2', '3', '4', '5', '6', '7', '8', '9', 'T', 'J', 'Q', 'K', 'A'}; //replace '10' with 'T' for now
        int rank1 = 0;
        int rank2 = 0;

        for (int i = 0; i < ranks.length; i++) {
            if (ranks[i] == card1.getRank())
                rank1 = i;
        }

        for (int i = 0; i < ranks.length; i++) {
            if (ranks[i] == card2.getRank())
                rank2 = i;
        }

        if (rank2 > rank1)
            return card2;
        else
            return card1;
    }

    private void setTrump(Suit trump) {
        this.trump = trump;
    }

    public Player dealround() {

        return players.get(0);
    }

    public void dealGame(){

    }


}
