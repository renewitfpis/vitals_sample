package sg.lifecare.vitals2.ui.dashboard.careplan;


import sg.lifecare.data.remote.model.response.AssignedTaskResponse;
import sg.lifecare.vitals2.ui.base.MvpView;

public interface CarePlanMvpView extends MvpView {

    void onAssignedTasksResult(AssignedTaskResponse response);
}
