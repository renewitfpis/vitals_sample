package sg.lifecare.vitals2.ui.device.ble.and;


import sg.lifecare.ble.parser.WeightMeasurement;
import sg.lifecare.vitals2.ui.base.MvpView;

public interface ANDUC352MvpView extends ANDMvpView {

    void onWeightMeasurementRead(WeightMeasurement weight);
}
