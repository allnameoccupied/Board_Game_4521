/**
 * # COMP 4521    #  Chan Hung Yan        20423715          hychanbr@connect.ust.hk
 * # COMP 4521    #  Tai Ka Chun          20433540          kctaiab@connect.ust.hk
 * # COMP 4521    #  Mong Kin Ip          20433629          kimong@connect.ust.hk
 */

package com.app.boardgame4521;

import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.app.boardgame4521.enumm.Position;
import com.app.boardgame4521.enumm.Suit;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Objects;

public class GameActivity extends AppCompatActivity {

    static Game game = new Game();
    int myPos = 0;
    String path = "player" + myPos;
    Player thisPlayer = game.getPlayers().get(myPos); //need to change

    TextView myTarget;
    ImageView trump_img;
    TextView round_number;
    private int tmpTarget = 0;
    private ImageView[] cardImgs = new ImageView[13];
    private ImageView my_card;
    private ImageView east_card;
    private ImageView west_card;
    private ImageView north_card;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference rdb = db.collection("active_room").document("room1");
    CollectionReference pdb = db.collection("active_room").document("room1").collection("players");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        findViewById(R.id.bt_down).setOnClickListener(this::targetDownHandler);
        findViewById(R.id.bt_up).setOnClickListener(this::targetUpHandler);
        findViewById(R.id.bt_confirm).setOnClickListener(this::targetConfirmHandler);
        myTarget = findViewById(R.id.myTarget);
        trump_img = findViewById(R.id.trump_img);
        round_number = findViewById(R.id.round_number);
        my_card = findViewById(R.id.myCard);
        east_card = findViewById(R.id.east_card);
        west_card = findViewById(R.id.west_card);
        north_card = findViewById(R.id.north_card);
        initCardImgArray();
        game.startGame();
        initRoundUI();
    }

    private void initRoundUI() {
        rdb.get().addOnCompleteListener((task) -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null) {
                    if (document.exists()) {
                        setTrumpImg(Suit.valueOf(Objects.requireNonNull(document.get("trump")).toString()));
                        round_number.setText(Objects.requireNonNull(document.get("round")).toString());
                        Log.d("GameActivity", "DocumentSnapshot data");
                    } else {
                        Log.d("GameActivity", "No such document");
                    }
                }
            } else {
                Log.d("GameActivity", "get failed with ", task.getException());
            }
        });
        pdb.document(path).get().addOnCompleteListener((task) -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null) {
                    if (document.exists()) {
                        thisPlayer.setCards(Objects.requireNonNull(document.toObject(Player.class)).getCards());
                        refreshCardImg(thisPlayer);
                        Log.d("GameActivity", "DocumentSnapshot data");
                    } else {
                        Log.d("GameActivity", "No such document");
                    }
                }
            } else {
                Log.d("GameActivity", "get failed with ", task.getException());
            }
        });
    }

    public void targetDownHandler(View view) {
        if (tmpTarget == 0) return;
        tmpTarget--;
        myTarget.setText(String.valueOf(tmpTarget));
    }

    public void targetUpHandler(View view) {
        if (tmpTarget == game.getRound()) return;
        tmpTarget++;
        myTarget.setText(String.valueOf(tmpTarget));
    }

    public void targetConfirmHandler(View view) {
        pdb.document(path).get().addOnCompleteListener((task) -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    game.getPlayers().get(myPos).settingTarget = Boolean.getBoolean(Objects.requireNonNull(document.get("settingTarget")).toString());
                    Log.d("GameActivity", "onclick");
                }
            }
        });
        if (game.getPlayers().get(myPos).settingTarget) {
            //add target to db
            Log.d("GameActivity", "setting target = true");
            pdb.document(path).update("target", Integer.parseInt(myTarget.getText().toString()))
                    //("target", Integer.parseInt(myTarget.getText().toString()), "settingTarget", false)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            game.getPlayers().get(myPos).settingTarget = false;
                            Log.d("GameActivity", "target added");
                        }
                    });
            pdb.document(path).update("settingTarget", false);
            lockBid();
            //check if target needed to be changed
            pdb.document(path).get().addOnCompleteListener((task) -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        if (!document.get("target").toString().equals(myTarget.getText().toString())) { //check if need to change or not
                            myTarget.setText(Objects.requireNonNull(document.get("target")).toString());
                        }
                    }
                }
            });
        }
    }

    public void lockBid() {
        findViewById(R.id.bt_down).setEnabled(false);
        findViewById(R.id.bt_up).setEnabled(false);
    }

    public void startBid() {
        findViewById(R.id.bt_down).setEnabled(true);
        findViewById(R.id.bt_up).setEnabled(true);
    }

    public void selectCardHandler(View view) {
        int cardNo = Integer.parseInt(view.getTag().toString()); //the position of the card
        Card c = thisPlayer.getCards().get(cardNo);
        Suit s = game.getStartSuit();
        if (thisPlayer.selectingCard && thisPlayer.isValidPlay(c, s)) {
            view.setVisibility(View.GONE);
            setPlayCard(Position.S, c);
            thisPlayer.setPlayingCard(c);
            if (s == null) game.setStartSuit(c.getSuit());
            //add playing card to db
            pdb.document(path).update("playingCard", thisPlayer.getPlayingCard())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("GameActivity", "card played");
                        } else {
                            Log.d("GameActivity", "Error when playing card");
                        }
                    });
            rdb.update("startSuit", c.getSuit())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("GameActivity", "start suit set");
                        } else {
                            Log.d("GameActivity", "Error when setting start suit");
                        }
                    });
            thisPlayer.selectingCard = false;
        }
    }

    public void setPlayCard(Position pos, Card card) {
        Resources res = getResources();
        String mDrawableName = "card_" + card.getRank()
                + card.getSuit().toString().substring(0, 1).toLowerCase();
        int resID = res.getIdentifier(mDrawableName, "drawable", getPackageName());
        switch (pos) {
            case E:
                east_card.setVisibility(View.VISIBLE);
                east_card.setImageResource(resID);
                break;
            case S:
                my_card.setVisibility(View.VISIBLE);
                my_card.setImageResource(resID);
                break;
            case W:
                west_card.setVisibility(View.VISIBLE);
                west_card.setImageResource(resID);
                break;
            case N:
                north_card.setVisibility(View.VISIBLE);
                north_card.setImageResource(resID);
                break;
        }
    }

    public void setTrumpImg(Suit suit) {
        switch (suit) {
            case Diamond:
                trump_img.setImageResource(R.drawable.diamond);
                break;
            case Club:
                trump_img.setImageResource(R.drawable.club);
                break;
            case Heart:
                trump_img.setImageResource(R.drawable.heart);
                break;
            case Spade:
                trump_img.setImageResource(R.drawable.spade);
                break;
            default:
                break;
        }
    }

    public void refreshCardImg(Player player) {
        List<Card> pile = player.getCards();

        for (int i = 0; i < pile.size(); ++i) { //loading images
            Resources res = getResources();
            String mDrawableName = "card_" + pile.get(i).getRank()
                    + pile.get(i).getSuit().toString().substring(0, 1).toLowerCase();
            int resID = res.getIdentifier(mDrawableName, "drawable", getPackageName());
            cardImgs[i].setVisibility(View.VISIBLE);
            cardImgs[i].setImageResource(resID);
        }
        for (int i = pile.size(); i < 13; ++i) { //remove remaining card images
            cardImgs[i].setVisibility(View.GONE);
        }
    }

    public void initCardImgArray() {
        cardImgs[0] = findViewById(R.id.card0);
        cardImgs[1] = findViewById(R.id.card1);
        cardImgs[2] = findViewById(R.id.card2);
        cardImgs[3] = findViewById(R.id.card3);
        cardImgs[4] = findViewById(R.id.card4);
        cardImgs[5] = findViewById(R.id.card5);
        cardImgs[6] = findViewById(R.id.card6);
        cardImgs[7] = findViewById(R.id.card7);
        cardImgs[8] = findViewById(R.id.card8);
        cardImgs[9] = findViewById(R.id.card9);
        cardImgs[10] = findViewById(R.id.card10);
        cardImgs[11] = findViewById(R.id.card11);
        cardImgs[12] = findViewById(R.id.card12);
        for (int i = 0; i < 13; ++i) {
            cardImgs[i].setTag(i);
        }
    }
}