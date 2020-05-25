package com.app.boardgame4521;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.app.boardgame4521.enumm.Position;
import com.app.boardgame4521.enumm.Suit;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Objects;

public class GameActivity extends AppCompatActivity {

    Game game = new Game();
    Player thisPlayer = game.getPlayers().get(1); //need to change

    TextView myTarget;
    ImageView trump_img;
    TextView round_number;
    private int tmpTarget = 0;
    private ImageView[] cardImgs = new ImageView[13];
    boolean validPlay = true;
    private ImageView my_card;
    private ImageView east_card;
    private ImageView west_card;
    private ImageView north_card;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference ref = db.collection("active_room").document("room1");

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
        game.initRound();
        initRoundUI();

    }

    private void initRoundUI(){
        ref.get().addOnCompleteListener((task) -> {
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
        String path = "player" + 1; //need change
        ref.collection("players").document(path).get().addOnCompleteListener((task) -> {
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
        if (tmpTarget > 0) tmpTarget--;
        myTarget.setText(String.valueOf(tmpTarget));
    }

    public void targetUpHandler(View view) {
        if (tmpTarget < game.getRound()) tmpTarget++;
        myTarget.setText(String.valueOf(tmpTarget));
    }

    public void targetConfirmHandler(View view) {

        lockBid();
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
//        Card c = thisPlayer.getCards().get(cardNo);
        if (thisPlayer.isSelectingCard() && validPlay) {
            view.setVisibility(View.GONE);
            Card card1 = new Card(10, Suit.Diamond);//need to change
            setPlayCard(Position.S, card1);
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
        List<Card> pile;
//        pile = new ArrayList<>();
        pile = player.getCards();
        //example:
//        Card card1 = new Card(10, Suit.Diamond);
//        Card card2 = new Card(5, Suit.Heart);
//        Card card3 = new Card(8, Suit.Club);
//        pile.add(card1);
//        pile.add(card2);
//        pile.add(card3);
//        for (Suit s : Suit.values()) {
//            Card card = new Card(2, s);
//            pile.add(card);
//        }

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