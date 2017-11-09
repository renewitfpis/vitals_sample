package sg.lifecare.vitals2.ui.base;


import java.net.UnknownHostException;

import retrofit2.HttpException;

public interface MvpPresenter<V extends MvpView> {

    void onAttach(V mvpView);

    void onDetach();

    void handleNetworkError(HttpException exception);

    void handleNetworkError(UnknownHostException exception);
}
