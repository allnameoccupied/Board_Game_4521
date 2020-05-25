package com.app.boardgame4521;

import android.util.Log;

import androidx.annotation.Nullable;

import com.app.boardgame4521.enumm.Position;
import com.app.boardgame4521.enumm.Suit;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class Game {
    private List<Player> players = new ArrayList<>();
    private Suit trump = null;
    private Suit startSuit = null;
    private final List<Card> cardPile = new ArrayList<>();
    private int totalTarget = 0;
    Thread[] threads = new Thread[5];

    {
        for (Suit s : Suit.values()) {
            for (int i = 2; i < 15; ++i) { // 2-10, J, Q, K, A
                Card card = new Card(i, s);
                cardPile.add(card);
            }
        }
    }

    private int round = 1;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference pdb = db.collection("active_room").document("room1").collection("players");
    Map<String, Object> roomInDb = new HashMap<>();

    public Game() {
        Player player0 = new Player("A", Position.E);
        Player player1 = new Player("B", Position.S);
        Player player2 = new Player("C", Position.W);
        Player player3 = new Player("D", Position.N);
        players.add(player0);
        players.add(player1);
        players.add(player2);
        players.add(player3);
        //add room in db
        roomInDb.put("round", round);
        roomInDb.put("trump", null);
        roomInDb.put("startSuit", null);
        db.collection("active_room").document("room1").set(roomInDb)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Game", "Room added");
                    } else {
                        Log.d("game", "Error when adding room");
                    }
                });
        //add players in db
        for (int i = 0; i < 4; ++i) {
            String path = "player" + i;
            pdb.document(path).set(players.get(i))
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("Game", "Players added");
                        } else {
                            Log.d("game", "Error when adding players");
                        }
                    });
        }
    }

    public List<Player> getPlayers() {
        return players;
    }

    public int getRound() {
        return round;
    }

    public Suit getTrump() { return trump; }

    public Suit getStartSuit() { return startSuit; }

    public void setStartSuit(Suit suit) {this.startSuit = suit;}
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

    public void initRound() {
        // shuffle
        for (int i = 0; i < 52; ++i) {
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
        for (int i = 0; i < 4; ++i) {
            String path = "player" + i;
            pdb.document(path).update("cards", players.get(i).getCards())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("Game", "cards added");
                        } else {
                            Log.d("Game", "Error when adding cards");
                        }
                    });
        }
        // get the trump
        if (round * 4 < 52)
            setTrump(cardPile.get(round * 4).getSuit());
        else
            setTrump(cardPile.get(round * 4 - 1).getSuit());

        // need to ask user target input
        totalTarget = 0;
//        for (int i = 0; i < 4; i++) {
//            setTargetListener((((round - 1) % 4) + i) % 4, i);
//            if(i > 0){
//                while(players.get((((round - 1) % 4) + i - 1) % 4).settingTarget){ }
//                //registration.remove();
//                //setTargetListener((((round - 1) % 4) + i) % 4, i);
//            }
//        }

        for (int i = 0; i < 4; i++) {
            int id = (((round - 1) % 4) + i) % 4;
            addThreadForTarget(id, i);
        }

    }


    private void addThreadForTarget(int id, int i) {
        String path = "player" + id;

        threads[i] = new Thread(() -> {
            Log.d("Game", "Thread is added");
            //enable setting target
            players.get(id).settingTarget = true;
            pdb.document(path).update("settingTarget", true)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("Game", "enable setting target");
                        } else {
                            Log.d("Game", "Error when enable setting target");
                        }
                    });
            //checking loop
            while (true) {
                if (!players.get(id).settingTarget) {
                    pdb.document(path).get().addOnCompleteListener((task) -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null) {
                                if (document.exists()) {
                                    int target = ((Long) Objects.requireNonNull(document.get("target"))).intValue();
                                    totalTarget += target;
                                    if(i == 3 && totalTarget == round){
                                        players.get(id).setTarget(target - 1);
                                    }
                                    Log.d("GameActivity", "DocumentSnapshot data");
                                } else {
                                    Log.d("GameActivity", "No such document");
                                }
                            }
                        } else {
                            Log.d("GameActivity", "get failed with ", task.getException());
                        }
                    });
                    pdb.document(path).update("settingTarget", false, "target", players.get(id).getTarget())
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Log.d("Game", "disable setting target in thread");
                                } else {
                                    Log.d("Game", "Error when disable setting target in thread");
                                }
                            });
                    if(i != 3) threads[i + 1].start();
                    return;
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        if(i == 0) threads[i].start();
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
            } else {
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
            } else {
                if (card.getSuit() == trump)
                    winner = card;
            }
        }

        return players.get((players.indexOf(lastWinner) + pile.indexOf(winner)) % 4);
    }

    private void setTrump(Suit trump) {
        this.trump = trump;
        roomInDb.put("trump", trump);
        db.collection("active_room").document("room1").update(roomInDb)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Game", "trump changed");
                    } else {
                        Log.d("Game", "Error when changing trump");
                    }
                });
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
