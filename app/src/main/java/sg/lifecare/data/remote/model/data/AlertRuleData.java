package sg.lifecare.data.remote.model.data;

import java.util.Calendar;
import java.util.Locale;

public class AlertRuleData {

    public static final int ACTIVITY_TYPE_GEOFENCE_ENTRY = 1;
    public static final int ACTIVITY_TYPE_GEOFENCE_EXIT = 2;

    private String EntityId;
    private String RuleEditEntityId;
    private String RuleId;
    private double Latitude;
    private double Longitude;
    private int IntValue;
    private String StartTime;
    private String EndTime;
    private String ArmState;
    private int ActivityType;
    private String Name;
    private String Type;

    public AlertRuleData() {

    }

    public String getEntityId() {
        return EntityId;
    }

    public double getLatitude() {
        return Latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public int getRadius() {
        return IntValue;
    }

    public String getStartTime() {
        return StartTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public String getArmStateAsString() {
        return ArmState;
    }

    public String getActivityTypeAstring() {
        switch (ActivityType) {
            case ACTIVITY_TYPE_GEOFENCE_ENTRY:
                return "geofence-on-entry";

            case ACTIVITY_TYPE_GEOFENCE_EXIT:
                return "geofence-on-exit";
        }

        return "";
    }

    public String getName() {
        return Name;
    }

    public String getType() {
        return Type;
    }

    public void setRuleId(String ruleId) {
        RuleId = ruleId;
    }

    public void setEntityId(String id) {
        EntityId = id;
    }

    public void setRuleEditEntityId(String id) {
        RuleEditEntityId = id;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public void setRadius(int radius) {
        IntValue = radius;
    }

    public void setStartTime(Calendar time) {
        int minute = time.get(Calendar.HOUR_OF_DAY) * 60 + time.get(Calendar.MINUTE);
        StartTime = String.format(Locale.getDefault(), "%d", minute);
    }

    public void setEndTime(Calendar time) {
        int minute = time.get(Calendar.HOUR_OF_DAY) * 60 + time.get(Calendar.MINUTE);
        EndTime = String.format(Locale.getDefault(), "%d", minute);
    }

    public void setArmState(String state) {
        ArmState = state;
    }

    public void setActivityType(int type) {
        ActivityType = type;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setType(String type) {
        Type = type;
    }

}
