package sg.lifecare.data.local.database;


import android.content.Context;
import android.text.TextUtils;

import org.reactivestreams.Publisher;

import java.io.FileNotFoundException;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposables;
import io.reactivex.functions.Function;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;
import sg.lifecare.data.remote.model.data.BloodPressureEventData;
import sg.lifecare.data.remote.model.response.AssignedTaskForDeviceResponse;
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

    /*private Flowable<Realm> getRealmFlowable() {
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
    }*/

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

    private Patient findOrAddPatient(Realm realm, @NonNull String id) {
        Patient patient = realm.where(Patient.class).equalTo("id", id).findFirst();

        if (patient == null) {
            patient = realm.createObject(Patient.class, id);
        }

        return patient;
    }

    public Flowable<Boolean> addBloodGlucoseFlowable(BloodPressure bloodPressure) {
        return Flowable.create(new FlowableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<Boolean> flowableEmitter)
                    throws Exception {

                getRealm().executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        String patientId = bloodPressure.getPatientId();
                        if (!TextUtils.isEmpty(patientId)) {
                            Patient patient = findOrAddPatient(realm, patientId);

                            Date takenTime = bloodPressure.getTakenTime();
                            Date lastUpdateTime = patient.getLastUpdateTime();

                            if (takenTime != null) {
                                if (lastUpdateTime == null) {
                                    patient.setLastUpdateTime(takenTime);
                                } else {
                                    if (takenTime.after(lastUpdateTime)) {
                                        patient.setLastUpdateTime(takenTime);
                                    }
                                }
                            }
                        }

                        realm.copyToRealm(bloodPressure);
                    }
                });



                flowableEmitter.onNext(true);
                flowableEmitter.onComplete();
            }
        }, BackpressureStrategy.LATEST);
    }

}
