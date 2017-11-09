package sg.lifecare.vitals2.ui.device.ble.and;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import sg.lifecare.ble.device.aandd.ANDUC352Manager;
import sg.lifecare.ble.device.aandd.ANDUC352ManagerCallbacks;
import sg.lifecare.ble.parser.WeightMeasurement;
import sg.lifecare.ble.utility.BleUtils;
import sg.lifecare.data.DataManager;
import sg.lifecare.data.local.DeviceData;
import timber.log.Timber;

public class ANDUC352Presenter<V extends ANDUC352MvpView> extends ANDPresenter<V, ANDUC352ManagerCallbacks>
        implements ANDMvpPresenter<V>, ANDUC352ManagerCallbacks {

    @Inject
    public ANDUC352Presenter(DataManager dataManager,
            CompositeDisposable compositeDisposable) {
        super(dataManager, compositeDisposable);
    }


    @Override
    protected void initManager(Context context) {
        mManager = new ANDUC352Manager(context);
        mManager.setGattCallbacks(this);
    }

    @Override
    public void onWeightMeasurementRead(WeightMeasurement weightMeasurement) {
        Timber.d("onWeightMeasurementRead");

        if (mLastReadTime == 0) {
            addReadDisconnectTimer();
        }

        getMvpView().onWeightMeasurementRead(weightMeasurement);

        mLastReadTime = System.currentTimeMillis();
    }

    @Override
    public void onDeviceDisconnected(BluetoothDevice device) {
        Timber.d("onDeviceDisconnected");
        if (mManager.isPairing()) {
            if (BleUtils.isDeviceBonded(device.getAddress())) {
                DeviceData deviceData = getDataManager().getPreferencesHelper().getDeviceData();
                deviceData.addANDUC352(device.getAddress(), device.getName());

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
