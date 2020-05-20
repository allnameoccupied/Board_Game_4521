package com.app.boardgame4521;

import com.app.boardgame4521.enumm.Position;
import com.app.boardgame4521.enumm.Suit;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private String name;
    private int score;
    private int target;
    private Position position;
    private List<Card> cards;

    protected Player(String name, Position position){
        this.name = name;
        this.position = position;
        this.cards = new ArrayList<>();
    }
    public String getName() { return name; }
    public int getScore() { return score; }
    public int getTarget() { return target; }
    public Position getPosition() { return position; }
    public List<Card> getCards() { return cards; }

    public boolean playCard(Card card){

        return true;
    }

    public boolean bid(){

        return true;
    }

    public void addScore(int winning) {
        this.score += winning;
    }

    public void addCard(Card card){
        cards.add(card);
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public Card selectCard(Suit start) {

        Card choice = cards.get(0); // pretend player input
        boolean allowAllSuit = true; // only true when the player doesnt have the starting suit

        for (Card card : cards) {
            if (card.getSuit() == start)
                allowAllSuit = false;
        }

        if (allowAllSuit || choice.getSuit() == start) {
            return choice;
        }
        else
            return null; // let the player choose again?

    }
}

