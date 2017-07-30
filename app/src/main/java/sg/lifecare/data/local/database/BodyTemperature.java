package sg.lifecare.data.local.database;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.Sort;
import sg.lifecare.data.remote.model.data.BodyTemperatureEventData;

public class BodyTemperature extends RealmObject {

    // event entity id from server
    private String entityId;
    // measurement device id
    private String deviceId;

    private float temperature;

    private Date takenTime; // reading taken time
    private String takerId; // reading taken by
    private String patientId;  // reading belongs to
    private boolean isUploaded;
    private Date uploadedTime;

    public static void addBodyTemperature(Realm realm, BodyTemperatureEventData data) {
        realm.beginTransaction();

        BodyTemperature bodyTemperatureDb = realm.createObject(BodyTemperature.class);
        bodyTemperatureDb.setEntityId("");
        bodyTemperatureDb.setTemperature(data.getTemperature());
        bodyTemperatureDb.setIsUploaded(false);

        bodyTemperatureDb.setTakerId(data.getNurseId());
        bodyTemperatureDb.setPatientId(data.getPatientId());
        bodyTemperatureDb.setTakenTime(data.getReadTime());

        Patient.addOrUpdatePatient(realm, data.getPatientId(), data.getReadTime());

        realm.commitTransaction();
    }

    public static BodyTemperature getLatestByPatientId(Realm realm, String patientId) {
        RealmResults<BodyTemperature> bodyTemperatures = realm.where(BodyTemperature.class)
                .equalTo("patientId", patientId)
                .findAllSorted("takenTime", Sort.DESCENDING);

        if (bodyTemperatures.size() == 0) {
            return null;
        }

        return bodyTemperatures.get(0);
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
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
