package sg.lifecare.vitals2.ui.device.ble.and;


import sg.lifecare.ble.parser.BodyWeightMeasurement;

public interface ANDUC352MvpView extends ANDMvpView {

    void onWeightMeasurementRead(BodyWeightMeasurement weight);
}
