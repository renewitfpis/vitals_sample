package sg.lifecare.data.remote.model.data;

import android.support.annotation.NonNull;

import java.util.Date;

public class BloodGlucoseTaskData {

    private float mConcentration;

    private transient String mRemarks;
    private transient String mType;   // before/after
    private transient String mUnit = "mmol/L";

    private Date CreateDate;
    private String DeviceId;
    private String EntityId;
    private String EventTypeId = "20015";
    private String EventTypeName = "Gluco Update Data";
    private String ExtraData;
    private String TaskAssignedId;
    private boolean WriteToSocket = false;

    public void setCreateDate(Date createDate) {
        CreateDate = createDate;
    }

    public void setConcentration(float concentration) {
        mConcentration = concentration;
        updateExtraData();
    }

    public void setDeviceId(@NonNull String deviceId) {
        DeviceId = deviceId;
    }

    public void setEntityId(@NonNull String entityId) {
        EntityId = entityId;
    }

    public void setMetricUnit() {
        mUnit = "mmol/L";
        updateExtraData();
    }

    public void setRemarks(@NonNull String remarks) {
        mRemarks = remarks;
        updateExtraData();
    }

    public void setTypeAfterMeal() {
        mType = "AfterMeal";
        updateExtraData();
    }

    public void setTypeBeforeMeal() {
        mType = "BeforeMeal";
        updateExtraData();
    }

    private void updateExtraData() {
        StringBuilder sb = new StringBuilder();
        sb.append("Concentration:").append(mConcentration);
        sb.append("&Unit:").append(mUnit);
        sb.append("&Type:").append(mType);
        sb.append("&Remarks:").append(mRemarks);
        ExtraData = sb.toString();
    }

    public String getEntityId() {
        return EntityId;
    }

    public void setTaskAssignId(@NonNull String taskAssignedId) {
        TaskAssignedId = taskAssignedId;
    }

}
