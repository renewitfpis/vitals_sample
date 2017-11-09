package sg.lifecare.vitals2.ui.dashboard;


import sg.lifecare.data.remote.model.response.EntityDetailResponse;
import sg.lifecare.data.remote.model.response.LogoutResponse;
import sg.lifecare.vitals2.ui.base.MvpView;

public interface DashboardMvpView extends MvpView {

    void startLoginActivity();

    void onUserEntityDetailResult(EntityDetailResponse.Data userEntity);

    void onLogoutResult(LogoutResponse response);
}
