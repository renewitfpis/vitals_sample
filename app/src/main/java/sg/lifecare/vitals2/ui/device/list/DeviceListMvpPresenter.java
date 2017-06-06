package sg.lifecare.vitals2.ui.device.list;

import sg.lifecare.data.local.DeviceData;
import sg.lifecare.framework.di.PerActivity;
import sg.lifecare.vitals2.ui.base.MvpPresenter;
import sg.lifecare.vitals2.ui.base.MvpView;

@PerActivity
public interface DeviceListMvpPresenter<V extends MvpView> extends MvpPresenter<V> {

    DeviceData getDeviceData();
}
