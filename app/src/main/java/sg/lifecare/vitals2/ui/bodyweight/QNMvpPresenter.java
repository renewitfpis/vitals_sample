package sg.lifecare.vitals2.ui.bodyweight;

import android.content.Context;

import com.kitnew.ble.QNBleCallback;
import com.kitnew.ble.QNBleDevice;
import com.kitnew.ble.QNBleScanCallback;

import sg.lifecare.framework.di.PerActivity;
import sg.lifecare.vitals2.ui.base.MvpPresenter;
import sg.lifecare.vitals2.ui.base.MvpView;

@PerActivity
public interface QNMvpPresenter<V extends MvpView> extends MvpPresenter<V> {

    void init(Context context);
    void uninit();

    void startScan(QNBleScanCallback callback);
    void stopScan();

    void connectDevice(QNBleDevice device, QNFragment.User user, QNBleCallback callback);
    void disconnectDevice();
}
