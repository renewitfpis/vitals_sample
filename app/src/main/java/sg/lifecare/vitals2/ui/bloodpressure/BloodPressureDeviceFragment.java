package sg.lifecare.vitals2.ui.bloodpressure;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import sg.lifecare.ble.parser.BloodPressureMeasurement;
import sg.lifecare.data.local.DeviceData;
import sg.lifecare.utils.DateUtils;
import sg.lifecare.vitals2.R;
import sg.lifecare.vitals2.ui.base.BaseFragment;
import sg.lifecare.vitals2.ui.device.ble.and.ANDUA651MvpView;
import sg.lifecare.vitals2.ui.device.ble.and.ANDUA651Presenter;
import sg.lifecare.vitals2.ui.device.scanner.BleScannerMvpView;
import sg.lifecare.vitals2.ui.device.scanner.BleScannerPresenter;
import timber.log.Timber;

public class BloodPressureDeviceFragment extends BaseFragment implements
        BloodPressureDeviceMvpView, ANDUA651MvpView, BleScannerMvpView {

    public static final String TAG = BloodPressureDeviceFragment.class.getSimpleName();

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

    @BindView(R.id.systolic_value_text)
    TextView mSystolicValueText;

    @BindView(R.id.diastolic_value_text)
    TextView mDiastolicValueText;

    @BindView(R.id.pulse_rate_value_text)
    TextView mPulseValueText;

    @BindView(R.id.systolic_label)
    TextView mSystolicLabel;

    @BindView(R.id.diastolic_label)
    TextView mDiastolicLabel;

    @BindView(R.id.pulse_rate_label)
    TextView mPulseLabel;

    @BindView(R.id.save_button)
    Button mSaveButton;

    @BindView(R.id.connect_button)
    Button mConnectButton;

    @BindView(R.id.stop_button)
    Button mStopButton;

    @BindView(R.id.notes_layout)
    TextInputLayout mNotesLayout;

    private List<ScanFilter> mScanFilters = new ArrayList<>();
    private List<BloodPressureMeasurement> mBloodPressures = new ArrayList<>();
    private String mDeviceId;

    public static BloodPressureDeviceFragment newInstance() {
        BloodPressureDeviceFragment fragment = new BloodPressureDeviceFragment();
        return fragment;
    }

    @Inject
    BloodPressureDevicePresenter<BloodPressureDeviceMvpView> mBloodPressurePresenter;

    @Inject
    ANDUA651Presenter<ANDUA651MvpView> mANDUA651Presenter;

    @Inject
    BleScannerPresenter<BleScannerMvpView> mBleScannerPresenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blood_pressure_device, container, false);

        Timber.d("onCreateView");

        getActivityComponent().inject(this);
        setUnbinder(ButterKnife.bind(this, view));

        mBloodPressurePresenter.onAttach(this);

        mBleScannerPresenter.onAttach(this);

        mANDUA651Presenter.onAttach(this);
        mANDUA651Presenter.init(getContext());

        setupViews(view);

        return view;
    }

    @Override
    public void onDestroyView() {
        mBloodPressurePresenter.onDetach();

        mBleScannerPresenter.stopScan();
        mBleScannerPresenter.onDetach();

        mANDUA651Presenter.uninit();
        mANDUA651Presenter.onDetach();

        super.onDestroyView();
    }

    @Override
    protected void setupViews(View view) {
        List<DeviceData.Device> devices = mBloodPressurePresenter.getDevices();

        for (DeviceData.Device device : devices) {
            mScanFilters.add(new ScanFilter.Builder().setDeviceAddress(device.getId()).build());
        }

        setContentVisibility(View.INVISIBLE);

        if ((devices.size() > 0) && (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            setupScanView();
        } else {
            setupConnectView();
        }
    }

    private void setContentVisibility(int visibility) {
        mTimestampCardView.setVisibility(visibility);
        mSystolicValueText.setVisibility(visibility);
        mDiastolicValueText.setVisibility(visibility);
        mPulseValueText.setVisibility(visibility);
        mSystolicLabel.setVisibility(visibility);
        mDiastolicLabel.setVisibility(visibility);
        mPulseLabel.setVisibility(visibility);
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

        BloodPressureMeasurement bloodPressure = mBloodPressures.get(mBloodPressures.size()-1);

        mSystolicValueText.setText(String.format(Locale.getDefault(), "%d", (int)bloodPressure.getSystolic()));
        mDiastolicValueText.setText(String.format(Locale.getDefault(), "%d", (int)bloodPressure.getDiastolic()));
        mPulseValueText.setText(String.format(Locale.getDefault(), "%d", (int)bloodPressure.getPulseRate()));

        DateTime dateTime = new DateTime(bloodPressure.getTimestamp());
        mTimestampText.setText(DateUtils.FULL_DATETIME_FORMAT.print(dateTime));

        setContentVisibility(View.VISIBLE);
    }

    @OnClick(R.id.cancel_button)
    public void onCancelClick() {
        getActivity().finish();
    }

    @OnClick(R.id.connect_button)
    public void onConnectClick() {
        // check permissions
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mBleScannerPresenter.startScan(mScanFilters);
            setupScanView();
            mBloodPressures.clear();
        } else {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
        }



    }

    @OnClick(R.id.stop_button)
    public void onStopClick() {
        mBleScannerPresenter.stopScan();
        mANDUA651Presenter.stopConnect();
        setupConnectView();
    }

    @OnClick(R.id.save_button)
    public void onSaveClick() {

        Intent intent = new Intent();

        if (mBloodPressures.size() > 0) {
            BloodPressureMeasurement bloodPressure = mBloodPressures.get(mBloodPressures.size()-1);
            intent.putExtra(BloodPressureActivity.PARAM_DATA, bloodPressure);
            intent.putExtra(BloodPressureActivity.PARAM_DEVICE_ID, mDeviceId);
            getActivity().setResult(Activity.RESULT_OK, intent);
        } else {
            getActivity().setResult(Activity.RESULT_CANCELED);
        }

        getActivity().finish();

    }

    @Override
    public void onPairingSuccess() {

    }

    @Override
    public void onPairingFail() {

    }

    @Override
    public void onReadCompleted() {
        Timber.d("onReadCompleted");
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mBloodPressures.size() > 0) {
                    setupSaveView();
                } else {
                    setupConnectView();
                }
            }
        });
    }

    @Override
    public void onBloodPressureMeasurementRead(BloodPressureMeasurement bloodPressure) {
        mBloodPressures.add(bloodPressure);
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
        if (results.size() > 0) {
            mBleScannerPresenter.stopScan();
            mANDUA651Presenter.readDevice(results.get(0).getDevice());
            mDeviceId = results.get(0).getDevice().getAddress();
        }
    }

    @Override
    public void onNoDeviceFound() {
        setupConnectView();
    }
}
