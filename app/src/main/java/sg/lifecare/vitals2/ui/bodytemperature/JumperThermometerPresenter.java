package sg.lifecare.vitals2.ui.bodytemperature;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import sg.lifecare.ble.device.jumper.JumperManager;
import sg.lifecare.ble.device.jumper.JumperThermometerCallbacks;
import sg.lifecare.ble.device.jumper.JumperThermometerManager;
import sg.lifecare.data.DataManager;
import sg.lifecare.vitals2.ui.base.BasePresenter;
import timber.log.Timber;

public class JumperThermometerPresenter<V extends JumperThermometerMvpView> extends BasePresenter<V>
        implements JumperThermometerMvpPresenter<V>, JumperThermometerCallbacks {

    private JumperManager<JumperThermometerCallbacks> mManager;
    private boolean mHasResult = false;

    @Inject
    public JumperThermometerPresenter(DataManager dataManager,
            CompositeDisposable compositeDisposable) {
        super(dataManager, compositeDisposable);
    }

    @Override
    public void init(Context context) {
        mManager = new JumperThermometerManager(context);
        mManager.setGattCallbacks(this);
    }

    @Override
    public void uninit() {
        disconnect();

        mManager.close();
        mManager = null;
    }

    @Override
    public void connect(BluetoothDevice device) {
        if (mManager.isConnected()) {
            Timber.w("connect: device still connected");
            return;
        }

        Timber.d("connect: wait");
        getCompositeDisposable().add(
                Observable.timer(1, TimeUnit.SECONDS)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Long>() {
                            @Override
                            public void accept(@NonNull Long aLong) throws Exception {
                                Timber.d("connect: start");
                                mHasResult = false;
                                mManager.connect(device);
                            }
                        }));
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
        Timber.d("onDeviceConnected");
    }

    @Override
    public void onDeviceDisconnecting(BluetoothDevice device) {

    }

    @Override
    public void onDeviceDisconnected(BluetoothDevice device) {
        Timber.d("onDeviceDisconnected");

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
    public void onTemperatureRead(double temperature) {
        mHasResult = true;
        if (getMvpView() != null) {
            getMvpView().onTemperatureRead(temperature);
        }
    }
}
