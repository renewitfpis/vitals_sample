package sg.lifecare.vitals2.ui.dashboard.patient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.RealmChangeListener;
import sg.lifecare.ble.parser.BloodPressureMeasurement;
import sg.lifecare.data.local.database.BloodPressure;
import sg.lifecare.vitals2.R;
import sg.lifecare.vitals2.R2;
import sg.lifecare.vitals2.ui.base.BaseFragment;
import sg.lifecare.vitals2.ui.bloodpressure.BloodPressureActivity;
import timber.log.Timber;

public class PatientMainFragment extends BaseFragment implements PatientMainMvpView,
        RealmChangeListener<BloodPressure> {

    private static final String PARAM_PATIENT_ID = "patient_id";
    private static final String PARAM_NURSE_ID = "nurse_id";

    private static final int REQ_BP_DATA = 1;

    @Inject
    PatientMainMvpPresenter<PatientMainMvpView> mPresenter;

    @BindView(R2.id.name_text)
    TextView mNameText;

    @BindView(R2.id.bp_value_layout)
    View mBpValueLayout;

    @BindView(R2.id.bp_value_text)
    TextView mBpValueText;

    private String mPatientId = "";
    private String mNurseId = "";
    private BloodPressure mBloodPressure;

    public static PatientMainFragment newInstance(String nurseId, String patientId) {
        Bundle data = new Bundle();
        data.putString(PARAM_NURSE_ID, nurseId);
        data.putString(PARAM_PATIENT_ID, patientId);

        PatientMainFragment fragment = new PatientMainFragment();
        fragment.setArguments(data);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle data = getArguments();
        mNurseId = data.getString(PARAM_NURSE_ID);
        mPatientId = data.getString(PARAM_PATIENT_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_patient_main, container, false);

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
        mNameText.setText(mPatientId);


    }

    @Override
    public void onStart() {
        super.onStart();

        mBloodPressure = mPresenter.getLatestBloodPressure(mPatientId);

        if (mBloodPressure != null) {
            mBloodPressure.addChangeListener(this);
            updateBloodPressureView(mBloodPressure.getSystolic(), mBloodPressure.getDiastolic());
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mBloodPressure != null) {
            mBloodPressure.removeAllChangeListeners();
            mBloodPressure = null;
        }
    }

    private void updateBloodPressureView(int systolic, int diastolic) {
        mBpValueLayout.setVisibility(View.VISIBLE);
        mBpValueText.setText(String.format(Locale.getDefault(), "%d/%d", systolic, diastolic));
    }

    @OnClick(R2.id.measure_button)
    public void onMeasureClick() {
        showBloodPressureDeviceFragment();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Timber.d("onActivityResult");

        if (requestCode == REQ_BP_DATA) {
            if (resultCode == Activity.RESULT_OK) {
                BloodPressureMeasurement bp = BloodPressureActivity.getData(data);
                Timber.d(bp.toString());

                mPresenter.postBloodPressureData(bp, mNurseId, mPatientId);

                updateBloodPressureView((int)bp.getSystolic(), (int)bp.getDiastolic());
            }

        }

    }

    public void showBloodPressureDeviceFragment() {
        startActivityForResult(
                BloodPressureActivity.getStartIntent(getContext(),
                        BloodPressureActivity.TYPE_DEVICE, BloodPressureActivity.DEVICE_URION),
                REQ_BP_DATA);
    }

    @Override
    public void onChange(BloodPressure bloodPressure) {
        updateBloodPressureView(bloodPressure.getSystolic(), bloodPressure.getDiastolic());
    }
}
