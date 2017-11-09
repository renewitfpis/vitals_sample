package sg.lifecare.vitals2.ui.base;


import android.support.annotation.StringRes;

public interface MvpView {

    void showLoading();

    void hideLoading();

    void onError(@StringRes int resId);

    void onError(String message);

    boolean isNetworkConnected();

    void hideKeyboard();

    void onNetworkError(String message);
}
