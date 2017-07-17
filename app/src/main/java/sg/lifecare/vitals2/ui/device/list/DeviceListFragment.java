package sg.lifecare.vitals2.ui.device.list;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import sg.lifecare.ble.utility.BleUtils;
import sg.lifecare.data.local.DeviceData;
import sg.lifecare.vitals2.R;
import sg.lifecare.vitals2.ui.base.BaseFragment;
import sg.lifecare.vitals2.ui.device.ble.BleDeviceAddFragment;
import timber.log.Timber;

public class DeviceListFragment extends BaseFragment implements DeviceListMvpView {

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_ADD_DEVICE = 100;

    private static final int PERMISSION_REQ_LOCATION = 1;

    @Inject
    DeviceListMvpPresenter<DeviceListMvpView> mPresenter;

    @BindView(R.id.list)
    RecyclerView mRecyclerView;

    private DeviceAdapter mDeviceAdapter;

    public static DeviceListFragment newInstance() {
        return new DeviceListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_device_list, container, false);
        getActivityComponent().inject(this);
        setUnbinder(ButterKnife.bind(this, view));

        mPresenter.onAttach(this);


        setupViews(view);

        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.clear();
        menuInflater.inflate(R.menu.device_add, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add) {
            showDeviceAddFragment();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void setupViews(View view) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                layoutManager.getOrientation());
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.list_divider));

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        mDeviceAdapter = new DeviceAdapter(this::showRemoveDeviceAlert);
        mDeviceAdapter.replaceDevices(mPresenter.getDeviceData().getDevices());
        mRecyclerView.setAdapter(mDeviceAdapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Timber.d("onActivityResult");

        if (requestCode == REQUEST_ADD_DEVICE) {
            mDeviceAdapter.replaceDevices(mPresenter.getDeviceData().getDevices());
        }
    }

    private void showDeviceAddFragment() {
        // check permissions
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (BleUtils.isBluetoothEnabled(getContext())) {
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();

                BleDeviceAddFragment fragment = BleDeviceAddFragment.newInstance(DeviceData.DEVICE_AND_UA651);
                fragment.setTargetFragment(this, REQUEST_ADD_DEVICE);
                fragment.show(ft, BleDeviceAddFragment.class.getSimpleName());
            } else {
                final Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            }
        } else {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQ_LOCATION);
        }
    }

    private void showRemoveDeviceAlert(final DeviceData.Device device) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AppDialog);
        builder.setTitle(R.string.device_alert_remove_title);
        String msg = String.format(getResources().getString(R.string.device_alert_remove_msg), device.getName());
        builder.setMessage(msg);
        builder.setNegativeButton(R.string.action_cancel, (dialog, which) -> dialog.dismiss());
        builder.setPositiveButton(R.string.action_remove, (dialog, which) -> {
            boolean removed = mPresenter.getDeviceData().removeDeviceById(device.getId());

            Timber.d("removed %b", removed);

            BleUtils.removeDevice(device.getId());

            mDeviceAdapter.replaceDevices(mPresenter.getDeviceData().getDevices());
            mDeviceAdapter.notifyDataSetChanged();

        });
        builder.create();
        builder.show();
    }


}
