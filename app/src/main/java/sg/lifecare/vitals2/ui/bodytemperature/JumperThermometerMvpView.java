package sg.lifecare.vitals2.ui.bodytemperature;

import android.bluetooth.BluetoothDevice;

import sg.lifecare.vitals2.ui.base.MvpView;

public interface JumperThermometerMvpView extends MvpView {

    void onDeviceDisconnected(BluetoothDevice device);
    void onDeviceErrorDisconnected(BluetoothDevice device);
    void onTemperatureRead(double temperature);
}
