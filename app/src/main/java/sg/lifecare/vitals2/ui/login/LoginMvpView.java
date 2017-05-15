package sg.lifecare.vitals2.ui.login;

import android.support.annotation.StringRes;

import sg.lifecare.vitals2.ui.base.MvpView;

public interface LoginMvpView extends MvpView {

    void setLoginButtonEnabled(boolean enabled);
    void onEmailError(@StringRes int error);
    void onPasswordError(@StringRes int error);
    void startHomeActivity();
}
