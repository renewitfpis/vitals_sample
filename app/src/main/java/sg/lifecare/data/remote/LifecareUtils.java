package sg.lifecare.data.remote;


import android.support.annotation.NonNull;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Calendar;

public class LifecareUtils {

    public static final String EVENT_ID_BLOOD_PRESSURE = "20013";
    public static final String EVENT_ID_BODY_WEIGHT = "20014";
    public static final String EVENT_ID_BLOOD_GLUCOSE = "20015";
    public static final String EVENT_ID_SPO2 = "20050";
    public static final String EVENT_ID_BODY_TEMPERATURE = "20051";

    public static final String TYPE_BLOOD_GLUCOSE = "GM";
    public static final String TYPE_BLOOD_PRESSURE = "BP";
    public static final String TYPE_BODY_WEIGHT = "SW";
    public static final String TYPE_SPO2 = "PO";

    public static final String TASK_CHECK_LIST = "C";
    public static final String TASK_NOTICE = "N";
    public static final String TASK_QUESTION = "T";
    public static final String TASK_DEVICE = "D";

    // user levels
    public static final int GUEST_LEVEL =  100;
    public static final int USER_LEVEL = 200;
    public static final int STUDENT_LEVEL = 300;
    public static final int CAREGIVER_LEVEL = 400;
    public static final int TEACHER_LEVEL = 400;
    public static final int CLINICIAN_LEVEL = 410;
    public static final int DOCTOR_LEVEL = 412;
    public static final int ENTERPRISE_ADMIN_LEVEL = 440;
    public static final int ENTERPRISE_SYSTEM_ADMIN_LEVEL = 450;
    public static final int SYSTEM_ADMIN_LEVEL = 500;

    private static final DateTimeFormatter sDayFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");

    public static String convertToDayFormat(DateTime day) {
        return sDayFormatter.print(day);
    }


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

    public static boolean isBodyTemperatureEventId(@NonNull String id) {
        return EVENT_ID_BODY_TEMPERATURE.equals(id);
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

    public static boolean isCaregiver(int level) {
        if (CAREGIVER_LEVEL == level || CLINICIAN_LEVEL == level || DOCTOR_LEVEL == level) {
            return true;
        }

        return false;
    }
}
