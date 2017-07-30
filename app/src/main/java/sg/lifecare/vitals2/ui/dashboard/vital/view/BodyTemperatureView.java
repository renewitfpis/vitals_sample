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
import sg.lifecare.data.local.database.BodyTemperature;
import sg.lifecare.vitals2.R;
import sg.lifecare.vitals2.R2;

public class BodyTemperatureView extends VitalView {

    @BindView(R2.id.bt_value_layout)
    View mBtValueLayout;

    @BindView(R2.id.bt_value_text)
    TextView mBtValueText;

    @Override
    public View getView(ViewGroup parent) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_vital_body_temperature, parent, false);

        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void setup(Realm realm, String id) {
        BodyTemperature bt = BodyTemperature.getLatestByPatientId(realm, id);

        if (bt == null) {
            mBtValueLayout.setVisibility(View.INVISIBLE);
        } else {
            mBtValueLayout.setVisibility(View.VISIBLE);
            mBtValueText.setText(String.format(Locale.getDefault(), "%.1f", bt.getTemperature()));
        }
    }

    @OnClick(R2.id.measure_button)
    public void onMeasureClick() {
        if (mListener != null) {
            mListener.onMeasureClick(this);
        }
    }
}
