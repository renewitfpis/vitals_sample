package sg.lifecare.vitals2.ui.login;

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;

import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import sg.lifecare.vitals2.R;
import sg.lifecare.vitals2.ui.base.BaseFragment;

public class ForgotPasswordFragment extends BaseFragment implements ForgotPasswordMvpView {

    @BindView(R.id.title_text)
    TextView mTitleText;

    @BindView(R.id.email_edittext)
    EditText mEmailEditText;

    @BindView(R.id.email_textinputlayout)
    TextInputLayout mEmailTextInputLayout;

    @BindView(R.id.send_button)
    Button mSendButton;

    @Inject
    ForgotPasswordMvpPresenter<ForgotPasswordMvpView> mPresenter;

    public static ForgotPasswordFragment newInstance() {
        Bundle args = new Bundle();
        ForgotPasswordFragment fragment = new ForgotPasswordFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forgot_password, container, false);

        getActivityComponent().inject(this);
        setUnbinder(ButterKnife.bind(this, view));

        mPresenter.onAttach(this);

        setupViews(view);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mPresenter.onDetach();
    }

    @OnClick(R.id.send_button)
    void onSendClick(View v) {
        hideKeyboard();
        if (isNetworkConnected()) {
            if (mEmailTextInputLayout.isErrorEnabled()) {
                mEmailTextInputLayout.setErrorEnabled(false);
            }

            mPresenter.resetPassword(mEmailEditText.getText().toString());
        } else {
            onError(R.string.error_check_network_connection);
        }
    }

    @OnClick(R.id.back_button)
    void onBackClick(View v) {
        getBaseActivity().onFragmentDetached(ForgotPasswordFragment.class.getSimpleName());


    }

    @Override
    protected void setupViews(View view) {
        mTitleText.setText(R.string.login_title_forgot_password);

        RxTextView.textChanges(mEmailEditText)
                .debounce(500, TimeUnit.MILLISECONDS)
                .map(input -> input.length() > 0)
                .distinctUntilChanged()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::setSendButtonEnabled);

    }

    @Override
    public void setSendButtonEnabled(boolean enabled) {
        mSendButton.setEnabled(enabled);
    }

    @Override
    public void onEmailError(@StringRes int error) {
        mEmailTextInputLayout.setError(getString(error));
    }
}
