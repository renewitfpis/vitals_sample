package sg.lifecare.vitals2.ui.dashboard.nurse;


import io.realm.RealmResults;
import sg.lifecare.data.local.database.Patient;
import sg.lifecare.framework.di.PerActivity;
import sg.lifecare.vitals2.ui.base.MvpPresenter;
import sg.lifecare.vitals2.ui.base.MvpView;

@PerActivity
public interface NurseMainMvpPresenter<V extends MvpView> extends MvpPresenter<V> {

    RealmResults<Patient> getPatients();

}
