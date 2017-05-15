package sg.lifecare.vitals2;


import android.app.Application;

import com.facebook.stetho.Stetho;

import javax.inject.Inject;

import sg.lifecare.framework.data.DataManager;
import sg.lifecare.framework.di.module.ApplicationModule;
import sg.lifecare.vitals2.di.component.ApplicationComponent;
import sg.lifecare.vitals2.di.component.DaggerApplicationComponent;
import timber.log.Timber;

public class VitalsApp extends Application {

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
