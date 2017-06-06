package sg.lifecare.data.remote;


import android.support.annotation.NonNull;

public class LifecareUtils {

    public static final String EVENT_ID_BLOOD_PRESSURE = "20013";
    public static final String EVENT_ID_BODY_WEIGHT = "20014";
    public static final String EVENT_ID_BLOOD_GLUCOSE = "20015";
    public static final String EVENT_ID_SPO2 = "20050";

    public static final String TYPE_BLOOD_GLUCOSE = "GM";
    public static final String TYPE_BLOOD_PRESSURE = "BP";
    public static final String TYPE_BODY_WEIGHT = "SW";
    public static final String TYPE_SPO2 = "PO";

    public static final String TASK_CHECK_LIST = "C";
    public static final String TASK_NOTICE = "N";
    public static final String TASK_QUESTION = "T";
    public static final String TASK_DEVICE = "D";


    public static boolean isBodyWeightEventId(@NonNull String id) {
        return EVENT_ID_BODY_WEIGHT.equals(id);
    }

    public static boolean isBloodGlucoseEventId(@NonNull String id) {
        return EVENT_ID_BLOOD_GLUCOSE.equals(id);
    }

    public static boolean isBloodPressureEventId(@NonNull String id) {
        return EVENT_ID_BLOOD_PRESSURE.equals(id);
    }

    public static boolean isSpO2EventId(@NonNull String id) {
        return EVENT_ID_SPO2.equals(id);
    }

    public static boolean isBloodGlucoseType(@NonNull String type) {
        return TYPE_BLOOD_GLUCOSE.equals(type);
    }

    public static boolean isBloodPressureType(@NonNull String type) {
        return TYPE_BLOOD_PRESSURE.equals(type);
    }

    public static boolean isBodyWeightType(@NonNull String type) {
        return TYPE_BODY_WEIGHT.equals(type);
    }

    public static boolean isSpO2Type(@NonNull String type) {
        return TYPE_SPO2.equals(type);
    }

    public static boolean isCheckListTask(@NonNull String type) {
        return TASK_CHECK_LIST.equals(type);
    }

    public static boolean isNoticeTask(@NonNull String type) {
        return TASK_NOTICE.equals(type);
    }

    public static boolean isQuestionTask(@NonNull String type) {
        return TASK_QUESTION.equals(type);
    }

    public static boolean isDeviceTask(@NonNull String type) {
        return TASK_DEVICE.equals(type);
    }
}
