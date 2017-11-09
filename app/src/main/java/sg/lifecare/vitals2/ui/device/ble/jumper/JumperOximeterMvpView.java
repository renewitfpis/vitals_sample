package sg.lifecare.vitals2.ui.device.ble.jumper;

import sg.lifecare.vitals2.ui.base.MvpView;

public interface JumperOximeterMvpView extends MvpView {

    void onSpo2PulsePiRead(int spo2, int pulse, double pi);
}
