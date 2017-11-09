package sg.lifecare.vitals2.ui.device.ble.and;


import sg.lifecare.vitals2.ui.base.MvpView;

public interface ANDMvpView extends MvpView {

    void onPairingSuccess();

    void onPairingFail();

    void onReadCompleted();
}
