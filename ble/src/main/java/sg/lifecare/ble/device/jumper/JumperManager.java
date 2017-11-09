package sg.lifecare.ble.device.jumper;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Context;

import java.util.Deque;
import java.util.LinkedList;
import java.util.UUID;

import sg.lifecare.ble.profile.BleManager;
import sg.lifecare.ble.profile.BleManagerCallbacks;
import sg.lifecare.ble.utility.ParserUtils;
import timber.log.Timber;

public abstract class JumperManager<V extends BleManagerCallbacks> extends BleManager<V> {

    public static final UUID PROPRIETARY_SERVICE = UUID.fromString("cdeacb80-5235-4c07-8846-93a37ee6b86d");
    private static final UUID NOTIFY_CHARACTERISTIC = UUID.fromString("cdeacb81-5235-4c07-8846-93a37ee6b86d");
    private static final UUID WRITE_CHARACTERISTIC = UUID.fromString("cdeacb82-5235-4c07-8846-93a37ee6b86d");

    private BluetoothGattCharacteristic mNotifyCharacteristic;
    private BluetoothGattCharacteristic mWriteCharacteristic;

    JumperManager(Context context) {
        super(context);
    }

    @Override
    protected BleManagerGattCallback getGattCallback() {
        return mGattCallback;
    }

    abstract void characteristicNotified(final BluetoothGatt gatt,
            final BluetoothGattCharacteristic characteristic);

    private final BleManagerGattCallback mGattCallback = new BleManagerGattCallback() {

        @Override
        protected boolean isRequiredServiceSupported(BluetoothGatt gatt) {
            BluetoothGattService service = gatt.getService(PROPRIETARY_SERVICE);
            if (service != null) {
                mNotifyCharacteristic = service.getCharacteristic(NOTIFY_CHARACTERISTIC);
                mWriteCharacteristic = service.getCharacteristic(WRITE_CHARACTERISTIC);
                return true;
            }
            return false;
        }

        @Override
        protected Deque<Request> initGatt(BluetoothGatt gatt) {
            final LinkedList<Request> requests = new LinkedList<>();

            requests.add(Request.newEnableNotificationsRequest(mNotifyCharacteristic));
            //requests.add(Request.newWriteRequest(mWriteCharacteristic, BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE));

            return requests;
        }

        @Override
        protected void onDeviceDisconnected() {
            Timber.d("onDeviceDisconnected");
        }

        @Override
        protected void onCharacteristicWrite(final BluetoothGatt gatt,
                final BluetoothGattCharacteristic characteristic) {
            Timber.d("onCharacteristicWrite");
        }

        @Override
        protected void onCharacteristicNotified(final BluetoothGatt gatt,
                final BluetoothGattCharacteristic characteristic) {
            Timber.d("onCharacteristicNotified");
            Timber.d(ParserUtils.parse(characteristic));

            characteristicNotified(gatt, characteristic);
        }
    };
}
