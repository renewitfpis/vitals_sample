package sg.lifecare.vitals2.ui.urion;

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
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import no.nordicsemi.android.support.v18.scanner.ScanFilter;
import no.nordicsemi.android.support.v18.scanner.ScanResult;
import sg.lifecare.ble.parser.BloodPressureMeasurement;
import sg.lifecare.ble.utility.BleUtils;
import sg.lifecare.utils.DateUtils;
import sg.lifecare.vitals2.R;
import sg.lifecare.vitals2.ui.base.BaseFragment;
import sg.lifecare.vitals2.ui.bloodpressure.BloodPressureActivity;
import sg.lifecare.vitals2.ui.device.ble.urion.UrionMvpPresenter;
import sg.lifecare.vitals2.ui.device.ble.urion.UrionMvpView;
import sg.lifecare.vitals2.ui.device.scanner.BleScannerMvpView;
import sg.lifecare.vitals2.ui.device.scanner.BleScannerPresenter;
import timber.log.Timber;

public class UrionFragment extends BaseFragment implements BleScannerMvpView, UrionMvpView {

    private static final int PERMISSION_REQ_LOCATION = 1;

    private static final int REQUEST_ENABLE_BT = 1;

    @Inject
    BleScannerPresenter<BleScannerMvpView> mBleScannerPresenter;

    @Inject
    UrionMvpPresenter<UrionMvpView> mUrionPresenter;

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

    private BloodPressureMeasurement mMeasurement = null;
    private BluetoothDevice mDevice;



    public static UrionFragment newInstance() {
        return new UrionFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blood_pressure_urion, container, false);

        Timber.d("onCreateView");

        getActivityComponent().inject(this);
        setUnbinder(ButterKnife.bind(this, view));

        mBleScannerPresenter.onAttach(this);

        mUrionPresenter.onAttach(this);
        mUrionPresenter.init(getContext());

        setupViews(view);

        return view;
    }

    @Override
    public void onDestroyView() {

        mBleScannerPresenter.stopScan();
        mBleScannerPresenter.onDetach();

        mUrionPresenter.uninit();
        mUrionPresenter.onDetach();

        super.onDestroyView();
    }

    @Override
    protected void setupViews(View view) {
        setupConnectView(R.string.device_msg_start_connect);
    }

    @OnClick(R.id.connect_button)
    public void onConnectClick() {
        mMeasurement = null;

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQ_LOCATION);
        } else {
            if (BleUtils.isBluetoothEnabled(getContext())) {
                setupScanView();

                List<ScanFilter> scanFilters = new ArrayList<>();
                //scanFilters.add(new ScanFilter.Builder().setServiceUuid(new ParcelUuid(UrionManager.PROPRIETARY_SERVICE)).build());
                //scanFilters.add(new ScanFilter.Builder().setDeviceName("Bluetooth BP").build());

                mDevice = null;
                mMeasurement = null;
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
        mUrionPresenter.disconnect();
    }

    @OnClick(R.id.save_button)
    public void onSaveClick() {

        Intent intent = new Intent();

        if (mMeasurement != null) {
            intent.putExtra(BloodPressureActivity.PARAM_DATA, mMeasurement);
            intent.putExtra(BloodPressureActivity.PARAM_DEVICE_ID, mDevice.getAddress());
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
        mSaveButton.setVisibility(View.VISIBLE);
        mConnectButton.setVisibility(View.GONE);
        mStopButton.setVisibility(View.GONE);

        mProgressBar.setVisibility(View.INVISIBLE);
        mProgressText.setVisibility(View.INVISIBLE);

        mSystolicValueText.setText(String.format(Locale.getDefault(), "%d", (int)mMeasurement.getSystolic()));
        mDiastolicValueText.setText(String.format(Locale.getDefault(), "%d", (int)mMeasurement.getDiastolic()));
        mPulseValueText.setText(String.format(Locale.getDefault(), "%d", (int)mMeasurement.getPulseRate()));

        DateTime dateTime = new DateTime(mMeasurement.getTimestamp());
        mTimestampText.setText(DateUtils.FULL_DATETIME_FORMAT.print(dateTime));

        setContentVisibility(View.VISIBLE);
    }

    private void setupTransferView() {
        mSystolicValueText.setVisibility(View.VISIBLE);
        mDiastolicValueText.setVisibility(View.VISIBLE);
        mPulseValueText.setVisibility(View.VISIBLE);
        mSystolicLabel.setVisibility(View.VISIBLE);
        mDiastolicLabel.setVisibility(View.VISIBLE);
        mPulseLabel.setVisibility(View.VISIBLE);

        mProgressBar.setVisibility(View.INVISIBLE);
        mProgressText.setVisibility(View.INVISIBLE);

        mSystolicValueText.setText("__");
        mDiastolicValueText.setText("__");
        mPulseValueText.setText("__");

    }

    @Override
    public void showScanLoading() {

    }

    @Override
    public void hideScanLoading() {

    }

    @Override
    public void bleScanResult(int callbackType, ScanResult result) {
        if ((mDevice == null) && (result.getDevice() != null) &&
                "Bluetooth BP".equalsIgnoreCase(result.getDevice().getName())) {
            mDevice = result.getDevice();

            Timber.d("bleScanResult: deviceType=%d", mDevice.getType());

             mBleScannerPresenter.stopScan();
            mUrionPresenter.connect(mDevice);
        }
    }

    @Override
    public void bleBatchScanResults(List<ScanResult> results) {
        if (results.size() > 0) {
            /*if (mDevice == null) {
                mDevice = results.get(0).getDevice();
                /*getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //mBleScannerPresenter.stopScan();
                        mUrionPresenter.connect(mDevice);
                    }
                });*/
                /*mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mBleScannerPresenter.stopScan();
                        //mUrionPresenter.connect(mDevice);
                    }
                }, 1000);
            }*/

            //mBleScannerPresenter.stopScan();
            //mUrionPresenter.connect(results.get(0).getDevice());
            //mDeviceId = results.get(0).getDevice().getAddress();
        }
    }

    @Override
    public void onNoDeviceFound() {
        setupConnectView(R.string.device_msg_no_device);
    }

    @Override
    public void onDeviceConnected(BluetoothDevice device) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressText.setText(String.format(getContext().getResources().getString(R.string.urion_connected_to),
                        device.getName()));
            }
        });
    }

    @Override

    public void onDeviceErrorDisconnected(BluetoothDevice device) {
        Timber.d("onDeviceErrorDisconnected");

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setupConnectView(R.string.device_msg_error_disconnected);
            }
        });
    }

    @Override
    public void onMeasureStart() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setupTransferView();
            }
        });
    }

    @Override
    public void onPulseRead(int pulse) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mPulseValueText.setText(String.format(Locale.getDefault(), "%d", pulse));
            }
        });

    }

    @Override
    public void onResultRead(int systolic, int diastolic, int pulse) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //mSystolicValueText.setText(String.format(Locale.getDefault(), "%d", systolic));
                //mDiastolicValueText.setText(String.format(Locale.getDefault(), "%d", diastolic));
                //mPulseValueText.setText(String.format(Locale.getDefault(), "%d", pulse));
                mMeasurement = BloodPressureMeasurement.get(systolic, diastolic, pulse, Calendar.getInstance().getTime());
                setupSaveView();
            }
        });

    }
}
