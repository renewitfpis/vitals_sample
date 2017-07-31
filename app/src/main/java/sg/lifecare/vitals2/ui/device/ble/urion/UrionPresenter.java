package sg.lifecare.vitals2.ui.device.ble.urion;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import sg.lifecare.ble.device.urion.UrionManager;
import sg.lifecare.ble.device.urion.UrionManagerCallbacks;
import sg.lifecare.data.DataManager;
import sg.lifecare.vitals2.ui.base.BasePresenter;
import timber.log.Timber;

public class UrionPresenter<V extends UrionMvpView> extends BasePresenter<V> implements UrionMvpPresenter<V>,
        UrionManagerCallbacks {

    private UrionManager mManager;

    private int mResultCount = 0;

    @Inject
    public UrionPresenter(DataManager dataManager,
            CompositeDisposable compositeDisposable) {
        super(dataManager, compositeDisposable);
    }

    @Override
    public void init(Context context) {
        mManager = new UrionManager(context);
        mManager.setGattCallbacks(this);
    }

    @Override
    public void uninit() {
        if (mManager.isConnected()) {
            mManager.disconnect();
        }

        mManager.close();
        mManager = null;
    }

    @Override
    public void connect(BluetoothDevice device) {
        mResultCount = 0;

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
        if (getMvpView() != null) {
            getMvpView().onDeviceConnected(device);
        }
    }

    @Override
    public void onDeviceDisconnecting(BluetoothDevice device) {

    }

    @Override
    public void onDeviceDisconnected(BluetoothDevice device) {
        if ((getMvpView() != null) && (mResultCount == 0) ) {
            getMvpView().onDeviceErrorDisconnected(device);
        }
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
    public void onStartMeasure() {
        getMvpView().onMeasureStart();
    }

    @Override
    public void onPulseRead(int pulse) {
        getMvpView().onPulseRead(pulse);
    }

    @Override
    public void onResultRead(int systolic, int diastolic, int pulse) {
        if (mResultCount == 0) {
            getMvpView().onResultRead(systolic, diastolic, pulse);
        }

        mResultCount++;
    }
}
