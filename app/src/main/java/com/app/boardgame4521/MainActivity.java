package com.app.boardgame4521;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

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

        //max test button init
        findViewById(R.id.maxtest1).setOnClickListener(this::maxTestButtonHandler1);
        findViewById(R.id.maxtest2).setOnClickListener(this::maxTestButtonHandler2);
        findViewById(R.id.maxtest3).setOnClickListener(this::maxTestButtonHandler3);
        findViewById(R.id.maxtest4).setOnClickListener(this::maxTestButtonHandler4);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case utilGoogle.googleSignInActivityRequestCode : utilGoogle.signInGoogle_handleResult(data);
        }
    }

    public void maxTestButtonHandler1(View view){
        utilGoogle.signInGoogle(this);
    }

    public void maxTestButtonHandler2(View view){
        utilGoogle.signInFirebase();
    }

    public void maxTestButtonHandler3(View view){
        util.log(utilGoogle.getFirebaseUser().getEmail());
    }

    public void maxTestButtonHandler4(View view){
    }
}
