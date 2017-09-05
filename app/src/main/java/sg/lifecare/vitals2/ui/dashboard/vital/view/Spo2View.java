package sg.lifecare.vitals2.ui.dashboard.vital.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import sg.lifecare.data.local.database.Spo2;
import sg.lifecare.vitals2.R;
import sg.lifecare.vitals2.R2;


public class Spo2View extends VitalView {

    @BindView(R2.id.spo2_value_layout)
    View mSpo2ValueLayout;

    @BindView(R2.id.spo2_value_text)
    TextView mSpo2ValueText;

    @Override
    public View getView(ViewGroup parent) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_vital_spo2, parent, false);

        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void setup(Realm realm, String id) {
        Spo2 spo2 = Spo2.getLatestByPatientId(realm, id);

        if (spo2 == null) {
            mSpo2ValueLayout.setVisibility(View.INVISIBLE);
        } else {
            mSpo2ValueLayout.setVisibility(View.VISIBLE);
            mSpo2ValueText.setText(String.format(Locale.getDefault(), "%d", spo2.getSpo2()));
        }
    }

    @OnClick(R2.id.measure_button)
    public void onMeasureClick() {
        if (mListener != null) {
            mListener.onMeasureClick(this);
        }
    }
}

