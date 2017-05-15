package sg.lifecare.data.remote;

import java.io.IOException;

import javax.inject.Inject;

import okhttp3.Interceptor;
import okhttp3.Response;
import sg.lifecare.data.local.RxBus;


public class UnauthorizedInterceptor implements Interceptor {

    @Inject
    RxBus mRxBus;

    public UnauthorizedInterceptor() {
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
        if (response.code() == 401) {
            mRxBus.post(new RxBus.AuthenticationErrorEvent());
        }
        return response;
    }
}
