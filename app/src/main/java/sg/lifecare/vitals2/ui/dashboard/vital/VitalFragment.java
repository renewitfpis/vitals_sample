package sg.lifecare.vitals2.ui.dashboard.vital;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import sg.lifecare.ble.parser.BloodGlucoseMeasurement;
import sg.lifecare.ble.parser.BloodPressureMeasurement;
import sg.lifecare.ble.parser.BodyTemperatureMeasurement;
import sg.lifecare.ble.parser.BodyWeightMeasurement;
import sg.lifecare.ble.parser.Spo2Measurement;
import sg.lifecare.data.local.database.BloodGlucose;
import sg.lifecare.data.remote.model.response.AssistsedEntityResponse;
import sg.lifecare.data.remote.model.response.EntityDetailResponse;
import sg.lifecare.vitals2.R;
import sg.lifecare.vitals2.ui.base.BaseFragment;
import sg.lifecare.vitals2.ui.bloodglucose.BloodGlucoseActivity;
import sg.lifecare.vitals2.ui.bloodpressure.BloodPressureActivity;
import sg.lifecare.vitals2.ui.bodytemperature.BodyTemperatureActivity;
import sg.lifecare.vitals2.ui.bodyweight.BodyWeightActivity;
import sg.lifecare.vitals2.ui.dashboard.DashboardActivity;
import sg.lifecare.vitals2.ui.dashboard.vital.view.BloodGlucoseView;
import sg.lifecare.vitals2.ui.dashboard.vital.view.BloodPressureView;
import sg.lifecare.vitals2.ui.dashboard.vital.view.BodyTemperatureView;
import sg.lifecare.vitals2.ui.dashboard.vital.view.BodyWeightView;
import sg.lifecare.vitals2.ui.dashboard.vital.view.Spo2View;
import sg.lifecare.vitals2.ui.dashboard.vital.view.VitalView;
import sg.lifecare.vitals2.ui.dashboard.vital.view.VitalViewListener;
import sg.lifecare.vitals2.ui.spo2.Spo2Activity;
import timber.log.Timber;

public class VitalFragment extends BaseFragment implements VitalMvpView, VitalViewListener {

    private static final int REQ_BP_DATA = 1;
    private static final int REQ_BW_DATA = 2;
    private static final int REQ_BT_DATA = 3;
    private static final int REQ_SPO2_DATA = 4;
    private static final int REQ_BG_DATA = 5;

    private static final int POSITION_BP = 0;
    private static final int POSITION_BW = 1;
    private static final int POSITION_BT = 2;
    private static final int POSITION_SPO2 = 3;
    private static final int POSITION_BG = 4;

    private static final String KEY_MEMBER_POSITION = "member_position";

    @Inject
    VitalMvpPresenter<VitalMvpView> mPresenter;

    @BindView(R.id.gridview)
    GridView mGridView;

    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout mRefreshLayout;

    private ArrayList<VitalView> mData;
    private VitalAdapter mAdapter;
    private AssistsedEntityResponse.Data mMember = null;
    private EntityDetailResponse.Data mUser;

    public static VitalFragment newInstance() {
        return new VitalFragment();
    }

    public static VitalFragment newInstance(int memberPosition) {

        Bundle data = new Bundle();
        data.putInt(KEY_MEMBER_POSITION, memberPosition);

        VitalFragment fragment = new VitalFragment();
        fragment.setArguments(data);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vital, container, false);

        Timber.d("onCreateView");

        getActivityComponent().inject(this);
        setUnbinder(ButterKnife.bind(this, view));

        mPresenter.onAttach(this);

        Bundle data = getArguments();
        if (data != null) {
            mMember = mPresenter.getMember(data.getInt(KEY_MEMBER_POSITION));
        }

        mUser = mPresenter.getUser();

        Timber.d("patientId: %s", getPatientId());

        setupViews(view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        Timber.d("onResume");

        if (mMember != null) {
            //getBaseActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            ActionBar actionBar = getBaseActivity().getSupportActionBar();
            if (actionBar != null) {
                String name = mMember.getName();

                actionBar.setTitle(name);
            }
        }
    }

    @Override
    public void onDestroyView() {

        mPresenter.onDetach();

        super.onDestroyView();
    }

    @Override
    protected void setupViews(View view) {

        mData = new ArrayList<>();
        mData.add(new BloodPressureView());
        mData.add(new BodyWeightView());
        mData.add(new BodyTemperatureView());
        mData.add(new Spo2View());
        mData.add(new BloodGlucoseView());

        mAdapter = new VitalAdapter(mData, mPresenter.getDatabase(), getPatientId(), this);

        mGridView.setAdapter(mAdapter);

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ((DashboardActivity) getActivity()).startSyncService(getPatientId());
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Timber.d("onActivityResult");

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQ_BP_DATA) {
                BloodPressureMeasurement bp = BloodPressureActivity.getData(data);
                String deviceId = BloodPressureActivity.getDeviceId(data);

                mPresenter.postBloodPressureData(bp, mUser.getId(), getPatientId(), deviceId);
                mAdapter.updateView(POSITION_BP);

            } else if (requestCode == REQ_BW_DATA) {
                BodyWeightMeasurement bw = BodyWeightActivity.getData(data);
                String deviceId = BodyWeightActivity.getDeviceId(data);

                mPresenter.postBodyWeightData(bw, mUser.getId(), getPatientId(), deviceId);
                mAdapter.updateView(POSITION_BW);
            } else if (requestCode == REQ_BT_DATA) {
                BodyTemperatureMeasurement bt = BodyTemperatureActivity.getData(data);
                String deviceId = BodyTemperatureActivity.getDeviceId(data);

                mPresenter.postBodyTemperatureData(bt, mUser.getId(), getPatientId(), deviceId);
                mAdapter.updateView(POSITION_BT);
            } else if (requestCode == REQ_SPO2_DATA) {
                Spo2Measurement spo2 = Spo2Activity.getData(data);
                String deviceId = Spo2Activity.getDeviceId(data);

                mPresenter.postSpo2Data(spo2, mUser.getId(), getPatientId(), deviceId);
                mAdapter.updateView(POSITION_SPO2);
             } else if (requestCode == REQ_BG_DATA) {
                BloodGlucoseMeasurement bg = BloodGlucoseActivity.getData(data);
                String deviceId = BloodGlucoseActivity.getDeviceId(data);

                mPresenter.postBloodGlucoseData(bg, mUser.getId(), getPatientId(), deviceId);
                mAdapter.updateView(POSITION_BG);
            }
        }
    }

    private void updateAllViews() {
        mAdapter.updateView(POSITION_BP);
        mAdapter.updateView(POSITION_BW);
        mAdapter.updateView(POSITION_BT);
        mAdapter.updateView(POSITION_SPO2);
        mAdapter.updateView(POSITION_BG);
    }

    @Override
    public void onMeasureClick(VitalView vitalView) {

        if (vitalView instanceof BloodPressureView) {
            startActivityForResult(
                    BloodPressureActivity.getStartIntent(getContext(),
                            BloodPressureActivity.TYPE_DEVICE,
                            BloodPressureActivity.DEVICE_URION),
                            REQ_BP_DATA);
        } else if (vitalView instanceof BodyWeightView) {
            startActivityForResult(
                    BodyWeightActivity.getStartIntent(getContext(),
                            BodyWeightActivity.TYPE_DEVICE,
                            BodyWeightActivity.DEVICE_QN),
                            REQ_BW_DATA);
        } else if (vitalView instanceof BodyTemperatureView) {
            startActivityForResult(
                    BodyTemperatureActivity.getStartIntent(getContext(),
                            BodyTemperatureActivity.TYPE_DEVICE,
                            BodyTemperatureActivity.DEVICE_JUMPER),
                            REQ_BT_DATA);
        } else if (vitalView instanceof Spo2View) {
            startActivityForResult(
                    Spo2Activity.getStartIntent(getContext(),
                            Spo2Activity.TYPE_DEVICE,
                            Spo2Activity.DEVICE_JUMPER),
                            REQ_SPO2_DATA);
        } else if (vitalView instanceof BloodGlucoseView) {
            startActivityForResult(
                    BloodGlucoseActivity.getStartIntent(getContext(),
                            BloodGlucoseActivity.TYPE_DEVICE,
                            BloodGlucoseActivity.DEVICE_INO_SMART),
                            REQ_BG_DATA);
        }
    }

    private String getPatientId() {
        return mMember == null ? mUser.getId() : mMember.getId();
    }

    public void onDataSyncing() {
        mRefreshLayout.setEnabled(false);
        mRefreshLayout.setRefreshing(true);
    }

    public void onDataSyncCompleted() {
        Timber.d("onDataSyncCompleted");
        mRefreshLayout.setEnabled(true);
        mRefreshLayout.setRefreshing(false);

        updateAllViews();
    }
}
