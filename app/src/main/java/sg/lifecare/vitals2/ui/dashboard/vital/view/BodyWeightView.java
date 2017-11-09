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
import sg.lifecare.data.local.database.BodyWeight;
import sg.lifecare.vitals2.R;
import sg.lifecare.vitals2.R2;

public class BodyWeightView extends VitalView {

    @BindView(R2.id.bw_value_layout)
    View mBwValueLayout;

    @BindView(R2.id.bw_value_text)
    TextView mBwValueText;


    @Override
    public View getView(ViewGroup parent) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_vital_body_weight, parent, false);

        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void setup(Realm realm, String id) {
        BodyWeight bw = BodyWeight.getLatestByPatientId(realm, id);

        if (bw == null) {
            mBwValueLayout.setVisibility(View.INVISIBLE);
        } else {
            mBwValueLayout.setVisibility(View.VISIBLE);
            mBwValueText.setText(String.format(Locale.getDefault(), "%.1f", bw.getWeight()));
        }
    }

    @OnClick(R2.id.measure_button)
    public void onMeasureClick() {
        if (mListener != null) {
            mListener.onMeasureClick(this);
        }
    }
}
