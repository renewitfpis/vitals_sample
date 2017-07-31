package sg.lifecare.vitals2.ui.device.ble.urion;

import android.bluetooth.BluetoothDevice;

import sg.lifecare.vitals2.ui.base.MvpView;

public interface UrionMvpView extends MvpView {

    void onDeviceConnected(BluetoothDevice device);
    void onDeviceErrorDisconnected(BluetoothDevice device);
    void onMeasureStart();
    void onPulseRead(int pulse);
    void onResultRead(int systolic, int diastolic, int pulse);
}
