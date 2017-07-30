package sg.lifecare.vitals2.ui.bodyweight;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import sg.lifecare.ble.parser.BodyWeightMeasurement;
import sg.lifecare.vitals2.R;
import sg.lifecare.vitals2.ui.base.BaseActivity;
import sg.lifecare.vitals2.ui.qn.QNFragment;
import timber.log.Timber;

public class BodyWeightActivity extends BaseActivity {

    public static final int TYPE_MANUAL = 1;
    public static final int TYPE_DEVICE = 2;

    public static final String PARAM_DATA = "data";
    public static final String PARAM_DEVICE_ID = "device_id";
    private static final String PARAM_TYPE = "type";
    private static final String PARAM_DEVICE_MODEL = "device_model";

    public static final int DEVICE_AND = 1;
    public static final int DEVICE_QN = 2;

    private int mType;
    private int mModel;

    public static Intent getStartIntent(Context context, int type) {
        Intent intent = new Intent(context, BodyWeightActivity.class);
        intent.putExtra(PARAM_TYPE, type);
        return intent;
    }

    public static Intent getStartIntent(Context context, int type, int model) {
        Intent intent = new Intent(context, BodyWeightActivity.class);
        intent.putExtra(PARAM_TYPE, type);
        intent.putExtra(PARAM_DEVICE_MODEL, model);
        return intent;
    }

    public static BodyWeightMeasurement getData(Intent intent) {
        return (BodyWeightMeasurement) intent.getSerializableExtra(PARAM_DATA);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_body_weight);

        mType = getIntent().getIntExtra(PARAM_TYPE, 0);
        mModel = getIntent().getIntExtra(PARAM_DEVICE_MODEL, 0);

        setup();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void setup() {

        if (TYPE_MANUAL == mType) {
            showManualFragment();
        } else if (TYPE_DEVICE == mType) {

            if (mModel == DEVICE_QN) {
                showQnFragment();
            } else {
            }
        }
    }

    @Override
    public void onBackPressed() {
        Timber.d("onBackPressed: count=%d", getSupportFragmentManager().getBackStackEntryCount());
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
        } else {
            finish();
        }
    }

    private void showManualFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_content, BodyWeightDeviceFragment.newInstance(), BodyWeightDeviceFragment.TAG)
                .commit();
    }

    private void showQnFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_content, QNFragment.newInstance(), QNFragment.class.getSimpleName())
                .commit();
    }
}
