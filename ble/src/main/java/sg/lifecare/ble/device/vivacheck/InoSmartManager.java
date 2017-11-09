package sg.lifecare.ble.device.vivacheck;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import sg.lifecare.ble.profile.BleManager;
import timber.log.Timber;

public class InoSmartManager extends BleManager<InoSmartManagerCallbacks> {

    private static final UUID SERVICE_1 = UUID.fromString("0003cdd0-0000-1000-8000-00805f9b0131");
    private static final UUID CHARACTERISTIC_1_NOTIFY = UUID.fromString("0003cdd1-0000-1000-8000-00805f9b0131");
    private static final UUID CHARACTERISTIC_1_WRITE = UUID.fromString("0003cdd2-0000-1000-8000-00805f9b0131");

    private static final UUID SERVICE_2 = UUID.fromString("0000fee7-0000-1000-8000-00805f9b34fb");
    private static final UUID CHARACTERISTIC_2_WRITE = UUID.fromString("0000fec7-0000-1000-8000-00805f9b34fb");
    private static final UUID CHARACTERISTIC_2_INDICATE = UUID.fromString("0000fec8-0000-1000-8000-00805f9b34fb");
    private static final UUID CHARACTERISTIC_2_READ = UUID.fromString("0000fec9-0000-1000-8000-00805f9b34fb");

    private static final byte BEGIN = (byte)0x7b;
    private static final byte END = (byte)0x7d;
    private static final byte TARGET_CODE = (byte)0x20;
    private static final byte SOURCE_CODE = (byte)0x10;
    private static final byte COMMAND_READ_SERIAL_NUMBER = (byte)0x77;
    private static final byte COMMAND_READ_UNIT = (byte)0xaa;
    private static final byte COMMAND_TEST_SYNCHRONIZATION = (byte)0x12;
    private static final byte COMMAND_TURN_OFF_BLUETOOTH = (byte)0xd2;
    private static final byte COMMAND_READ_CUSTOMER_HISTORY = (byte)0xdd;
    private static final byte COMMAND_HISTORY_DONE = (byte)0xd1;
    private static final byte COMMAND_READ_SOFTWARE_VERSION = (byte)0x66;
    private static final byte COMMAND_DELETE_HISTORY = (byte)0x55;
    private static final byte COMMAND_DELETE_ONE_HHISTORY = (byte)0x56;
    private static final byte COMMAND_READ_SEVERAL_HISTORY = (byte)0x13;
    private static final byte COMMAND_TIME_SET_AND_READ = 0x44;
    private static final byte EXTENDED_CODE_READ = (byte)0x55;
    private static final byte EXTENDED_CODE_WRITE = (byte)0x66;
    private static final byte EXTENDED_CODE_READ_ANSWER = (byte)0xaa;
    private static final byte EXTENDED_CODE_WRITE_ANSWER = (byte)0x99;

    private static final int COMMAND_INDEX = 5;
    private static final int COMMAND_EXTENDED_CODE_INDEX = 6;
    private static final int DATA_SIZE_INDEX = 7;
    private static final int DATA_INDEX = 9;

    private static final byte UNIT_MG_DL = (byte) 0x11;
    private static final byte UNIT_MMOL_L = (byte) 0x22;

    private static final byte MEAL_NONE = (byte) 0x00;
    private static final byte MEAL_FPG = (byte) 0x11;
    private static final byte MEAL_PPG = (byte) 0x22;

    private static final byte SAMPLE_BLOOD = (byte) 0x11;
    private static final byte SAMPLE_CONTROL = (byte) 0x22;

    private static final byte[] READ_SERIAL_NUMBER_COMMAND = {
            BEGIN,
            0x01, SOURCE_CODE, 0x01, TARGET_CODE,
            COMMAND_READ_SERIAL_NUMBER, EXTENDED_CODE_READ,
            0x00, 0x00,
            0x01, 0x0b, 0x0b, 0x04,
            END
    };

    private static final byte[] READ_UNIT_COMMAND = {
            BEGIN,
            0x01, SOURCE_CODE, 0x01, TARGET_CODE,
            COMMAND_READ_UNIT, EXTENDED_CODE_READ,
            0x00, 0x00,
            0x02, 0x01, 0x0d, 0x08,
            END

    };

    private static final byte[] READ_CUSTOMER_HISTORY_COMMAND = {
            BEGIN,
            0x01, SOURCE_CODE, 0x01, TARGET_CODE,
            COMMAND_READ_CUSTOMER_HISTORY, EXTENDED_CODE_READ,
            0x00, 0x00,
            0x03, 0x0a, 0x06, 0x0c,
            END
    };

    private static final byte[] READ_TIME_COMMAND = {
            BEGIN,
            0x01, SOURCE_CODE, 0x01, TARGET_CODE,
            COMMAND_TIME_SET_AND_READ, EXTENDED_CODE_READ,
            0x00, 0x00,
            0x01, 0x04, 0x0f, 0x00,
            END
    };

    private static final byte[] READ_SOFTWARE_VERSION_COMMAND = {
            BEGIN,
            0x01, SOURCE_CODE, 0x01, TARGET_CODE,
            COMMAND_READ_SOFTWARE_VERSION, EXTENDED_CODE_READ,
            0x00, 0x00,
            0x01, 0x0e, 0x08, 0x08,
            END
    };

    private static final byte[] READ_ONE_HISTORY_COMMAND = {
            BEGIN,
            0x01, TARGET_CODE, 0x01, SOURCE_CODE,
            COMMAND_READ_SEVERAL_HISTORY, EXTENDED_CODE_READ,
            0x00, 0x01,
            0x01,
            0x04, 0x03, 0x07, 0x07,
            END
    };

    private BluetoothGattCharacteristic mCharacteristic1Notify;
    private BluetoothGattCharacteristic mCharacteristic1Write;
    private BluetoothGattCharacteristic mCharacteristic2Write;
    private BluetoothGattCharacteristic mCharacteristic2Indicate;
    private BluetoothGattCharacteristic mCharacteristic2Read;


    private static final int DATA_LEN = 100;
    private byte[] mTempData = new byte[DATA_LEN];
    private int mTempDataLength = 0;

    private boolean mAppendSerialNumber = false;
    private boolean mAppendHistory = false;

    private List<CustomerHistory> mCustomerHistories = new ArrayList<>();

    private final BleManagerGattCallback mGattCallback =  new BleManagerGattCallback() {

        @Override
        protected boolean isRequiredServiceSupported(BluetoothGatt gatt) {
            Timber.d("isRequiredServiceSupported");

            BluetoothGattService service1 = gatt.getService(SERVICE_1);
            BluetoothGattService service2 = gatt.getService(SERVICE_2);

            if ((service1 != null) &&  (service2 != null)) {
                mCharacteristic1Notify = service1.getCharacteristic(CHARACTERISTIC_1_NOTIFY);
                mCharacteristic1Write = service1.getCharacteristic(CHARACTERISTIC_1_WRITE);

                mCharacteristic2Write = service2.getCharacteristic(CHARACTERISTIC_2_WRITE);
                mCharacteristic2Indicate = service2.getCharacteristic(CHARACTERISTIC_2_INDICATE);
                mCharacteristic2Read = service2.getCharacteristic(CHARACTERISTIC_2_READ);

                return true;
            }

            return false;
        }

        @Override
        protected Deque<Request> initGatt(BluetoothGatt gatt) {
            Timber.d("initGatt");

            final LinkedList<Request> requests = new LinkedList<>();
            //requests.add(Request.newEnableNotificationsRequest(mCharacteristic1Notify));
            //requests.add(Request.newWriteRequest(mCharacteristic1Write, READ_SERIAL_NUMBER_COMMAND));
            //requests.add(Request.newEnableNotificationsRequest(mCharacteristic1Notify));
            //requests.add(Request.newWriteRequest(mCharacteristic1Write, READ_UNIT_COMMAND));
            requests.add(Request.newEnableNotificationsRequest(mCharacteristic1Notify));
            requests.add(Request.newWriteRequest(mCharacteristic1Write, READ_ONE_HISTORY_COMMAND));

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

        protected void onCharacteristicNotified(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
            byte[] data = characteristic.getValue();

            if (data.length == 0) {
                return;
            }

            if (data[0] == BEGIN) {
                switch (data[COMMAND_INDEX]) {
                    case COMMAND_READ_SERIAL_NUMBER:
                        Timber.d("COMMAND_READ_SERIAL_NUMBER");

                        mAppendSerialNumber = true;

                        clearTempData();
                        appendTempData(data);
                        break;

                    case COMMAND_READ_UNIT:
                        Timber.d("COMMAND_READ_UNIT");

                        //(0x) 7B-01-20-01-10-AA-AA-00-01-11-0E-0F-07-04-7D
                        byte[] test = {(byte)0x01, (byte)0x20, (byte)0x01, (byte)0x10, (byte)0xaa, (byte) 0xaa, (byte)0x00, (byte)0x01, (byte)0x11};
                        int initValue = Crc16.INITIAL_VALUE;

                        int crc = Crc16.calc(initValue, test);

                        Timber.d("crc: %x", crc);

                        byte unit = getUnit(data);
                        Timber.d("%x", unit);
                        break;

                    case COMMAND_READ_SEVERAL_HISTORY:
                        Timber.d("COMMAND_READ_SEVERAL_HISTORY");

                        mCustomerHistories.clear();
                        mAppendHistory = true;

                        clearTempData();
                        appendTempData(data);
                        break;

                    case COMMAND_HISTORY_DONE:
                        Timber.d("COMMAND_HISTORY_DONE");

                        clearTempData();
                        mAppendHistory = false;
                        break;
                }
            } else {
                if (mAppendSerialNumber) {
                    appendTempData(data);

                    String sn = getSerialNumber(mTempData);

                    Timber.d("sn: %s", sn);

                    Arrays.fill(mTempData, 0, DATA_LEN, (byte) 0);
                    mTempDataLength = 0;
                    mAppendSerialNumber = false;
                } else if (mAppendHistory) {
                    appendTempData(data);
                    processCustomerHistory();
                }
            }
        }

        private void appendTempData(byte[] data) {
            System.arraycopy(data, 0, mTempData, mTempDataLength, data.length);
            mTempDataLength += data.length;
        }

        private void clearTempData() {
            Arrays.fill(mTempData, (byte)0x00);
            mTempDataLength = 0;
        }

        private void processCustomerHistory() {
            Timber.d("processCustomerHistory: data_length=%d", mTempDataLength);
            if (mTempDataLength >= 23) {
                byte[] temp = new byte[23];
                System.arraycopy(mTempData, 0, temp, 0, 23);

                CustomerHistory history = getCustomerHistory(temp);
                Timber.d("processCustomerHistory: %s", history.toString());
                mCustomerHistories.add(history);

                System.arraycopy(mTempData, 23, mTempData, 0, mTempDataLength - 23);

                mTempDataLength -= 23;

                if (mTempDataLength == 14 && mTempData[COMMAND_INDEX] == COMMAND_HISTORY_DONE) {
                    mAppendHistory = false;
                    clearTempData();

                    mCallbacks.onResultUpdate(mCustomerHistories);
                }
            }
        }

        private CustomerHistory getCustomerHistory(byte[] data) {
            CustomerHistory customerHistory = new CustomerHistory();

            int year = (0x00ff & data[DATA_INDEX]) + 2000;
            int month = (0x00ff & data[DATA_INDEX + 1]);
            int day = (0x00ff & data[DATA_INDEX + 2]);
            int hour = (0x00ff & data[DATA_INDEX + 3]);
            int minute = (0x00ff & data[DATA_INDEX + 4]);

            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, month - 1);
            cal.set(Calendar.DAY_OF_MONTH, day);
            cal.set(Calendar.HOUR_OF_DAY, hour);
            cal.set(Calendar.MINUTE, minute);

            customerHistory.mTimestamp = cal.getTime();
            customerHistory.mResult = (((0x00ff & data[DATA_INDEX + 5]) * 100) + (0x00ff & data[DATA_INDEX + 6])) / 10;
            customerHistory.mMeal = data[DATA_INDEX + 7];
            customerHistory.mSample = data[DATA_INDEX + 8];

            return customerHistory;
        }
    };

    public InoSmartManager(Context context) {
        super(context);
    }

    @Override
    protected BleManagerGattCallback getGattCallback() {
        return mGattCallback;
    }

    @Override
    public void connect(final BluetoothDevice device) {
        super.connect(device);
    }

    private byte[] getContent(byte[] data) {
        int contentLength = ((data[DATA_SIZE_INDEX] & 0x00ff) << 8) | (data[DATA_SIZE_INDEX+1] & 0x00ff);
        byte[] content = new byte[contentLength];

        Timber.d("getContent: len=%d", contentLength);

        for (int i = 0, j = 0; i < contentLength; i++) {
            if (data[DATA_INDEX + i] != (byte)0x00) {
                content[j++] = data[DATA_INDEX + i];
            }
        }

        for (int i= 0; i < contentLength; i++) {
            Timber.d("content: %x", content[i]);
        }

        return content;
    }

    private String getSerialNumber(byte[] data) {
        byte[] content = getContent(data);

        return new String(content).trim();
    }

    private byte getUnit(byte[] data) {
        byte[] content = getContent(data);
        return content[0];
    }



    private Date getTime(byte[] data) {
        int year = (0x00ff & data[DATA_INDEX]) + 2000;
        int month = (0x00ff & data[DATA_INDEX + 1]);
        int day = (0x00ff & data[DATA_INDEX + 2]);
        int hour = (0x00ff & data[DATA_INDEX + 3]);
        int minute = (0x00ff & data[DATA_INDEX + 4]);
        int second = (0x00ff & data[DATA_INDEX + 5]);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, second);

        return cal.getTime();
    }

    private String getSoftwareVersion(byte[] data) {
        byte[] content = getContent(data);

        return content.toString();
    }

    public void setTime(byte[] data) {

    }

    public class CustomerHistory {
        private Date mTimestamp;
        private float mResult;
        private byte mMeal;
        private byte mSample;

        public Date getTimestamp() {
            return mTimestamp;
        }

        public float getResult() {
            return mResult;
        }

        public byte getMeal() {
            return mMeal;
        }

        public byte getSample() {
            return mSample;
        }

        public boolean isBeforeMeal() {
            return mMeal == MEAL_FPG;
        }

        public boolean isAfterMeal() {
            return mMeal == MEAL_PPG;
        }

        @Override
        public String toString() {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm", Locale.getDefault());

            return String.format(Locale.getDefault(), "Date: %s, Result: %.1f, Meal=%x, Sample: %x",
                    dateFormat.format(mTimestamp), mResult, mMeal, mSample);
        }
    }
}
