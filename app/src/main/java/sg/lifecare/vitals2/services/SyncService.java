package sg.lifecare.vitals2.services;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.support.annotation.Nullable;

import org.joda.time.DateTime;
import org.reactivestreams.Publisher;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import sg.lifecare.data.DataManager;
import sg.lifecare.data.local.database.BloodGlucose;
import sg.lifecare.data.remote.model.response.BloodGlucoseResponse;
import sg.lifecare.data.remote.model.response.BloodPressureResponse;
import sg.lifecare.data.remote.model.response.BodyWeightResponse;
import sg.lifecare.vitals2.VitalsApp;
import sg.lifecare.vitals2.di.component.DaggerServiceComponent;
import sg.lifecare.vitals2.di.component.ServiceComponent;
import timber.log.Timber;

public class SyncService extends Service {

    private CompositeDisposable mCompositeDisposable;

    @Inject
    DataManager mDataManager;

    @Override
    public void onCreate() {
        super.onCreate();

        Timber.d("Service started");

        ServiceComponent component = DaggerServiceComponent.builder()
                .applicationComponent(((VitalsApp) getApplication()).getComponent())
                .build();
        component.inject(this);

        mCompositeDisposable = new CompositeDisposable();

        DateTime start = new DateTime().withDayOfMonth(1);
        DateTime end = new DateTime();
        String userId = mDataManager.getUserEntity().getId();

        Flowable<Object> flow = Flowable.zip(mDataManager.getBloodPressures(userId, start, end), mDataManager.getBloodPressures(userId, start, end), (key, val) -> key);

        mCompositeDisposable.add(Flowable.interval(0, 1, TimeUnit.MINUTES)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap(new Function<Long, Publisher<Boolean>>() {
                    @Override
                    public Publisher<Boolean> apply(@NonNull Long aLong) throws Exception {
                        return isNetworkOn();
                    }
                })
                .flatMap(new Function<Boolean, Publisher<Boolean>>() {

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
                })
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
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(done -> {
                    Timber.d("done " + done);
                }));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (!mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.dispose();
        }
    }

    private Flowable<Boolean> isNetworkOn() {
        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return Flowable.just(networkInfo != null && networkInfo.isConnected());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}