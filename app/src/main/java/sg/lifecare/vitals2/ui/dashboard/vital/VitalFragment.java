package sg.lifecare.vitals2.ui.dashboard.vital;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import sg.lifecare.ble.parser.BloodPressureMeasurement;
import sg.lifecare.ble.parser.BodyTemperatureMeasurement;
import sg.lifecare.ble.parser.BodyWeightMeasurement;
import sg.lifecare.vitals2.R;
import sg.lifecare.vitals2.ui.base.BaseFragment;
import sg.lifecare.vitals2.ui.bloodpressure.BloodPressureActivity;
import sg.lifecare.vitals2.ui.bodytemperature.BodyTemperatureActivity;
import sg.lifecare.vitals2.ui.bodyweight.BodyWeightActivity;
import sg.lifecare.vitals2.ui.dashboard.vital.view.BloodPressureView;
import sg.lifecare.vitals2.ui.dashboard.vital.view.BodyTemperatureView;
import sg.lifecare.vitals2.ui.dashboard.vital.view.BodyWeightView;
import sg.lifecare.vitals2.ui.dashboard.vital.view.VitalView;
import sg.lifecare.vitals2.ui.dashboard.vital.view.VitalViewListener;
import timber.log.Timber;

public class VitalFragment extends BaseFragment implements VitalMvpView, VitalViewListener {

    private static final int REQ_BP_DATA = 1;
    private static final int REQ_BW_DATA = 2;
    private static final int REQ_BT_DATA = 3;

    @Inject
    VitalMvpPresenter<VitalMvpView> mPresenter;

    @BindView(R.id.gridview)
    GridView mGridView;

    private ArrayList<VitalView> mData;
    private VitalAdapter mAdapter;


    public static VitalFragment newInstance() {
        return new VitalFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vital, container, false);

        Timber.d("onCreateView");

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

        mData = new ArrayList<>();
        mData.add(new BloodPressureView());
        mData.add(new BodyWeightView());
        mData.add(new BodyTemperatureView());

        mAdapter = new VitalAdapter(mData, mPresenter.getDatabase(), mPresenter.getPatientId(), this);
        mGridView.setAdapter(mAdapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Timber.d("onActivityResult");

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQ_BP_DATA) {
                BloodPressureMeasurement bp = BloodPressureActivity.getData(data);

                mPresenter.postBloodPressureData(bp, mPresenter.getPatientId(),
                        mPresenter.getPatientId());

            } else if (requestCode == REQ_BW_DATA) {
                BodyWeightMeasurement bw = BodyWeightActivity.getData(data);

                mPresenter.postBodyWeightData(bw, mPresenter.getPatientId(),
                        mPresenter.getPatientId());
            } else if (requestCode == REQ_BT_DATA) {
                BodyTemperatureMeasurement bt = BodyTemperatureActivity.getData(data);

                mPresenter.postBodyTemperatureData(bt, mPresenter.getPatientId(),
                        mPresenter.getPatientId());
            }

            mAdapter.notifyDataSetChanged();
        }

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
        }
    }
}
