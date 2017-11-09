package sg.lifecare.vitals2.ui.login;

import android.support.annotation.StringRes;

import sg.lifecare.vitals2.ui.base.MvpView;

public interface ForgotPasswordMvpView extends MvpView {

    void setSendButtonEnabled(boolean enabled);
    void onEmailError(@StringRes int error);
}
