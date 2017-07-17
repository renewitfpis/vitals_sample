package sg.lifecare.vitals2.ui.dashboard;

import android.text.TextUtils;

import java.net.SocketTimeoutException;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;
import sg.lifecare.data.DataManager;
import sg.lifecare.data.remote.model.response.AssistsedEntityResponse;
import sg.lifecare.data.remote.model.response.EntityDetailResponse;
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

        /*getCompositeDisposable().add(Observable.zip(
                getDataManager().getEntityObservable(id),
                getDataManager().getMembersEntityObservable(id),
                (entityDetailResponse, assistsedEntityResponse) -> {
                    if (!assistsedEntityResponse.isError()) {
                        getDataManager().setMembersEntity(assistsedEntityResponse.getData());
                    }

                  return entityDetailResponse;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    Timber.d("done");
                    if (!isViewAttached()) {
                        return;
                    }

                    getMvpView().hideLoading();

                    if (response.isError()) {
                        getMvpView().startLoginActivity();
                    } else {
                        EntityDetailResponse.Data entities = response.getData().get(0);
                        testAddUser(entities);
                        getDataManager().setUserEntity(entities);
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
                }));*/

        getCompositeDisposable().add(
                getDataManager().getEntityObservable(id)
                        .subscribeOn(Schedulers.io())
                        .doOnError(new Consumer<Throwable>() {
                            @Override
                            public void accept(@NonNull Throwable throwable) throws Exception {
                                Timber.d("error 1");
                            }
                        })
                        .flatMap(new Function<EntityDetailResponse, ObservableSource<EntityDetailResponse>>() {
                            @Override
                            public ObservableSource<EntityDetailResponse> apply(
                                    @NonNull EntityDetailResponse entityDetailResponse)
                                    throws Exception {
                                return getDataManager().getMembersEntityObservable(id).flatMap(
                                        new Function<AssistsedEntityResponse, ObservableSource<EntityDetailResponse>>() {
                                            @Override
                                            public ObservableSource<EntityDetailResponse> apply(
                                                    @NonNull AssistsedEntityResponse assistsedEntityResponse)
                                                    throws Exception {

                                                if (assistsedEntityResponse.isError()) {
                                                    Timber.w(assistsedEntityResponse.getErrorDesc());
                                                } else {
                                                    getDataManager().setMembersEntity(assistsedEntityResponse.getData());
                                                }
                                                return Observable.just(entityDetailResponse);
                                            }
                                        })
                                        .onErrorReturn(
                                                new Function<Throwable, EntityDetailResponse>() {
                                                    @Override
                                                    public EntityDetailResponse apply(
                                                            @NonNull Throwable throwable)
                                                            throws Exception {
                                                        return entityDetailResponse;
                                                    }
                                                });
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> {
                            if (!isViewAttached()) {
                                return;
                            }

                            getMvpView().hideLoading();

                            if (response.isError()) {
                                getMvpView().startLoginActivity();
                            } else {
                                EntityDetailResponse.Data entities = response.getData().get(0);
                                getDataManager().setUserEntity(entities);
                                getMvpView().onUserEntityDetailResult(getDataManager().getUserEntity());

                            }
                        }, throwable -> {
                            Timber.d("error");

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



        /*getCompositeDisposable().add(getDataManager().getEntityObservable(id)
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
                        EntityDetailResponse.Data entities = response.getData().get(0);
                        testAddUser(entities);
                        getDataManager().setUserEntity(entities);
                        if (entities.isNormalUser()) {
                            getMvpView().onUserEntityDetailResult(getDataManager().getUserEntity());
                        } else {
                            getUserEntity();
                        }
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
                }));*/

    }

    @Override
    public void getMembersEntity() {
        String id = getDataManager().getPreferencesHelper().getEntityId();

        if (TextUtils.isEmpty(id)) {
            return;
        }

        getCompositeDisposable().add(getDataManager().getMembersEntityObservable(id)
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
                        List<AssistsedEntityResponse.Data> entities = response.getData();
                        getDataManager().setMembersEntity(entities);
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
