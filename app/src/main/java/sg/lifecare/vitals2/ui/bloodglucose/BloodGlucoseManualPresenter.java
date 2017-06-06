package sg.lifecare.vitals2.ui.bloodglucose;


import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import sg.lifecare.data.DataManager;
import sg.lifecare.vitals2.ui.base.BasePresenter;

public class BloodGlucoseManualPresenter<V extends BloodGlucoseManualMvpView> extends BasePresenter<V>
        implements BloodGlucoseManualMvpPresenter<V> {


    @Inject
    public BloodGlucoseManualPresenter(DataManager dataManager,
            CompositeDisposable compositeDisposable) {
        super(dataManager, compositeDisposable);
    }
}
