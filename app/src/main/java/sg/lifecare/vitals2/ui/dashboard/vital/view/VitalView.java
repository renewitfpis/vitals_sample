package sg.lifecare.vitals2.ui.dashboard.vital.view;

import android.view.View;
import android.view.ViewGroup;

import io.realm.Realm;

public abstract class VitalView  {

    VitalViewListener mListener;

    abstract public View getView(ViewGroup parent);
    abstract public void setup(Realm realm, String id);

    public void setListener(VitalViewListener listener) {
        mListener = listener;
    }


}
