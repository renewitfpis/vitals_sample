package sg.lifecare.vitals2.di.component;

import dagger.Component;
import sg.lifecare.framework.di.PerActivity;
import sg.lifecare.vitals2.TestActivity;
import sg.lifecare.vitals2.di.module.ActivityModule;
import sg.lifecare.vitals2.ui.dashboard.DashboardActivity;
import sg.lifecare.vitals2.ui.login.ForgotPasswordFragment;
import sg.lifecare.vitals2.ui.login.LoginActivity;

@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(TestActivity testActivity);

    void inject(LoginActivity loginActivity);
    void inject(ForgotPasswordFragment forgotPasswordFragment);

    void inject(DashboardActivity dashboardActivity);
}
