package sg.lifecare.ble.utility;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;

import java.lang.reflect.Method;
import java.util.Set;

import timber.log.Timber;

public class BleUtils {

    public static boolean isDeviceBonded(String deviceId) {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        for(BluetoothDevice bt : pairedDevices){
            if(bt.getAddress().equalsIgnoreCase(deviceId)){
                return true;
            }
        }

        return false;
    }

    public static boolean isBluetoothEnabled(Context context) {
        final BluetoothManager manager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter adapter = manager.getAdapter();
        if (adapter != null) {
            return adapter.isEnabled();
        }

        return false;
    }

    public static void removeDevice(String deviceId) {

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        for(BluetoothDevice bt : pairedDevices){
            Timber.d("device %s  %s", bt.getName(), bt.getAddress());
        }

        for(BluetoothDevice bt : pairedDevices){
            if(bt.getAddress().equalsIgnoreCase(deviceId)){
                try {
                    Timber.d("Unpairing Device");
                    Method m = bt.getClass().getMethod("removeBond", (Class[]) null);
                    m.invoke(bt, (Object[]) null);
                } catch (Exception e) {
                    Timber.e(e, e.getMessage());
                }
            }
        }
    }
}
