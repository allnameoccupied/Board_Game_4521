package com.app.boardgame4521.MaxUtil;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public final class utilRealm {
    //BASE
    public static Context APP_CONTEXT;
    private static Realm realm;

    //LIFECYCLE
    public static void INIT(){
        APP_CONTEXT = util.APP_CONTEXT;

        Realm.init(APP_CONTEXT);
        RealmConfiguration configuration = new RealmConfiguration.Builder()
                .schemaVersion(1)
                .migration(new utilRealm_Schema())
//                .deleteRealmIfMigrationNeeded()     //for developing use
                .build();
        realm = Realm.getInstance(configuration);

//        resetRealm();   //for developing use
    }

    //REALM
    public static Realm getRealm(){return realm;}

    public static void executeTransaction(Realm.Transaction transaction){
        realm.executeTransaction(transaction);
    }

    public static void shutdownRealm(){realm.close();}

    public static void resetRealm(){executeTransaction(realm1 -> realm.deleteAll());}
}
