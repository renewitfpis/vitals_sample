package sg.lifecare.vitals2.ui.dashboard;


import java.util.List;

import sg.lifecare.data.remote.model.response.AssistsedEntityResponse;
import sg.lifecare.data.remote.model.response.EntityDetailResponse;
import sg.lifecare.data.remote.model.response.LogoutResponse;
import sg.lifecare.vitals2.ui.base.MvpView;

public interface DashboardMvpView extends MvpView {

    void startLoginActivity();

    void onUserEntityResult(EntityDetailResponse.Data userEntity);

    void onMembersEntityResult(List<AssistsedEntityResponse.Data> membersEntity);

    void onLogoutResult(LogoutResponse response);
}
