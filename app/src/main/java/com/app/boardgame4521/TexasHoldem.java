/**
 * # COMP 4521    #  Chan Hung Yan        20423715          hychanbr@connect.ust.hk
 * # COMP 4521    #  Tai Ka Chun          20433540          kctaiab@connect.ust.hk
 * # COMP 4521    #  Mong Kin Ip          20433629          kimong@connect.ust.hk
 */

package com.app.boardgame4521;

import com.app.boardgame4521.enumm.Position;
import com.app.boardgame4521.enumm.Suit;

import java.util.ArrayList;
import java.util.List;

public class TexasHoldem {

    private List<Player> players;
    private Position blind;
    private List<Card> community;
    private int pot;

    public TexasHoldem(List<Player> players) {
        this.players = players;
        this.blind = Position.E;
        this.community = new ArrayList<>();
        this.pot = 0;
    }

    // --------------- helper functions ---------------
    private List<Card> getAllCards(List<Card> cards) {
        List<Card> allCards = new ArrayList<>();
        allCards.addAll(this.community);
        allCards.addAll(cards);
        return allCards;
    }

    // --------------- get functions ---------------
    public List<Player> getPlayers() { return players; }
    public Position getBlind() { return blind; }
    public List<Card> getCommunity() { return community; }
    public int getPot() { return pot; }

    // --------------- game functions ---------------
    public List<Card> isOnePair(Player player) {
        List<Card> allCards = this.getAllCards(player.getCards());

        for (int i = 0; i < allCards.size() - 1; i++) {
            for (int j = i + 1; j < allCards.size(); j++) {
                if (allCards.get(i).getSuit() == allCards.get(j).getSuit()) {
                    List<Card> pair = new ArrayList<>();
                    pair.add(allCards.get(i));
                    pair.add(allCards.get(j));
                    return pair;
                }
            }
        }

        return null;
    }

    public List<Card> isTwoPair(Player player) {
        List<Card> allCards = this.getAllCards(player.getCards());

        List<Card> onePair = this.isOnePair(player);

        if (onePair == null)
            return null;

        allCards.removeAll(onePair);

        for (int i = 0; i < allCards.size() - 1; i++) {
            for (int j = i + 1; j < allCards.size(); j++) {
                if (allCards.get(i).getSuit() == allCards.get(j).getSuit()) {
                    List<Card> pair = new ArrayList<>();
                    pair.add(allCards.get(i));
                    pair.add(allCards.get(j));
                    return pair;
                }
            }
        }

        return null;
    }

    public boolean isFullHouse(Player player) {
        List<Card> allCards = this.getAllCards(player.getCards());

        for (Card card : allCards) {

        }

        return false;
    }
}
