package sg.lifecare.vitals2.ui.dashboard.careplan;


import android.text.TextUtils;

import java.net.SocketTimeoutException;
import java.util.Calendar;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;
import sg.lifecare.data.DataManager;
import sg.lifecare.data.remote.model.data.BloodGlucoseTaskData;
import sg.lifecare.data.remote.model.response.AssignedTaskResponse;
import sg.lifecare.vitals2.ui.base.BasePresenter;
import timber.log.Timber;

public class CarePlanPresenter<V extends CarePlanMvpView> extends BasePresenter<V>
        implements CarePlanMvpPresenter<V> {

    @Inject
    public CarePlanPresenter(DataManager dataManager, CompositeDisposable compositeDisposable) {
        super(dataManager, compositeDisposable);
    }

    @Override
    public void getUserAssignedTasks() {
        String entityId = getDataManager().getPreferencesHelper().getEntityId();

        if (TextUtils.isEmpty(entityId)) {
            Timber.w("getUserAssignedTasks: entityId is null");
            return;
        }

        getAssignedTasks(entityId);
    }

    @Override
    public void postBloodGlucoseTask(AssignedTaskResponse.Data task) {
        BloodGlucoseTaskData data = new BloodGlucoseTaskData();
        data.setTaskAssignId(task.getId());
        data.setEntityId(getDataManager().getUserEntity().getId());
        data.setCreateDate(Calendar.getInstance().getTime());
        data.setConcentration(5.4f);
        data.setMetricUnit();
        data.setTypeBeforeMeal();
        data.setRemarks("Test data");

        getMvpView().showLoading();

        getCompositeDisposable().add(
                getDataManager().postAssignedTaskForDevice(data)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> {
                            if (!isViewAttached()) {
                                return;
                            }

                            getMvpView().hideLoading();

                            /*if (response.isError()) {
                                getMvpView().onNetworkError(response.getErrorDesc());
                                return;
                            }F

                            getMvpView().onAssignedTasksResult(response);*/
                        }, throwable -> {
                            if (!isViewAttached()) {
                                return;
                            }

                            getMvpView().hideLoading();

                            if (throwable instanceof HttpException) {
                                handleNetworkError((HttpException) throwable);
                            } else if (throwable instanceof SocketTimeoutException) {
                                Timber.d("SocketTimeoutException");
                            }else {
                                Timber.e(throwable, throwable.getMessage());
                            }
                        }));
    }

    private void getAssignedTasks(String entityId) {

        getMvpView().showLoading();

        getCompositeDisposable().add(
                getDataManager().getAssignedTasks(entityId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> {
                            if (!isViewAttached()) {
                                return;
                            }

                            getMvpView().hideLoading();

                            if (response.isError()) {
                                getMvpView().onNetworkError(response.getErrorDesc());
                                return;
                            }

                            getMvpView().onAssignedTasksResult(response);
                        }, throwable -> {
                            if (!isViewAttached()) {
                                return;
                            }

                            getMvpView().hideLoading();

                            if (throwable instanceof HttpException) {
                                handleNetworkError((HttpException) throwable);
                            } else if (throwable instanceof SocketTimeoutException) {
                                Timber.d("SocketTimeoutException");
                            }else {
                                Timber.e(throwable, throwable.getMessage());
                            }
                        }));
    }
}
