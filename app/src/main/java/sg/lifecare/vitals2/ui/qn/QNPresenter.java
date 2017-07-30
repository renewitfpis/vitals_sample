package sg.lifecare.vitals2.ui.qn;

import android.content.Context;

import com.kitnew.ble.QNApiManager;
import com.kitnew.ble.QNBleApi;
import com.kitnew.ble.QNBleCallback;
import com.kitnew.ble.QNBleDevice;
import com.kitnew.ble.QNBleScanCallback;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import sg.lifecare.data.DataManager;
import sg.lifecare.vitals2.ui.base.BasePresenter;

public class QNPresenter<V extends QNMvpView> extends BasePresenter<V> implements QNMvpPresenter<V>{

    private QNBleApi mQNBleApi;

    @Inject
    public QNPresenter(DataManager dataManager,
            CompositeDisposable compositeDisposable) {
        super(dataManager, compositeDisposable);
    }

    @Override
    public void init(Context context) {
        mQNBleApi = QNApiManager.getApi(context);
        mQNBleApi.setWeightUnit(QNBleApi.WEIGHT_UNIT_KG);
    }

    @Override
    public void uninit() {
        disconnectDevice();

        mQNBleApi.stopScan();
        mQNBleApi = null;
    }

    @Override
    public void startScan(QNBleScanCallback callback) {
        stopScan();

        mQNBleApi.setScanMode(QNBleApi.SCAN_MODE_FIRST);
        mQNBleApi.startLeScan(null, null, callback);
    }

    @Override
    public void stopScan() {
        if (mQNBleApi.isScanning()) {
            mQNBleApi.stopScan();
        }
    }

    @Override
    public void connectDevice(QNBleDevice device, QNFragment.User user, QNBleCallback callback) {
        mQNBleApi.connectDevice(device, user.id  , user.height, user.gender, user.birthday, callback);
    }

    @Override
    public void disconnectDevice() {
        mQNBleApi.disconnectAll();
    }
}
