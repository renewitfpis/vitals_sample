package sg.lifecare.ble.device.jumper;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;

public class JumperOximeterManager extends JumperManager<JumperOximeterCallbacks> {
    public JumperOximeterManager(Context context) {
        super(context);
    }

    @Override
    void characteristicNotified(BluetoothGatt gatt,
            BluetoothGattCharacteristic characteristic) {

        byte[] data = characteristic.getValue();

        if (data != null) {
            if ((data.length == 11) && (data[0] == (byte) 0x80)) {

            } else if ((data.length == 4) && (data[0] == (byte)0x81)) {
                int pulse = data[1] & 0xff;
                int spo2 = data[2] & 0xff;
                double pi = ((data[3] & 0xff) * 10d) / 100d;

                mCallbacks.onSpo2PulsePiRead(spo2, pulse, pi);
            }
        }

    }
}
