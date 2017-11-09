package sg.lifecare.vitals2.ui.dashboard.nurse;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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
import sg.lifecare.vitals2.BuildConfig;
import sg.lifecare.vitals2.R;
import sg.lifecare.vitals2.R2;
import sg.lifecare.vitals2.ui.BarcodeBottomSheetFragment;
import sg.lifecare.vitals2.ui.base.BaseFragment;
import timber.log.Timber;

public class NurseScanFragment extends BaseFragment implements NurseScanMvpView {

    private static final int REQ_BARCODE = 1;

    private static final int PERMISSION_REQ_CAMERA = 1;

    public interface NurseScanListener {
        void showNurseMainFragment(String nurseId);
    }

    @Inject
    NurseScanMvpPresenter<NurseScanMvpView> mPresenter;

    @BindView(R.id.list)
    RecyclerView mRecyclerView;

    private NurseListAdapter mNurseListAdapter;

    private NurseScanListener mCallback;

    public static NurseScanFragment newInstance() {
        NurseScanFragment fragment = new NurseScanFragment();
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);


        if (context instanceof NurseScanListener) {
            mCallback = (NurseScanListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement NurseScenListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nurse_scan, container, false);

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

        mNurseListAdapter = new NurseListAdapter();
        mNurseListAdapter.replaceNurses(mPresenter.getNurses());
        mRecyclerView.setAdapter(mNurseListAdapter);
    }

    @OnClick(R2.id.scan_id_button)
    public void scanIdButton() {

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[] { Manifest.permission.CAMERA},
                    PERMISSION_REQ_CAMERA);

        } else {
            showBarcodeFragment();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
            @NonNull int[] grantResults) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQ_BARCODE) {
            if (BuildConfig.DEBUG) {
                mCallback.showNurseMainFragment("S82797351");
            } else {
                mCallback.showNurseMainFragment(BarcodeBottomSheetFragment.getBarcode(data));
            }
        }
    }

    private void showBarcodeFragment() {
        BarcodeBottomSheetFragment fragment =
                BarcodeBottomSheetFragment.newInstance();
        fragment.setTargetFragment(this, REQ_BARCODE);
        fragment.show(getActivity().getSupportFragmentManager(), BarcodeBottomSheetFragment.TAG);
    }

}
