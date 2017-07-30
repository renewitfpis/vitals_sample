package sg.lifecare.ble.device.aandd;


import sg.lifecare.ble.parser.BodyWeightMeasurement;

public interface ANDUC352ManagerCallbacks extends ANDManagerCallbacks {

    void onWeightMeasurementRead(BodyWeightMeasurement weightMeasurement);
}
