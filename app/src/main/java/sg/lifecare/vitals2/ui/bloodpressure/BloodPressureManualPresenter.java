package sg.lifecare.vitals2.ui.bloodpressure;


import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import sg.lifecare.data.DataManager;
import sg.lifecare.vitals2.ui.base.BasePresenter;

public class BloodPressureManualPresenter<V extends BloodPressureManualMvpView> extends BasePresenter<V>
        implements BloodPressureManualMvpPresenter<V> {

    @Inject
    public BloodPressureManualPresenter(DataManager dataManager,
            CompositeDisposable compositeDisposable) {
        super(dataManager, compositeDisposable);
    }
}
