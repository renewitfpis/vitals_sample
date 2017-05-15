package sg.lifecare.vitals2.di.component;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Component;
import sg.lifecare.framework.data.DataManager;
import sg.lifecare.framework.di.ApplicationContext;
import sg.lifecare.framework.di.module.ApplicationModule;
import sg.lifecare.vitals2.VitalsApp;

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    void inject(VitalsApp app);

    @ApplicationContext
    Context context();

    Application application();

    DataManager getDataManager();
}
