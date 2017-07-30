package sg.lifecare.vitals2.ui.base;

import io.reactivex.disposables.CompositeDisposable;
import io.realm.Realm;
import sg.lifecare.data.DataManager;

public class BaseRealmPresenter<V extends MvpView> extends BasePresenter<V> {

    protected Realm mRealm;


    public BaseRealmPresenter(DataManager dataManager,
            CompositeDisposable compositeDisposable) {
        super(dataManager, compositeDisposable);
    }


    @Override
    public void onAttach(V mvpView) {
        super.onAttach(mvpView);

        mRealm = getDataManager().getRealm();
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (mRealm != null) {
            mRealm.close();
            mRealm = null;
        }
    }
}
