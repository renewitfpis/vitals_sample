package sg.lifecare.vitals2.di.component;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Component;
import sg.lifecare.data.DataManager;
import sg.lifecare.data.remote.UnauthorizedInterceptor;
import sg.lifecare.framework.di.ApplicationContext;
import sg.lifecare.vitals2.VitalsApp;
import sg.lifecare.vitals2.di.module.ApplicationModule;

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    void inject(VitalsApp app);
    void inject(UnauthorizedInterceptor unauthorizedInterceptor);

    @ApplicationContext
    Context context();

    Application application();

    DataManager getDataManager();
}
