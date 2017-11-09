package sg.lifecare.vitals2.ui.dashboard;

import sg.lifecare.framework.di.PerActivity;
import sg.lifecare.vitals2.ui.base.MvpPresenter;

@PerActivity
public interface DashboardMvpPresenter<V extends DashboardMvpView> extends MvpPresenter<V> {

    boolean loadOfflineData();

    void getUserEntity();

    void getMembersEntity();

    void logout();
}
