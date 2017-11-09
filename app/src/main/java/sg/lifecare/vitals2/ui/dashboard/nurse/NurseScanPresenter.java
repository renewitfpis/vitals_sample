package sg.lifecare.vitals2.ui.dashboard.nurse;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import sg.lifecare.data.DataManager;
import sg.lifecare.data.remote.model.response.AssistsedEntityResponse;
import sg.lifecare.vitals2.ui.base.BasePresenter;

public class NurseScanPresenter<V extends NurseScanMvpView> extends BasePresenter<V>
        implements NurseScanMvpPresenter<V> {


    @Inject
    public NurseScanPresenter(DataManager dataManager,
            CompositeDisposable compositeDisposable) {
        super(dataManager, compositeDisposable);
    }

    @Override
    public List<AssistsedEntityResponse.Data> getNurses() {
        return getDataManager().getMembersEntity();
    }
}
