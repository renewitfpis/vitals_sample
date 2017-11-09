package sg.lifecare.ble.device.aandd;


import sg.lifecare.ble.parser.BloodPressureMeasurement;

public interface ANDUA651ManagerCallbacks extends ANDManagerCallbacks {

    void onBloodPressureMeasurementRead(BloodPressureMeasurement bloodPressureMeasurement);
}
