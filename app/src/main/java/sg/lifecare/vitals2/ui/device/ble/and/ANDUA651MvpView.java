package sg.lifecare.vitals2.ui.device.ble.and;


import sg.lifecare.ble.parser.BloodPressureMeasurement;

public interface ANDUA651MvpView extends ANDMvpView {

    void onBloodPressureMeasurementRead(BloodPressureMeasurement bloodPressure);
}
