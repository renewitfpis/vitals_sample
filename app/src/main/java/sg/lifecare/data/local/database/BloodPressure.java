package sg.lifecare.data.local.database;


import java.util.Date;

import io.realm.RealmObject;

public class BloodPressure extends RealmObject {

    // event entity id from server
    private String entityId;
    // measurement device id
    private String deviceId;

    private int systolic;
    private int diastolic;
    private int pulse;

    private Date takenTime;
    private String takerId;
    private boolean isUploaded;
    private Date uploadedTime;

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
