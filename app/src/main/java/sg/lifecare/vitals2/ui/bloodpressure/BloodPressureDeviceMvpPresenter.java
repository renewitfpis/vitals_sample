package sg.lifecare.vitals2.ui.bloodpressure;


import java.util.List;

import sg.lifecare.data.local.DeviceData;
import sg.lifecare.framework.di.PerActivity;
import sg.lifecare.vitals2.ui.base.MvpPresenter;
import sg.lifecare.vitals2.ui.base.MvpView;

@PerActivity
public interface BloodPressureDeviceMvpPresenter<V extends MvpView> extends MvpPresenter<V> {

    List<DeviceData.Device> getDevices();
}
