package sg.lifecare.vitals2.ui.dashboard.nurse;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import sg.lifecare.data.DataManager;
import sg.lifecare.data.local.database.Patient;
import sg.lifecare.vitals2.ui.base.BaseRealmPresenter;

public class NurseMainPresenter<V extends NurseMainMvpView> extends BaseRealmPresenter<V>
        implements NurseMainMvpPresenter<V> {

    @Inject
    public NurseMainPresenter(DataManager dataManager,
            CompositeDisposable compositeDisposable) {
        super(dataManager, compositeDisposable);
    }

    @Override
    public RealmResults<Patient> getPatients() {
        /*getCompositeDisposable().add(getDataManager().getPatientsFlowable()
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    getMvpView().onPatientsResult(response);
                }));*/

        return Patient.getPatients(mRealm);
    }
}
