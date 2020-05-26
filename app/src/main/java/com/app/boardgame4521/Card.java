/**
 * # COMP 4521    #  Chan Hung Yan        20423715          hychanbr@connect.ust.hk
 * # COMP 4521    #  Tai Ka Chun          20433540          kctaiab@connect.ust.hk
 * # COMP 4521    #  Mong Kin Ip          20433629          kimong@connect.ust.hk
 */

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

