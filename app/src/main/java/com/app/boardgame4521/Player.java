package com.app.boardgame4521;

import com.app.boardgame4521.enumm.Position;
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

    public void addCard(Card card){
        cards.add(card);
    }
}

