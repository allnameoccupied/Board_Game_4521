package com.app.boardgame4521;

import com.app.boardgame4521.enumm.Suit;

public class Card {
    private char rank;
    private Suit suit;

    public Card(char rank, Suit suit){
        this.rank = rank;
        this.suit = suit;
    }

    public char getRank() { return rank; }
    public Suit getSuit() { return suit; }
}

