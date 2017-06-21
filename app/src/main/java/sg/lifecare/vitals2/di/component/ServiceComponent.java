package sg.lifecare.vitals2.di.component;

import dagger.Component;
import sg.lifecare.framework.di.PerService;
import sg.lifecare.vitals2.di.module.ServiceModule;
import sg.lifecare.vitals2.services.SyncService;

@PerService
@Component(dependencies = ApplicationComponent.class, modules = ServiceModule.class)
public interface ServiceComponent {

    void inject(SyncService service);
}
