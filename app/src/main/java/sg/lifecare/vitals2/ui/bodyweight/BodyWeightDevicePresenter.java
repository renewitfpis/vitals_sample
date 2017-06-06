package sg.lifecare.vitals2.ui.bodyweight;


import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import sg.lifecare.data.DataManager;
import sg.lifecare.data.local.DeviceData;
import sg.lifecare.vitals2.ui.base.BasePresenter;

public class BodyWeightDevicePresenter<V extends BodyWeightDeviceMvpView> extends BasePresenter<V>
        implements BodyWeightDeviceMvpPresenter<V> {

    @Inject
    public BodyWeightDevicePresenter(DataManager dataManager,
            CompositeDisposable compositeDisposable) {
        super(dataManager, compositeDisposable);
    }

    public List<DeviceData.Device> getDevices() {
        List<DeviceData.Device> results = new ArrayList<>();
        List<DeviceData.Device> devices =
                getDataManager().getPreferencesHelper().getDeviceData().getWeightDevices();

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
