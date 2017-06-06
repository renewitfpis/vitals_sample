package sg.lifecare.ble.device.aandd;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;

import java.util.Calendar;
import java.util.Deque;
import java.util.LinkedList;
import java.util.UUID;

import sg.lifecare.ble.profile.BleManager;
import timber.log.Timber;

public abstract class ANDManager<V extends ANDManagerCallbacks> extends BleManager<V> {

    static final UUID DATE_TIME_CHARACTERISTIC = UUID.fromString("00002a08-0000-1000-8000-00805f9b34fb");

    BluetoothGattCharacteristic mCharacteristic;
    BluetoothGattCharacteristic mDateTimeCharacteristic;

    private boolean mIsPairing = false;

    ANDManager(Context context) {
        super(context);
    }

    public void connect(BluetoothDevice device, boolean isPairing) {
        mIsPairing = isPairing;
        connect(device);
    }

    public boolean isPairing() {
        return mIsPairing;
    }

    @Override
    protected BleManagerGattCallback getGattCallback() {
        return mGattCallback;
    }

    public abstract boolean isServiceSupported(BluetoothGatt gatt);
    public abstract void characteristicIndicated(final BluetoothGatt gatt,
            final BluetoothGattCharacteristic characteristic);

    private final BleManagerGattCallback mGattCallback = new BleManagerGattCallback() {

        @Override
        protected boolean isRequiredServiceSupported(BluetoothGatt gatt) {
            Timber.d("isRequiredServiceSupported");

            return isServiceSupported(gatt);
        }

        @Override
        protected Deque<Request> initGatt(BluetoothGatt gatt) {
            int state = gatt.getDevice().getBondState();
            Timber.d("initGatt: %s", bondStateToString(state));

            final LinkedList<Request> requests = new LinkedList<>();

            if (state ==  BluetoothDevice.BOND_BONDED) {
                if (mDateTimeCharacteristic != null) {
                    Timber.d("initGatt: add datetime request ");
                    requests.add(Request.newWriteRequest(mDateTimeCharacteristic, getTime()));
                }

                if (!mIsPairing && (mCharacteristic != null)) {
                    requests.add(Request.newEnableIndicationsRequest(mCharacteristic));
                }
            }

            return requests;
        }

        @Override
        protected void onDeviceDisconnected() {
            Timber.d("onDeviceDisconnected");

            mDateTimeCharacteristic = null;
            mCharacteristic = null;
        }

        @Override
        protected void onCharacteristicRead(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
            Timber.d("onCharacteristicRead");
            if (isDateTimeCharacteristic(characteristic)) {
                Calendar calendar = Calendar.getInstance();

                int year = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 0);
                int month = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 2);
                int day = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 3);
                int hour = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 4);
                int minute = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 5);
                int second = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 6);

                Timber.d("onCharacteristicRead: " + year + "-" + month + "-" + day + " " +
                        hour + ":" + minute + ":" + second);

                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month - 1);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, second);

                mCallbacks.onDateTimeRead(calendar.getTime());

                setDateTime();
            }
        }

        @Override
        protected void onCharacteristicWrite(final BluetoothGatt gatt,
                final BluetoothGattCharacteristic characteristic) {
            Timber.d("onCharacteristicWrite");
            if (isDateTimeCharacteristic(characteristic)) {
                mCallbacks.onDateTimeWrite();

                /*if (!mIsPairing && (mCharacteristic != null)) {
                    //requests.add(Request.newEnableIndicationsRequest(mCharacteristic));
                    enableIndications(mCharacteristic);
                }*/
            }
        }

        @Override
        protected void onCharacteristicIndicated(final BluetoothGatt gatt,
                final BluetoothGattCharacteristic characteristic) {
            Timber.d("onCharacteristicIndicated");
            characteristicIndicated(gatt, characteristic);
        }

        private boolean isDateTimeCharacteristic(final BluetoothGattCharacteristic characteristic) {
            if (characteristic == null)
                return false;

            return DATE_TIME_CHARACTERISTIC.equals(characteristic.getUuid());
        }

        private void setDateTime() {
            mDateTimeCharacteristic.setValue(getTime());
            writeCharacteristic(mDateTimeCharacteristic);
        }

        private byte[] getTime() {
            Calendar cal = Calendar.getInstance(/*TimeZone.getTimeZone("UTC")*/);
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH) + 1;
            int day = cal.get(Calendar.DAY_OF_MONTH);
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            int minute = cal.get(Calendar.MINUTE);
            int second = cal.get(Calendar.SECOND);
            Timber.d("setDateTime() = year : " + year + ", month = " + month + ", day = " + day
                    + ", hour = " + hour + ", min = " +minute + ", sec = " + second);

            byte[] value = new byte[7];

            value[0] = (byte)(year & 0xff);
            value[1] = (byte)((year>>8) & 0xff);
            value[2] = (byte)(month & 0xff);
            value[3] = (byte)(day & 0xff);
            value[4] = (byte)(hour & 0xff);
            value[5] = (byte)(minute & 0xff);
            value[6] = (byte)(second & 0xff);

            return value;
        }

    };
}
