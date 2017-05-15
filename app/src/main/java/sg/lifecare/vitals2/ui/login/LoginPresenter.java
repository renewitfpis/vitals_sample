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


public class LoginPresenter<V extends LoginMvpView> extends BasePresenter<V> implements LoginMvpPresenter<V> {

    @Inject
    public LoginPresenter(DataManager dataManager, CompositeDisposable compositeDisposable) {
        super(dataManager, compositeDisposable);
    }

    @Override
    public void login(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            getMvpView().onEmailError(R.string.error_required_field);
            return;
        } else if (!CommonUtils.isValidEmail(email)){
            getMvpView().onEmailError(R.string.error_invalid_email);
            return;
        }

        if (TextUtils.isEmpty(password)) {
            getMvpView().onPasswordError(R.string.error_required_field);
            return;
        }

        getMvpView().showLoading();
        getMvpView().setLoginButtonEnabled(false);

        getCompositeDisposable().add(
                getDataManager().login(email, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(loginResponse -> {
                    if (!isViewAttached()) {
                        return;
                    }

                    if (loginResponse.isError()) {
                        getMvpView().hideLoading();
                        getMvpView().setLoginButtonEnabled(true);
                        getMvpView().onNetworkError(loginResponse.getErrorDesc());
                    } else {
                        // save the entity id
                        getDataManager().getPreferencesHelper().setEntityId(loginResponse.getData().getId());
                        getMvpView().startHomeActivity();
                    }


                }, throwable -> {
                    if (!isViewAttached()) {
                        return;
                    }

                    getMvpView().hideLoading();
                    getMvpView().setLoginButtonEnabled(true);

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
