/**
 * # COMP 4521    #  Chan Hung Yan        20423715          hychanbr@connect.ust.hk
 * # COMP 4521    #  Tai Ka Chun          20433540          kctaiab@connect.ust.hk
 * # COMP 4521    #  Mong Kin Ip          20433629          kimong@connect.ust.hk
 */

package com.app.boardgame4521;

import android.util.Log;

import androidx.annotation.Nullable;

import com.app.boardgame4521.enumm.Position;
import com.app.boardgame4521.enumm.Suit;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
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
    private boolean targetIsSet = false;
    private boolean RoundEnd = false;
    private final List<Card> cardPile = new ArrayList<>();
    private int totalTarget = 2;
    Thread[] threads = new Thread[5];
    private List<Card> cardsToCompare = new ArrayList<>();
    int winnerID = 0;

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
    DocumentReference rdb = db.collection("active_room").document("room1");
    Map<String, Object> roomInDb = new HashMap<>();

    public Game() {
        for(int i = 0; i < 4; ++i) {
            String path = "player" + i;
            pdb.document(path).get().addOnCompleteListener((task) -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        players.add(document.toObject(Player.class));
                        Log.d("GameActivity", "DocumentSnapshot data");
                    }
                }
            });
        }
        if(players.size() == 0) {
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
            roomInDb.put("targetIsSet", targetIsSet);
            roomInDb.put("RoundEnd", isRoundEnd());
            rdb.set(roomInDb)
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
        }else{
            rdb.get().addOnCompleteListener((task) -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        roomInDb.put("round", document.get("round"));
                        roomInDb.put("trump", document.get("trump"));
                        roomInDb.put("startSuit", null);
                        roomInDb.put("targetIsSet", targetIsSet);
                        roomInDb.put("RoundEnd", isRoundEnd());
                        Log.d("GameActivity", "DocumentSnapshot data");
                    }
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

    public Suit getTrump() {
        return trump;
    }

    public Suit getStartSuit() {
        return startSuit;
    }

    public void setStartSuit(Suit suit) {
        this.startSuit = suit;
    }

    public boolean isTargetSet() {
        return targetIsSet;
    }

    public void setTargetIsSet(boolean b) {
        this.targetIsSet = b;
    }

    public boolean isRoundEnd() {
        return RoundEnd;
    }

    public void setRoundEnd(boolean roundEnd) {
        RoundEnd = roundEnd;
    }

    // Note:
    // startGame is a complete game run for a total of 13 round
    // Need to take care user input for trump, target, cards and bid
    public void startGame() {
        threads[4] = new Thread(() -> {
            Log.d("Game", "Game is started");
            while (round < 14) {

                rdb.get().addOnCompleteListener((task) -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null) {
                            if (document.exists()) {
                                setTargetIsSet(Boolean.getBoolean(Objects.requireNonNull(document.get("targetIsSet")).toString()));
                                setRoundEnd(Boolean.getBoolean(Objects.requireNonNull(document.get("RoundEnd")).toString()));
                                Log.d("GameActivity", "DocumentSnapshot data");
                            } else {
                                Log.d("GameActivity", "No such document");
                            }
                        }
                    } else {
                        Log.d("GameActivity", "get failed with ", task.getException());
                    }
                });
                //initRound();
                if (threads[0] == null && threads[1] == null && threads[2] == null && threads[3] == null) {
                    initRound();
                }
                if (!threads[0].isAlive() && !threads[1].isAlive() && !threads[2].isAlive()
                        && !threads[3].isAlive() && !isTargetSet()) {
                    initRound();
                }
                //oneRound();
//                if (!threads[0].isAlive() && !threads[1].isAlive() && !threads[2].isAlive()
//                        && !threads[3].isAlive() && isTargetSet()) {
//                    if(isRoundEnd()){
//                        round++;
//                        setTargetIsSet(false);
//                        setRoundEnd(false);
//                        rdb.update("targetIsSet", false, "roundEnd", false)
//                                .addOnCompleteListener(task -> {
//                                    if (task.isSuccessful()) {
//                                        Log.d("Game", "targetIsSet = false");
//                                    } else {
//                                        Log.d("Game", "Error when targetIsSet = false");
//                                    }
//                                });
//                    }else{
//                        for(int r = 0; r < round; ++r) {
//                            while(threads[0].isAlive() && threads[1].isAlive() && threads[2].isAlive() && threads[3].isAlive()){} //do nothing
//                            for (int i = 0; i < 4; ++i) {
//                                int id = (((round - 1) % 4) + i) % 4;
//                                addThreadForPlayingCard(id, i);
//                            }
//                        }
//                    }
//                }

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Player winner = players.get(0);
            for (Player player : players) {
                if (player.getScore() > winner.getScore())
                    winner = player;
            }
            resetGame();
        });
        threads[4].start();
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
                                    if (i == 3 && totalTarget == round) {
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
                    pdb.document(path).update("settingTarget", false)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Log.d("Game", "disable setting target in thread");
                                } else {
                                    Log.d("Game", "Error when disable setting target in thread");
                                }
                            });
                    pdb.document(path).update( "target", players.get(id).getTarget());
                    if (i != 3) threads[i + 1].start();
                    return;
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        if (i == 0) threads[i].start();
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

    private void addThreadForPlayingCard(int id, int i) {
        String path = "player" + id;

        threads[i] = new Thread(() -> {
            Log.d("Game", "Thread is added");
            //enable setting target
            players.get(id).selectingCard = true;
            pdb.document(path).update("selectingCard", true)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("Game", "enable selecting card");
                        } else {
                            Log.d("Game", "Error when enable selecting card");
                        }
                    });
            //checking loop
            while (true) {
                if (!players.get(id).selectingCard) {
                    pdb.document(path).get().addOnCompleteListener((task) -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null) {
                                if (document.exists()) {
                                    Card card = (Card) document.get("playingCard");
                                    cardsToCompare.add(card);
                                    Log.d("Game", "DocumentSnapshot data");
                                } else {
                                    Log.d("Game", "No such document");
                                }
                            }
                        } else {
                            Log.d("GameActivity", "get failed with ", task.getException());
                        }
                    });
                    pdb.document(path).update("selectingCard", false)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Log.d("Game", "disable selectingCard in thread");
                                } else {
                                    Log.d("Game", "Error when disable selectingCard in thread");
                                }
                            });
                    if (cardsToCompare.size() == 4) {
                        Card winner = cardsToCompare.get(0);
                        for (Card card : cardsToCompare) {
                            if (winner.getSuit() == card.getSuit()) {
                                if (card.getRank() > winner.getRank())
                                    winner = card;
                            } else {
                                if (card.getSuit() == trump)
                                    winner = card;
                            }
                        }
                        winnerID = (((round - 1) % 4) + cardsToCompare.indexOf(winner)) % 4;
                        Log.d("turn winner: ", String.valueOf(winnerID));
                    }
                    if (i != 3) threads[i + 1].start();
                    return;
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        if (i == 0) threads[i].start();
    }

    private Player firstTrial() {

        List<Card> pile = cardsToCompare;

//        Card card1 = players.get((round - 1) % 4).selectCard(null);
//        pile.add(card1);
//        Suit start = card1.getSuit();
//        // need to ask user input
//        for (int i = 1; i < 4; i++) {
//            pile.add(players.get((((round - 1) % 4) + i) % 4).selectCard(start));
//        }

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
        rdb.update(roomInDb)
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
