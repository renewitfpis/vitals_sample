package sg.lifecare.vitals2.ui.dashboard;

import android.text.TextUtils;

import java.net.SocketTimeoutException;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;
import sg.lifecare.data.DataManager;
import sg.lifecare.vitals2.ui.base.BasePresenter;
import timber.log.Timber;

public class DashboardPresenter<V extends DashboardMvpView> extends BasePresenter<V>
        implements DashboardMvpPresenter<V> {

    @Inject
    public DashboardPresenter(DataManager dataManager, CompositeDisposable compositeDisposable) {
        super(dataManager, compositeDisposable);
    }

    /**
     * Get login user entity data
     */
    @Override
    public void getUserEntity() {
        String id = getDataManager().getPreferencesHelper().getEntityId();

        Timber.d("getUserEntityDetail: id=%s", id);

        if (TextUtils.isEmpty(id)) {
            getMvpView().startLoginActivity();
        }

        getCompositeDisposable().add(getDataManager().getEntity(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {

                    if (!isViewAttached()) {
                        return;
                    }

                    getMvpView().hideLoading();

                    if (response.isError()) {
                        getMvpView().startLoginActivity();
                    } else {
                        getDataManager().setUserEntity(response.getData().get(0));
                        getMvpView().onUserEntityDetailResult(getDataManager().getUserEntity());
                    }

                }, throwable -> {
                    if (!isViewAttached()) {
                        return;
                    }

                    getMvpView().hideLoading();

                    if (throwable instanceof HttpException) {
                        handleNetworkError((HttpException) throwable);
                    } else if (throwable instanceof SocketTimeoutException) {
                        Timber.d("SocketTimeoutException");
                    }else {
                        Timber.e(throwable, throwable.getMessage());
                    }
                }));

    }

    @Override
    public void logout() {
        getCompositeDisposable().add(getDataManager().logout()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (!isViewAttached()) {
                        return;
                    }

                    getMvpView().hideLoading();
                    getMvpView().onLogoutResult(response);
                }, throwable -> {
                    if (!isViewAttached()) {
                        return;
                    }

                    getMvpView().hideLoading();
                    Timber.e(throwable, throwable.getMessage());

                    // still logout
                    getMvpView().onLogoutResult(null);
                }));
    }

    @Override
    public void handleNetworkError(HttpException exception) {
        super.handleNetworkError(exception);

        int code = exception.code();

        if (code == 500) {
            getMvpView().startLoginActivity();
        }
    }
}
