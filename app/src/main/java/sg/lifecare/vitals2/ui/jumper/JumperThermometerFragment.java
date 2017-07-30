package sg.lifecare.vitals2.ui.jumper;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.ParcelUuid;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import no.nordicsemi.android.support.v18.scanner.ScanFilter;
import no.nordicsemi.android.support.v18.scanner.ScanResult;
import sg.lifecare.ble.device.jumper.JumperManager;
import sg.lifecare.ble.parser.BodyTemperatureMeasurement;
import sg.lifecare.ble.utility.BleUtils;
import sg.lifecare.utils.DateUtils;
import sg.lifecare.vitals2.R;
import sg.lifecare.vitals2.ui.base.BaseFragment;
import sg.lifecare.vitals2.ui.bodytemperature.BodyTemperatureActivity;
import sg.lifecare.vitals2.ui.device.ble.jumper.JumperThermometerMvpPresenter;
import sg.lifecare.vitals2.ui.device.ble.jumper.JumperThermometerMvpView;
import sg.lifecare.vitals2.ui.device.scanner.BleScannerMvpView;
import sg.lifecare.vitals2.ui.device.scanner.BleScannerPresenter;
import timber.log.Timber;

public class JumperThermometerFragment extends BaseFragment
        implements BleScannerMvpView, JumperThermometerMvpView {

    private static final int PERMISSION_REQ_LOCATION = 1;

    private static final int REQUEST_ENABLE_BT = 1;

    @Inject
    BleScannerPresenter<BleScannerMvpView> mBleScannerPresenter;

    @Inject
    JumperThermometerMvpPresenter<JumperThermometerMvpView> mJumperPresenter;

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

    @BindView(R.id.body_temperature_label)
    TextView mLabel;

    @BindView(R.id.save_button)
    Button mSaveButton;

    @BindView(R.id.connect_button)
    Button mConnectButton;

    @BindView(R.id.stop_button)
    Button mStopButton;

    @BindView(R.id.notes_layout)
    TextInputLayout mNotesLayout;

    private BodyTemperatureMeasurement mMeasurement = null;

    private BluetoothDevice mDevice;

    public static JumperThermometerFragment newInstance() {
        return new JumperThermometerFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_body_temperature_device, container, false);

        Timber.d("onCreateView");

        getActivityComponent().inject(this);
        setUnbinder(ButterKnife.bind(this, view));

        mBleScannerPresenter.onAttach(this);

        mJumperPresenter.onAttach(this);
        mJumperPresenter.init(getContext());

        setupViews(view);

        return view;
    }

    @Override
    public void onDestroyView() {

        mBleScannerPresenter.stopScan();
        mBleScannerPresenter.onDetach();

        mJumperPresenter.uninit();
        mJumperPresenter.onDetach();

        super.onDestroyView();
    }

    @Override
    protected void setupViews(View view) {
        setupConnectView();
    }


    @OnClick(R.id.connect_button)
    public void onConnectClick() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQ_LOCATION);
        } else {
            if (BleUtils.isBluetoothEnabled(getContext())) {
                setupScanView();

                List<ScanFilter> scanFilters = new ArrayList<>();
                scanFilters.add(new ScanFilter.Builder().setServiceUuid(new ParcelUuid(
                        JumperManager.PROPRIETARY_SERVICE)).build());
                //scanFilters.add(new ScanFilter.Builder().setDeviceName("My Thermometer").build());

                mDevice = null;
                mBleScannerPresenter.startScan(scanFilters, 0);
            } else {
                final Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            }
        }
    }

    @OnClick(R.id.stop_button)
    public void onStopClick() {
        setupConnectView();

        mBleScannerPresenter.stopScan();
        mJumperPresenter.disconnect();
    }


    @OnClick(R.id.save_button)
    public void onSaveClick() {

        Intent intent = new Intent();

        if (mMeasurement != null) {
            intent.putExtra(BodyTemperatureActivity.PARAM_DATA, mMeasurement);
            intent.putExtra(BodyTemperatureActivity.PARAM_DEVICE_ID, mDevice.getAddress());
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

    private void setupScanView() {
        mSaveButton.setVisibility(View.GONE);
        mConnectButton.setVisibility(View.GONE);
        mStopButton.setVisibility(View.VISIBLE);

        mProgressBar.setVisibility(View.VISIBLE);
        mProgressText.setText(R.string.device_msg_scanning);

        setContentVisibility(View.INVISIBLE);
    }

    private void setupConnectView() {
        mSaveButton.setVisibility(View.GONE);
        mConnectButton.setVisibility(View.VISIBLE);
        mStopButton.setVisibility(View.GONE);

        mProgressBar.setVisibility(View.INVISIBLE);
        mProgressText.setText(R.string.device_msg_start_connect);

        setContentVisibility(View.INVISIBLE);
    }

    private void setupSaveView() {
        mSaveButton.setVisibility(View.VISIBLE);
        mConnectButton.setVisibility(View.GONE);
        mStopButton.setVisibility(View.GONE);

        mProgressBar.setVisibility(View.INVISIBLE);
        mProgressText.setVisibility(View.INVISIBLE);


        DateTime dateTime = new DateTime(mMeasurement.getTimestamp());
        mTimestampText.setText(DateUtils.FULL_DATETIME_FORMAT.print(dateTime));

        mValueText.setText(new DecimalFormat("##.#").format(mMeasurement.getTemperature()));

        setContentVisibility(View.VISIBLE);

    }

    private void setContentVisibility(int visibility) {
        mTimestampCardView.setVisibility(visibility);
        mValueText.setVisibility(visibility);
        mLabel.setVisibility(visibility);
        mNotesLayout.setVisibility(visibility);
    }

    @Override
    public void showScanLoading() {

    }

    @Override
    public void hideScanLoading() {

    }

    @Override
    public void bleScanResult(int callbackType, ScanResult result) {
        if (mDevice == null) {
            mDevice = result.getDevice();

            mBleScannerPresenter.stopScan();
            mJumperPresenter.connect(mDevice);
        }
    }

    @Override
    public void bleBatchScanResults(List<ScanResult> results) {
        /*if (results.size() > 0) {
            if (mDevice == null) {
                mDevice = results.get(0).getDevice();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mBleScannerPresenter.stopScan();
                        mJumperPresenter.connect(mDevice);
                    }
                }, 500);
            }
        }*/
    }

    @Override
    public void onNoDeviceFound() {
        setupConnectView();
    }


    @Override
    public void onTemperatureRead(double temperature) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMeasurement = BodyTemperatureMeasurement.get((float) temperature,
                        Calendar.getInstance().getTime());
                setupSaveView();

            }
        });
    }
}
