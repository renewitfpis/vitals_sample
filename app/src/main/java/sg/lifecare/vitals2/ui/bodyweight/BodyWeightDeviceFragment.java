package sg.lifecare.vitals2.ui.bodyweight;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import no.nordicsemi.android.support.v18.scanner.ScanFilter;
import no.nordicsemi.android.support.v18.scanner.ScanResult;
import sg.lifecare.ble.parser.WeightMeasurement;
import sg.lifecare.data.local.DeviceData;
import sg.lifecare.utils.DateUtils;
import sg.lifecare.vitals2.R;
import sg.lifecare.vitals2.ui.base.BaseFragment;
import sg.lifecare.vitals2.ui.device.ble.and.ANDUC352MvpView;
import sg.lifecare.vitals2.ui.device.ble.and.ANDUC352Presenter;
import sg.lifecare.vitals2.ui.device.scanner.BleScannerMvpView;
import sg.lifecare.vitals2.ui.device.scanner.BleScannerPresenter;
import timber.log.Timber;

public class BodyWeightDeviceFragment extends BaseFragment
        implements BodyWeightDeviceMvpView, ANDUC352MvpView, BleScannerMvpView {

    public static final String TAG = BodyWeightDeviceFragment.class.getSimpleName();

    public static BodyWeightDeviceFragment newInstance() {
        BodyWeightDeviceFragment fragment = new BodyWeightDeviceFragment();
        return fragment;
    }

    @Inject
    BodyWeightDevicePresenter<BodyWeightDeviceMvpView> mBodyWeightPresenter;

    @Inject
    ANDUC352Presenter<ANDUC352MvpView> mANDUC352Presenter;

    @Inject
    BleScannerPresenter<BleScannerMvpView> mBleScannerPresenter;

    @BindView(R.id.progress_layout)
    View mProgressLayout;

    @BindView(R.id.search_progressbar)
    ProgressBar mProgressBar;

    @BindView(R.id.progress_text)
    TextView mProgressText;

    @BindView(R.id.timestamp_cardview)
    CardView mTimestampCardView;

    @BindView(R.id.timestamp_text)
    TextView mTimestampText;

    @BindView(R.id.value_text)
    TextView mValueText;

    @BindView(R.id.body_weight_label)
    TextView mLabel;

    @BindView(R.id.save_button)
    Button mSaveButton;

    @BindView(R.id.connect_button)
    Button mConnectButton;

    @BindView(R.id.stop_button)
    Button mStopButton;

    @BindView(R.id.notes_layout)
    TextInputLayout mNotesLayout;

    private List<ScanFilter> mScanFilters = new ArrayList<>();
    private List<WeightMeasurement> mWeights = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_body_weight_device, container, false);

        Timber.d("onCreateView");

        getActivityComponent().inject(this);
        setUnbinder(ButterKnife.bind(this, view));

        mBodyWeightPresenter.onAttach(this);

        mBleScannerPresenter.onAttach(this);

        mANDUC352Presenter.onAttach(this);
        mANDUC352Presenter.init(getContext());

        setupViews(view);

        return view;
    }

    @Override
    public void onDestroyView() {
        mBodyWeightPresenter.onDetach();

        mBleScannerPresenter.stopScan();
        mBleScannerPresenter.onDetach();

        mANDUC352Presenter.uninit();
        mANDUC352Presenter.onDetach();

        super.onDestroyView();
    }


    @Override
    protected void setupViews(View view) {
        List<DeviceData.Device> devices = mBodyWeightPresenter.getDevices();

        for (DeviceData.Device device : devices) {
            mScanFilters.add(new ScanFilter.Builder().setDeviceAddress(device.getId()).build());
        }

        setContentVisibility(View.INVISIBLE);

        if (devices.size() > 0) {
            setupScanView();
        } else {
            setupConnectView();
        }
    }

    private void setContentVisibility(int visibility) {
        mTimestampCardView.setVisibility(visibility);
        mValueText.setVisibility(visibility);
        mLabel.setVisibility(visibility);
        mNotesLayout.setVisibility(visibility);
    }

    private void setupScanView() {
        mSaveButton.setVisibility(View.GONE);
        mConnectButton.setVisibility(View.GONE);
        mStopButton.setVisibility(View.VISIBLE);

        mProgressBar.setVisibility(View.VISIBLE);
        mProgressText.setText(R.string.device_msg_scanning);

        mBleScannerPresenter.startScan(mScanFilters);
    }

    private void setupConnectView() {
        mSaveButton.setVisibility(View.GONE);
        mConnectButton.setVisibility(View.VISIBLE);
        mStopButton.setVisibility(View.GONE);

        mProgressBar.setVisibility(View.INVISIBLE);
        mProgressText.setText(R.string.device_msg_no_device);
    }

    private void setupSaveView() {
        mSaveButton.setVisibility(View.VISIBLE);
        mConnectButton.setVisibility(View.GONE);
        mStopButton.setVisibility(View.GONE);

        mProgressBar.setVisibility(View.INVISIBLE);
        mProgressText.setVisibility(View.INVISIBLE);

        WeightMeasurement weight = mWeights.get(mWeights.size()-1);

        mValueText.setText(String.format(Locale.getDefault(), "%.1f", weight.getWeight()));

        DateTime dateTime = new DateTime(weight.getTimestamp());
        mTimestampText.setText(DateUtils.FULL_DATETIME_FORMAT.print(dateTime));

        setContentVisibility(View.VISIBLE);
    }

    @OnClick(R.id.cancel_button)
    public void onCancelClick() {
        getActivity().finish();
    }

    @OnClick(R.id.connect_button)
    public void onConnectClick() {
        mBleScannerPresenter.startScan(mScanFilters);
        setupScanView();
        mWeights.clear();
    }

    @OnClick(R.id.stop_button)
    public void onStopClick() {
        mBleScannerPresenter.stopScan();
        mANDUC352Presenter.stopConnect();
        setupConnectView();
    }

    @Override
    public void onPairingSuccess() {

    }

    @Override
    public void onPairingFail() {

    }

    @Override
    public void onWeightMeasurementRead(WeightMeasurement weight) {
        Timber.d("onWeightMeasurementRead");
        mWeights.add(weight);
    }

    @Override
    public void onReadCompleted() {
        Timber.d("onReadCompleted");
        getActivity().runOnUiThread(() -> {
            if (mWeights.size() > 0) {
                setupSaveView();
            } else {
                setupConnectView();
            }
        });
    }

    @Override
    public void showScanLoading() {

    }

    @Override
    public void hideScanLoading() {

    }

    @Override
    public void bleBatchScanResults(List<ScanResult> results) {
        if (results.size() > 0) {
            mBleScannerPresenter.stopScan();
            mANDUC352Presenter.readDevice(results.get(0).getDevice());
        }
    }

    @Override
    public void onNoDeviceFound() {
        setupConnectView();
    }
}
