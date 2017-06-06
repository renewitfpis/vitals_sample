package sg.lifecare.vitals2.ui.device.ble.and;


import android.bluetooth.BluetoothDevice;
import android.content.Context;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import sg.lifecare.ble.device.aandd.ANDUA651Manager;
import sg.lifecare.ble.device.aandd.ANDUA651ManagerCallbacks;
import sg.lifecare.ble.parser.BloodPressureMeasurement;
import sg.lifecare.ble.utility.BleUtils;
import sg.lifecare.data.DataManager;
import sg.lifecare.data.local.DeviceData;
import timber.log.Timber;

public class ANDUA651Presenter<V extends ANDUA651MvpView> extends ANDPresenter<V, ANDUA651ManagerCallbacks>
        implements ANDMvpPresenter<V>, ANDUA651ManagerCallbacks {

    @Inject
    public ANDUA651Presenter(DataManager dataManager,
            CompositeDisposable compositeDisposable) {
        super(dataManager, compositeDisposable);
    }

    @Override
    void initManager(Context context) {
        mManager = new ANDUA651Manager(context);
        mManager.setGattCallbacks(this);
    }

    @Override
    public void onBloodPressureMeasurementRead(BloodPressureMeasurement bloodPressure) {
        Timber.d("onBloodPressureMeasurementRead");

        if (mLastReadTime == 0) {
            addReadDisconnectTimer();
        }

        getMvpView().onBloodPressureMeasurementRead(bloodPressure);

        mLastReadTime = System.currentTimeMillis();
    }

    @Override
    public void onDeviceDisconnected(BluetoothDevice device) {
        Timber.d("onDeviceDisconnected");
        if (mManager.isPairing()) {
            if (BleUtils.isDeviceBonded(device.getAddress())) {
                DeviceData deviceData = getDataManager().getPreferencesHelper().getDeviceData();
                deviceData.addANDUA651(device.getAddress(), device.getName());

                getMvpView().onPairingSuccess();
            } else {
                getMvpView().onPairingFail();
            }
        } else {
            removeReadDisconnectTimer();

            getMvpView().onReadCompleted();
        }
    }
}
