package sg.lifecare.vitals2.ui.bodyweight;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import sg.lifecare.vitals2.R;
import sg.lifecare.vitals2.ui.base.BaseActivity;

public class BodyWeightActivity extends BaseActivity {

    public static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, BodyWeightActivity.class);
        return intent;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_body_weight);

        setup();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void setup() {
        showBloodGlucoseManualFragment();
    }

    private void showBloodGlucoseManualFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_content, BodyWeightDeviceFragment.newInstance(), BodyWeightDeviceFragment.TAG)
                .commit();
    }
}
