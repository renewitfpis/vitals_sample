package sg.lifecare.data.remote.model.data;

import android.support.annotation.NonNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import timber.log.Timber;

public abstract class EventData {

    private static final String ISO8601_TIMESTAMP = "yyyy-MM-dd'T'HH mm ss'Z'";
    private static final DateFormat ISO8601_TIMESTAMP_FORMAT = new SimpleDateFormat(ISO8601_TIMESTAMP, Locale.getDefault());

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

    protected String getIsoTimestamp(Date timestamp) {
        try {
            ISO8601_TIMESTAMP_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
            return ISO8601_TIMESTAMP_FORMAT.format(timestamp);
        } catch (Exception e) {
            Timber.e(e, e.getMessage());
        }
        return "";
    }

    protected abstract void updateExtraData();

}
