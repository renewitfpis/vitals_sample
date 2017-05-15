package sg.lifecare.vitals2.di.module;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import sg.lifecare.data.DataManager;
import sg.lifecare.data.remote.LifecareService;
import sg.lifecare.framework.di.ApplicationContext;

@Module
public class ApplicationModule {

    private final Application mApplication;

    public ApplicationModule(Application application) {
        mApplication = application;
    }

    @Provides
    @ApplicationContext
    Context provideContext() {
        return mApplication;
    }

    @Provides
    Application provideApplication() {
        return mApplication;
    }

    @Provides
    @Singleton
    LifecareService provideLifecareService() {
        return LifecareService.Factory.makeLifecareService(mApplication);
    }

    //@Provides
    //@Singleton
    //RxBus provideRxBus(RxBus rxBus) {
    //    return new RxBus();
    //}
}
