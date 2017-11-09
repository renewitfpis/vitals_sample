package sg.lifecare.vitals2.ui.bloodglucose;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.TextInputLayout;
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
import sg.lifecare.ble.device.vivacheck.InoSmartManager;
import sg.lifecare.ble.parser.BloodGlucoseMeasurement;
import sg.lifecare.ble.utility.BleUtils;
import sg.lifecare.utils.DateUtils;
import sg.lifecare.vitals2.R;
import sg.lifecare.vitals2.ui.base.BaseFragment;
import sg.lifecare.vitals2.ui.device.scanner.BleScannerMvpView;
import sg.lifecare.vitals2.ui.device.scanner.BleScannerPresenter;
import timber.log.Timber;

public class InoSmartFragment extends BaseFragment implements BleScannerMvpView, InoSmartMvpView {

    private static final int PERMISSION_REQ_LOCATION = 1;

    private static final int REQUEST_ENABLE_BT = 1;

    @Inject
    BleScannerPresenter<BleScannerMvpView> mBleScannerPresenter;

    @Inject
    InoSmartMvpPresenter<InoSmartMvpView> mInoSmartPresenter;

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

    @BindView(R.id.blood_glucose_label)
    TextView mLabel;

    @BindView(R.id.save_button)
    Button mSaveButton;

    @BindView(R.id.connect_button)
    Button mConnectButton;

    @BindView(R.id.stop_button)
    Button mStopButton;

    @BindView(R.id.notes_layout)
    TextInputLayout mNotesLayout;

    private BluetoothDevice mDevice;
    private List<InoSmartManager.CustomerHistory> mCustomerHistories = null;

    public static InoSmartFragment newInstance() {
        return new InoSmartFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blood_glucose_ino_smart, container, false);

        Timber.d("onCreateView");

        getActivityComponent().inject(this);
        setUnbinder(ButterKnife.bind(this, view));

        mBleScannerPresenter.onAttach(this);

        mInoSmartPresenter.onAttach(this);
        mInoSmartPresenter.init(getContext());

        setupViews(view);

        return view;
    }

    @Override
    public void onDestroyView() {

        mBleScannerPresenter.stopScan();
        mBleScannerPresenter.onDetach();

        mInoSmartPresenter.uninit();
        mInoSmartPresenter.onDetach();

        super.onDestroyView();
    }

    @Override
    protected void setupViews(View view) {
        setupConnectView(R.string.device_msg_start_connect);
    }

    @OnClick(R.id.connect_button)
    public void onConnectClick() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQ_LOCATION);
        } else {
            if (BleUtils.isBluetoothEnabled(getContext())) {
                setupScanView();

                List<ScanFilter> scanFilters = new ArrayList<>();
                //scanFilters.add(new ScanFilter.Builder().setServiceUuid(new ParcelUuid(UrionManager.PROPRIETARY_SERVICE)).build());
                //scanFilters.add(new ScanFilter.Builder().setDeviceName("Bluetooth BP").build());

                mDevice = null;
                mCustomerHistories = null;
                mBleScannerPresenter.startScan(scanFilters, 0);
            } else {
                final Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            }
        }
    }

    @OnClick(R.id.stop_button)
    public void onStopClick() {
        setupConnectView(R.string.device_msg_start_connect);

        mBleScannerPresenter.stopScan();
        mInoSmartPresenter.disconnect();
    }

    @OnClick(R.id.save_button)
    public void onSaveClick() {

        Intent intent = new Intent();

        if ((mCustomerHistories != null) && (mCustomerHistories.size() > 0)) {
            BloodGlucoseMeasurement measurement = BloodGlucoseMeasurement.get(
                    mCustomerHistories.get(0).getResult(), mCustomerHistories.get(0).getTimestamp());
            if (mCustomerHistories.get(0).isAfterMeal()) {
                measurement.setAfterMeal();
            } else if (mCustomerHistories.get(0).isBeforeMeal()) {
                measurement.setBeforeMeal();
            }

            intent.putExtra(BloodGlucoseActivity.PARAM_DATA, measurement);
            intent.putExtra(BloodGlucoseActivity.PARAM_DEVICE_ID, mDevice.getAddress());
            getActivity().setResult(Activity.RESULT_OK, intent);
        } else {
            getActivity().setResult(Activity.RESULT_CANCELED);
        }

        getActivity().finish();

    }

    @OnClick(R.id.cancel_button)
    public void onCancelClick() {
        getActivity().finish();
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
    }

    private void setupConnectView(@StringRes int res) {
        mSaveButton.setVisibility(View.GONE);
        mConnectButton.setVisibility(View.VISIBLE);
        mStopButton.setVisibility(View.GONE);

        mProgressBar.setVisibility(View.INVISIBLE);
        mProgressText.setVisibility(View.VISIBLE);
        mProgressText.setText(res);

        setContentVisibility(View.INVISIBLE);
    }

    private void setupSaveView() {
        mProgressBar.setVisibility(View.INVISIBLE);
        mProgressText.setVisibility(View.INVISIBLE);

        mStopButton.setVisibility(View.GONE);

        if ((mCustomerHistories != null) && (mCustomerHistories.size() > 0)) {
            mSaveButton.setVisibility(View.VISIBLE);
            mConnectButton.setVisibility(View.GONE);

            DateTime dateTime = new DateTime(mCustomerHistories.get(0).getTimestamp());
            mTimestampText.setText(DateUtils.FULL_DATETIME_FORMAT.print(dateTime));
            mValueText.setText(String.format(Locale.getDefault(), "%.1f", mCustomerHistories.get(0).getResult()));

            setContentVisibility(View.VISIBLE);
        } else {
            setupConnectView(R.string.device_msg_no_data);
        }
    }

    @Override
    public void showScanLoading() {

    }

    @Override
    public void hideScanLoading() {

    }

    @Override
    public void bleScanResult(int callbackType, ScanResult result) {
        Timber.d("bleScanResult: %s", result.getDevice().getName());
        if ((mDevice == null) && (result.getDevice() != null) &&
                "BLE-Vivachek".equalsIgnoreCase(result.getDevice().getName())) {
            mDevice = result.getDevice();

            Timber.d("bleScanResult: deviceType=%d", mDevice.getType());

            mBleScannerPresenter.stopScan();

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mInoSmartPresenter.connect(mDevice);
                }
            });

        }
    }

    @Override
    public void bleBatchScanResults(List<ScanResult> results) {

    }

    @Override
    public void onNoDeviceFound() {
        setupConnectView(R.string.device_msg_no_device);
    }

    @Override
    public void onDeviceDisconnected(BluetoothDevice device) {
        if (mCustomerHistories != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setupSaveView();
                }
            });
        }
    }

    @Override
    public void onDeviceErrorDisconnected(BluetoothDevice device) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setupConnectView(R.string.device_msg_error_disconnected);
            }
        });
    }

    @Override
    public void onDeviceResultUpdate(List<InoSmartManager.CustomerHistory> customerHistories) {

        // we just want to get result
        mCustomerHistories = customerHistories;
        mInoSmartPresenter.disconnect();
    }
}
