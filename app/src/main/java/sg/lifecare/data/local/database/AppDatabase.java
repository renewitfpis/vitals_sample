package sg.lifecare.data.local.database;


import android.content.Context;
import android.text.BoringLayout;

import org.reactivestreams.Publisher;

import java.io.FileNotFoundException;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import io.reactivex.functions.Function;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmConfiguration;
import sg.lifecare.data.remote.model.response.BloodGlucoseResponse;
import sg.lifecare.data.remote.model.response.BloodPressureResponse;
import sg.lifecare.data.remote.model.response.BodyWeightResponse;
import sg.lifecare.data.remote.model.response.EntityDetailResponse;
import sg.lifecare.framework.di.ApplicationContext;
import timber.log.Timber;

public class AppDatabase {

    private RealmConfiguration mRealmConfiguration;

    @Inject
    public AppDatabase(@ApplicationContext final Context context) {
        Realm.init(context);

        if (mRealmConfiguration == null) {
            mRealmConfiguration = new RealmConfiguration.Builder()
                    .name("app-db")
                    .schemaVersion(0)
                    .build();

        }

        try {
            Realm.migrateRealm(mRealmConfiguration, new Migration());
        } catch (FileNotFoundException e) {
            Timber.e(e.getMessage(), e);
        }
    }

    public Realm getRealm() {
        return Realm.getDefaultInstance();
    }

    private Flowable<Realm> getRealmFlowable() {
        return Flowable.create(new FlowableOnSubscribe<Realm>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<Realm> emitter)
                    throws Exception {
                Timber.d("getRealmFlowable: subscribe");
                Realm observableRealm = getRealm();

                final RealmChangeListener<Realm> listener = _realm -> {
                    emitter.onNext(_realm);
                };
                emitter.setDisposable(Disposables.fromRunnable(() -> {
                    observableRealm.removeChangeListener(listener);
                    observableRealm.close();
                }));
                observableRealm.addChangeListener(listener);
                emitter.onNext(observableRealm);
            }
        }, BackpressureStrategy.LATEST);
    }

    public Flowable<Boolean> addNewUser(EntityDetailResponse.Data user) {
        return getRealmFlowable()
                .flatMap(new Function<Realm, Publisher<Boolean>>() {
                    @Override
                    public Publisher<Boolean> apply(@NonNull Realm realm) throws Exception {
                        User realmUser = realm.where(User.class).equalTo("entityId", user.getId()).findFirst();

                        if (realmUser == null) {
                            realm.beginTransaction();
                            User userDb = realm.createObject(User.class, user.getId());
                            userDb.setName(user.getName());
                            userDb.setId(user.getId());
                            realm.commitTransaction();
                        }

                        return Flowable.just(true);
                    }
                });
    }

    public void addBloodPressures(String userId, List<BloodPressureResponse.Data> bps) {
        getRealm().executeTransaction(realm -> {
            for (BloodPressureResponse.Data bp : bps) {
                BloodPressure bloodPressureDb =
                        realm.where(BloodPressure.class).equalTo("entityId", bp.getId()).findFirst();
                if (bloodPressureDb == null) {
                    bloodPressureDb = realm.createObject(BloodPressure.class);
                    bloodPressureDb.setEntityId(bp.getId());
                    bloodPressureDb.setSystolic(bp.getSystolic());
                    bloodPressureDb.setDiastolic(bp.getDiastolic());
                    bloodPressureDb.setPulse(bp.getPulse());
                    bloodPressureDb.setIsUploaded(true);
                    bloodPressureDb.setUploadTime(bp.getCreateDate());

                    bloodPressureDb.setTakerId(userId);
                    bloodPressureDb.setTakenTime(bp.getCreateDate());
                    Timber.d("addBloodPressures: add %s", bp.getId());
                } else {
                    Timber.d("addBloodPressures: skip %s", bp.getId());
                }
            }
        });
    }

    public void addBloodGlucoses(String userId, List<BloodGlucoseResponse.Data> bgs) {
        getRealm().executeTransaction(realm -> {
            for (BloodGlucoseResponse.Data bg : bgs) {
                BloodGlucose bloodGlucoseDb =
                        realm.where(BloodGlucose.class).equalTo("entityId", bg.getId()).findFirst();
                if (bloodGlucoseDb == null) {
                    bloodGlucoseDb = realm.createObject(BloodGlucose.class);
                    bloodGlucoseDb.setEntityId(bg.getId());
                    bloodGlucoseDb.setGlucose(bg.getConcentration());
                    bloodGlucoseDb.setIsUploaded(true);
                    bloodGlucoseDb.setUploadTime(bg.getCreateDate());

                    bloodGlucoseDb.setTakerId(userId);
                    bloodGlucoseDb.setTakenTime(bg.getCreateDate());
                    Timber.d("addBloodGlucoses: add %s", bg.getId());
                } else {
                    Timber.d("addBloodGlucoses: skip %s", bg.getId());
                }
            }
        });
    }

    public void addBodyWeights(String userId, List<BodyWeightResponse.Data> bws) {
        getRealm().executeTransaction(realm -> {
            for (BodyWeightResponse.Data bw : bws) {
                BodyWeight bodyWeightDb =
                        realm.where(BodyWeight.class).equalTo("entityId", bw.getId()).findFirst();
                if (bodyWeightDb == null) {
                    bodyWeightDb = realm.createObject(BodyWeight.class);
                    bodyWeightDb.setEntityId(bw.getId());
                    bodyWeightDb.setWeight(bw.getWeight());
                    bodyWeightDb.setIsUploaded(true);
                    bodyWeightDb.setUploadTime(bw.getCreateDate());

                    bodyWeightDb.setTakerId(userId);
                    bodyWeightDb.setTakenTime(bw.getCreateDate());
                    Timber.d("addBodyWeights: add %s", bw.getId());
                } else {
                    Timber.d("addBodyWeights: skip %s", bw.getId());
                }
            }
        });
    }

    public Flowable<Long> addBloodPressuresFlowable(String userId, List<BloodPressureResponse.Data> bps) {
        return Flowable.create(new FlowableOnSubscribe<Long>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<Long> flowableEmitter) throws Exception {
                long count = 0;
                Realm realm = getRealm();
                realm.beginTransaction();
                for (BloodPressureResponse.Data bp : bps) {
                    BloodPressure bloodPressureDb = realm.where(BloodPressure.class).findFirst();
                    if (bloodPressureDb == null) {
                        bloodPressureDb = realm.createObject(BloodPressure.class);
                        bloodPressureDb.setEntityId(bp.getId());
                        bloodPressureDb.setSystolic(bp.getSystolic());
                        bloodPressureDb.setDiastolic(bp.getDiastolic());
                        bloodPressureDb.setPulse(bp.getPulse());
                        bloodPressureDb.setIsUploaded(true);
                        bloodPressureDb.setUploadTime(bp.getCreateDate());

                        bloodPressureDb.setTakerId(userId);
                        bloodPressureDb.setTakenTime(bp.getCreateDate());
                        Timber.d("addBloodPressureData: add %s", bp.getId());
                        count++;
                    } else {
                        Timber.d("addBloodPressureData: skip %s", bp.getId());
                    }
                }
                realm.commitTransaction();
                realm.close();

                flowableEmitter.onNext(count);
                flowableEmitter.onComplete();
            }
        }, BackpressureStrategy.LATEST);
        /*return getRealmFlowable()
                .flatMap(new Function<Realm, Publisher<Boolean>>() {
                    @Override
                    public Publisher<Boolean> apply(@NonNull Realm realm) throws Exception {
                        realm.beginTransaction();
                        for (BloodPressureResponse.Data bp : bps) {
                            BloodPressure bloodPressureDb = realm.where(BloodPressure.class).findFirst();
                            if (bloodPressureDb == null) {
                                bloodPressureDb = realm.createObject(BloodPressure.class);
                                bloodPressureDb.setEntityId(bp.getId());
                                bloodPressureDb.setSystolic(bp.getSystolic());
                                bloodPressureDb.setDiastolic(bp.getDiastolic());
                                bloodPressureDb.setPulse(bp.getPulse());
                                bloodPressureDb.setIsUploaded(true);
                                bloodPressureDb.setUploadTime(bp.getCreateDate());

                                bloodPressureDb.setTakerId(userId);
                                bloodPressureDb.setTakenTime(bp.getCreateDate());
                                Timber.d("addBloodPressureData: add %s", bp.getId());
                            } else {
                                Timber.d("addBloodPressureData: skip %s", bp.getId());
                            }
                        }
                        realm.commitTransaction();

                        Timber.d("add done");

                        return Flowable.just(true);
                    }
                });*/
    }

}
