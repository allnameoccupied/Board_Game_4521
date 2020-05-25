package com.app.boardgame4521;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.Timestamp;
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
    public String email;
    public Timestamp last_login;
    public int level;
    public int match_played;
    public String name;
    public Timestamp reg_date;

    //func
    public static User getCurrentUser(){return currentUser;}

    public static void firstTimeSet(DocumentSnapshot userDataSnapshot){
        if (currentUser.isSet){return;}
        currentUser.isSet = true;
        currentUser.email = userDataSnapshot.getId();
        currentUser.last_login = userDataSnapshot.getTimestamp("last_login");
        currentUser.level = ((Long)userDataSnapshot.get("level")).intValue();
        currentUser.match_played = ((Long)userDataSnapshot.get("match_played")).intValue();
        currentUser.name = userDataSnapshot.getString("name");
        currentUser.reg_date = userDataSnapshot.getTimestamp("reg_date");
    }

    public static void resetUser(){currentUser = new User();}
}
