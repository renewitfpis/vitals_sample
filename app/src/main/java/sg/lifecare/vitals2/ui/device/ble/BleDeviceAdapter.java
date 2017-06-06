package sg.lifecare.vitals2.ui.device.ble;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import no.nordicsemi.android.support.v18.scanner.ScanResult;
import sg.lifecare.vitals2.R;
import timber.log.Timber;

class BleDeviceAdapter extends RecyclerView.Adapter<BleDeviceAdapter.DeviceViewHolder> {

    private List<ScanResult> mScanResults = new ArrayList<>();

    @Override
    public DeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device_ble_device,
                parent, false);
        return new DeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DeviceViewHolder holder, int position) {
        ScanResult scanResult = mScanResults.get(position);

        holder.bindView(scanResult);
    }

    @Override
    public int getItemCount() {
        return mScanResults.size();
    }

    void replaceResult(ScanResult result) {
        mScanResults.clear();
        mScanResults.add(result);
        notifyDataSetChanged();
    }

    void clearResults() {
        mScanResults.clear();
        notifyDataSetChanged();
    }

    class DeviceViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.name_text)
        TextView mNameText;
        @BindView(R.id.progressbar)
        ProgressBar mProgressBar;

        DeviceViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

        }

        private void bindView(ScanResult scanResult) {
            Timber.d("bindView");
            if (scanResult.getDevice() != null) {
                Timber.d("name: %s", scanResult.getDevice().getName());
                mNameText.setText(scanResult.getDevice().getName());
            }
        }
    }
}
