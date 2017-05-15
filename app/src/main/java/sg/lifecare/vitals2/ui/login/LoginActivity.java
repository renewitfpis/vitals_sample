package sg.lifecare.vitals2.ui.login;

import com.jakewharton.rxbinding2.widget.RxTextView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import sg.lifecare.vitals2.BuildConfig;
import sg.lifecare.vitals2.R;
import sg.lifecare.vitals2.ui.base.BaseActivity;


public class LoginActivity extends BaseActivity implements LoginMvpView {

    @Inject
    LoginMvpPresenter<LoginMvpView> mPresenter;

    @BindView(R.id.email_edittext)
    EditText mEmailEdit;

    @BindView(R.id.password_edittext)
    EditText mPasswordEdit;

    @BindView(R.id.login_button)
    Button mLoginButton;

    @BindView(R.id.email_textinputlayout)
    TextInputLayout mEmailTextInputLayout;

    @BindView(R.id.password_textinputlayout)
    TextInputLayout mPasswordTextInputLayout;

    public static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_login);

        getActivityComponent().inject(this);

        setUnbinder(ButterKnife.bind(this));
        mPresenter.onAttach(this);

        setup();
    }

    @Override
    protected void onDestroy() {
        mPresenter.onDetach();

        super.onDestroy();
    }

    @Override
    protected void setup() {
        Observable.combineLatest(RxTextView.textChanges(mEmailEdit), RxTextView.textChanges(mPasswordEdit),
                (email, password) -> !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password))
                .subscribe(aBoolean -> mLoginButton.setEnabled(aBoolean));

        if (BuildConfig.DEBUG) {
            mEmailEdit.setText("smartears_caregiver@lifecare.sg");
            mPasswordEdit.setText("lalaland888");
        }
    }

    @Override
    public void onFragmentDetached(String tag) {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentByTag(tag);
        if (fragment != null) {
            fm.beginTransaction()
                    .disallowAddToBackStack()
                    .remove(fragment)
                    .commitNow();
        }
    }

    @OnClick(R.id.forgot_password_text)
    void onForgotPasswordClick(View v) {
        showForgotPasswordFragment();
    }

    @OnClick(R.id.sign_up_text)
    void onSignUpClick(View v) {

    }

    @OnClick(R.id.login_button)
    void onLoginClick(View v) {
        mPresenter.login(mEmailEdit.getText().toString(), mPasswordEdit.getText().toString());
    }

    private void showForgotPasswordFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .disallowAddToBackStack()
                .add(R.id.fragment_content, ForgotPasswordFragment.newInstance(),
                        ForgotPasswordFragment.class.getSimpleName())
                .commit();
    }

    @Override
    public void setLoginButtonEnabled(boolean enabled) {
        mLoginButton.setEnabled(enabled);
    }

    @Override
    public void onEmailError(@StringRes int error) {
        mEmailTextInputLayout.setError(getString(error));
    }

    @Override
    public void onPasswordError(@StringRes int error) {
        mPasswordTextInputLayout.setError(getString(error));
    }

    @Override
    public void startHomeActivity() {
        //Intent intent = HomeActivity.getStartIntent(this);
        //startActivity(intent);
        //finish();
    }


}
