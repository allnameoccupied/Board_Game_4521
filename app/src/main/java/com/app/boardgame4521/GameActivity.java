package com.app.boardgame4521;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class GameActivity extends AppCompatActivity {

    Game game = new Game();
    int rnd = new Random().nextInt() % 4;
    Player thisUser = game.getPlayer(rnd);
    private int tmpTarget = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

//        findViewById(R.id.bt_down).setOnClickListener(this::targetDownHandler);
//        findViewById(R.id.bt_up).setOnClickListener(this::targetUpHandler);
    }
//    public void targetDownHandler(View view){
//        if(tmpTarget > 0) tmpTarget--;
//    }
//    public void targetUpHandler(View view){
//        tmpTarget++;
//    }


}
