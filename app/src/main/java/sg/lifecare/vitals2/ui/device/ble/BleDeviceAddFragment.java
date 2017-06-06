package sg.lifecare.vitals2.ui.device.ble;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import no.nordicsemi.android.support.v18.scanner.ScanResult;
import sg.lifecare.ble.parser.BloodPressureMeasurement;
import sg.lifecare.ble.parser.WeightMeasurement;
import sg.lifecare.data.local.DeviceData;
import sg.lifecare.vitals2.R;
import sg.lifecare.vitals2.R2;
import sg.lifecare.vitals2.ui.base.BaseDialogFragment;
import sg.lifecare.vitals2.ui.device.ble.and.ANDUA651MvpView;
import sg.lifecare.vitals2.ui.device.ble.and.ANDUA651Presenter;
import sg.lifecare.vitals2.ui.device.ble.and.ANDUC352MvpView;
import sg.lifecare.vitals2.ui.device.ble.and.ANDUC352Presenter;
import sg.lifecare.vitals2.ui.device.scanner.BleScannerMvpPresenter;
import sg.lifecare.vitals2.ui.device.scanner.BleScannerMvpView;
import timber.log.Timber;

public class BleDeviceAddFragment extends BaseDialogFragment
        implements BleScannerMvpView, ANDUC352MvpView, ANDUA651MvpView {

    private static final String PARAM_DEVICE_TYPE = "type";

    @Inject
    BleScannerMvpPresenter<BleScannerMvpView> mBleScannerPresenter;

    @Inject
    ANDUC352Presenter<ANDUC352MvpView> mANDUC352Presenter;

    @Inject
    ANDUA651Presenter<ANDUA651MvpView> mANDUA651Presenter;

    @BindView(R2.id.device_name_text)
    TextView mDeviceName;

    @BindView(R2.id.search_progressbar)
    ProgressBar mProgressBar;

    @BindView(R2.id.list)
    RecyclerView mRecyclerView;

    @BindView(R2.id.message_text)
    TextView mMessageText;

    @BindView(R2.id.refresh_button)
    Button mRefreshButton;

    @BindView(R.id.cancel_button)
    Button mCancelButton;

    private int mDeviceType;
    private BleDeviceAdapter mAdapter;

    public static BleDeviceAddFragment newInstance(int deviceType) {
        Bundle data = new Bundle();
        data.putInt(PARAM_DEVICE_TYPE, deviceType);

        BleDeviceAddFragment fragment = new BleDeviceAddFragment();
        fragment.setArguments(data);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle data = getArguments();
        mDeviceType = data.getInt(PARAM_DEVICE_TYPE);

        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AppDialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ble_device_add, container, false);

        getActivityComponent().inject(this);
        setUnbinder(ButterKnife.bind(this, view));

        mBleScannerPresenter.onAttach(this);

        mANDUC352Presenter.onAttach(this);
        mANDUC352Presenter.init(getContext());

        mANDUA651Presenter.onAttach(this);
        mANDUA651Presenter.init(getContext());

        setupViews(view);

        return view;
    }

    @Override
    protected void setupViews(View view) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                layoutManager.getOrientation());
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.list_divider));

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        mAdapter = new BleDeviceAdapter();
        mRecyclerView.setAdapter(mAdapter);

        mDeviceName.setText(DeviceData.getDeviceNameByType(mDeviceType));

        mRefreshButton.setVisibility(View.GONE);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        startScanDevice();

    }

    private void startScanDevice() {
        if (mDeviceType == DeviceData.DEVICE_AND_UC352) {
            mBleScannerPresenter.scanForANDUC352();
        } else if (mDeviceType == DeviceData.DEVICE_AND_UA651) {
            mBleScannerPresenter.scanForANDUA651();
        }
    }

    @Override
    public void onDestroyView() {
        mBleScannerPresenter.stopScan();
        mANDUC352Presenter.uninit();
        mANDUA651Presenter.uninit();

        mBleScannerPresenter.onDetach();
        mANDUC352Presenter.onDetach();
        mANDUA651Presenter.onDetach();

        super.onDestroyView();

        Timber.d("onDestroyView");
    }

    @Override
    public void showScanLoading() {
        mProgressBar.setVisibility(View.VISIBLE);
        mMessageText.setText(R.string.device_msg_scanning);
    }

    @Override
    public void hideScanLoading() {
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void bleBatchScanResults(List<ScanResult> results) {
        if (results.size() > 0) {
            mBleScannerPresenter.stopScan();
            mAdapter.replaceResult(results.get(0));

            if (mDeviceType == DeviceData.DEVICE_AND_UC352) {
                mMessageText.setText("");
                mANDUC352Presenter.registerDevice(results.get(0).getDevice());
            } else if (mDeviceType == DeviceData.DEVICE_AND_UA651) {
                mMessageText.setText("");
                mANDUA651Presenter.registerDevice(results.get(0).getDevice());
            }
        }
    }

    @Override
    public void onNoDeviceFound() {
        mMessageText.setText(R.string.device_msg_no_device);
        mRefreshButton.setVisibility(View.VISIBLE);
    }

    @OnClick(R2.id.cancel_button)
    public void onCancelClick() {
        getDialog().dismiss();
    }

    @OnClick(R2.id.refresh_button)
    public void onRefreshClick() {
        mRefreshButton.setVisibility(View.GONE);
        startScanDevice();
    }

    @Override
    public void onPairingSuccess() {
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, null);
        getDialog().dismiss();
    }

    @Override
    public void onPairingFail() {
        getActivity().runOnUiThread(() -> {
            mAdapter.clearResults();
            mMessageText.setText(R.string.device_msg_no_device);
            mRefreshButton.setVisibility(View.VISIBLE);
        });

    }

    @Override
    public void onWeightMeasurementRead(WeightMeasurement weight) {

    }

    @Override
    public void onReadCompleted() {

    }

    @Override
    public void onBloodPressureMeasurementRead(BloodPressureMeasurement bloodPressure) {

    }
}
