/**
 * # COMP 4521    #  Chan Hung Yan        20423715          hychanbr@connect.ust.hk
 * # COMP 4521    #  Tai Ka Chun          20433540          kctaiab@connect.ust.hk
 * # COMP 4521    #  Mong Kin Ip          20433629          kimong@connect.ust.hk
 */

package com.app.boardgame4521;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

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
        findViewById(R.id.google_login).setOnClickListener(this::maxTestButtonHandler1);
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
        util.log(User.getCurrentUser().isSet);
        util.log(User.getCurrentUser().email);
        util.log(User.getCurrentUser().name);
        util.log(User.getCurrentUser().reg_date.toString());
    }

    public void maxTestButtonHandler3(View view){
        startActivity(new Intent(MainActivity.this,Room_Selection.class));
    }

    public void maxTestButtonHandler4(View view){
        if (!utilGoogle.isOnlineReady()){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Please check your internet connection and login to Google to proceed");
            builder.setNeutralButton("OK",(dialog, which) -> dialog.dismiss());
            builder.show();
            return;
        }
        //should seperate choose room and create room
        Intent intent = new Intent(MainActivity.this, GameActivity.class);
        startActivity(intent);
    }
}
