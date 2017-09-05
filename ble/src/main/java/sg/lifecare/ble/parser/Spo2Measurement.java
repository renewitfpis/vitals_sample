package sg.lifecare.ble.parser;

import java.io.Serializable;
import java.util.Date;

public class Spo2Measurement implements Serializable {

    public static Spo2Measurement get(int spo2, int pulse, Date timestamp) {
        Spo2Measurement measurement = new Spo2Measurement(spo2, pulse);
        measurement.setTimestamp(timestamp);

        return measurement;
    }

    private int mSpo2;
    private int mPulse;
    private Date mTimestamp;

    private Spo2Measurement(int spo2, int pulse) {
        mSpo2 = spo2;
        mPulse = pulse;
    }

    public int getSpo2() {
        return mSpo2;
    }

    public int getPulse() {
        return mPulse;
    }

    public Date getTimestamp() {
        return mTimestamp;
    }

    public void setTimestamp(Date timestamp) {
        mTimestamp = timestamp;
    }
}
