package com.app.boardgame4521.MaxUtil;

import android.content.Context;
import android.content.Intent;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import androidx.fragment.app.FragmentActivity;

public final class utilGoogle {
    //BASE
    public static Context APP_CONTEXT;

    //LIFECYCLE
    public static void INIT(){
        APP_CONTEXT = util.APP_CONTEXT;

        //google part
        googleAccount = GoogleSignIn.getLastSignedInAccount(APP_CONTEXT);
        logoutGoogle();
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestProfile()
                .requestEmail()
                .requestIdToken(utilConfidential.GOOGLE_LOGIN_CREDENTIAL)
                .requestServerAuthCode(utilConfidential.GOOGLE_LOGIN_CREDENTIAL)
                .build();
        googleSignInClient = GoogleSignIn.getClient(APP_CONTEXT,signInOptions);

        //firebase part
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        logoutFirebase();

        //firestore part
//        firestore = FirebaseFirestore.getInstance();

        //firebase force re-login
//        if (isUserSignedInGoogle()){
//            signInFirebase();
//        }
    }

    //GOOGLE
    private static GoogleSignInAccount googleAccount;
    private static GoogleSignInClient googleSignInClient;
    public static final int googleSignInActivityRequestCode = 69;

    public static GoogleSignInAccount getUserGoogleAccount(){return googleAccount;}

    public static boolean isUserSignedInGoogle(){return googleAccount != null;}

    /** also call signInGoogle_handleResult in activity handleActivityResult
     * to handle sign in
     *
     * @param currentActivity: current activity for start activity
     * @return activity request code for handle result
     */
    public static int signInGoogle(FragmentActivity currentActivity){
        logoutGoogle();
        currentActivity.startActivityForResult(new Intent(googleSignInClient.getSignInIntent()),googleSignInActivityRequestCode);
        return googleSignInActivityRequestCode;
    }

    public static void signInGoogle_handleResult(Intent data){
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            googleAccount = task.getResult(ApiException.class);
            signInFirebase();
        } catch (Throwable e){
            util.log(e.getMessage());
        }
    }

    public static void logoutGoogle(){if (isUserSignedInGoogle())googleSignInClient.signOut();}

    public static void disconnectGoogle(){if (isUserSignedInGoogle())googleSignInClient.revokeAccess();}

    //FIREBASE
    private static FirebaseUser firebaseUser;

    public static FirebaseUser getFirebaseUser(){return firebaseUser;}

    public static boolean isUserSignedInFirebase(){return firebaseUser != null;}

    public static void signInFirebase(){
        if (!isUserSignedInGoogle()){return;}
        logoutFirebase();
        AuthCredential credential = GoogleAuthProvider.getCredential(googleAccount.getIdToken(),null);
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            uponFirebaseLogin();
                        }
                });
    }

    public static void logoutFirebase(){if (isUserSignedInFirebase())FirebaseAuth.getInstance().signOut();}

    //FIRESTORE
    private static FirebaseFirestore firestore;

    //user related in firestore
    private static CollectionReference USER_COLLECTION;
    //user structure
    /** name of document: user email
     *      field:
     *      last_login: last time user login
     *      level: level ( = x matches required to complete x levels)(lv 1 -> 1 game -> lv 2 -> 2 games -> lv 3)
     *      match_played: match played from first login
     *      name: name of user (accord to GOOGLE AC)
     *      reg_date: first time user login
     */

    private static CollectionReference ACTIVE_ROOM_COLLECTION;

    /** add code here if u want to do sth with firestore upon user login*/
    private static void uponFirebaseLogin(){
        //init firestore here
        firestore = FirebaseFirestore.getInstance();
        USER_COLLECTION = firestore.collection("users");
        ACTIVE_ROOM_COLLECTION = firestore.collection("active_room");

        //check if new user
        DocumentReference userRef = USER_COLLECTION.document(firebaseUser.getEmail());
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                if (!task.getResult().exists()){
                    //first time login
                    userRef.set(genMapforFirstTimeUser());
                } else {
                    //already a user
                    DocumentSnapshot userFromFirestore = task.getResult();

                }
            }
        });
    }

    private static Map<String,Object> genMapforFirstTimeUser(){
        Map<String,Object> userMap = new HashMap<>();
        userMap.put("last_login", FieldValue.serverTimestamp());
        userMap.put("level", 1);
        userMap.put("match_played", 0);
        userMap.put("name", googleAccount.getDisplayName());
        userMap.put("reg_date", FieldValue.serverTimestamp());
        return userMap;
    }

    public static FirebaseFirestore getFirestore(){return firestore;}
}
