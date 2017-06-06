package sg.lifecare.ble.parser;


import android.bluetooth.BluetoothGattCharacteristic;

import java.util.Calendar;
import java.util.Date;

public class WeightMeasurement {

    private static final String TAG = "WeightMeasurement";

    private static final byte MEASUREMENT_UNIT= 0x01;
    private static final byte TIMESTAMP_PRESENT = 0x02;
    private static final byte USER_ID_PRESENT = 0x04;
    private static final byte BMI_AND_HEIGHT_PRESENT = 0x08;

    private static final byte UNIT_SI = 0;
    private static final byte UNIT_IMPERIAL = 1;

    public static final String UNIT_KG = "kg";
    public static final String UNIT_POUND = "lbs";

    public static final String UNIT_METER = "m";
    public static final String UNIT_INCH = "in";

    public static WeightMeasurement parse(final BluetoothGattCharacteristic characteristic) {
        if (characteristic == null) {
            return null;
        }

        int offset = 0;
        final int flags = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset++);

        final int unit = flags & MEASUREMENT_UNIT;
        final boolean timestampPresent = (flags & TIMESTAMP_PRESENT) > 0;
        final boolean userIdPresent = (flags & USER_ID_PRESENT) > 0;
        final boolean bmiAndHeightPresent = (flags & BMI_AND_HEIGHT_PRESENT) > 0;
        final int weight = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, offset);
        offset += 2;

        WeightMeasurement weightMeasurement = new WeightMeasurement(weight / (unit == UNIT_SI ? 200f : 100f), unit);

        if (timestampPresent) {
            final Calendar calendar = Calendar.getInstance();

            calendar.set(Calendar.YEAR, characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, offset));
            calendar.set(Calendar.MONTH, characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset + 2) - 1);
            calendar.set(Calendar.DAY_OF_MONTH, characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset + 3));
            calendar.set(Calendar.HOUR_OF_DAY, characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset + 4));
            calendar.set(Calendar.MINUTE, characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset + 5));
            calendar.set(Calendar.SECOND, characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset + 6));
            offset += 7;

            weightMeasurement.setTimestamp(calendar.getTime());
        }

        if (userIdPresent) {
            int userId = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset++);

            weightMeasurement.setUserId(userId);
        }

        if (bmiAndHeightPresent) {
            int bmi = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, offset);
            int height = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, offset + 2);


            weightMeasurement.setBmiAndHeight(bmi / 10f, height / (unit == UNIT_SI ?  1000f : 10f));
        }


        return weightMeasurement;

    }

    private final float mWeight;
    private final int mUnit;
    private Date mTimestamp;
    private int mUserId = 255;
    private float mBmi = -1f;
    private float mHeight = -1f;

    private WeightMeasurement(float weight, int unit) {

        mWeight = weight;
        mUnit = unit;
        //Logger.debug(TAG, "mWeight: " + mWeight);
    }

    public float getWeight() {
        return mWeight;
    }

    public String getWeightUnit() {
        return mUnit == UNIT_SI ? UNIT_KG : UNIT_POUND;
    }

    public Date getTimestamp() {
        return mTimestamp;
    }

    public int getUserId() {
        return mUserId;
    }

    public float getBmi() {
        return mBmi;
    }

    public float getHeight() {
        return mHeight;
    }

    public String getHeightUnit() {
        return mUnit == UNIT_SI ? UNIT_METER : UNIT_INCH;
    }

    private void setTimestamp(Date timestamp) {
        mTimestamp = timestamp;
    }

    private void setUserId(int userId) {
        mUserId = userId;
    }

    private void setBmiAndHeight(float bmi, float height) {
        mBmi = bmi;
        mHeight = height;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("[WeightMeasurement] Weight: " + mWeight + " " + getWeightUnit());

        if (mTimestamp != null) {
            sb.append(", Timestamp: " + mTimestamp);
        }

        if (mUserId != 255) {
            sb.append(", UserID: " + mUserId);
        }

        if ((mHeight >= 0f) && (mBmi >= 0f)) {
            sb.append(", BMI: " + mBmi + ", Height: "+ mHeight + " " + getHeightUnit());
        }

        return sb.toString();
    }
}
