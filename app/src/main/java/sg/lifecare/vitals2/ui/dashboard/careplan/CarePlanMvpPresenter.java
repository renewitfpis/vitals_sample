package sg.lifecare.vitals2.ui.dashboard.careplan;

import sg.lifecare.data.remote.model.response.AssignedTaskResponse;
import sg.lifecare.framework.di.PerActivity;
import sg.lifecare.vitals2.ui.base.MvpPresenter;
import sg.lifecare.vitals2.ui.base.MvpView;

@PerActivity
public interface CarePlanMvpPresenter<V extends MvpView> extends MvpPresenter<V> {

    void getUserAssignedTasks();

    void postBloodGlucoseTask(AssignedTaskResponse.Data task);
}
