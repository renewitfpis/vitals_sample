package sg.lifecare.vitals2.ui.device.ble.and;


import android.bluetooth.BluetoothDevice;
import android.content.Context;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;
import sg.lifecare.ble.device.aandd.ANDManager;
import sg.lifecare.ble.device.aandd.ANDManagerCallbacks;
import sg.lifecare.ble.utility.BleUtils;
import sg.lifecare.data.DataManager;
import sg.lifecare.vitals2.ui.base.BasePresenter;
import timber.log.Timber;

abstract class ANDPresenter<V extends ANDMvpView, E extends ANDManagerCallbacks> extends BasePresenter<V>
        implements ANDMvpPresenter<V>, ANDManagerCallbacks {

    private static final long READ_TIMEOUT = 3000;

    long mLastReadTime = 0;

    private Disposable mReadDisconnectDisposible;

    ANDManager<E> mManager;

    ANDPresenter(DataManager dataManager,
            CompositeDisposable compositeDisposable) {
        super(dataManager, compositeDisposable);
    }

    abstract void initManager(Context context);

    @Override
    public void onDateTimeRead(Date date) {

    }

    @Override
    public void onDateTimeWrite() {
        Timber.d("onDateTimeWrite");
        if (mManager.isPairing()) {
            //mManager.disconnect();
            getCompositeDisposable().add(Observable.timer(2, TimeUnit.SECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(response -> {
                        Timber.d("onNext");
                        mManager.disconnect();
                    }, throwable -> {
                        Timber.e(throwable.getMessage(), throwable);
                    }));
        }

        // waiting for measurement data
        mLastReadTime = 0;
    }

    @Override
    public void init(Context context) {
        initManager(context);
    }

    @Override
    public void uninit() {
        stopConnect();

        if (mManager != null) {
            mManager.setGattCallbacks(null);
        }
    }

    @Override
    public void registerDevice(BluetoothDevice device) {
        stopConnect();

        BleUtils.removeDevice(device.getAddress());
        mManager.connect(device, true);
    }

    @Override
    public void readDevice(BluetoothDevice device) {
        stopConnect();

        mManager.connect(device, false);
    }

    @Override
    public void stopConnect() {
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

    void addReadDisconnectTimer() {
        mReadDisconnectDisposible = Flowable.interval(READ_TIMEOUT, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSubscriber<Long>() {
                    @Override
                    public void onNext(Long v) {
                        Timber.d("onNext");
                        long current = System.currentTimeMillis();
                        if ((current - mLastReadTime) > READ_TIMEOUT) {
                            if ((mManager != null) && (mManager.isConnected())) {
                                mManager.disconnect();
                                onComplete();
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        Timber.e(t.getMessage(), t);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    void removeReadDisconnectTimer() {
        if ((mReadDisconnectDisposible != null) && !mReadDisconnectDisposible.isDisposed()) {
            mReadDisconnectDisposible.dispose();
        }
    }
}
