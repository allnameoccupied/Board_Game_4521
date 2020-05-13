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
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.fragment.app.FragmentActivity;

public final class utilGoogle {
    //BASE
    public static Context APP_CONTEXT;

    //LIFECYCLE
    public static void INIT(){
        APP_CONTEXT = util.APP_CONTEXT;

        //google part
        googleAccount = GoogleSignIn.getLastSignedInAccount(APP_CONTEXT);
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestProfile()
                .requestEmail()
                .requestIdToken(utilConfidential.GOOGLE_LOGIN_CREDENTIAL)
                .requestServerAuthCode(utilConfidential.GOOGLE_LOGIN_CREDENTIAL)
                .build();
        googleSignInClient = GoogleSignIn.getClient(APP_CONTEXT,signInOptions);

        //firebase part
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //firestore part
        firestore = FirebaseFirestore.getInstance();
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
        } catch (Throwable e){
            util.log(e.getMessage());
        }
    }

    public static void logoutGoogle(){googleSignInClient.signOut();}

    public static void disconnectGoogle(){googleSignInClient.revokeAccess();}

    //FIREBASE
    private static FirebaseUser firebaseUser;

    public static FirebaseUser getFirebaseUser(){return firebaseUser;}

    public static boolean isUserSignedInFirebase(){return firebaseUser != null;}

    public static void signInFirebase(){
        if (!isUserSignedInGoogle()){return;}
        AuthCredential credential = GoogleAuthProvider.getCredential(googleAccount.getIdToken(),null);
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        }
                });
    }

    public static void logoutFirebase(){FirebaseAuth.getInstance().signOut();}

    //FIRESTORE
    private static FirebaseFirestore firestore;

    public static FirebaseFirestore getFirestore(){return firestore;}
}
