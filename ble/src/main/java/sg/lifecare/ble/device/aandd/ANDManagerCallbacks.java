package sg.lifecare.ble.device.aandd;


import java.util.Date;

import sg.lifecare.ble.profile.BleManagerCallbacks;

public interface ANDManagerCallbacks extends BleManagerCallbacks {

    void onDateTimeRead(Date date);
    void onDateTimeWrite();
}
