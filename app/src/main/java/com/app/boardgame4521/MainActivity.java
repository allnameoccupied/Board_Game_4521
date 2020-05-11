package com.app.boardgame4521;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.app.boardgame4521.MaxUtil.util;
import com.app.boardgame4521.MaxUtil.utilGoogle;
import com.app.boardgame4521.MaxUtil.utilRealm;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //maxutil
        util.INIT(getApplicationContext());
    }
}
