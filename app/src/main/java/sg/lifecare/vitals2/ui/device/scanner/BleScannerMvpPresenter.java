package sg.lifecare.vitals2.ui.device.scanner;


import java.util.List;

import no.nordicsemi.android.support.v18.scanner.ScanFilter;
import sg.lifecare.framework.di.PerActivity;
import sg.lifecare.vitals2.ui.base.MvpPresenter;
import sg.lifecare.vitals2.ui.base.MvpView;

@PerActivity
public interface BleScannerMvpPresenter<V extends MvpView> extends MvpPresenter<V> {

    void scanForANDUC352();

    void scanForANDUA651();

    void startScan(List<ScanFilter> filters);

    void startScan(List<ScanFilter> filters, long timeoutSec);

    void startScan(long timeoutSec);

    void stopScan();
}
