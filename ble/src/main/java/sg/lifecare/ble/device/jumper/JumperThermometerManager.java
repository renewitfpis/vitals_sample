package sg.lifecare.ble.device.jumper;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;

public class JumperThermometerManager extends JumperManager<JumperThermometerCallbacks> {

    public JumperThermometerManager(Context context) {
        super(context);
    }

    @Override
    void characteristicNotified(BluetoothGatt gatt,
            BluetoothGattCharacteristic characteristic) {

        byte[] data = characteristic.getValue();

        if ((data != null) && (data.length == 5)) {
            if (data[0] == (byte)0xaa) {
                int a = data[2];
                double temperature = (((double) Math.round(((((double) ((a << 8) + (data[3] & 0xff))) * 1.0d) / 100.0d) * 10.0d)) * 1.0d) / 10.0d;

                mCallbacks.onTemperatureRead(temperature);
            }
        }
    }

}
