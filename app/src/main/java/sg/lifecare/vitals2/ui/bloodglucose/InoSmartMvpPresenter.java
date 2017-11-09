package sg.lifecare.vitals2.ui.bloodglucose;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import sg.lifecare.framework.di.PerActivity;
import sg.lifecare.vitals2.ui.base.MvpPresenter;
import sg.lifecare.vitals2.ui.base.MvpView;

@PerActivity
public interface InoSmartMvpPresenter<V extends MvpView> extends MvpPresenter<V> {

    void init(Context context);
    void uninit();

    void connect(BluetoothDevice device);
    void disconnect();
}
