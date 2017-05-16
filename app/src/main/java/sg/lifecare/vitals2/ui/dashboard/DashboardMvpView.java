package sg.lifecare.vitals2.ui.dashboard;


import sg.lifecare.data.remote.model.response.EntityDetailResponse;
import sg.lifecare.vitals2.ui.base.MvpView;

public interface DashboardMvpView extends MvpView {

    void startLoginActivity();

    void onUserEntityUpdate(EntityDetailResponse.Data userEntity);
}
