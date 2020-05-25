package com.app.boardgame4521;

import com.app.boardgame4521.enumm.Suit;

public class Card {
    private int rank;
    private Suit suit;

    public Card(){}

    public Card(int rank, Suit suit){
        this.rank = rank;
        this.suit = suit;
    }

    public int getRank() { return rank; }
    public Suit getSuit() { return suit; }
}

