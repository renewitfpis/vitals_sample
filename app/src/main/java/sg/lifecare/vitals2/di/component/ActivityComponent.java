package sg.lifecare.vitals2.di.component;

import dagger.Component;
import sg.lifecare.framework.di.PerActivity;
import sg.lifecare.framework.di.module.ActivityModule;
import sg.lifecare.vitals2.TestActivity;

@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(TestActivity testActivity);
}
