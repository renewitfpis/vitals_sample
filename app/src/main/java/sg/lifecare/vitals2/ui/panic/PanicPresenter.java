package sg.lifecare.vitals2.ui.panic;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import sg.lifecare.data.DataManager;
import sg.lifecare.vitals2.ui.base.BasePresenter;

public class PanicPresenter<V extends PanicMvpView> extends BasePresenter<V> {

    @Inject
    public PanicPresenter(DataManager dataManager,
            CompositeDisposable compositeDisposable) {
        super(dataManager, compositeDisposable);
    }
}
