package sg.lifecare.ble.parser;

import android.bluetooth.BluetoothGattCharacteristic;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * https://www.bluetooth.com/specifications/gatt/viewer?attributeXmlFile=org.bluetooth.characteristic.blood_pressure_measurement.xml
 */

public class BloodPressureMeasurement implements Serializable {

    private static final String TAG = "BloodPressureMeasurement";

    private static final byte BLOOD_PRESSURE_UNIT = 0x1;
    private static final byte TIMESTAMP_PRESENT = 0x2;
    private static final byte PULSE_RATE_PRESENT = 0x4;
    private static final byte USER_ID_PRESENT = 0x8;
    private static final byte MEASUREMENT_STATUS_PRESENT = 0x10;

    private static final byte UNIT_SI = 0;
    private static final byte UNIT_IMPERIAL = 1;

    public static final String UNIT_MMHG = "mmHg";
    public static final String UNIT_KPA = "kPa";

    public static final int STATUS_BODY_MOVEMENT = 0x1;
    public static final int STATUS_CUFF_FIT = 0x2;
    public static final int STATUS_IRREGULAR_PULSE = 0x4;
    public static final int STATUS_PULSE_RATE_ABOVE_UPPER_LIMIT = 0x10;
    public static final int STATUS_PULSE_RATE_BELOW_LOWER_LIMIT = 0x18;
    public static final int STATUS_IMPROPER_MEASUREMENT_POSITION = 0x20;

    public static BloodPressureMeasurement parse(final BluetoothGattCharacteristic characteristic) {
        if (characteristic == null) {
            return null;
        }

        int offset = 0;
        final int flags = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset++);

        final int unit = flags & BLOOD_PRESSURE_UNIT;
        final boolean timestampPresent = (flags & TIMESTAMP_PRESENT) > 0;
        final boolean pulseRatePresent = (flags & PULSE_RATE_PRESENT) > 0;
        final boolean userIdPresent = (flags & USER_ID_PRESENT) > 0;
        final boolean measurementStatusPresent = (flags & MEASUREMENT_STATUS_PRESENT) > 0;

        final float systolic = characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT, offset);
        final float diastolic = characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT, offset + 2);
        final float meanArterialPressure = characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT, offset + 4);
        offset += 6;

        BloodPressureMeasurement bloodPressureMeasurement =
                new BloodPressureMeasurement(unit, systolic, diastolic, meanArterialPressure);

        if (timestampPresent) {
            final Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, offset));
            calendar.set(Calendar.MONTH, characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset + 2) -1);
            calendar.set(Calendar.DAY_OF_MONTH, characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset + 3));
            calendar.set(Calendar.HOUR_OF_DAY, characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset + 4));
            calendar.set(Calendar.MINUTE, characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset + 5));
            calendar.set(Calendar.SECOND, characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset + 6));
            offset += 7;

            bloodPressureMeasurement.setTimestamp(calendar.getTime());
        }

        if (pulseRatePresent) {
            final float pulseRate = characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT, offset);
            offset += 2;

            bloodPressureMeasurement.setPulseRate(pulseRate);
        }

        if (userIdPresent) {
            final int userId = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset);
            offset += 1;

            bloodPressureMeasurement.setUserId(userId);
        }

        if (measurementStatusPresent) {
            final int status = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, offset);

            bloodPressureMeasurement.setStatus(status);
        }

        return bloodPressureMeasurement;
    }

    public static BloodPressureMeasurement get(float systolic, float diastolic, float pulseRate, Date timestamp) {
        BloodPressureMeasurement measurement = new BloodPressureMeasurement(UNIT_SI, systolic, diastolic, 0);
        measurement.setPulseRate(pulseRate);
        measurement.setTimestamp(timestamp);

        return measurement;
    }

    private final int mUnit;
    private final float mSystolic;
    private final float mDiastolic;
    private final float mMeanArterialPressure;
    private Date mTimestamp;
    private float mPulseRate = -1f;
    private int mUserId = 255;
    private int mStatus = -1;

    private BloodPressureMeasurement(final int unit, final float systolic, final float diastolic, final float meanArterialPressure) {
        mUnit = unit;
        mSystolic = systolic;
        mDiastolic = diastolic;
        mMeanArterialPressure = meanArterialPressure;
    }

    public float getSystolic() {
        return mSystolic;
    }

    public float getDiastolic() {
        return mDiastolic;
    }

    public float getMeanArterialPressure() {
        return mMeanArterialPressure;
    }

    public Date getTimestamp() {
        return mTimestamp;
    }

    public float getPulseRate() {
        return mPulseRate;
    }

    public int getUserId() {
        return mUserId;
    }

    public int getStatus() {
        return mStatus;
    }

    public String getUnit() {
        return mUnit == UNIT_SI ? UNIT_MMHG : UNIT_KPA;
    }

    private void setTimestamp(Date timestamp) {
        mTimestamp = timestamp;
    }

    private void setPulseRate(float pulseRate) {
        mPulseRate = pulseRate;
    }

    private void setUserId(int userId) {
        mUserId = userId;
    }

    private void setStatus(int status) {
        mStatus = status;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("[BloodPressureMeasurement] Systolic: " + mSystolic + " " + getUnit());
        sb.append(", Diastolic: " + mDiastolic + " " + getUnit());
        sb.append(", Mean Arterial Pressure: " + mMeanArterialPressure + " " + getUnit());

        if (mPulseRate >= 0f) {
            sb.append(", Pulse Rate: " + mPulseRate);
        }

        if (mTimestamp != null) {
            sb.append(", timestamp: " + mTimestamp);
        }

        if (mUserId != 255) {
            sb.append(", UserID: " + mUserId);
        }

        if (mStatus >= 0) {
            sb.append(", Status: " + mStatus);
        }


        return sb.toString();
    }
}

