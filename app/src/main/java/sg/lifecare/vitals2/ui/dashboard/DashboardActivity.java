package sg.lifecare.vitals2.ui.dashboard;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import sg.lifecare.data.remote.model.response.EntityDetailResponse;
import sg.lifecare.data.remote.model.response.LogoutResponse;
import sg.lifecare.vitals2.R;
import sg.lifecare.vitals2.R2;
import sg.lifecare.vitals2.ui.BarcodeBottomSheetFragment;
import sg.lifecare.vitals2.ui.base.BaseActivity;
import sg.lifecare.vitals2.ui.bloodglucose.BloodGlucoseActivity;
import sg.lifecare.vitals2.ui.bloodglucose.BloodGlucoseManualFragment;
import sg.lifecare.vitals2.ui.bloodpressure.BloodPressureActivity;
import sg.lifecare.vitals2.ui.bodyweight.BodyWeightActivity;
import sg.lifecare.vitals2.ui.bodyweight.BodyWeightDeviceFragment;
import sg.lifecare.vitals2.ui.dashboard.careplan.CarePlanFragment;
import sg.lifecare.vitals2.ui.device.DeviceActivity;
import sg.lifecare.vitals2.ui.login.LoginActivity;
import timber.log.Timber;

public class DashboardActivity extends BaseActivity
        implements DashboardMvpView, CarePlanFragment.CarePlanTaskListener {

    @Inject
    DashboardMvpPresenter<DashboardMvpView> mPresenter;

    @BindView(R2.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @BindView(R2.id.toolbar)
    Toolbar mToolbar;

    @BindView(R2.id.navigation_view)
    NavigationView mNavigationView;

    private CircleImageView mUserProfileImage;
    private TextView mUserNameText;

    private ActionBarDrawerToggle mDrawerToggle;

    public static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, DashboardActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        getActivityComponent().inject(this);
        setUnbinder(ButterKnife.bind(this));

        mPresenter.onAttach(this);

        setup();

        mPresenter.getUserEntity();
    }

    @Override
    protected void onDestroy() {
        mPresenter.onDetach();

        super.onDestroy();
    }

    @Override
    protected void setup() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.dashboard_title);
        getSupportActionBar().hide();

        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                mToolbar,
                R.string.drawer_open,
                R.string.drawer_close) {

            @Override
            public void onDrawerOpened(View view) {
                super.onDrawerOpened(view);
                hideKeyboard();
            }

            @Override
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

        };

        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        setupNavigationView();
    }

    private void setupNavigationView() {
        View headerLayout = mNavigationView.getHeaderView(0);
        mUserProfileImage = (CircleImageView) headerLayout.findViewById(R.id.user_profile_image);
        mUserNameText = (TextView) headerLayout.findViewById(R.id.user_name_text);

        mNavigationView.setNavigationItemSelectedListener(item -> {


                    switch (item.getItemId()) {

                        case R.id.nav_item_devices:
                            startDeviceActivity();
                            break;

                        case R.id.nav_barcode:
                            showBarcodeFragment();
                            break;

                        case R.id.nav_item_logout:
                            mPresenter.logout();
                            break;
                    }

                    mDrawerLayout.closeDrawer(GravityCompat.START);

                    return true;
                }
        );
    }

    @Override
    public void startLoginActivity() {
        Intent intent = LoginActivity.getStartIntent(this);
        startActivity(intent);
        finish();
    }

    @Override
    public void onUserEntityDetailResult(EntityDetailResponse.Data userEntity) {
        getSupportActionBar().show();

        mUserNameText.setText(userEntity.getName());

        if (!TextUtils.isEmpty(userEntity.getDefaultMediaUrl())) {
            Glide.with(this)
                    .load(userEntity.getDefaultMediaUrl())
                    .into(mUserProfileImage);
        }

        showCarePlanFragment();
    }

    @Override
    public void onLogoutResult(LogoutResponse response) {
        startLoginActivity();
    }



    @Override
    public void showBloodGlucoseManualFragment() {
        Timber.d("showBloodGlucoseManualFragment");
        startActivity(BloodGlucoseActivity.getStartIntent(this, BloodGlucoseActivity.TYPE_MANUAL));
    }

    @Override
    public void showBodyWeightDeviceFragment() {
        Timber.d("showBodyWeightDeviceFragment");
        startActivity(BodyWeightActivity.getStartIntent(this));
    }

    @Override
    public void showBloodPressureDeviceFragment() {
        startActivity(BloodPressureActivity.getStartIntent(this, BloodPressureActivity.TYPE_DEVICE));
    }

    @Override
    public void showBloodPressureManualFragment() {
        startActivity(BloodPressureActivity.getStartIntent(this, BloodPressureActivity.TYPE_MANUAL));
    }

    private void showCarePlanFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_content, CarePlanFragment.newInstance(), CarePlanFragment.class.getSimpleName())
                .commit();
    }

    private void startDeviceActivity() {
        Intent intent = DeviceActivity.getStartIntent(this);
        startActivity(intent);
    }

    private void showBarcodeFragment() {
        BarcodeBottomSheetFragment fragment =
                BarcodeBottomSheetFragment.newInstance();
        fragment.show(getSupportFragmentManager(), BarcodeBottomSheetFragment.TAG);
    }
}
