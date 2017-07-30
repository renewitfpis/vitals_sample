package sg.lifecare.data.local.database;


import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.Sort;
import sg.lifecare.data.remote.model.data.BodyWeightEventData;
import sg.lifecare.data.remote.model.response.BodyWeightResponse;
import timber.log.Timber;

public class BodyWeight extends RealmObject {

    // event entity id from server
    private String entityId;
    // measurement device id
    private String deviceId;

    private float weight;

    private Date takenTime; // reading taken time
    private String takerId; // reading taken by
    private String patientId;  // reading belongs to
    private boolean isUploaded;
    private Date uploadedTime;

    public static void addBodyWeights(Realm realm, List<BodyWeightResponse.Data> bws) {
        if ((bws != null) && (bws.size() > 0)) {

            realm.beginTransaction();

            for (BodyWeightResponse.Data bw : bws) {
                BodyWeight bodyWeightDb =
                        realm.where(BodyWeight.class).equalTo("entityId", bw.getId()).findFirst();
                if (bodyWeightDb == null) {


                    bodyWeightDb = realm.createObject(BodyWeight.class);
                    bodyWeightDb.setEntityId(bw.getId());
                    bodyWeightDb.setWeight(bw.getWeight());
                    bodyWeightDb.setIsUploaded(true);
                    bodyWeightDb.setUploadTime(bw.getCreateDate());

                    bodyWeightDb.setTakerId(bw.getTakerId());
                    bodyWeightDb.setPatientId(bw.getPatientId());
                    bodyWeightDb.setTakenTime(bw.getCreateDate());

                    Timber.d("addBodyWeights: add %s", bw.getId());
                } else {
                    Timber.d("addBodyWeights: skip %s", bw.getId());
                }

                Patient.addOrUpdatePatient(realm, bw.getPatientId(), bw.getTakenTime());
            }

            realm.commitTransaction();
        }
    }

    public static void addBodyWeight(Realm realm, BodyWeightEventData data) {
        realm.beginTransaction();

        BodyWeight bloodPressureDb = realm.createObject(BodyWeight.class);
        bloodPressureDb.setEntityId("");
        bloodPressureDb.setWeight(data.getWeight());
        bloodPressureDb.setIsUploaded(false);

        bloodPressureDb.setTakerId(data.getNurseId());
        bloodPressureDb.setPatientId(data.getPatientId());
        bloodPressureDb.setTakenTime(data.getReadTime());

        Patient.addOrUpdatePatient(realm, data.getPatientId(), data.getReadTime());

        realm.commitTransaction();
    }

    public static BodyWeight getLatestByPatientId(Realm realm, String patientId) {
        RealmResults<BodyWeight> bodyWeights = realm.where(BodyWeight.class)
                .equalTo("patientId", patientId)
                .findAllSorted("takenTime", Sort.DESCENDING);

        if (bodyWeights.size() == 0) {
            return null;
        }

        return bodyWeights.get(0);
    }

    public float getWeight() {
        return weight;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public void setTakenTime(Date takenTime) {
        this.takenTime = takenTime;
    }

    public void setTakerId(String takerId) {
        this.takerId = takerId;
    }

    public void setIsUploaded(boolean uploaded) {
        this.isUploaded = uploaded;
    }

    public void setUploadTime(Date uploadedTime) {
        this.uploadedTime = uploadedTime;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }
}
