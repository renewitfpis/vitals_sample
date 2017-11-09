package sg.lifecare.data.local;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.securepreferences.SecurePreferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import sg.lifecare.vitals2.R;
import timber.log.Timber;

/**
 * Save added device data
 */

public class DeviceData {

    private static final String PREFS_NAME = "device_data";

    private static DeviceData sInstance;

    public static final int DEVICE_AND_UC352 = 1;
    public static final int DEVICE_AND_UA651 = 2;

    private static final String COMPANY_AND = "A&D";
    private static final String COMPANY_ACCUCHEK = "AccuChek";
    private static final String COMPANY_TERUMO = "Terumo";
    private static final String COMPANY_NONIN = "Nonin";

    private static final String METHOD_BLE = "BLE";
    private static final String METHOD_NFC = "NFC";

    private static final String TYPE_BG = "BG";
    private static final String TYPE_BP = "BP";
    private static final String TYPE_WS = "WS";
    private static final String TYPE_SPO2 = "SP02";

    private SharedPreferences mSharedPreferences;
    private ArrayList<Device> mDevices = new ArrayList<>();

    static DeviceData getInstance(final Context context) {
        if (sInstance == null) {
            sInstance = new DeviceData(context);
        }

        return sInstance;
    }

    public static int getDeviceNameByType(int type) {
        switch (type) {
            case DEVICE_AND_UC352:
                return R.string.device_uc_352;

            case DEVICE_AND_UA651:
                return R.string.device_ua_651;

            default:
                return -1;
        }
    }

    private DeviceData(final Context context) {
        mSharedPreferences = new SecurePreferences(context);

        Gson gson = new Gson();
        String jsonString = mSharedPreferences.getString("devices", "");

        Timber.d("DeviceStore: jsonString=%s", jsonString);

        mDevices = gson.fromJson(jsonString, new TypeToken<List<Device>>() {}.getType());

        if (mDevices == null) {
            mDevices = new ArrayList<>();
        }
    }

    public List<Device> getDevices() {
        return mDevices;
    }

    public void clearAll() {
        mDevices = new ArrayList<>();
        save();
    }

    public List<Device> getWeightDevices() {
        ArrayList<Device> devices = new ArrayList<>();
        if ((mDevices != null) && (mDevices.size() > 0)) {
            for (Device device : mDevices) {
                if (TYPE_WS.equals(device.type)) {
                    devices.add(device);
                }
            }
        }

        return devices;
    }

    public List<Device> getBloodGlucoseDevices() {
        ArrayList<Device> devices = new ArrayList<>();
        if ((mDevices != null) && (mDevices.size() > 0)) {
            for (Device device : mDevices) {
                if (TYPE_BG.equals(device.type)) {
                    devices.add(device);
                }
            }
        }

        return devices;
    }

    public List<Device> getBleBloodGlucoseDevices() {
        ArrayList<Device> devices = new ArrayList<>();
        if ((mDevices != null) && (mDevices.size() > 0)) {
            for (Device device : mDevices) {
                if (TYPE_BG.equals(device.type) && device.isBle()) {
                    devices.add(device);
                }
            }
        }
        return devices;
    }

    public List<Device> getNfcBloodGlucoseDevices() {
        ArrayList<Device> devices = new ArrayList<>();
        if ((mDevices != null) && (mDevices.size() > 0)) {
            for (Device device : mDevices) {
                if (TYPE_BG.equals(device.type) && device.isNfc()) {
                    devices.add(device);
                }
            }
        }
        return devices;
    }

    public List<Device> getBloodPressureDevices() {
        ArrayList<Device> devices = new ArrayList<>();
        if ((mDevices != null) && (mDevices.size() > 0)) {
            for (Device device : mDevices) {
                if (TYPE_BP.equals(device.type)) {
                    devices.add(device);
                }
            }
        }

        return devices;
    }

    public List<Device> getNoninDevices() {
        ArrayList<Device> devices = new ArrayList<>();
        if ((mDevices != null) && (mDevices.size() > 0)) {
            for (Device device : mDevices) {
                if (COMPANY_NONIN.equalsIgnoreCase(device.getCompany())) {
                    devices.add(device);
                }
            }
        }

        return devices;
    }

    public boolean addTerumoMedisafeFit(String deviceId, String name) {
        return addNfcDevice(deviceId, name, TYPE_BG, COMPANY_TERUMO);
    }

    public boolean addANDUC352(String deviceId, String name) {
        return addBleDevice(deviceId, name, TYPE_WS, COMPANY_AND);
    }

    public boolean addANDUA651(String deviceId, String name) {
        return addBleDevice(deviceId, name, TYPE_BP, COMPANY_AND);
    }

    public boolean addAccuChekAvivaConnect(String deviceId, String name) {
        return addBleDevice(deviceId, name, TYPE_BG, COMPANY_ACCUCHEK);
    }

    public boolean addNonin3230(String deviceId, String name) {
        return addBleDevice(deviceId, name, TYPE_SPO2, COMPANY_NONIN);
    }

    private boolean addNfcDevice(String deviceId, String name, String type, String company) {
        return addDevice(deviceId, METHOD_NFC, name, type, company);
    }

    private boolean addBleDevice(String deviceId, String name, String type, String company) {
        return addDevice(deviceId, METHOD_BLE, name, type, company);
    }

    private boolean addDevice(String deviceId, String method, String name, String type, String company) {
        if (TextUtils.isEmpty(deviceId) || TextUtils.isEmpty(name)) {
            Timber.w("addBleDevice: invalid parameters");
            return false;
        }

        boolean isFound = false;

        if (mDevices == null) {
            mDevices = new ArrayList<>();
        } else {

            for (Device device : mDevices) {
                if (deviceId.equals(device.device_id)) {
                    device.device_id = deviceId;
                    device.method = method;
                    device.name = name;
                    device.type = type;
                    device.company = company;

                    isFound = true;
                }
            }
        }

        if (!isFound) {
            Device device = new Device();

            device.device_id = deviceId;
            device.method = method;
            device.name = name;
            device.type = type;
            device.company = company;

            mDevices.add(device);
        }

        save();
        return true;
    }

    public boolean removeDeviceById(String id) {

        if (!TextUtils.isEmpty(id)) {

            for (int i = 0; i < mDevices.size(); i++) {
                if (id.equalsIgnoreCase(mDevices.get(i).getId())) {
                    mDevices.remove(i);
                    save();
                    return true;
                }
            }
        }
        return false;
    }

    private void save() {
        String jsonString = new Gson().toJson(mDevices, new TypeToken<List<Device>>() {}.getType());

        Timber.d("save: %s", jsonString);

        mSharedPreferences.edit().putString("devices", jsonString).apply();
    }



    public class Device {
        String device_id;   // MAC id
        String method;      // BLE, NFC
        String name;        // user defined name
        String type;        // blood glucose, weight
        String company;

        public String getId() {
            return device_id;
        }

        public String getName() {
            return name;
        }

        public boolean isBle() {
            return METHOD_BLE.equals(method);
        }

        public boolean isNfc() {
            return METHOD_NFC.equals(method);
        }

        public String getCompany() {
            return company;
        }

        @Override
        public String toString() {
            return String.format("DeviceId: %s, Method: %s, Name: %s, Type: %s, Company: %s",
                    device_id, method, name, type, company);
        }
    }
}

