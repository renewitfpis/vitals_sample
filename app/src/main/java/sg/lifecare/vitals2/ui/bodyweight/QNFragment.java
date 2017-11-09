package sg.lifecare.vitals2.ui.bodyweight;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kitnew.ble.QNBleCallback;
import com.kitnew.ble.QNBleDevice;
import com.kitnew.ble.QNBleScanCallback;
import com.kitnew.ble.QNData;
import com.kitnew.ble.QNItemData;

import org.joda.time.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import sg.lifecare.ble.parser.BodyWeightMeasurement;
import sg.lifecare.ble.utility.BleUtils;
import sg.lifecare.utils.DateUtils;
import sg.lifecare.vitals2.R;
import sg.lifecare.vitals2.ui.base.BaseFragment;
import timber.log.Timber;

public class QNFragment extends BaseFragment implements QNMvpView, QNBleScanCallback,
        QNBleCallback {

    private static final int PERMISSION_REQ_LOCATION = 1;

    private static final int REQUEST_ENABLE_BT = 1;

    @Inject
    QNMvpPresenter<QNMvpView> mPresenter;

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

    @BindView(R.id.extra_layout)
    View mExtraLayout;

    @BindView(R.id.bmi_text)
    TextView mBmiText;

    @BindView(R.id.body_fat_text)
    TextView mBodyFat;

    @BindView(R.id.body_water_text)
    TextView mBodyWaterText;

    @BindView(R.id.bmr_text)
    TextView mBmrText;

    @BindView(R.id.sub_fat_text)
    TextView mSubFatText;

    @BindView(R.id.visceral_fat_text)
    TextView mVisceralFat;

    @BindView(R.id.skeletal_muscle_text)
    TextView mSkeletalMuscleText;

    @BindView(R.id.bone_mass_text)
    TextView mBoneMassText;

    @BindView(R.id.protein_text)
    TextView mProteinText;

    private BodyWeightMeasurement mMeasurement = null;

    private QNBleDevice mQNBleDevice;

    public static QNFragment newInstance() {
        return new QNFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_body_weight_qn, container, false);

        getActivityComponent().inject(this);
        setUnbinder(ButterKnife.bind(this, view));

        mPresenter.onAttach(this);

        setupViews(view);

        return view;
    }

    @Override
    public void onDestroyView() {
        mPresenter.onDetach();
        super.onDestroyView();
    }

    @Override
    protected void setupViews(View view) {

        setupConnectView();
    }

    @Override
    public void onStart() {
        super.onStart();

        mPresenter.init(getContext());

    }

    @Override
    public void onStop() {
        super.onStop();

        mPresenter.uninit();
    }

    @OnClick(R.id.cancel_button)
    public void onCancelClick() {
        getActivity().finish();
    }

    @OnClick(R.id.connect_button)
    public void onConnectClick() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQ_LOCATION);
        } else {
            if (BleUtils.isBluetoothEnabled(getContext())) {
                setupScanView();
                mPresenter.startScan(this);
            } else {
                final Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            }
        }
    }

    @OnClick(R.id.stop_button)
    public void onStopClick() {
        setupConnectView();
        mPresenter.stopScan();
    }

    @OnClick(R.id.save_button)
    public void onSaveClick() {

        Intent intent = new Intent();

        if (mMeasurement != null) {
            intent.putExtra(BodyWeightActivity.PARAM_DATA, mMeasurement);
            intent.putExtra(BodyWeightActivity.PARAM_DEVICE_ID, mQNBleDevice.getMac());
            getActivity().setResult(Activity.RESULT_OK, intent);
        } else {
            getActivity().setResult(Activity.RESULT_CANCELED);
        }

        getActivity().finish();

    }

    private void setContentVisibility(int visibility) {
        mTimestampCardView.setVisibility(visibility);
        mValueText.setVisibility(visibility);
        mLabel.setVisibility(visibility);
        mNotesLayout.setVisibility(visibility);

        mExtraLayout.setVisibility(visibility);
    }

    private void setupScanView() {
        mSaveButton.setVisibility(View.GONE);
        mConnectButton.setVisibility(View.GONE);
        mStopButton.setVisibility(View.VISIBLE);

        mProgressBar.setVisibility(View.VISIBLE);
        mProgressText.setText(R.string.device_msg_scanning);
    }

    private void setupConnectView() {
        mSaveButton.setVisibility(View.GONE);
        mConnectButton.setVisibility(View.VISIBLE);
        mStopButton.setVisibility(View.GONE);

        mProgressBar.setVisibility(View.INVISIBLE);
        mProgressText.setText(R.string.device_msg_start_connect);

        setContentVisibility(View.INVISIBLE);
    }

    private void setupTransferView() {
        mProgressBar.setVisibility(View.INVISIBLE);
        mProgressText.setVisibility(View.INVISIBLE);

        mValueText.setVisibility(View.VISIBLE);
        mLabel.setVisibility(View.VISIBLE);

        mValueText.setText("__");

    }

    private void setupSaveView() {
        mSaveButton.setVisibility(View.VISIBLE);
        mConnectButton.setVisibility(View.GONE);
        mStopButton.setVisibility(View.GONE);

        mProgressBar.setVisibility(View.INVISIBLE);
        mProgressText.setVisibility(View.INVISIBLE);

        //WeightMeasurement weight = mWeights.get(mWeights.size()-1);

        //mValueText.setText(String.format(Locale.getDefault(), "%.1f", weight.getWeight()));

        //DateTime dateTime = new DateTime(weight.getTimestamp());
        //mTimestampText.setText(DateUtils.FULL_DATETIME_FORMAT.print(dateTime));

        setContentVisibility(View.VISIBLE);
    }

    private void setupData(QNData data) {
        mMeasurement = BodyWeightMeasurement.get(data.getWeight(), data.getCreateTime());

        mValueText.setText(String.format(Locale.getDefault(), "%.2f", data.getWeight()));

        DateTime dateTime = new DateTime(data.getCreateTime());
        mTimestampText.setText(DateUtils.FULL_DATETIME_FORMAT.print(dateTime));

        mBmiText.setText(String.format(Locale.getDefault(), "%.1f", data.getFloatValue(QNData.TYPE_BMI)));
        mBodyFat.setText(String.format(Locale.getDefault(), "%.1f %%", data.getFloatValue(QNData.TYPE_BODYFAT)));
        mSubFatText.setText(String.format(Locale.getDefault(), "%.1f %%", data.getFloatValue(QNData.TYPE_SUBFAT)));
        mVisceralFat.setText(String.format(Locale.getDefault(), "%d", data.getIntValue(QNData.TYPE_VISFAT)));
        mBodyWaterText.setText(String.format(Locale.getDefault(), "%.1f %%", data.getFloatValue(QNData.TYPE_WATER)));
        mSkeletalMuscleText.setText(String.format(Locale.getDefault(), "%.1f %%", data.getFloatValue(QNData.TYPE_SKELETAL_MUSCLE)));
        mBoneMassText.setText(String.format(Locale.getDefault(), "%.1f kg", data.getFloatValue(QNData.TYPE_BONE)));
        mProteinText.setText(String.format(Locale.getDefault(), "%.1f %%", data.getFloatValue(QNData.TYPE_PROTEIN)));
        mBmrText.setText(String.format(Locale.getDefault(), "%d kcal", data.getIntValue(QNData.TYPE_BMR)));
    }

    @Override
    public void onScan(QNBleDevice qnBleDevice) {
        Timber.d("onScan: %s", qnBleDevice.toString());

        mPresenter.stopScan();

        mQNBleDevice = qnBleDevice;

        User user = new User();
        user.setId("1");
        user.setBirthday("1982-12-24");
        user.setHeight(172);
        user.setMale();

        mPresenter.connectDevice(mQNBleDevice, user, this);
    }

    @Override
    public void onCompete(int errorCode) {
        Timber.d("onComplete: %d", errorCode);
    }

    @Override
    public void onConnectStart(QNBleDevice qnBleDevice) {
        Timber.d("onConnectStart");

        mProgressText.setText(String.format(getContext().getResources().getString(R.string.qn_connecting_to),
                qnBleDevice.getDeviceName()));
    }

    @Override
    public void onConnected(QNBleDevice qnBleDevice) {
        Timber.d("onConnected");
        mProgressText.setText(String.format(getContext().getResources().getString(R.string.qn_connected_to),
                qnBleDevice.getDeviceName()));

        setupTransferView();
    }

    @Override
    public void onDisconnected(QNBleDevice qnBleDevice) {
        Timber.d("onDisconnected");
    }

    @Override
    public void onUnsteadyWeight(QNBleDevice qnBleDevice, float weight) {
        Timber.d("onUnsteadyWeight: %.2f", weight);

        mValueText.setText(String.format(Locale.getDefault(), "%.2f", weight));
    }

    @Override
    public void onReceivedData(QNBleDevice qnBleDevice, QNData qnData) {
        Timber.d("onReceivedData");

        List<QNItemData> items = qnData.getAll();

        for (QNItemData item : items) {
            Timber.d("name: %s, type=%d, value=%f, valueStr=%s", item.name, item.type,
                    item.value, item.valueStr);
        }

        setupData(qnData);
        setupSaveView();
    }

    @Override
    public void onReceivedStoreData(QNBleDevice qnBleDevice, List<QNData> list) {
        Timber.d("onReceivedStoreData");
    }

    @Override
    public void onDeviceModelUpdate(QNBleDevice qnBleDevice) {
        Timber.d("onDeviceModelUpdate");
    }

    class User {
        String id;
        Date birthday;
        int height; // in CM
        int gender = 0; // 1 - Male, 0, female


        void setId(String id) {
            this.id = id;
        }

        void setBirthday(String birthday) {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-M-d", Locale.getDefault());

            try {
                this.birthday = df.parse(birthday);
            } catch (ParseException e) {
                Timber.e(e);
            }
        }

        void setMale() {
            this.gender = 1;
        }

        void setFemale() {
            this.gender = 0;
        }

        void setHeight(int height) {
            this.height = height;
        }
    }
}
