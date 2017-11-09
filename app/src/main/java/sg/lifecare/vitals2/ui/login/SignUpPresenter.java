package sg.lifecare.vitals2.ui.login;

import io.reactivex.disposables.CompositeDisposable;
import sg.lifecare.data.DataManager;
import sg.lifecare.vitals2.ui.base.BasePresenter;

public class SignUpPresenter<V extends SignUpMvpView> extends BasePresenter<V>
        implements SignUpMvpPresenter<V>{

    public SignUpPresenter(DataManager dataManager, CompositeDisposable compositeDisposable) {
        super(dataManager, compositeDisposable);
    }
}
