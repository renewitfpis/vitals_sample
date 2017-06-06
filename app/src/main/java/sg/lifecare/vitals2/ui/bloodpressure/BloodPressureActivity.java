package sg.lifecare.vitals2.ui.bloodpressure;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import sg.lifecare.vitals2.R;
import sg.lifecare.vitals2.ui.base.BaseActivity;

public class BloodPressureActivity extends BaseActivity {

    public static final int TYPE_MANUAL = 1;
    public static final int TYPE_DEVICE = 2;

    private static final String PARAM_TYPE = "type";

    private int mType;

    public static Intent getStartIntent(Context context, int type) {
        Intent intent = new Intent(context, BloodPressureActivity.class);
        intent.putExtra(PARAM_TYPE, type);
        return intent;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_glucose);

        mType = getIntent().getIntExtra(PARAM_TYPE, 0);

        setup();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void setup() {
        if (TYPE_MANUAL == mType) {
            showBloodPressureManualFragment();
        } else if (TYPE_DEVICE == mType) {
            showBloodPressureDeviceFragment();
        }
    }

    private void showBloodPressureDeviceFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_content, BloodPressureDeviceFragment.newInstance(), BloodPressureDeviceFragment.TAG)
                .commit();
    }

    private void showBloodPressureManualFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_content, BloodPressureManualFragment.newInstance(), BloodPressureManualFragment.TAG)
                .commit();
    }
}
