package sg.lifecare.vitals2.services;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import org.joda.time.DateTime;
import org.reactivestreams.Publisher;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmResults;
import sg.lifecare.data.DataManager;
import sg.lifecare.data.local.database.BloodGlucose;
import sg.lifecare.data.local.database.BloodPressure;
import sg.lifecare.data.local.database.BodyTemperature;
import sg.lifecare.data.local.database.BodyWeight;
import sg.lifecare.data.remote.model.data.BloodPressureEventData;
import sg.lifecare.data.remote.model.data.BodyTemperatureEventData;
import sg.lifecare.data.remote.model.data.BodyWeightEventData;
import sg.lifecare.utils.NetworkUtils;
import sg.lifecare.vitals2.VitalsApp;
import sg.lifecare.vitals2.di.component.DaggerServiceComponent;
import sg.lifecare.vitals2.di.component.ServiceComponent;
import timber.log.Timber;

public class SyncService extends Service {

    public static final String ACTION_STATE = "sg.lifecare.vitals2.services.syncservice.ACTION_STATE";
    public static final int SYNCING = 1;
    public static final int COMPLETED = 2;
    public static final int ERROR = 3;

    private CompositeDisposable mCompositeDisposable;

    private final IBinder mBinder = new LocalBinder();

    @Inject
    DataManager mDataManager;

    private String mUserId = "";

    public static Intent getStartIntent(Context context, String id) {
        Intent intent = new Intent(context, SyncService.class);
        intent.putExtra("id", id);

        return intent;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Timber.d("Service started");

        ServiceComponent component = DaggerServiceComponent.builder()
                .applicationComponent(((VitalsApp) getApplication()).getComponent())
                .build();
        component.inject(this);

        mCompositeDisposable = new CompositeDisposable();



        // upload data
        /*Realm aRealm = mDataManager.getRealm();
        RealmResults<BloodPressure> bps = BloodPressure.getNotUploadedBloodPressures(aRealm);


        List<BloodPressure> bloodPressures= new ArrayList<>();
        for (BloodPressure bp : bps) {
            bloodPressures.add(aRealm.copyFromRealm(bp));
        }

        aRealm.close();
        Timber.d("test 0");*/

        /*Flowable<Boolean> bpUploadFlowable = Flowable.fromIterable(bloodPressures).flatMap(
                new Function<BloodPressure, Publisher<Boolean>>() {
                    @Override
                    public Publisher<Boolean> apply(@NonNull BloodPressure bp)
                            throws Exception {
                        BloodPressureEventData eventData = new BloodPressureEventData();
                        eventData.setEntityId(mDataManager.getUserEntity().getId());
                        eventData.setCreateDate(Calendar.getInstance().getTime());
                        eventData.setSystolic(bp.getSystolic());
                        eventData.setDistolic(bp.getDiastolic());
                        eventData.setPulse(bp.getPulse());
                        eventData.setNurseId(bp.getTakerId());
                        eventData.setPatientId(bp.getPatientId());
                        eventData.setReadTime(bp.getTakenTime());
                        eventData.setDeviceId(bp.getDeviceId());

                        return mDataManager.postAssignedTaskForDevice(eventData)
                                .map(response -> {

                                    if (!response.isError()) {
                                        Realm realm = mDataManager.getRealm();
                                        realm.executeTransactionAsync(new Realm.Transaction() {
                                            @Override
                                            public void execute(Realm realm) {
                                                bp.setIsUploaded(true);
                                                realm.insert(bp);
                                            }
                                        });
                                    }
                                    return true;
                                }).onErrorReturn(throwable -> {
                                    Timber.e(throwable.getMessage(), throwable);
                                    return false;
                                });
                    }
                });*/

        /*mCompositeDisposable.add(Flowable.interval(0, 1, TimeUnit.MINUTES)
                .subscribeOn(Schedulers.io())
                .map(new Function<Long, Boolean>() {

                    @Override
                    public Boolean apply(@NonNull Long aLong) throws Exception {
                        return NetworkUtils.isNetworkConnected(getApplicationContext());
                    }
                })
                .flatMap(new Function<Boolean, Publisher<Boolean>>() {
                    @Override
                    public Publisher<Boolean> apply(@NonNull Boolean isNetworkOn) throws Exception {
                        if (isNetworkOn) {
                            return Flowable.zip(bpUploadFlowable, bpFlowable, bgFlowable,
                                    (bpUp, bp, bg) -> true);
                        } else {
                            return Flowable.just(false);
                        }
                    }
                })*/

                /*.flatMap(new Function<Boolean, Publisher<Boolean>>() {

                    @Override
                    public Publisher<Boolean> apply(@NonNull Boolean aBoolean) throws Exception {
                        return Flowable.zip(
                                mDataManager.getBloodPressures(userId, start, end),
                                mDataManager.getBloodGlucoses(userId, start, end),
                                mDataManager.getBodyWeight(userId, start, end),
                                (bloodPressureResponse, bloodGlucoseResponse, bodyWeightResponse) -> {
                                    mDataManager.addBloodPressures(userId, bloodPressureResponse.getData());
                                    mDataManager.addBloodGlucoses(userId, bloodGlucoseResponse.getData());
                                    mDataManager.addBodyWeights(userId, bodyWeightResponse.getData());
                                    return true;
                                });
                    }
                })*/
                /*.flatMap(new Function<Boolean, Publisher<BloodPressureResponse>>() {
                    @Override
                    public Publisher<BloodPressureResponse> apply(
                            @NonNull Boolean isNetworkOn) throws Exception {
                        if (isNetworkOn) {
                            return mDataManager.getBloodPressures(userId, start, end);
                        } else {
                            return Flowable.empty();
                        }
                    }
                })
                .flatMap(new Function<BloodPressureResponse, Publisher<Long>>() {
                    @Override
                    public Publisher<Long> apply(
                            @NonNull BloodPressureResponse bloodPressureResponse)
                            throws Exception {
                        if ((bloodPressureResponse.getData() != null) &&
                                (bloodPressureResponse.getData().size() > 0)) {
                            //return mDataManager.addBloodPressures(userId, bloodPressureResponse.getData());
                            //mDataManager.addBloodPressures(userId, bloodPressureResponse.getData());
                            return mDataManager.addBloodPressuresFlowable(userId, bloodPressureResponse.getData());
                        } else {
                            return Flowable.just(0L);
                        }
                    }
                })*/

                /*.flatMap(new Function<Boolean, Publisher<BloodGlucoseResponse>>() {
                    @Override
                    public Publisher<BloodGlucoseResponse> apply(
                            @NonNull Boolean result)
                            throws Exception {
                        return mDataManager.getBloodGlucoses(userId, start, end);
                    }
                })
                .flatMap(new Function<BloodGlucoseResponse, Publisher<BodyWeightResponse>>() {
                    @Override
                    public Publisher<BodyWeightResponse> apply(
                            @NonNull BloodGlucoseResponse bloodGlucoseResponse)
                            throws Exception {
                        return mDataManager.getBodyWeight(userId, start, end);
                    }
                })*/

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mCompositeDisposable.dispose();
    }

    private Flowable<Boolean> downloadBloodGlucose(String userId, DateTime start, DateTime end) {
        return mDataManager.getBloodGlucoses(userId, start, end)
                .map(bloodGlucoseResponse -> {
                    Realm realm = mDataManager.getRealm();
                    BloodGlucose.addBloodGlucoses(realm, bloodGlucoseResponse.getData());
                    realm.close();
                    return true;
                })
                .onErrorReturn(throwable -> {
                    Timber.e(throwable.getMessage(), throwable);
                    return false;
                });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Timber.d("onStartCommand: startId=%d", startId);
        if (TextUtils.isEmpty(mUserId)) {
            mUserId = intent.getStringExtra("id");
            startSync();
        }
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private void startSync() {

        Timber.d("startSync: user_id=%s", mUserId);

        DateTime start = new DateTime().withDayOfMonth(1);
        DateTime end = new DateTime();

        if (!NetworkUtils.isNetworkConnected(getApplicationContext())) {
            Timber.d("no network");
            stopSelf();
            return;
        }

        if (TextUtils.isEmpty(mUserId)) {
            stopSelf();
            return;
        }

        sendMessage(SYNCING);

        ArrayList<Single<List<Boolean>>> allFlowables = new ArrayList<>();
        allFlowables.add(uploadBloodPressure());
        allFlowables.add(uploadBodyTemperature());
        allFlowables.add(uploadBodyWeight());
        allFlowables.add(downloadBloodPressure(mUserId, start, end));
        allFlowables.add(downloadBodyTemperature(mUserId, start, end));
        allFlowables.add(downloadBodyWeight(mUserId, start, end));

        /*Flowable<List<Boolean>> allFlowables = Single.concatArray(
                uploadBloodPressure(),
                uploadBodyTemperature(),
                uploadBodyWeight(),
                downloadBloodPressure(mUserId, start, end),
                downloadBodyTemperature(mUserId, start, end),
                downloadBodyTemperature(mUserId, start, end));*/

        mCompositeDisposable.add(Single.concat(allFlowables)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(done -> {
                    Timber.d("done " + done);
                }, throwable -> {
                    Timber.e(throwable);
                    sendMessage(ERROR);
                    stopSelf();
                }, () -> {
                    Timber.d("done");
                    sendMessage(COMPLETED);
                    stopSelf();
                }));

        /*mCompositeDisposable.add(Flowable.zip(
                uploadBloodPressure(),
                uploadBodyTemperature(),
                uploadBodyWeight(),
                downloadBloodPressure(mUserId, start, end),
                downloadBodyTemperature(mUserId, start, end),
                downloadBodyWeight(mUserId, start, end),
                (bpUp, btUp, bwUp, bpDown, btDown, bwDown) -> {
                    Timber.d("%b %b", bpUp, btUp, bwUp, bpDown, btDown, bwDown);
                    return true;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(done -> {
                    Timber.d("done " + done);

                    sendMessage(COMPLETED);
                    stopSelf();
                }, throwable -> {
                    Timber.e(throwable);
                    sendMessage(ERROR);
                    stopSelf();
                }));*/
    }

    private Single<List<Boolean>> uploadBloodPressure() {
        return Flowable.create(
                (FlowableOnSubscribe<RealmResults<BloodPressure>>) emitter -> {

                    Realm realm = mDataManager.getRealm();

                    RealmResults<BloodPressure> bps =
                            realm.where(BloodPressure.class).equalTo("isUploaded", false).findAll();
                    Timber.d("bps size is %d", bps.size());
                    emitter.onNext(bps);

                    realm.close();
                    emitter.onComplete();
                }, BackpressureStrategy.BUFFER)
                .flatMapIterable(bps -> bps)
                .flatMap(new Function<BloodPressure, Publisher<Boolean>>() {
                    @Override
                    public Publisher<Boolean> apply(@NonNull BloodPressure bp)
                            throws Exception {
                        BloodPressureEventData eventData = new BloodPressureEventData();
                        eventData.setEntityId(mDataManager.getUserEntity().getId());
                        eventData.setCreateDate(Calendar.getInstance().getTime());
                        eventData.setSystolic(bp.getSystolic());
                        eventData.setDistolic(bp.getDiastolic());
                        eventData.setPulse(bp.getPulse());
                        eventData.setNurseId(bp.getTakerId());
                        eventData.setPatientId(bp.getPatientId());
                        eventData.setReadTime(bp.getTakenTime());
                        eventData.setDeviceId(bp.getDeviceId());

                        return mDataManager.postAssignedTaskForDevice(eventData)
                                .map(response -> {
                                    Timber.d("response is %b", !response.isError());
                                    if (!response.isError()) {
                                        Timber.d("update id %s", response.getData().getId());

                                        Realm realm = mDataManager.getRealm();
                                        realm.beginTransaction();
                                        bp.setIsUploaded(true);
                                        bp.setEntityId(response.getData().getId());
                                        bp.setUploadTime(response.getData().getCreateDate());
                                        realm.copyFromRealm(bp);
                                        realm.commitTransaction();
                                        realm.close();

                                    }
                                    return true;
                                }).onErrorReturn(throwable -> {
                                    Timber.e(throwable.getMessage(), throwable);
                                    return false;
                                });
                    }
                }).toList();
    }

    private Single<List<Boolean>> uploadBodyTemperature() {
        return Flowable.create(
                (FlowableOnSubscribe<RealmResults<BodyTemperature>>) emitter -> {

                    Realm realm = mDataManager.getRealm();

                    emitter.onNext(realm.where(BodyTemperature.class).equalTo("isUploaded", false).findAll());

                    realm.close();

                    emitter.onComplete();
                }, BackpressureStrategy.BUFFER)
                .flatMapIterable(bps -> bps)
                .flatMap(new Function<BodyTemperature, Publisher<Boolean>>() {
                    @Override
                    public Publisher<Boolean> apply(@NonNull BodyTemperature bt)
                            throws Exception {
                        BodyTemperatureEventData eventData = new BodyTemperatureEventData();
                        eventData.setEntityId(mDataManager.getUserEntity().getId());
                        eventData.setCreateDate(Calendar.getInstance().getTime());
                        eventData.setTemperature(bt.getTemperature());
                        eventData.setNurseId(bt.getTakerId());
                        eventData.setPatientId(bt.getPatientId());
                        eventData.setReadTime(bt.getTakenTime());
                        eventData.setDeviceId(bt.getDeviceId());

                        return mDataManager.postAssignedTaskForDevice(eventData)
                                .map(response -> {

                                    if (!response.isError()) {
                                        Realm realm = mDataManager.getRealm();
                                        realm.beginTransaction();
                                        bt.setIsUploaded(true);
                                        bt.setEntityId(response.getData().getId());
                                        bt.setUploadTime(response.getData().getCreateDate());
                                        realm.copyFromRealm(bt);
                                        realm.commitTransaction();
                                        realm.close();
                                    }
                                    return true;
                                }).onErrorReturn(throwable -> {
                                    Timber.e(throwable.getMessage(), throwable);
                                    return false;
                                });
                    }
                }).toList();
    }

    private Single<List<Boolean>> uploadBodyWeight() {
        return Flowable.create(
                (FlowableOnSubscribe<RealmResults<BodyWeight>>) emitter -> {

                    Realm realm = mDataManager.getRealm();

                    emitter.onNext(realm.where(BodyWeight.class).equalTo("isUploaded", false).findAll());

                    realm.close();

                    emitter.onComplete();
                }, BackpressureStrategy.BUFFER)
                .flatMapIterable(bps -> bps)
                .flatMap(new Function<BodyWeight, Publisher<Boolean>>() {
                    @Override
                    public Publisher<Boolean> apply(@NonNull BodyWeight bw)
                            throws Exception {
                        BodyWeightEventData eventData = new BodyWeightEventData();
                        eventData.setEntityId(mDataManager.getUserEntity().getId());
                        eventData.setCreateDate(Calendar.getInstance().getTime());
                        eventData.setWeight(bw.getWeight());
                        eventData.setNurseId(bw.getTakerId());
                        eventData.setPatientId(bw.getPatientId());
                        eventData.setReadTime(bw.getTakenTime());
                        eventData.setDeviceId(bw.getDeviceId());

                        return mDataManager.postAssignedTaskForDevice(eventData)
                                .map(response -> {

                                    if (!response.isError()) {
                                        Realm realm = mDataManager.getRealm();
                                        realm.beginTransaction();
                                        bw.setIsUploaded(true);
                                        bw.setEntityId(response.getData().getId());
                                        bw.setUploadTime(response.getData().getCreateDate());
                                        realm.copyFromRealm(bw);
                                        realm.commitTransaction();
                                        realm.close();
                                    }
                                    return true;
                                }).onErrorReturn(throwable -> {
                                    Timber.e(throwable.getMessage(), throwable);
                                    return false;
                                });
                    }
                }).toList();
    }

    private Single<List<Boolean>> downloadBloodPressure(String userId, DateTime start, DateTime end) {
        return mDataManager.getBloodPressures(userId, start, end)
                .map(response -> {
                    Realm realm = mDataManager.getRealm();
                    BloodPressure.addBloodPressures(realm, response.getData());
                    realm.close();
                    return true;
                })
                .onErrorReturn(throwable -> {
                    Timber.e(throwable.getMessage(), throwable);
                    return false;
                }).toList();
    }

    private Single<List<Boolean>> downloadBodyTemperature(String userId, DateTime start, DateTime end) {
        return mDataManager.getBodyTemperatures(userId, start, end)
                .map(response -> {
                    Realm realm = mDataManager.getRealm();
                    BodyTemperature.addBodyTemperatures(realm, response.getData());
                    realm.close();
                    return true;
                })
                .onErrorReturn(throwable -> {
                    Timber.e(throwable.getMessage(), throwable);
                    return false;
                }).toList();
    }

    private Single<List<Boolean>> downloadBodyWeight(String userId, DateTime start, DateTime end) {
        return mDataManager.getBodyWeights(userId, start, end)
                .map(response -> {
                    Realm realm = mDataManager.getRealm();
                    BodyWeight.addBodyWeights(realm, response.getData());
                    realm.close();
                    return true;

                })
                .onErrorReturn(throwable -> {
                    Timber.e(throwable.getMessage(), throwable);
                    return false;
                }).toList();
    }


    private void sendMessage(int state) {
        Intent intent = new Intent(ACTION_STATE);
        intent.putExtra("state", state);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public class LocalBinder extends Binder {
        SyncService getService() {
            return SyncService.this;
        }
    }
}
