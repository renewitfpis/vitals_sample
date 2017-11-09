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
import sg.lifecare.data.local.database.BloodGlucose;
import sg.lifecare.vitals2.R;
import sg.lifecare.vitals2.R2;

public class BloodGlucoseView extends VitalView {

    @BindView(R.id.bg_value_layout)
    View mBgValueLayout;

    @BindView(R.id.bg_value_text)
    TextView mBgValueText;

    @Override
    public View getView(ViewGroup parent) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_vital_blood_glucose, parent, false);

        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void setup(Realm realm, String id) {
        BloodGlucose bg = BloodGlucose.getLatestByPatientId(realm, id);

        if (bg == null) {
            mBgValueLayout.setVisibility(View.INVISIBLE);
        } else {
            mBgValueLayout.setVisibility(View.VISIBLE);
            mBgValueText.setText(String.format(Locale.getDefault(), "%.1f", bg.getGlucose()));
        }
    }

    @OnClick(R2.id.measure_button)
    public void onMeasureClick() {
        if (mListener != null) {
            mListener.onMeasureClick(this);
        }
    }
}
