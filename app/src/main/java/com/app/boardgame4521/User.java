package com.app.boardgame4521;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Calendar;

//class for representing a user
//use
public class User {
    //one instance of current user
    private static User currentUser = new User();

    //data
    public boolean isSet = false;
    public String name;
    public int level;
    public Calendar reg_date;
    public int start_playing_time;

    //func
    public static User getCurrentUser(){return currentUser.isSet?currentUser:null;}

    public static void firstTimeSet(GoogleSignInAccount googleAC, FirebaseUser firebaseAC, DocumentSnapshot userDataFromFirestore){
        if (currentUser.isSet){return;}
        currentUser.isSet = true;
        currentUser.name = firebaseAC.getDisplayName();
        currentUser.level = (int) userDataFromFirestore.get("level");
    }
}
