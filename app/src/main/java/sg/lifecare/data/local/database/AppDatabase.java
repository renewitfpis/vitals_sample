package sg.lifecare.data.local.database;


import android.content.Context;

import java.io.FileNotFoundException;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import sg.lifecare.framework.di.ApplicationContext;
import timber.log.Timber;

public class AppDatabase {

    private RealmConfiguration mRealmConfiguration;

    @Inject
    public AppDatabase(@ApplicationContext final Context context) {
        Timber.d("AppDatabase");
        Realm.init(context);

        if (mRealmConfiguration == null) {
            mRealmConfiguration = new RealmConfiguration.Builder()
                    .name("app-db")
                    .schemaVersion(2)
                    .build();

        }

        try {
            Realm.migrateRealm(mRealmConfiguration, new Migration());
        } catch (FileNotFoundException e) {
            Timber.e(e.getMessage(), e);
        }
    }

    public Realm getRealm() {
        return Realm.getInstance(mRealmConfiguration);
    }
}
