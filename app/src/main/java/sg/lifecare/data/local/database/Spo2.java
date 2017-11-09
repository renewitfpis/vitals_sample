package sg.lifecare.data.local.database;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.Sort;
import sg.lifecare.data.remote.model.data.SpO2EventData;

public class Spo2 extends RealmObject {

    // event entity id from server
    private String entityId;
    // measurement device id
    private String deviceId;

    private int spo2;
    private int pulse;
    private double pi;

    private Date takenTime; // reading taken time
    private String takerId; // reading taken by
    private String patientId;  // reading belongs to
    private boolean isUploaded;
    private Date uploadedTime;

    public static Spo2 addSpo2(Realm realm, SpO2EventData data) {
        realm.beginTransaction();

        Spo2 spo2Db = realm.createObject(Spo2.class);
        spo2Db.setEntityId("");
        spo2Db.setSpo2(data.getSpO2());
        spo2Db.setPulse(data.getPulse());
        spo2Db.setPi(data.getPi());
        spo2Db.setIsUploaded(false);

        spo2Db.setTakerId(data.getNurseId());
        spo2Db.setPatientId(data.getPatientId());
        spo2Db.setTakenTime(data.getReadTime());

        Patient.addOrUpdatePatient(realm, data.getPatientId(), data.getReadTime());

        realm.commitTransaction();

        return spo2Db;
    }

    public static Spo2 getLatestByPatientId(Realm realm, String patientId) {
        RealmResults<Spo2> spo2s = realm.where(Spo2.class)
                .equalTo("patientId", patientId)
                .findAllSorted("takenTime", Sort.DESCENDING);

        if (spo2s.size() == 0) {
            return null;
        }

        return spo2s.get(0);
    }

    public int getPulse() {
        return pulse;
    }

    public int getSpo2() {
        return spo2;
    }

    public double getPi() {
        return pi;
    }

    public void setPulse(int pulse) {
        this.pulse = pulse;
    }

    public void setSpo2(int spo2) {
        this.spo2 = spo2;
    }

    public void setPi(double pi) {
        this.pi = pi;
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
}
