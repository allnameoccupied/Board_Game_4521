package com.app.boardgame4521;

import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.app.boardgame4521.enumm.Suit;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameActivity extends AppCompatActivity {

    Game game = new Game();
    int rnd = new Random().nextInt() % 4;
    Player thisUser = game.getPlayers().get(1); //need to change
    TextView myTarget;
    ImageView trump_img;
    private int tmpTarget = 0;
    private ImageView[] cardImgs = new ImageView[13];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        findViewById(R.id.bt_down).setOnClickListener(this::targetDownHandler);
        findViewById(R.id.bt_up).setOnClickListener(this::targetUpHandler);
        findViewById(R.id.bt_confirm).setOnClickListener(this::targetConfirmHandler);
        myTarget = findViewById(R.id.myTarget);
        trump_img = findViewById(R.id.trump_img);
        initCardImgArray();
        setCardImg();
//        game.startGame();
//        setTrumpImg(Suit.Club);
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

    public void setTrumpImg(Suit suit) {
        String path = "R.drawable." + suit.toString().toLowerCase();
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
        }
    }

    public void setCardImg() {
        List<Card> pile = new ArrayList<>();
        //= player.getCards();
        Card card1 = new Card(10, Suit.Diamond);
        Card card2 = new Card(5, Suit.Heart);
        Card card3 = new Card(8, Suit.Club);
        pile.add(card1);
        pile.add(card2);
        pile.add(card3);
        for (Suit s : Suit.values()) {
                Card card = new Card(2, s);
                pile.add(card);
        }
        for (int i = 0; i < pile.size(); ++i) {
            Resources res = getResources();
            String mDrawableName = "card_" + pile.get(i).getRank()
                    + pile.get(i).getSuit().toString().substring(0,1).toLowerCase();
            int resID = res.getIdentifier(mDrawableName, "drawable", getPackageName());
            cardImgs[i].setVisibility(View.VISIBLE);
            cardImgs[i].setImageResource(resID);
        }
    }

    public void initCardImgArray() {
        cardImgs[0] = findViewById(R.id.card1);
        cardImgs[1] = findViewById(R.id.card2);
        cardImgs[2] = findViewById(R.id.card3);
        cardImgs[3] = findViewById(R.id.card4);
        cardImgs[4] = findViewById(R.id.card5);
        cardImgs[5] = findViewById(R.id.card6);
        cardImgs[6] = findViewById(R.id.card7);
        cardImgs[7] = findViewById(R.id.card8);
        cardImgs[8] = findViewById(R.id.card9);
        cardImgs[9] = findViewById(R.id.card10);
        cardImgs[10] = findViewById(R.id.card11);
        cardImgs[11] = findViewById(R.id.card12);
        cardImgs[12] = findViewById(R.id.card13);
    }
}