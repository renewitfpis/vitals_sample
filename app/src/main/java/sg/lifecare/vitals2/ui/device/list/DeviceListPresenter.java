package sg.lifecare.vitals2.ui.device.list;


import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import sg.lifecare.data.DataManager;
import sg.lifecare.data.local.DeviceData;
import sg.lifecare.vitals2.ui.base.BasePresenter;

public class DeviceListPresenter<V extends DeviceListMvpView> extends BasePresenter<V>
        implements DeviceListMvpPresenter<V> {

    @Inject
    public DeviceListPresenter(DataManager dataManager,
            CompositeDisposable compositeDisposable) {
        super(dataManager, compositeDisposable);
    }

    @Override
    public DeviceData getDeviceData() {
        return getDataManager().getPreferencesHelper().getDeviceData();
    }
}
