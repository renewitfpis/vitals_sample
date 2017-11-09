package sg.lifecare.ble.device.aandd;


import sg.lifecare.ble.parser.WeightMeasurement;

public interface ANDUC352ManagerCallbacks extends ANDManagerCallbacks {

    void onWeightMeasurementRead(WeightMeasurement weightMeasurement);
}
