package sg.lifecare.data.local.database;


import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.Sort;
import sg.lifecare.data.remote.model.data.BloodPressureEventData;
import sg.lifecare.data.remote.model.response.BloodPressureResponse;
import timber.log.Timber;

public class BloodPressure extends RealmObject {

    // event entity id from server
    private String entityId;
    // measurement device id
    private String deviceId;

    private int systolic;
    private int diastolic;
    private int pulse;

    private Date takenTime; // reading taken time
    private String takerId; // reading taken by
    private String patientId;  // reading belongs to
    private boolean isUploaded;
    private Date uploadedTime;

    public static void addBloodPressures(Realm realm, List<BloodPressureResponse.Data> bps) {
        if ((bps != null) && (bps.size() > 0)) {
            realm.beginTransaction();

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

                    bloodPressureDb.setTakerId(bp.getTakerId());
                    bloodPressureDb.setPatientId(bp.getPatientId());
                    bloodPressureDb.setTakenTime(bp.getTakenTime());


                    Timber.d("addBloodPressures: add %s, %s, %s", bp.getId(), bp.getPatientId(), bp.getTakerId());

                } else {
                    Timber.d("addBloodPressures: add %s, %s, %s", bp.getId(), bp.getPatientId(), bp.getTakerId());
                }

                Patient.addOrUpdatePatient(realm, bp.getPatientId(), bp.getTakenTime());
            }

            realm.commitTransaction();
        }
    }

    public static void addBloodPressure(Realm realm, BloodPressureEventData data) {
        realm.beginTransaction();

        BloodPressure bloodPressureDb = realm.createObject(BloodPressure.class);
        bloodPressureDb.setEntityId("");
        bloodPressureDb.setSystolic(data.getSystolic());
        bloodPressureDb.setDiastolic(data.getDiastolic());
        bloodPressureDb.setPulse(data.getPulse());
        bloodPressureDb.setIsUploaded(false);

        bloodPressureDb.setTakerId(data.getNurseId());
        bloodPressureDb.setPatientId(data.getPatientId());
        bloodPressureDb.setTakenTime(data.getReadTime());

        Patient.addOrUpdatePatient(realm, data.getPatientId(), data.getReadTime());

        realm.commitTransaction();


    }

    public static BloodPressure getLatestByPatientId(Realm realm, String patientId) {
        RealmResults<BloodPressure> bloodPressures = realm.where(BloodPressure.class)
                .equalTo("patientId", patientId)
                .findAllSorted("takenTime", Sort.DESCENDING);

        if (bloodPressures.size() == 0) {
            return null;
        }

        return bloodPressures.get(0);
    }

    public int getSystolic() {
        return systolic;
    }

    public int getDiastolic() {
        return diastolic;
    }

    public int getPulse() {
        return pulse;
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

    public boolean isUploaded() {
        return isUploaded;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setTakenTime(Date takenTime) {
        this.takenTime = takenTime;
    }

    public void setTakerId(String takerId) {
        this.takerId = takerId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public void setIsUploaded(boolean uploaded) {
        this.isUploaded = uploaded;
    }

    public void setUploadTime(Date uploadedTime) {
        this.uploadedTime = uploadedTime;
    }

    public void setDiastolic(int diastolic) {
        this.diastolic = diastolic;
    }

    public void setSystolic(int systolic) {
        this.systolic = systolic;
    }

    public void setPulse(int pulse) {
        this.pulse = pulse;
    }
}
