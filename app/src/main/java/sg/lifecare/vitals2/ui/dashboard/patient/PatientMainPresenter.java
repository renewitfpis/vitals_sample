package sg.lifecare.vitals2.ui.dashboard.patient;

import java.net.SocketTimeoutException;
import java.util.Calendar;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;
import sg.lifecare.ble.parser.BloodPressureMeasurement;
import sg.lifecare.data.DataManager;
import sg.lifecare.data.local.database.BloodPressure;
import sg.lifecare.data.remote.model.data.BloodPressureEventData;
import sg.lifecare.vitals2.ui.base.BaseRealmPresenter;
import timber.log.Timber;

public class PatientMainPresenter<V extends PatientMainMvpView> extends BaseRealmPresenter<V>
        implements PatientMainMvpPresenter<V> {

    @Inject
    public PatientMainPresenter(DataManager dataManager,
            CompositeDisposable compositeDisposable) {
        super(dataManager, compositeDisposable);
    }

    @Override
    public void postBloodPressureData(BloodPressureMeasurement bp, String nurseId, String patientId) {
        BloodPressureEventData eventData = new BloodPressureEventData();
        eventData.setEntityId(getDataManager().getUserEntity().getId());
        eventData.setCreateDate(Calendar.getInstance().getTime());
        eventData.setSystolic((int) bp.getSystolic());
        eventData.setDistolic((int) bp.getDiastolic());
        eventData.setPulse((int) bp.getPulseRate());
        eventData.setNurseId(nurseId);
        eventData.setPatientId(patientId);
        eventData.setReadTime(bp.getTimestamp());

        getMvpView().showLoading();

        BloodPressure.addBloodPressure(getDataManager().getRealm(), eventData);

        getCompositeDisposable().add(
                getDataManager().postAssignedTaskForDevice(eventData)
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

    @Override
    public BloodPressure getLatestBloodPressure(String patientId) {
        return BloodPressure.getLatestByPatientId(mRealm, patientId);
    }

}
