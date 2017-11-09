package sg.lifecare.data.local.database;


import java.util.Date;

import io.realm.RealmObject;

public class BloodGlucose extends RealmObject {

    private String entityId;
    private String deviceId;
    private float glucose;
    private boolean isBeforeMeal;

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

    public void setGlucose(float glucose) {
        this.glucose = glucose;
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
