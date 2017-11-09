package sg.lifecare.ble.device.urion;

import sg.lifecare.ble.profile.BleManagerCallbacks;

public interface UrionManagerCallbacks extends BleManagerCallbacks {

    void onStartMeasure();

    void onPulseRead(int pulse);

    void onResultRead(int systolic, int diastolic, int pulse);
}
