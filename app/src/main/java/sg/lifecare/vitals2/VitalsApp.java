package sg.lifecare.vitals2;


import android.support.multidex.MultiDexApplication;

import com.facebook.stetho.Stetho;

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
    }

    public ApplicationComponent getComponent() {
        return mApplicationComponent;
    }

    public void setComponent(ApplicationComponent applicationComponent) {
        mApplicationComponent = applicationComponent;
    }
}
