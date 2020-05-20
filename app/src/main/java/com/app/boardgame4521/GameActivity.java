package com.app.boardgame4521;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class GameActivity extends AppCompatActivity {

    Game game = new Game();
    int rnd = new Random().nextInt() % 4;
    Player thisUser = game.getPlayers().get(rnd);
    TextView myTarget;
    private int tmpTarget = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        findViewById(R.id.bt_down).setOnClickListener(this::targetDownHandler);
        findViewById(R.id.bt_up).setOnClickListener(this::targetUpHandler);
        findViewById(R.id.bt_confirm).setOnClickListener(this::targetConfirmHandler);

        myTarget = findViewById(R.id.myTarget);
    }
    public void targetDownHandler(View view){
        if(tmpTarget > 0) tmpTarget--;
        myTarget.setText(String.valueOf(tmpTarget));
    }
    public void targetUpHandler(View view){
        tmpTarget++;
        myTarget.setText(String.valueOf(tmpTarget));
    }
    public void targetConfirmHandler(View view){
        findViewById(R.id.bt_down).setEnabled(false);
        findViewById(R.id.bt_up).setEnabled(false);
    }

}
