package sg.lifecare.vitals2.ui.bloodglucose;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import sg.lifecare.ble.device.vivacheck.InoSmartManager;
import sg.lifecare.ble.device.vivacheck.InoSmartManagerCallbacks;
import sg.lifecare.data.DataManager;
import sg.lifecare.vitals2.ui.base.BasePresenter;
import timber.log.Timber;

public class InoSmartPresenter <V extends InoSmartMvpView> extends BasePresenter<V> implements InoSmartMvpPresenter<V>,
        InoSmartManagerCallbacks {

    private InoSmartManager mManager;
    private boolean mHasResult = false;

    @Inject
    public InoSmartPresenter(DataManager dataManager,
            CompositeDisposable compositeDisposable) {
        super(dataManager, compositeDisposable);
    }

    @Override
    public void init(Context context) {
        mManager = new InoSmartManager(context);
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
        if (mManager.isConnected()) {
            Timber.w("connect: device still connected");
            return;
        }

        mHasResult = false;
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

            if (mHasResult) {
                getMvpView().onDeviceDisconnected(device);
            } else {
                getMvpView().onDeviceErrorDisconnected(device);
            }
        }
    }

    @Override
    public void onDeviceDisconnecting(BluetoothDevice device) {

    }

    @Override
    public void onDeviceDisconnected(BluetoothDevice device) {

        if (getMvpView() != null) {
            if (mHasResult) {
                getMvpView().onDeviceDisconnected(device);
            } else {
                getMvpView().onDeviceErrorDisconnected(device);
            }
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
    public void onResultUpdate(List<InoSmartManager.CustomerHistory> customerHistories) {
        mHasResult = true;

        if (getMvpView() != null) {
            getMvpView().onDeviceResultUpdate(customerHistories);
        }
    }
}
