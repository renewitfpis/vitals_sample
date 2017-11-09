package sg.lifecare.ble.device.aandd;


import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;

import java.util.UUID;

import sg.lifecare.ble.parser.BloodPressureMeasurement;
import timber.log.Timber;

public class ANDUA651Manager extends ANDManager<ANDUA651ManagerCallbacks> {

    public static final UUID BLOOD_PRESSURE_SERVICE = UUID.fromString("00001810-0000-1000-8000-00805f9b34fb");
    private static final UUID BLOOD_PRESSURE_MEASUREMENT_CHARACTERISTIC = UUID.fromString("00002a35-0000-1000-8000-00805f9b34fb");

    public ANDUA651Manager(Context context) {
        super(context);
    }

    @Override
    public boolean isServiceSupported(BluetoothGatt gatt) {
        BluetoothGattService service = gatt.getService(BLOOD_PRESSURE_SERVICE);
        if (service != null) {
            mCharacteristic = service.getCharacteristic(BLOOD_PRESSURE_MEASUREMENT_CHARACTERISTIC);
            mDateTimeCharacteristic = service.getCharacteristic(DATE_TIME_CHARACTERISTIC);
            return true;
        }

        return false;
    }

    @Override
    public void characteristicIndicated(BluetoothGatt gatt,
            BluetoothGattCharacteristic characteristic) {
        BloodPressureMeasurement bloodPressureMeasurement = BloodPressureMeasurement.parse(characteristic);

        if (bloodPressureMeasurement != null) {
            Timber.d("onCharacteristicIndicated: %s", bloodPressureMeasurement.toString());

            mCallbacks.onBloodPressureMeasurementRead(bloodPressureMeasurement);
        }
    }
}
