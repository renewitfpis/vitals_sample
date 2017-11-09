package sg.lifecare.vitals2.ui.device.ble.jumper;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import sg.lifecare.ble.device.jumper.JumperManager;
import sg.lifecare.ble.device.jumper.JumperOximeterCallbacks;
import sg.lifecare.ble.device.jumper.JumperOximeterManager;
import sg.lifecare.data.DataManager;
import sg.lifecare.vitals2.ui.base.BasePresenter;
import timber.log.Timber;

public class JumperOximeterPresenter<V extends JumperOximeterMvpView> extends BasePresenter<V>
        implements JumperOximeterMvpPresenter<V>, JumperOximeterCallbacks {

    private JumperManager<JumperOximeterCallbacks> mManager;

    @Inject
    public JumperOximeterPresenter(DataManager dataManager,
            CompositeDisposable compositeDisposable) {
        super(dataManager, compositeDisposable);
    }

    @Override
    public void init(Context context) {
        mManager = new JumperOximeterManager(context);
        mManager.setGattCallbacks(this);
    }

    @Override
    public void uninit() {
        if (mManager.isConnected()) {
            mManager.disconnect();
        }

        mManager = null;
    }

    @Override
    public void connect(BluetoothDevice device) {
        if (mManager.isConnected()) {
            Timber.w("connect: device still connected");
            return;
        }

        mManager.connect(device);
    }

    @Override
    public void disconnect() {
        if (mManager.isConnected()) {
            mManager.disconnect();
        }
    }

    @Override
    public void onDeviceConnecting(BluetoothDevice device) {

    }

    @Override
    public void onDeviceConnected(BluetoothDevice device) {

    }

    @Override
    public void onDeviceDisconnecting(BluetoothDevice device) {

    }

    @Override
    public void onDeviceDisconnected(BluetoothDevice device) {

    }

    @Override
    public void onLinklossOccur(BluetoothDevice device) {

    }

    @Override
    public void onServicesDiscovered(BluetoothDevice device,
            boolean optionalServicesFound) {

    }

    @Override
    public void onDeviceReady(BluetoothDevice device) {

    }

    @Override
    public boolean shouldEnableBatteryLevelNotifications(BluetoothDevice device) {
        return false;
    }

    @Override
    public void onBatteryValueReceived(BluetoothDevice device, int value) {

    }

    @Override
    public void onBondingRequired(BluetoothDevice device) {

    }

    @Override
    public void onBonded(BluetoothDevice device) {

    }

    @Override
    public void onError(BluetoothDevice device, String message, int errorCode) {

    }

    @Override
    public void onDeviceNotSupported(BluetoothDevice device) {

    }

    @Override
    public void onSpo2PulsePiRead(int spo2, int pulse, double pi) {

        getMvpView().onSpo2PulsePiRead(spo2, pulse, pi);

    }
}
