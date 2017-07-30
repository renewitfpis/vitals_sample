package sg.lifecare.vitals2.ui.device.ble.jumper;

import sg.lifecare.vitals2.ui.base.MvpView;

public interface JumperThermometerMvpView extends MvpView {

    void onTemperatureRead(double temperature);
}
