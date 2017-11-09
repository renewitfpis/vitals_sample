package sg.lifecare.data.local.database;


import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.Sort;
import sg.lifecare.data.remote.model.data.BloodGlucoseEventData;
import sg.lifecare.data.remote.model.response.BloodGlucoseResponse;
import timber.log.Timber;

public class BloodGlucose extends RealmObject {

    private String entityId;
    private String deviceId;
    private float glucose;
    private boolean isBeforeMeal;

    private Date takenTime; // reading taken time
    private String takerId; // reading taken by
    private String patientId;  // reading belongs to
    private boolean isUploaded;
    private Date uploadedTime;

    public static void addBloodGlucoses(Realm realm, List<BloodGlucoseResponse.Data> bgs) {
        if ((bgs != null) || (bgs.size() > 0)) {

            realm.beginTransaction();

            for (BloodGlucoseResponse.Data bg : bgs) {
                BloodGlucose bloodGlucoseDb =
                        realm.where(BloodGlucose.class).equalTo("entityId", bg.getId()).findFirst();
                if (bloodGlucoseDb == null) {


                    bloodGlucoseDb = realm.createObject(BloodGlucose.class);
                    bloodGlucoseDb.setEntityId(bg.getId());
                    bloodGlucoseDb.setGlucose(bg.getConcentration());
                    bloodGlucoseDb.setIsUploaded(true);
                    bloodGlucoseDb.setUploadTime(bg.getCreateDate());

                    bloodGlucoseDb.setTakerId(bg.getTakerId());
                    bloodGlucoseDb.setPatientId(bg.getPatientId());
                    bloodGlucoseDb.setTakenTime(bg.getTakenTime());

                    Timber.d("addBloodGlucoses: add %s", bg.getId());
                } else {
                    Timber.d("addBloodGlucoses: skip %s", bg.getId());
                }

                Patient.addOrUpdatePatient(realm, bg.getPatientId(), bg.getTakenTime());
            }

            realm.commitTransaction();
        }
    }

    public static BloodGlucose addBloodGlucose(Realm realm, BloodGlucoseEventData data) {
        realm.beginTransaction();

        BloodGlucose bloodGlucoseDb = realm.createObject(BloodGlucose.class);
        bloodGlucoseDb.setEntityId("");
        bloodGlucoseDb.setGlucose(data.getConcentration());
        bloodGlucoseDb.setIsUploaded(false);

        bloodGlucoseDb.setTakerId(data.getNurseId());
        bloodGlucoseDb.setPatientId(data.getPatientId());
        bloodGlucoseDb.setTakenTime(data.getReadTime());
        bloodGlucoseDb.setDeviceId(data.getDeviceId());

        Patient.addOrUpdatePatient(realm, data.getPatientId(), data.getReadTime());

        realm.commitTransaction();

        return bloodGlucoseDb;
    }

    public static BloodGlucose getLatestByPatientId(Realm realm, String patientId) {
        RealmResults<BloodGlucose> bloodGlucoses = realm.where(BloodGlucose.class)
                .equalTo("patientId", patientId)
                .findAllSorted("takenTime", Sort.DESCENDING);

        if (bloodGlucoses.size() == 0) {
            return null;
        }

        return bloodGlucoses.get(0);
    }

    public float getGlucose() {
        return glucose;
    }
    public Date getTakenTime() {
        return takenTime;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getTakerId() {
        return takerId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setGlucose(float glucose) {
        this.glucose = glucose;
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

}
