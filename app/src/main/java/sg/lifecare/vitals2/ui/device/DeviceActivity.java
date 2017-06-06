package sg.lifecare.vitals2.ui.device;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import sg.lifecare.vitals2.R;
import sg.lifecare.vitals2.R2;
import sg.lifecare.vitals2.ui.base.BaseActivity;
import sg.lifecare.vitals2.ui.device.list.DeviceListFragment;
import timber.log.Timber;

public class DeviceActivity extends BaseActivity {

    @BindView(R2.id.toolbar)
    Toolbar mToolbar;

    public static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, DeviceActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        Timber.d("onCreate");

        setUnbinder(ButterKnife.bind(this));


        setup();
        showDeviceListFragment();
    }

    @Override
    public boolean onSupportNavigateUp() {
        showPreviousFragment();
        return true;
    }


    @Override
    protected void setup() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.device_title);

        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    void showDeviceListFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        DeviceListFragment fragment = DeviceListFragment.newInstance();
        ft.add(R.id.fragment_content, fragment);
        ft.commit();
    }

    void showPreviousFragment() {
        FragmentManager fm = getSupportFragmentManager();
        int count = fm.getBackStackEntryCount();

        if (count > 0) {
            fm.popBackStack();
        } else {
            finish();
        }
    }
}
