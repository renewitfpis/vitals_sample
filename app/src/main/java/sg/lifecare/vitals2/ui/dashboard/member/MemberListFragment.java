package sg.lifecare.vitals2.ui.dashboard.member;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import sg.lifecare.data.remote.model.response.AssistsedEntityResponse;
import sg.lifecare.vitals2.R;
import sg.lifecare.vitals2.ui.base.BaseFragment;
import timber.log.Timber;

public class MemberListFragment extends BaseFragment implements MemberListMvpView, MemberListAdapter.OnItemClickListener {

    public interface MemberListFragmentListener {
        void showMemberVitalFragment(int position);
    }

    @Inject
    MemberListMvpPresenter<MemberListMvpView> mPresenter;

    @BindView(R.id.list)
    RecyclerView mListView;

    @BindView(R.id.message)
    TextView mMessageText;

    private MemberListFragmentListener mCallback;

    private MemberListAdapter mAdapter;

    public static MemberListFragment newInstance() {
        MemberListFragment fragment = new MemberListFragment();
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof MemberListFragmentListener) {
            mCallback = (MemberListFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement MemberListFragmentListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_member_list, container, false);

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
    public void onResume() {
        super.onResume();

        Timber.d("onResume");
    }

    @Override
    protected void setupViews(View view) {
        mMessageText.setVisibility(View.INVISIBLE);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mListView.getContext(),
                layoutManager.getOrientation());
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.list_divider));

        mListView.setHasFixedSize(true);
        mListView.setLayoutManager(new LinearLayoutManager(getContext()));
        mListView.addItemDecoration(dividerItemDecoration);

        mAdapter = new MemberListAdapter(this);
        mAdapter.replaceMembers(mPresenter.getMembers());
        mListView.setAdapter(mAdapter);

        if (mAdapter.getItemCount() == 0) {
            mMessageText.setVisibility(View.VISIBLE);
        } else {
            mMessageText.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onItemClick(int position) {
        mCallback.showMemberVitalFragment(position);
    }
}
