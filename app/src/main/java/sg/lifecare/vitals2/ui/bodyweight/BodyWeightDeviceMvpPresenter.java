package sg.lifecare.vitals2.ui.bodyweight;


import sg.lifecare.framework.di.PerActivity;
import sg.lifecare.vitals2.ui.base.MvpPresenter;
import sg.lifecare.vitals2.ui.base.MvpView;

@PerActivity
public interface BodyWeightDeviceMvpPresenter<V extends MvpView> extends MvpPresenter<V> {
}
