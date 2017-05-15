package sg.lifecare.data.local;

import android.support.annotation.NonNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import timber.log.Timber;

@Singleton
public class RxBus {

    private final PublishSubject<Object> mBus = PublishSubject.create();

    @Inject
    public RxBus() {
        Timber.d("RxBus");
    }

    public void post(@NonNull Object event) {
        if (mBus.hasObservers()) {
            mBus.onNext(event);
        }
    }

    public <T> Observable<T> observable(@NonNull final Class<T> eventClass) {
        return mBus
                .filter(o -> o != null)
                .filter(eventClass :: isInstance)
                .cast(eventClass);
    }

    public static class AuthenticationErrorEvent {
        public AuthenticationErrorEvent() {}
    }

    public static class UserLoggedOutEvent {
        public UserLoggedOutEvent() {}
    }
}
