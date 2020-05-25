package com.app.boardgame4521;

import com.app.boardgame4521.enumm.Position;
import com.app.boardgame4521.enumm.Suit;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private String name;
    private int score;
    private int target;
    private int stack;
    public boolean selectingCard;
    public boolean settingTarget;
    private Position position;
    private List<Card> cards = new ArrayList<>();

    public Player () {}

    protected Player(String name, Position position){
        this.name = name;
        this.position = position;
    }
    public String getName() { return name; }
    public int getScore() { return score; }
    public int getTarget() { return target; }
    public int getStack() { return stack; }
    public Position getPosition() { return position; }
    public List<Card> getCards() { return cards; }

    public void setCards(List<Card> cards) {
        this.cards = cards;
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
    public void setStack(int stack) { this.stack = stack; }
    public void clearCard() { this.cards = null; }

    public Card selectCard(Suit start) {

        Card choice = cards.get(0); // pretend player input
        boolean allowAllSuit = true; // only true when the player doesnt have the starting suit

        for (Card card : cards) {
            if (card.getSuit() == start)
                allowAllSuit = false;
        }

        if (allowAllSuit || choice.getSuit() == start) {
            cards.remove(choice);
            return choice;
        }
        else
            return null; // let the player choose again?

    }
}

