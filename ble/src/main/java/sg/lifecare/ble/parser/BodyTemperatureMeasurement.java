package sg.lifecare.ble.parser;

import java.io.Serializable;
import java.util.Date;

public class BodyTemperatureMeasurement implements Serializable{

    private static final byte UNIT_SI = 0;
    private static final byte UNIT_IMPERIAL = 1;


    private final float mTemperature;
    private final int mUnit;
    private Date mTimestamp;

    public static BodyTemperatureMeasurement get(float temperature, Date timestamp) {
        BodyTemperatureMeasurement measurement = new BodyTemperatureMeasurement(temperature, UNIT_SI);
        measurement.setTimestamp(timestamp);

        return measurement;
    }

    BodyTemperatureMeasurement(float temperature, int unit) {
        mTemperature = temperature;
        mUnit = unit;
    }

    public float getTemperature() {
        return mTemperature;
    }

    public Date getTimestamp() {
        return mTimestamp;
    }

    private void setTimestamp(Date timestamp) {
        mTimestamp = timestamp;
    }
}
