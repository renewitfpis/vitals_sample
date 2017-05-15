package sg.lifecare.vitals2.ui.login;

import sg.lifecare.framework.di.PerActivity;
import sg.lifecare.vitals2.ui.base.MvpPresenter;

@PerActivity
public interface ForgotPasswordMvpPresenter<V extends ForgotPasswordMvpView>
        extends MvpPresenter<V> {

    void resetPassword(String email);
}
