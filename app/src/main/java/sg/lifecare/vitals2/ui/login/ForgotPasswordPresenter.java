package sg.lifecare.vitals2.ui.login;

import android.text.TextUtils;

import java.net.SocketTimeoutException;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;
import sg.lifecare.data.DataManager;
import sg.lifecare.utils.CommonUtils;
import sg.lifecare.vitals2.R;
import sg.lifecare.vitals2.ui.base.BasePresenter;
import timber.log.Timber;

public class ForgotPasswordPresenter<V extends ForgotPasswordMvpView> extends BasePresenter<V>
        implements ForgotPasswordMvpPresenter<V> {

    @Inject
    public ForgotPasswordPresenter(DataManager dataManager, CompositeDisposable compositeDisposable) {
        super(dataManager, compositeDisposable);
    }

    @Override
    public void resetPassword(String email) {

        if (TextUtils.isEmpty(email)) {
            getMvpView().onEmailError(R.string.error_required_field);
            return;
        }

        if (!CommonUtils.isValidEmail(email)) {
            getMvpView().onEmailError(R.string.error_invalid_email);
            return;
        }

        getCompositeDisposable()
                .add(getDataManager().resetPassword(email)
                .doOnSubscribe(disposable -> getMvpView().showLoading())
                .doOnTerminate(() -> getMvpView().hideLoading())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resetPasswordResponse -> {
                    if (!isViewAttached()) {
                        return;
                    }

                    if (resetPasswordResponse.isError()) {
                        getMvpView().onNetworkError(resetPasswordResponse.getErrorDesc());
                    }
                }, throwable -> {
                    if (!isViewAttached()) {
                        return;
                    }

                    if (throwable instanceof HttpException) {
                        handleNetworkError((HttpException) throwable);
                    } else if (throwable instanceof SocketTimeoutException) {
                        Timber.d("SocketTimeoutException");
                    }else {
                        Timber.e(throwable, throwable.getMessage());
                    }
                }));
    }
}
