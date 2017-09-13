package sg.lifecare.vitals2.ui.dashboard.vital;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

import io.realm.Realm;
import sg.lifecare.vitals2.ui.dashboard.vital.view.BloodPressureView;
import sg.lifecare.vitals2.ui.dashboard.vital.view.BodyTemperatureView;
import sg.lifecare.vitals2.ui.dashboard.vital.view.BodyWeightView;
import sg.lifecare.vitals2.ui.dashboard.vital.view.Spo2View;
import sg.lifecare.vitals2.ui.dashboard.vital.view.VitalView;
import sg.lifecare.vitals2.ui.dashboard.vital.view.VitalViewListener;
import timber.log.Timber;

class VitalAdapter extends BaseAdapter {

    private ArrayList<VitalView> mData = new ArrayList<>();
    private String mId;
    private Realm mRealm;
    private VitalViewListener mListener;

    VitalAdapter(ArrayList<VitalView> data, Realm realm, @NonNull String id, VitalViewListener listener) {
        mData = data;
        mId = id;
        mRealm = realm;
        mListener = listener;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public VitalView getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Timber.d("getView: %d", position);
        View itemView = convertView;
        VitalView vitalView = mData.get(position);

        if (itemView == null) {
            if (vitalView instanceof BloodPressureView) {
                itemView = vitalView.getView(parent);

                //itemView.setTag(vitalView);
            } else if (vitalView instanceof BodyWeightView) {
                itemView = vitalView.getView(parent);
            } else if (vitalView instanceof BodyTemperatureView) {
                itemView = vitalView.getView(parent);
            } else if (vitalView instanceof Spo2View) {
                itemView = vitalView.getView(parent);
            }
        }

        vitalView.setup(mRealm, mId);
        vitalView.setListener(mListener);

        return itemView;
    }

    void updateView(int position) {
        mData.get(position).setup(mRealm, mId);
    }
}
