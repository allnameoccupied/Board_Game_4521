package com.app.boardgame4521;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.app.boardgame4521.MaxUtil.util;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //maxutil
        util.setAppContext(getApplicationContext());
        util.INIT();
    }
}
