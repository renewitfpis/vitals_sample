package sg.lifecare.ble.device.vivacheck;

import java.util.List;

import sg.lifecare.ble.profile.BleManagerCallbacks;

public interface InoSmartManagerCallbacks extends BleManagerCallbacks {

    void onResultUpdate(List<InoSmartManager.CustomerHistory> customerHistories);
}
