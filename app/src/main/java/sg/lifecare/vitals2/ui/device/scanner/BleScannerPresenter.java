package sg.lifecare.vitals2.ui.device.scanner;

import android.os.ParcelUuid;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;
import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat;
import no.nordicsemi.android.support.v18.scanner.ScanCallback;
import no.nordicsemi.android.support.v18.scanner.ScanFilter;
import no.nordicsemi.android.support.v18.scanner.ScanResult;
import no.nordicsemi.android.support.v18.scanner.ScanSettings;
import sg.lifecare.ble.device.aandd.ANDUA651Manager;
import sg.lifecare.ble.device.aandd.ANDUC352Manager;
import sg.lifecare.data.DataManager;
import sg.lifecare.vitals2.ui.base.BasePresenter;
import timber.log.Timber;

public class BleScannerPresenter<V extends BleScannerMvpView> extends BasePresenter<V>
        implements BleScannerMvpPresenter<V> {

    private static final int SCAN_DURATION = 30; // in seconds

    private static ScanSettings mScanSettings = new ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_BALANCED)
            .setReportDelay(1000)
            .setUseHardwareBatchingIfSupported(true)
            .build();

    private BluetoothLeScannerCompat mScanner;
    private boolean mIsScanning = false;

    private DisposableSubscriber<Long> mScanTimeoutSubscriber;

    @Inject
    public BleScannerPresenter(DataManager dataManager,
            CompositeDisposable compositeDisposable) {
        super(dataManager, compositeDisposable);

        mScanner = BluetoothLeScannerCompat.getScanner();
    }

    @Override
    public void scanForANDUC352() {
        final List<ScanFilter> filters = new ArrayList<>();
        filters.add(new ScanFilter.Builder().setServiceUuid(
                ParcelUuid.fromString(ANDUC352Manager.WEIGHT_SERVICE.toString())).build());
        startScan(filters);
    }

    @Override
    public void scanForANDUA651() {
        final List<ScanFilter> filters = new ArrayList<>();
        filters.add(new ScanFilter.Builder().setServiceUuid(
                ParcelUuid.fromString(ANDUA651Manager.BLOOD_PRESSURE_SERVICE.toString())).build());
        startScan(filters);
    }

    @Override
    public void stopScan() {
        if (mIsScanning) {
            mScanner.stopScan(mScanCallback);
            mIsScanning = false;
        }

        removeScanTimeout();

        getMvpView().hideScanLoading();
    }

    @Override
    public void startScan(List<ScanFilter> filters) {
        stopScan();

        try {
            mScanner.startScan(filters, mScanSettings, mScanCallback);
            mIsScanning = true;

            addScanTimeout();

            getMvpView().showScanLoading();
        } catch (IllegalStateException ex) {
            Timber.e(ex, ex.getMessage());
        }
    }

    private void addScanTimeout() {

        removeScanTimeout();

        mScanTimeoutSubscriber = new DisposableSubscriber<Long>() {
            @Override
            public void onNext(Long time) {
                Timber.d("onNext: %b", mIsScanning);
                if (mIsScanning) {
                    getMvpView().onNoDeviceFound();
                    stopScan();
                }
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onComplete() {
                Timber.d("onComplete");
            }
        };

        Flowable.timer(SCAN_DURATION, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mScanTimeoutSubscriber);
    }

    private void removeScanTimeout() {
        if ((mScanTimeoutSubscriber) != null && !mScanTimeoutSubscriber.isDisposed()) {
            mScanTimeoutSubscriber.dispose();
        }

        mScanTimeoutSubscriber = null;
    }

    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);

            Timber.d("onScanResult: callbackType=%d, data=%s", callbackType, result.toString());
        }

        @Override
        public void onBatchScanResults(final List<ScanResult> results) {
            for (ScanResult result : results) {
                Timber.d("onBatchScanResults: %s", result.toString());
            }
            if (getMvpView() != null) {
                getMvpView().bleBatchScanResults(results);
            }
        }

        @Override
        public void onScanFailed(final int errorCode) {
            // should never be called
        }
    };
}
