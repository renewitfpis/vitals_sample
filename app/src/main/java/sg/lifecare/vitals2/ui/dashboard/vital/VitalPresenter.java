package sg.lifecare.vitals2.ui.dashboard.vital;

import java.net.SocketTimeoutException;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import retrofit2.HttpException;
import sg.lifecare.ble.parser.BloodGlucoseMeasurement;
import sg.lifecare.ble.parser.BloodPressureMeasurement;
import sg.lifecare.ble.parser.BodyTemperatureMeasurement;
import sg.lifecare.ble.parser.BodyWeightMeasurement;
import sg.lifecare.ble.parser.Spo2Measurement;
import sg.lifecare.data.DataManager;
import sg.lifecare.data.local.database.BloodGlucose;
import sg.lifecare.data.local.database.BloodPressure;
import sg.lifecare.data.local.database.BodyTemperature;
import sg.lifecare.data.local.database.BodyWeight;
import sg.lifecare.data.local.database.Spo2;
import sg.lifecare.data.remote.model.data.BloodGlucoseEventData;
import sg.lifecare.data.remote.model.data.BloodPressureEventData;
import sg.lifecare.data.remote.model.data.BodyTemperatureEventData;
import sg.lifecare.data.remote.model.data.BodyWeightEventData;
import sg.lifecare.data.remote.model.data.SpO2EventData;
import sg.lifecare.data.remote.model.response.AssistsedEntityResponse;
import sg.lifecare.data.remote.model.response.EntityDetailResponse;
import sg.lifecare.utils.NetworkUtils;
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
    public AssistsedEntityResponse.Data getMember(int position) {
        List<AssistsedEntityResponse.Data> members = getDataManager().getMembersEntity();

        if (members != null && members.size() > position) {
            return members.get(position);
        }
        return null;
    }

    @Override
    public EntityDetailResponse.Data getUser() {
        return getDataManager().getUserEntity();
    }

    @Override
    public Realm getDatabase() {
        return getDataManager().getRealm();
    }

    @Override
    public void postBloodPressureData(BloodPressureMeasurement bp, String nurseId, String patientId,
            String deviceId) {
        BloodPressureEventData eventData = new BloodPressureEventData();
        eventData.setEntityId(getDataManager().getUserEntity().getId());
        eventData.setCreateDate(Calendar.getInstance().getTime());
        eventData.setSystolic((int) bp.getSystolic());
        eventData.setDistolic((int) bp.getDiastolic());
        eventData.setPulse((int) bp.getPulseRate());
        eventData.setNurseId(nurseId);
        eventData.setPatientId(patientId);
        eventData.setReadTime(bp.getTimestamp());
        eventData.setDeviceId(deviceId);

        getMvpView().showLoading();

        BloodPressure bloodPressureDb = BloodPressure.addBloodPressure(getDataManager().getRealm(), eventData);

        if (!NetworkUtils.isNetworkConnected(getDataManager().getContext())) {
            getMvpView().hideLoading();
            return;
        }

        getCompositeDisposable().add(
                getDataManager().postAssignedTaskForDevice(eventData)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> {
                            if (!isViewAttached()) {
                                return;
                            }

                            if (!response.isError()) {
                                mRealm.beginTransaction();
                                bloodPressureDb.setEntityId(response.getData().getId());
                                bloodPressureDb.setIsUploaded(true);
                                bloodPressureDb.setUploadTime(response.getData().getCreateDate());
                                mRealm.copyFromRealm(bloodPressureDb);
                                mRealm.commitTransaction();
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
    public void postBodyWeightData(BodyWeightMeasurement bw, String nurseId, String patientId,
            String deviceId) {
        BodyWeightEventData eventData = new BodyWeightEventData();
        eventData.setEntityId(getDataManager().getUserEntity().getId());
        eventData.setCreateDate(Calendar.getInstance().getTime());
        eventData.setWeight(bw.getWeight());
        eventData.setNurseId(nurseId);
        eventData.setPatientId(patientId);
        eventData.setReadTime(bw.getTimestamp());
        eventData.setDeviceId(deviceId);

        getMvpView().showLoading();

        BodyWeight bodyWeightDb = BodyWeight.addBodyWeight(getDataManager().getRealm(), eventData);

        if (!NetworkUtils.isNetworkConnected(getDataManager().getContext())) {
            getMvpView().hideLoading();
            return;
        }

        getCompositeDisposable().add(
                getDataManager().postAssignedTaskForDevice(eventData)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> {
                            if (!isViewAttached()) {
                                return;
                            }

                            if (!response.isError()) {
                                mRealm.beginTransaction();
                                bodyWeightDb.setEntityId(response.getData().getId());
                                bodyWeightDb.setIsUploaded(true);
                                bodyWeightDb.setUploadTime(response.getData().getCreateDate());
                                mRealm.copyFromRealm(bodyWeightDb);
                                mRealm.commitTransaction();
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
            String patientId, String deviceId) {

        BodyTemperatureEventData eventData = new BodyTemperatureEventData();
        eventData.setEntityId(getDataManager().getUserEntity().getId());
        eventData.setCreateDate(Calendar.getInstance().getTime());
        eventData.setTemperature(bt.getTemperature());
        eventData.setNurseId(nurseId);
        eventData.setPatientId(patientId);
        eventData.setReadTime(bt.getTimestamp());
        eventData.setDeviceId(deviceId);

        getMvpView().showLoading();

        BodyTemperature bodyTemperatureDb = BodyTemperature.addBodyTemperature(getDataManager().getRealm(), eventData);

        if (!NetworkUtils.isNetworkConnected(getDataManager().getContext())) {
            getMvpView().hideLoading();
            return;
        }

        getCompositeDisposable().add(
                getDataManager().postAssignedTaskForDevice(eventData)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> {
                            if (!isViewAttached()) {
                                return;
                            }

                            if (!response.isError()) {
                                mRealm.beginTransaction();
                                bodyTemperatureDb.setEntityId(response.getData().getId());
                                bodyTemperatureDb.setIsUploaded(true);
                                bodyTemperatureDb.setUploadTime(response.getData().getCreateDate());
                                mRealm.copyFromRealm(bodyTemperatureDb);
                                mRealm.commitTransaction();
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
    public void postSpo2Data(Spo2Measurement spo2, String nurseId, String patientId,
            String deviceId) {

        SpO2EventData eventData = new SpO2EventData();
        eventData.setEntityId(getDataManager().getUserEntity().getId());
        eventData.setCreateDate(Calendar.getInstance().getTime());
        eventData.setSpO2(spo2.getSpo2());
        eventData.setPulse(spo2.getPulse());
        eventData.setPi(spo2.getPi());
        eventData.setNurseId(nurseId);
        eventData.setPatientId(patientId);
        eventData.setReadTime(spo2.getTimestamp());
        eventData.setDeviceId(deviceId);

        getMvpView().showLoading();

        Spo2 spo2Db = Spo2.addSpo2(getDataManager().getRealm(), eventData);

        if (!NetworkUtils.isNetworkConnected(getDataManager().getContext())) {
            getMvpView().hideLoading();
            return;
        }

        getCompositeDisposable().add(
                getDataManager().postAssignedTaskForDevice(eventData)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> {
                            if (!isViewAttached()) {
                                return;
                            }

                            if (!response.isError()) {
                                mRealm.beginTransaction();
                                spo2Db.setEntityId(response.getData().getId());
                                spo2Db.setIsUploaded(true);
                                spo2Db.setUploadTime(response.getData().getCreateDate());
                                mRealm.copyFromRealm(spo2Db);
                                mRealm.commitTransaction();
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
    public void postBloodGlucoseData(BloodGlucoseMeasurement bg, String nurseId, String patientId,
            String deviceId) {
        BloodGlucoseEventData eventData = new BloodGlucoseEventData();
        eventData.setEntityId(getDataManager().getUserEntity().getId());
        eventData.setCreateDate(Calendar.getInstance().getTime());
        eventData.setConcentration(bg.getGlucose());
        if (bg.isAfterMeal()) {
            eventData.setTypeAfterMeal();
        } else if (bg.isBeforeMeal()) {
            eventData.setTypeBeforeMeal();
        }
        eventData.setNurseId(nurseId);
        eventData.setPatientId(patientId);
        eventData.setReadTime(bg.getTimestamp());
        eventData.setDeviceId(deviceId);

        getMvpView().showLoading();

        BloodGlucose bgDb = BloodGlucose.addBloodGlucose(getDataManager().getRealm(), eventData);

        if (!NetworkUtils.isNetworkConnected(getDataManager().getContext())) {
            getMvpView().hideLoading();
            return;
        }

        getCompositeDisposable().add(
                getDataManager().postAssignedTaskForDevice(eventData)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> {
                            if (!isViewAttached()) {
                                return;
                            }

                            if (!response.isError()) {
                                mRealm.beginTransaction();
                                bgDb.setEntityId(response.getData().getId());
                                bgDb.setIsUploaded(true);
                                bgDb.setUploadTime(response.getData().getCreateDate());
                                mRealm.copyFromRealm(bgDb);
                                mRealm.commitTransaction();
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
