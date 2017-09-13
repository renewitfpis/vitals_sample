package sg.lifecare.vitals2.ui.dashboard;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import sg.lifecare.data.remote.LifecareUtils;
import sg.lifecare.data.remote.model.response.AssistsedEntityResponse;
import sg.lifecare.data.remote.model.response.EntityDetailResponse;
import sg.lifecare.data.remote.model.response.LogoutResponse;
import sg.lifecare.vitals2.R;
import sg.lifecare.vitals2.R2;
import sg.lifecare.vitals2.services.SyncService;
import sg.lifecare.vitals2.ui.BarcodeBottomSheetFragment;
import sg.lifecare.vitals2.ui.base.BaseActivity;
import sg.lifecare.vitals2.ui.bloodglucose.BloodGlucoseActivity;
import sg.lifecare.vitals2.ui.bloodglucose.BloodGlucoseManualFragment;
import sg.lifecare.vitals2.ui.bloodpressure.BloodPressureActivity;
import sg.lifecare.vitals2.ui.bodyweight.BodyWeightActivity;
import sg.lifecare.vitals2.ui.bodyweight.BodyWeightDeviceFragment;
import sg.lifecare.vitals2.ui.dashboard.careplan.CarePlanFragment;
import sg.lifecare.vitals2.ui.dashboard.member.MemberListFragment;
import sg.lifecare.vitals2.ui.dashboard.nurse.NurseMainFragment;
import sg.lifecare.vitals2.ui.dashboard.nurse.NurseScanFragment;
import sg.lifecare.vitals2.ui.dashboard.patient.PatientMainFragment;
import sg.lifecare.vitals2.ui.dashboard.vital.VitalFragment;
import sg.lifecare.vitals2.ui.device.DeviceActivity;
import sg.lifecare.vitals2.ui.jumper.JumperOximeterFragment;
import sg.lifecare.vitals2.ui.jumper.JumperThermometerFragment;
import sg.lifecare.vitals2.ui.login.LoginActivity;
import sg.lifecare.vitals2.ui.panic.PanicFragment;
import sg.lifecare.vitals2.ui.qn.QNFragment;
import sg.lifecare.vitals2.ui.urion.UrionFragment;
import timber.log.Timber;

public class DashboardActivity extends BaseActivity implements
        DashboardMvpView, MemberListFragment.MemberListFragmentListener {

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
    public boolean onOptionsItemSelected(MenuItem item) {
        Timber.d("onOptionsItemSelected: %b", mDrawerToggle.onOptionsItemSelected(item));
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        } else {
            switch (item.getItemId()) {
                case android.R.id.home:
                    onBackPressed();
                    return true;
            }
        }

        return false;
    }

    @Override
    protected void setup() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.app_name);
        getSupportActionBar().hide();

        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
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

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerToggle.syncState();


        setupNavigationView();

        getSupportFragmentManager().addOnBackStackChangedListener(
                new android.support.v4.app.FragmentManager.OnBackStackChangedListener() {
                    @Override
                    public void onBackStackChanged() {
                        Timber.d("onBackStackChanged: count %d", getSupportFragmentManager().getBackStackEntryCount());
                        updateActionBar();
                    }
                });
    }

    private void updateActionBar() {
        int count = getSupportFragmentManager().getBackStackEntryCount();

        Timber.d("updateActionBar: count=%d", count);

        // update navigation button
        if (count == 1) {
            mDrawerToggle.setDrawerIndicatorEnabled(true);
            //getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            //getSupportActionBar().setDisplayShowHomeEnabled(true);
        } else {
            mDrawerToggle.setDrawerIndicatorEnabled(false);
            //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            //getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        mDrawerToggle.syncState();

        // update title
        if (count > 0) {
            String tag = getSupportFragmentManager().getBackStackEntryAt(count - 1).getName();

            if (MemberListFragment.class.getSimpleName().equals(tag)) {
                getSupportActionBar().setTitle(R.string.app_name);
            }

        }
    }

    private void setupNavigationView() {
        View headerLayout = mNavigationView.getHeaderView(0);
        mUserProfileImage = (CircleImageView) headerLayout.findViewById(R.id.user_profile_image);
        mUserNameText = (TextView) headerLayout.findViewById(R.id.user_name_text);

        mNavigationView.setNavigationItemSelectedListener(item -> {
                    switch (item.getItemId()) {
/*
                        case R.id.nav_item_devices:
                            startDeviceActivity();
                            break;

                        case R.id.nav_barcode:
                            showBarcodeFragment();
                            break;

                        case R.id.nav_panic:
                            showPanicFragment();
                            break;

                        case R.id.nav_qn:
                            showQNFragment();
                            break;

                        case R.id.nav_urion:
                            showUrionFragment();
                            break;

                        case R.id.nav_jumper_thermometer:
                            showJumperThermometerFragment();
                            break;

                        case R.id.nav_jumper_oximeter:
                            showJumperOximeterFragment();
                            break;
*/
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
    public void onBackPressed() {
        Timber.d("onBackPressed: count=%d", getSupportFragmentManager().getBackStackEntryCount());

        if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
            mDrawerLayout.closeDrawer(Gravity.START);
            return;
        }

        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
        } else {
            finish();
        }
    }

    @Override
    public void startLoginActivity() {
        Intent intent = LoginActivity.getStartIntent(this);
        startActivity(intent);
        finish();
    }

    @Override
    public void onUserEntityResult(EntityDetailResponse.Data userEntity) {

        mUserNameText.setText(userEntity.getName());

        if (!TextUtils.isEmpty(userEntity.getDefaultMediaUrl())) {
            Glide.with(this)
                    .load(userEntity.getDefaultMediaUrl())
                    .into(mUserProfileImage);
        }

        if (LifecareUtils.isCaregiver(userEntity.getAuthorizationLevel())) {
            mPresenter.getMembersEntity();
        } else {
            getSupportActionBar().show();
            showVitalFragment();
        }

        //showCarePlanFragment();

        //startService(new Intent(this, SyncService.class));

        //showNurseScanFragment();
    }

    @Override
    public void onMembersEntityResult(List<AssistsedEntityResponse.Data> membersEntity) {
        getSupportActionBar().show();
        showMemberListFragment();
    }

    @Override
    public void onLogoutResult(LogoutResponse response) {
        startLoginActivity();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu. menu_main, menu);
        return true;
    }

    //@Override
    //public void showBloodGlucoseManualFragment() {
    //    Timber.d("showBloodGlucoseManualFragment");
    //    startActivity(BloodGlucoseActivity.getStartIntent(this, BloodGlucoseActivity.TYPE_MANUAL));
    //}

    //@Override
    //public void showBodyWeightDeviceFragment() {
    //    Timber.d("showBodyWeightDeviceFragment");
    //    startActivity(BodyWeightActivity.getStartIntent(this, BodyWeightActivity.TYPE_MANUAL));
    //}

    //@Override
    //public void showBloodPressureDeviceFragment() {
    //    startActivity(BloodPressureActivity.getStartIntent(this, BloodPressureActivity.TYPE_DEVICE));
    //}

    //@Override
    //public void showBloodPressureManualFragment() {
    //    startActivity(BloodPressureActivity.getStartIntent(this, BloodPressureActivity.TYPE_MANUAL));
    //}

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

    private void showPanicFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_content, PanicFragment.newInstance(), PanicFragment.class.getSimpleName())
                .addToBackStack(PanicFragment.class.getSimpleName())
                .commit();

    }

    private void showQNFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_content, QNFragment.newInstance(), QNFragment.class.getSimpleName())
                .addToBackStack(QNFragment.class.getSimpleName())
                .commit();

    }

    public void showUrionFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_content, UrionFragment.newInstance(), UrionFragment.class.getSimpleName())
                .addToBackStack(UrionFragment.class.getSimpleName())
                .commit();

    }

    private void showJumperThermometerFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_content, JumperThermometerFragment.newInstance(), JumperThermometerFragment.class.getSimpleName())
                .addToBackStack(JumperThermometerFragment.class.getSimpleName())
                .commit();

    }

    private void showJumperOximeterFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_content, JumperOximeterFragment.newInstance(), JumperOximeterFragment.class.getSimpleName())
                .addToBackStack(JumperOximeterFragment.class.getSimpleName())
                .commit();

    }

    private void showNurseScanFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_content, NurseScanFragment.newInstance(), NurseScanFragment.class.getSimpleName())
                .addToBackStack(NurseScanFragment.class.getSimpleName())
                .commit();
    }

    //@Override
    //public void showNurseMainFragment(String nurseId) {
    //    getSupportFragmentManager()
    //            .beginTransaction()
    //            .add(R.id.fragment_content, NurseMainFragment.newInstance(nurseId), NurseMainFragment.class.getSimpleName())
    //            .addToBackStack(NurseMainFragment.class.getSimpleName())
    //            .commit();
    //}

    //@Override
    //public void showPatientMainFragment(String nurseId, String patientId) {
    //    getSupportFragmentManager()
    //            .beginTransaction()
    //            .add(R.id.fragment_content, PatientMainFragment.newInstance(nurseId, patientId),
    //                    PatientMainFragment.class.getSimpleName())
    //            .addToBackStack(PatientMainFragment.class.getSimpleName())
    //            .commit();
    //}

    private void showVitalFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_content, VitalFragment.newInstance(), VitalFragment.class.getSimpleName())
                .commit();
    }

    private void showMemberListFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_content, MemberListFragment.newInstance(), MemberListFragment.class.getSimpleName())
                .addToBackStack(MemberListFragment.class.getSimpleName())
                .commit();
    }

    @Override
    public void showMemberVitalFragment(int position) {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_content, VitalFragment.newInstance(position), VitalFragment.class.getSimpleName())
                .addToBackStack(VitalFragment.class.getSimpleName())
                .commit();
    }
}
