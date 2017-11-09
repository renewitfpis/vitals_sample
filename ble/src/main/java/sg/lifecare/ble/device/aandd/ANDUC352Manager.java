package sg.lifecare.ble.device.aandd;


import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;

import java.util.UUID;

import sg.lifecare.ble.parser.WeightMeasurement;
import timber.log.Timber;

public class ANDUC352Manager extends ANDManager<ANDUC352ManagerCallbacks> {

    public static final UUID WEIGHT_SERVICE = UUID.fromString("0000181d-0000-1000-8000-00805f9b34fb");
    private static final UUID WEIGHT_MEASUREMENT_CHARACTERISTIC = UUID.fromString("00002a9d-0000-1000-8000-00805f9b34fb");

    public ANDUC352Manager(Context context) {
        super(context);
    }

    @Override
    public boolean isServiceSupported(BluetoothGatt gatt) {
        BluetoothGattService service = gatt.getService(WEIGHT_SERVICE);
        if (service != null) {
            mCharacteristic = service.getCharacteristic(WEIGHT_MEASUREMENT_CHARACTERISTIC);
            mDateTimeCharacteristic = service.getCharacteristic(DATE_TIME_CHARACTERISTIC);
            return true;
        }
        return false;
    }

    @Override
    public void characteristicIndicated(BluetoothGatt gatt,
            BluetoothGattCharacteristic characteristic) {

        WeightMeasurement weightMeasurement = WeightMeasurement.parse(characteristic);

        if (weightMeasurement != null) {
            Timber.d("onCharacteristicIndicated: %s", weightMeasurement.toString());

            mCallbacks.onWeightMeasurementRead(weightMeasurement);
        }
    }
}
