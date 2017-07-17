package sg.lifecare.vitals2.ui.dashboard.nurse;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import sg.lifecare.data.local.database.Patient;
import sg.lifecare.vitals2.R;
import sg.lifecare.vitals2.R2;
import sg.lifecare.vitals2.ui.BarcodeBottomSheetFragment;
import sg.lifecare.vitals2.ui.base.BaseFragment;
import timber.log.Timber;

public class NurseMainFragment extends BaseFragment implements NurseMainMvpView,
        RealmChangeListener<RealmResults<Patient>> {

    private static final String PARAM_NURSE_ID = "nurse_id";
    private static final int REQ_BARCODE = 1;


    public interface NurseMainListener {
        void showPatientMainFragment(String nurseId, String patientId);
    }

    @Inject
    NurseMainMvpPresenter<NurseMainMvpView> mPresenter;

    @BindView(R.id.list)
    RecyclerView mRecyclerView;

    private NurseMainFragment.NurseMainListener mCallback;

    private RealmResults<Patient> mPatients;
    private PatientListAdapter mPatientListAdapter;
    private String mNurseId;

    public static NurseMainFragment newInstance(String nurseId) {
        Bundle data = new Bundle();
        data.putString(PARAM_NURSE_ID, nurseId);

        NurseMainFragment fragment = new NurseMainFragment();
        fragment.setArguments(data);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle data = getArguments();
        mNurseId = data.getString(PARAM_NURSE_ID);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);


        if (context instanceof NurseMainListener) {
            mCallback = (NurseMainListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement NurseMainListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nurse_main, container, false);

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
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                layoutManager.getOrientation());
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.list_divider));

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        mPatientListAdapter = new PatientListAdapter();

        mRecyclerView.setAdapter(mPatientListAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        mPatients = mPresenter.getPatients();
        mPatients.addChangeListener(this);
        mPatientListAdapter.replacePatients(mPatients);
    }

    @Override
    public void onStop() {
        super.onStop();
        mPatients.addChangeListener(this);
        mPatients = null;
    }

    @OnClick(R2.id.scan_id_button)
    public void scanIdButton() {
        showBarcodeFragment();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQ_BARCODE) {
            String code = BarcodeBottomSheetFragment.getBarcode(data);

            /*if (BuildConfig.DEBUG) {
                if (code.equals("4987350716903")) {
                    code = "S8279735A";
                }
            }*/
            mCallback.showPatientMainFragment(mNurseId, code);
        }
    }

    @Override
    public void onChange(RealmResults<Patient> patients) {
        Timber.d("onChange: patients");
        mPatientListAdapter.replacePatients(patients);
    }

    private void showBarcodeFragment() {
        BarcodeBottomSheetFragment fragment =
                BarcodeBottomSheetFragment.newInstance();
        fragment.setTargetFragment(this, REQ_BARCODE);
        fragment.show(getActivity().getSupportFragmentManager(), BarcodeBottomSheetFragment.TAG);
    }
}
