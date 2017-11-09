package sg.lifecare.vitals2.ui.jumper;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.ParcelUuid;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import no.nordicsemi.android.support.v18.scanner.ScanFilter;
import no.nordicsemi.android.support.v18.scanner.ScanResult;
import sg.lifecare.ble.device.jumper.JumperManager;
import sg.lifecare.ble.parser.Spo2Measurement;
import sg.lifecare.ble.utility.BleUtils;
import sg.lifecare.utils.DateUtils;
import sg.lifecare.vitals2.R;
import sg.lifecare.vitals2.ui.base.BaseFragment;
import sg.lifecare.vitals2.ui.device.ble.jumper.JumperOximeterMvpPresenter;
import sg.lifecare.vitals2.ui.device.ble.jumper.JumperOximeterMvpView;
import sg.lifecare.vitals2.ui.device.scanner.BleScannerMvpView;
import sg.lifecare.vitals2.ui.device.scanner.BleScannerPresenter;
import sg.lifecare.vitals2.ui.spo2.Spo2Activity;
import timber.log.Timber;

public class JumperOximeterFragment extends BaseFragment
        implements BleScannerMvpView, JumperOximeterMvpView {

    private static final int PERMISSION_REQ_LOCATION = 1;

    private static final int REQUEST_ENABLE_BT = 1;

    @Inject
    BleScannerPresenter<BleScannerMvpView> mBleScannerPresenter;

    @Inject
    JumperOximeterMvpPresenter<JumperOximeterMvpView> mJumperPresenter;

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

    @BindView(R.id.spo2_value_text)
    TextView mSpo2ValueText;

    @BindView(R.id.pi_value_text)
    TextView mPiValueText;

    @BindView(R.id.pulse_value_text)
    TextView mPulseValueText;

    @BindView(R.id.spo2_label)
    TextView mSpo2Label;

    @BindView(R.id.pi_label)
    TextView mPiLabel;

    @BindView(R.id.pulse_label)
    TextView mPulseLabel;

    @BindView(R.id.save_button)
    Button mSaveButton;

    @BindView(R.id.connect_button)
    Button mConnectButton;

    @BindView(R.id.stop_button)
    Button mStopButton;

    @BindView(R.id.confirm_button)
    Button mConfirmButton;

    @BindView(R.id.notes_layout)
    TextInputLayout mNotesLayout;

    private BluetoothDevice mDevice;
    private Spo2Measurement mMeasurement;

    public static JumperOximeterFragment newInstance() {
        return new JumperOximeterFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pulse_jumper, container, false);

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
                //scanFilters.add(new ScanFilter.Builder().setServiceUuid(new ParcelUuid(
                //        JumperManager.PROPRIETARY_SERVICE)).build());
                //scanFilters.add(new ScanFilter.Builder().setDeviceName("My Oximeter").build());

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
        mJumperPresenter.disconnect();
    }

    @OnClick(R.id.cancel_button)
    public void onCancelClick() {
        getActivity().finish();
    }

    @OnClick(R.id.confirm_button)
    public void onConfirmClick() {
        setupSaveView();

        mBleScannerPresenter.stopScan();
        mJumperPresenter.disconnect();
    }

    @OnClick(R.id.save_button)
    public void onSaveClick() {

        Intent intent = new Intent();

        if (mMeasurement != null) {
            intent.putExtra(Spo2Activity.PARAM_DATA, mMeasurement);
            intent.putExtra(Spo2Activity.PARAM_DEVICE_ID, mDevice.getAddress());
            getActivity().setResult(Activity.RESULT_OK, intent);
        } else {
            getActivity().setResult(Activity.RESULT_CANCELED);
        }

        getActivity().finish();

    }


    private void setupScanView() {
        mSaveButton.setVisibility(View.GONE);
        mConnectButton.setVisibility(View.GONE);
        mStopButton.setVisibility(View.VISIBLE);
        mConfirmButton.setVisibility(View.GONE);

        mProgressBar.setVisibility(View.VISIBLE);
        mProgressText.setText(R.string.device_msg_scanning);
    }

    private void setupConnectView(@StringRes int res) {
        mSaveButton.setVisibility(View.GONE);
        mConnectButton.setVisibility(View.VISIBLE);
        mStopButton.setVisibility(View.GONE);
        mConfirmButton.setVisibility(View.GONE);

        mProgressBar.setVisibility(View.INVISIBLE);
        mProgressText.setVisibility(View.VISIBLE);
        mProgressText.setText(res);

        setContentVisibility(View.INVISIBLE);
    }

    private void setupSaveView() {
        mSaveButton.setVisibility(View.VISIBLE);
        mConnectButton.setVisibility(View.GONE);
        mStopButton.setVisibility(View.GONE);
        mConfirmButton.setVisibility(View.GONE);

        mProgressBar.setVisibility(View.INVISIBLE);
        mProgressText.setVisibility(View.INVISIBLE);

        mSpo2ValueText.setText(String.format(Locale.getDefault(), "%d", mMeasurement.getSpo2()));
        mPulseValueText.setText(String.format(Locale.getDefault(), "%d", mMeasurement.getPulse()));
        mPiValueText.setText(new DecimalFormat("#.#").format(mMeasurement.getPi()));

        DateTime dateTime = new DateTime(mMeasurement.getTimestamp());
        mTimestampText.setText(DateUtils.FULL_DATETIME_FORMAT.print(dateTime));

        setContentVisibility(View.VISIBLE);
    }

    private void setTransferView(Spo2Measurement measurement) {

        if (mSpo2ValueText.getVisibility() == View.INVISIBLE) {
            mSpo2ValueText.setVisibility(View.VISIBLE);
            mPiValueText.setVisibility(View.VISIBLE);
            mPulseValueText.setVisibility(View.VISIBLE);
            mSpo2Label.setVisibility(View.VISIBLE);
            mPiLabel.setVisibility(View.VISIBLE);
            mPulseLabel.setVisibility(View.VISIBLE);
            mConfirmButton.setVisibility(View.VISIBLE);

            mProgressBar.setVisibility(View.INVISIBLE);
            mProgressText.setVisibility(View.INVISIBLE);
        }

        if ((measurement.getSpo2() > 0) && (measurement.getSpo2() != (byte)0x7f)) {
            mSpo2ValueText.setText(String.format(Locale.getDefault(), "%d", measurement.getSpo2()));
        }

        if ((measurement.getPulse() != 0xff)) {
            mPulseValueText.setText(String.format(Locale.getDefault(), "%d", measurement.getPulse()));
        }

        if (measurement.getPi() > 0.0d) {
            mPiValueText.setText(new DecimalFormat("#.#").format(measurement.getPi()));
        }

    }

    private void setContentVisibility(int visibility) {
        mTimestampCardView.setVisibility(visibility);
        mSpo2ValueText.setVisibility(visibility);
        mPiValueText.setVisibility(visibility);
        mPulseValueText.setVisibility(visibility);
        mSpo2Label.setVisibility(visibility);
        mPiLabel.setVisibility(visibility);
        mPulseLabel.setVisibility(visibility);
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
        Timber.d("bleScanResult");
        if ((mDevice == null) && (result.getDevice() != null)
                && "My Oximeter".equalsIgnoreCase(result.getDevice().getName())) {
            mDevice = result.getDevice();

            Timber.d("bleScanResult: deviceType=%d", mDevice.getType());

            mBleScannerPresenter.stopScan();
            mJumperPresenter.connect(mDevice);
        }
    }

    @Override
    public void bleBatchScanResults(List<ScanResult> results) {
        //if (results.size() > 0) {
        //    mBleScannerPresenter.stopScan();
        //    mJumperPresenter.connect(results.get(0).getDevice());
        //}
    }

    @Override
    public void onNoDeviceFound() {

    }


    @Override
    public void onSpo2PulsePiRead(int spo2, int pulse, double pi) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMeasurement = Spo2Measurement.get(spo2, pulse, Calendar.getInstance().getTime());
                mMeasurement.setPi(pi);
                setTransferView(mMeasurement);
            }
        });
    }
}
