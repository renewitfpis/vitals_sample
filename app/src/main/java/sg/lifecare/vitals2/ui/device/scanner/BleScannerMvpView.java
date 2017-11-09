package sg.lifecare.vitals2.ui.device.scanner;


import java.util.List;

import no.nordicsemi.android.support.v18.scanner.ScanResult;
import sg.lifecare.vitals2.ui.base.MvpView;

public interface BleScannerMvpView extends MvpView {

    void showScanLoading();

    void hideScanLoading();

    void bleScanResult(int callbackType, ScanResult result);

    void bleBatchScanResults(final List<ScanResult> results);

    void onNoDeviceFound();
}
