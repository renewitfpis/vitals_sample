package sg.lifecare.vitals2.ui.bloodglucose;


import sg.lifecare.framework.di.PerActivity;
import sg.lifecare.vitals2.ui.base.MvpPresenter;
import sg.lifecare.vitals2.ui.base.MvpView;

@PerActivity
public interface BloodGlucoseManualMvpPresenter<V extends MvpView> extends MvpPresenter<V> {
}
