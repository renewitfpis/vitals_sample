package sg.lifecare.vitals2.ui.dashboard.vital;

import java.net.SocketTimeoutException;
import java.util.Calendar;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import retrofit2.HttpException;
import sg.lifecare.ble.parser.BloodPressureMeasurement;
import sg.lifecare.ble.parser.BodyTemperatureMeasurement;
import sg.lifecare.ble.parser.BodyWeightMeasurement;
import sg.lifecare.data.DataManager;
import sg.lifecare.data.local.database.BloodPressure;
import sg.lifecare.data.local.database.BodyTemperature;
import sg.lifecare.data.local.database.BodyWeight;
import sg.lifecare.data.remote.model.data.BloodPressureEventData;
import sg.lifecare.data.remote.model.data.BodyTemperatureEventData;
import sg.lifecare.data.remote.model.data.BodyWeightEventData;
import sg.lifecare.vitals2.ui.base.BaseRealmPresenter;
import timber.log.Timber;

public class VitalPresenter<V extends VitalMvpView> extends BaseRealmPresenter<V>
        implements VitalMvpPresenter<V> {

    @Inject
    public VitalPresenter(DataManager dataManager,
            CompositeDisposable compositeDisposable) {
        super(dataManager, compositeDisposable);
    }

    @Override
    public String getPatientId() {
        return getDataManager().getUserEntity().getId();
    }

    @Override
    public Realm getDatabase() {
        return getDataManager().getRealm();
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
    public void postBodyWeightData(BodyWeightMeasurement bw, String nurseId, String patientId) {
        BodyWeightEventData eventData = new BodyWeightEventData();
        eventData.setEntityId(getDataManager().getUserEntity().getId());
        eventData.setCreateDate(Calendar.getInstance().getTime());
        eventData.setWeight(bw.getWeight());
        eventData.setNurseId(nurseId);
        eventData.setPatientId(patientId);
        eventData.setReadTime(bw.getTimestamp());

        getMvpView().showLoading();

        BodyWeight.addBodyWeight(getDataManager().getRealm(), eventData);

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
    public void postBodyTemperatureData(BodyTemperatureMeasurement bt, String nurseId,
            String patientId) {

        BodyTemperatureEventData eventData = new BodyTemperatureEventData();
        eventData.setEntityId(getDataManager().getUserEntity().getId());
        eventData.setCreateDate(Calendar.getInstance().getTime());
        eventData.setTemperature(bt.getTemperature());
        eventData.setNurseId(nurseId);
        eventData.setPatientId(patientId);
        eventData.setReadTime(bt.getTimestamp());

        getMvpView().showLoading();

        BodyTemperature.addBodyTemperature(getDataManager().getRealm(), eventData);

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
}
