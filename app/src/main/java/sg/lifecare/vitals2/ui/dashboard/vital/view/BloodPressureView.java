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
import sg.lifecare.data.local.database.BloodPressure;
import sg.lifecare.vitals2.R;
import sg.lifecare.vitals2.R2;

public class BloodPressureView extends VitalView {

    @BindView(R2.id.bp_value_layout)
    View mBpValueLayout;

    @BindView(R2.id.bp_value_text)
    TextView mBpValueText;

    @Override
    public View getView(ViewGroup parent) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_vital_blood_pressure, parent, false);

        ButterKnife.bind(this, view);

        return view;

    }

    @OnClick(R2.id.measure_button)
    public void onMeasureClick() {
        if (mListener != null) {
            mListener.onMeasureClick(this);
        }
    }

    @Override
    public void setup(Realm realm, String id) {
        BloodPressure bp = BloodPressure.getLatestByPatientId(realm, id);

        if (bp == null) {
            mBpValueLayout.setVisibility(View.INVISIBLE);
        } else {
            mBpValueLayout.setVisibility(View.VISIBLE);
            mBpValueText.setText(String.format(Locale.getDefault(), "%d/%d", bp.getSystolic(),
                    bp.getDiastolic()));
        }
    }
}
