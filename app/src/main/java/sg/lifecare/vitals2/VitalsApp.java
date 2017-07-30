package sg.lifecare.vitals2;


import android.support.multidex.MultiDexApplication;

import com.facebook.stetho.Stetho;
import com.kitnew.ble.QNApiManager;
import com.kitnew.ble.QNResultCallback;
import com.kitnew.ble.utils.QNLog;

import javax.inject.Inject;

import sg.lifecare.data.DataManager;
import sg.lifecare.vitals2.di.component.ApplicationComponent;
import sg.lifecare.vitals2.di.component.DaggerApplicationComponent;
import sg.lifecare.vitals2.di.module.ApplicationModule;
import timber.log.Timber;

public class VitalsApp extends MultiDexApplication {

    @Inject
    DataManager mDataManager;

    private ApplicationComponent mApplicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        Timber.plant(new Timber.DebugTree());

        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this);
        }

        mApplicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this)).build();
        mApplicationComponent.inject(this);

        QNLog.DEBUG = true;
        QNApiManager.getApi(getApplicationContext()).initSDK("123456789", false,
                new QNResultCallback() {
                    @Override
                    public void onCompete(int errorCode) {
                        Timber.i("QN init result %d", errorCode);
                    }
                });

    }

    public ApplicationComponent getComponent() {
        return mApplicationComponent;
    }

    public void setComponent(ApplicationComponent applicationComponent) {
        mApplicationComponent = applicationComponent;
    }
}
