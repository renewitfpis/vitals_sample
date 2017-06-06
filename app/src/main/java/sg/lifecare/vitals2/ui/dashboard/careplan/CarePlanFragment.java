package sg.lifecare.vitals2.ui.dashboard.careplan;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import sg.lifecare.data.remote.model.response.AssignedTaskResponse;
import sg.lifecare.vitals2.R;
import sg.lifecare.vitals2.R2;
import sg.lifecare.vitals2.ui.base.BaseFragment;
import timber.log.Timber;

public class CarePlanFragment extends BaseFragment
        implements CarePlanMvpView, CarePlanAdapter.OnItemClickListener {

    public interface CarePlanTaskListener {
        void showBloodGlucoseManualFragment();
        void showBodyWeightDeviceFragment();
        void showBloodPressureDeviceFragment();
        void showBloodPressureManualFragment();
    }

    private CarePlanTaskListener mCallback;

    @Inject
    CarePlanMvpPresenter<CarePlanMvpView> mPresenter;

    @BindView(R2.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R2.id.recycler_view)
    RecyclerView mRecycleView;

    private CarePlanAdapter mAdapter;

    public static CarePlanFragment newInstance() {
        return new CarePlanFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof CarePlanTaskListener) {
            mCallback = (CarePlanTaskListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement CarePlanTaskListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_careplan, container, false);

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
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            Timber.d("refresh");
            mPresenter.getUserAssignedTasks();
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecycleView.setLayoutManager(linearLayoutManager);
        //mRecycleView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        mAdapter = new CarePlanAdapter(this);
        mRecycleView.setAdapter(mAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();

        mPresenter.getUserAssignedTasks();
    }

    @Override
    public void showLoading() {
        mSwipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void hideLoading() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onAssignedTasksResult(AssignedTaskResponse response) {
        mAdapter.replaceAll(response.getData());
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AssignedTaskResponse.Data task) {
        Timber.d("onItemClick: %s", task.isBloodGlucose());
        if (task.isBloodGlucose()) {
            // test
            //mPresenter.postBloodGlucoseTask(task);
            mCallback.showBloodGlucoseManualFragment();
        } else if (task.isBloodPressure()) {
            //mCallback.showBloodPressureManualFragment();
            mCallback.showBloodPressureDeviceFragment();
        } else if (task.isBodyWeight()) {
            //mPresenter.postBodyWeightTask(task);
            mCallback.showBodyWeightDeviceFragment();
        } else if (task.isSpo2()) {
            //mPresenter.postSpO2Task(task);
        }
    }


}
