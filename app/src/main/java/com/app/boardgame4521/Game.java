package com.app.boardgame4521;

import com.app.boardgame4521.enumm.Position;
import com.app.boardgame4521.enumm.Suit;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game {
    private List<Player> players = new ArrayList<>();
    private Suit trump;
    private final List<Card> cardPile = new ArrayList<>();
    private int round = 1;

    public Game() {
        for (Suit s : Suit.values()) {
            for (int i = 2; i < 15; ++i) { // 2-10, J, Q, K, A
                Card card = new Card(i, s);
                cardPile.add(card);
            }
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

    public List<Player> getPlayers() {
        return players;
    }

    public int getRound() {
        return round;
    }
    // Note:
    // startGame is a complete game run for a total of 13 round
    // Need to take care user input for trump, target, cards and bid

    public void startGame() {

        while (round < 14) {
            initRound();
            oneRound();
            round++;
        }

        Player winner = players.get(0);
        for (Player player : players) {
            if (player.getScore() > winner.getScore())
                winner = player;
        }

        resetGame();

    }

    private void initRound() {
        // shuffle
        for(int i = 0; i < 52; ++i){
            int rnd = new Random().nextInt(52);
            Card tmp = cardPile.get(rnd);
            cardPile.set(rnd, cardPile.get(i));
            cardPile.set(i, tmp);
        }

        // distribute cards
        for (int i = 0; i < round; i += 4) {
            for (int j = 0; j < 4; j++) {
                players.get(j).addCard(cardPile.get(i + j));
            }
        }

        // get the trump
        if (round * 4 < 52)
            setTrump(cardPile.get(round * 4).getSuit());
        else
            setTrump(cardPile.get(round * 4 - 1).getSuit());

        // need to ask user target input
        for (int i = 0; i < 4; i++) {
            players.get((((round - 1) % 4) + i) % 4).setTarget(1);

            if (i == 3) {
                int totalTarget = 0;
                for (Player player : players)
                    totalTarget += player.getTarget();
                if (totalTarget == round)
                    players.get((((round - 1) % 4) + 3) % 4).setTarget(4); // need the last player to change his/her target
            }
        }
    }

    private void oneRound() {

        Player winner = firstTrial();
        winner.setStack(winner.getStack() + 1);
        Player nextWinner;

        for (int i = 1; i < round; i++) {
            nextWinner = nextTrial(winner);
            nextWinner.setStack(nextWinner.getStack() + 1);
            winner = nextWinner;
        }

        for (Player player : players) {
            if (player.getTarget() == player.getStack())
                player.addScore(player.getTarget() * player.getTarget() + 10);
            else
                player.addScore(-1 * Math.abs(player.getTarget() - player.getStack()));

            player.setStack(0);
            player.setTarget(0);
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
                if (card.getRank() > winner.getRank())
                    winner = card;
            }
            else {
                if (card.getSuit() == trump)
                    winner = card;
            }
        }

        return players.get((((round - 1) % 4) + pile.indexOf(winner)) % 4);
    }

    private Player nextTrial(Player lastWinner) {

        List<Card> pile = new ArrayList<>();

        Card card1 = lastWinner.selectCard(null);
        pile.add(card1);
        Suit start = card1.getSuit();
        // need to ask user input
        for (int i = 1; i < 4; i++) {
            pile.add(players.get((players.indexOf(lastWinner) + i) % 4).selectCard(start));
        }

        Card winner = pile.get(0);
        for (Card card : pile) {
            if (winner.getSuit() == card.getSuit()) {
                if (card.getRank() > winner.getRank())
                    winner = card;
            }
            else {
                if (card.getSuit() == trump)
                    winner = card;
            }
        }

        return players.get((players.indexOf(lastWinner) + pile.indexOf(winner)) % 4);
    }

    private void setTrump(Suit trump) {
        this.trump = trump;
    }

    private void resetGame() {
        for (Player player : players) {
            player.setTarget(0);
            player.setStack(0);
            player.addScore(-1 * player.getScore());
            player.clearCard();
        }
    }

}
