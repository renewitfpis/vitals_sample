package sg.lifecare.vitals2.ui.bloodpressure;


import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import sg.lifecare.data.DataManager;
import sg.lifecare.data.local.DeviceData;
import sg.lifecare.vitals2.ui.base.BasePresenter;

public class BloodPressureDevicePresenter<V extends BloodPressureDeviceMvpView> extends
        BasePresenter<V> implements BloodPressureDeviceMvpPresenter<V>{

    @Inject
    public BloodPressureDevicePresenter(DataManager dataManager,
            CompositeDisposable compositeDisposable) {
        super(dataManager, compositeDisposable);
    }

    @Override
    public List<DeviceData.Device> getDevices() {
        List<DeviceData.Device> results = new ArrayList<>();
        List<DeviceData.Device> devices =
                getDataManager().getPreferencesHelper().getDeviceData().getBloodPressureDevices();

        if ((devices != null) && (devices.size() > 0)) {
            for (DeviceData.Device device : devices) {
                if (device.isBle()) {
                    results.add(device);
                }
            }
        }
        return results;
    }
}
