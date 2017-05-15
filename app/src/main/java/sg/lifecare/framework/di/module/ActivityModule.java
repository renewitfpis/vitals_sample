package sg.lifecare.framework.di.module;

import android.app.Activity;
import android.content.Context;

import dagger.Module;
import dagger.Provides;
import io.reactivex.disposables.CompositeDisposable;
import sg.lifecare.framework.di.ActivityContext;

@Module
public class ActivityModule {

    private Activity mActivity;

    public ActivityModule(Activity activity) {
        mActivity = activity;
    }

    @Provides
    @ActivityContext
    Context provideContent() {
        return mActivity;
    }

    @Provides
    CompositeDisposable provideCompositeDisposible() {
        return new CompositeDisposable();
    }
}
