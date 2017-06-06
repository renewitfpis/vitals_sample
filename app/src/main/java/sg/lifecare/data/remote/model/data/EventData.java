package sg.lifecare.data.remote.model.data;

import android.support.annotation.NonNull;

import java.util.Date;

public abstract class EventData {

    private Date CreateDate;
    private String DeviceId;
    private String EntityId;
    private String TaskAssignedId;

    private String ExtraData;

    private String EventTypeId;
    private String EventTypeName;

    private boolean WriteToSocket = false;

    public EventData(String eventTypeId, String eventTypeName) {
        EventTypeId = eventTypeId;
        EventTypeName = eventTypeName;
    }

    public void setCreateDate(Date createDate) {
        CreateDate = createDate;
    }

    public void setDeviceId(@NonNull String deviceId) {
        DeviceId = deviceId;
    }

    public void setEntityId(@NonNull String entityId) {
        EntityId = entityId;
    }

    protected void setExtraData(String extraData) {
        ExtraData = extraData;
    }

    public void setTaskAssignId(@NonNull String taskAssignedId) {
        TaskAssignedId = taskAssignedId;
    }

    protected abstract void updateExtraData();

}
