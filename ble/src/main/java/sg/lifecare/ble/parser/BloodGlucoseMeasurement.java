package sg.lifecare.ble.parser;

import java.io.Serializable;
import java.util.Date;

public class BloodGlucoseMeasurement implements Serializable {

    public static BloodGlucoseMeasurement get(float glucose, Date timestamp) {
        return new BloodGlucoseMeasurement(glucose, timestamp);
    }

    private static final byte MEAL_UNKNOWN = 0x00;
    private static final byte BEFORE_MEAL = 0x01;
    private static final byte AFTER_MEAL = 0x02;

    private float mGlucose;
    private Date mTimestamp;
    private byte mMeal = MEAL_UNKNOWN;

    private BloodGlucoseMeasurement(float glucose, Date timestamp) {
        mGlucose = glucose;
        mTimestamp = timestamp;
    }

    public float getGlucose() {
        return mGlucose;
    }

    public Date getTimestamp() {
        return  mTimestamp;
    }

    public boolean isBeforeMeal() {
        return mMeal == BEFORE_MEAL;
    }

    public boolean isAfterMeal() {
        return mMeal == AFTER_MEAL;
    }

    public void setBeforeMeal() {
        mMeal = BEFORE_MEAL;
    }

    public void setAfterMeal() {
        mMeal = AFTER_MEAL;
    }

}
