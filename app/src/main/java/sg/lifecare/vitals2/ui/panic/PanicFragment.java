package sg.lifecare.vitals2.ui.panic;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import no.nordicsemi.android.support.v18.scanner.ScanFilter;
import no.nordicsemi.android.support.v18.scanner.ScanResult;
import sg.lifecare.vitals2.R;
import sg.lifecare.vitals2.ui.base.BaseFragment;
import sg.lifecare.vitals2.ui.device.scanner.BleScannerMvpPresenter;
import sg.lifecare.vitals2.ui.device.scanner.BleScannerMvpView;
import timber.log.Timber;

public class PanicFragment extends BaseFragment implements BleScannerMvpView {

    @Inject
    BleScannerMvpPresenter<BleScannerMvpView> mBleScannerPresenter;

    @BindView(R.id.status_text)
    TextView mStatusText;

    static Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            final PanicFragment fragment = (PanicFragment) msg.obj;
            if (fragment != null) {
                fragment.updateStatus(false);
            }
        }
    };

    public static PanicFragment newInstance() {
        return new PanicFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_panic, container, false);

        getActivityComponent().inject(this);
        setUnbinder(ButterKnife.bind(this, view));

        mBleScannerPresenter.onAttach(this);

        setupViews(view);

        return view;
    }

    @Override
    public void onDestroyView() {
        mBleScannerPresenter.stopScan();
        mBleScannerPresenter.onDetach();

        super.onDestroyView();

        Timber.d("onDestroyView");
    }

    @Override
    public void onStart() {
        super.onStart();

        final List<ScanFilter> filters = new ArrayList<>();
        filters.add(new ScanFilter.Builder().setDeviceAddress("C6:2D:EB:80:3E:29").build());

        mBleScannerPresenter.startScan(filters, 0);
    }

    @Override
    public void onStop() {
        super.onStop();

        mBleScannerPresenter.stopScan();
    }

    @Override
    protected void setupViews(View view) {
        updateStatus(false);
    }

    @Override
    public void showScanLoading() {

    }

    @Override
    public void hideScanLoading() {

    }

    @Override
    public void bleScanResult(int callbackType, ScanResult result) {

    }

    @Override
    public void bleBatchScanResults(List<ScanResult> results) {
        Timber.d("bleBatchScanResults: size=", results.size());

        if (results.size() > 0) {
            updateStatus(true);

            mHandler.removeMessages(1);

            Message msg = mHandler.obtainMessage(1, this);
            mHandler.sendMessageDelayed(msg, 10000);
        }
    }

    @Override
    public void onNoDeviceFound() {

    }

    private void updateStatus(boolean status) {
        if (status) {
            mStatusText.setText("Panic");
            mStatusText.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.red_300));
        } else {
            mStatusText.setText("No Event");
            mStatusText.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.green_300));
        }
    }


}
