package sg.lifecare.ble.device.urion;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.Deque;
import java.util.LinkedList;
import java.util.UUID;

import sg.lifecare.ble.profile.BleManager;
import sg.lifecare.ble.utility.ParserUtils;
import timber.log.Timber;

public class UrionManager extends BleManager<UrionManagerCallbacks> {

    private static final UUID PROPRIETARY_SERVICE = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");
    private static final UUID NOTIFY_CHARACTERISTIC = UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb");
    private static final UUID WRITE_CHARACTERISTIC = UUID.fromString("0000fff2-0000-1000-8000-00805f9b34fb");

    private static final int MSG_WRITE_CHECK = 1;

    private BluetoothGattCharacteristic mNotifyCharacteristic;
    private BluetoothGattCharacteristic mWriteCharacteristic;

    private Handler mHandler;
    private int mWriteCounter = 0;

    private byte[] START_CMD =  new byte[] {(byte)0xfd, (byte)0xfd, (byte)0xfa, (byte)0x05, (byte)0x0d, (byte)0x0a};

    public UrionManager(Context context) {
        super(context);

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                Timber.d("handleMessage: mwriteCounter=%d", mWriteCounter);
                switch (message.what) {
                    case MSG_WRITE_CHECK:
                        //enqueue(Request.newWriteRequest(mWriteCharacteristic, START_CMD));
                        break;
                }
            }
        };
    }

    @Override
    public void connect(final BluetoothDevice device) {
        super.connect(device);

        mWriteCounter = 0;

        if (mBluetoothGatt != null) {
            mBluetoothGatt.connect();
        }
    }

    @Override
    protected BleManagerGattCallback getGattCallback() {
        Timber.d("getGattCallback");
        return mGattCallback;
    }

    private final BleManagerGattCallback mGattCallback =  new BleManagerGattCallback() {

        @Override
        protected boolean isRequiredServiceSupported(BluetoothGatt gatt) {
            Timber.d("isRequiredServiceSupported");
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
            Timber.d("initGatt");
            final LinkedList<Request> requests = new LinkedList<>();

            requests.add(Request.newEnableNotificationsRequest(mNotifyCharacteristic));
            requests.add(Request.newWriteRequest(mWriteCharacteristic, START_CMD));

            return requests;
        }

        @Override
        protected void onDeviceDisconnected() {
            Timber.d("onDeviceDisconnected");
            mHandler.removeMessages(MSG_WRITE_CHECK);
        }

        @Override
        protected void onCharacteristicWrite(final BluetoothGatt gatt,
                final BluetoothGattCharacteristic characteristic) {
            Timber.d("onCharacteristicWrite");
            mWriteCounter++;
            mHandler.sendEmptyMessageDelayed(MSG_WRITE_CHECK, 20000);
        }

        @Override
        protected void onCharacteristicNotified(final BluetoothGatt gatt,
                final BluetoothGattCharacteristic characteristic) {
            Timber.d("onCharacteristicNotified");
            Timber.d(ParserUtils.parse(characteristic));
            mHandler.removeMessages(MSG_WRITE_CHECK);

            byte[] data = characteristic.getValue();

            if (data != null) {
                if ((data.length == 1) && (data[0] == (byte)0xA5)) {
                    mWriteCharacteristic.setValue(START_CMD);
                    writeCharacteristic(mWriteCharacteristic);
                    //enqueue(Request.newWriteRequest(mWriteCharacteristic, START_CMD));
                } else if (data.length == 5) {
                    if (data[2] == (byte)0x06) {
                        mCallbacks.onStartMeasure();
                    }
                } else if (data.length == 7) {
                    if (data[2] == (byte)0xfb) {
                        int pulse = (data[3] & 0xff) * 256 + (data[4] & 0xff);
                        mCallbacks.onPulseRead(pulse);
                    }
                } else if (data.length == 8) {
                    if (data[2] == (byte)0xfc) {
                        int sys = (data[3] & 0xff);
                        int dia = (data[4] & 0xff);
                        int pulse = (data[5] & 0xff);

                        mCallbacks.onResultRead(sys, dia, pulse);
                    }
                }
            }
        }
    };
}
