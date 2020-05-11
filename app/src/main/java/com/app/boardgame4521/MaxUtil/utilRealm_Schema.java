package com.app.boardgame4521.MaxUtil;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

public final class utilRealm_Schema implements RealmMigration {
    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();

        //version 1 : 11/5/2020 23:33
        //1st test
        //class : test_class_1
        if (oldVersion == 0){
            schema.create("test_class_1")
                    .addField("name",String.class)
                    .addField("num",int.class);
            oldVersion++;
        }
    }
}
