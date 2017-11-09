package sg.lifecare.vitals2.ui.bloodglucose;

import android.bluetooth.BluetoothDevice;

import java.util.List;

import sg.lifecare.ble.device.vivacheck.InoSmartManager;
import sg.lifecare.vitals2.ui.base.MvpView;

public interface InoSmartMvpView extends MvpView {

    void onDeviceDisconnected(BluetoothDevice device);
    void onDeviceErrorDisconnected(BluetoothDevice device);

    void onDeviceResultUpdate(List<InoSmartManager.CustomerHistory> customerHistories);
}
