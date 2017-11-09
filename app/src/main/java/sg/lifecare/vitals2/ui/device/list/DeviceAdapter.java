package sg.lifecare.vitals2.ui.device.list;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import sg.lifecare.data.local.DeviceData;
import sg.lifecare.vitals2.R;

class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder> {

    private ArrayList<DeviceData.Device> mDevices = new ArrayList<>();
    private OnItemLongClickListener mItemLongClickListener;

    interface OnItemLongClickListener {
        void onItemLongClick(DeviceData.Device device);
    }


    DeviceAdapter(OnItemLongClickListener listener) {
        mItemLongClickListener = listener;
    }

    @Override
    public DeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_device_device, parent, false);
        DeviceViewHolder viewHolder = new DeviceViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(DeviceViewHolder holder, int position) {
        final DeviceData.Device device = mDevices.get(position);

        holder.mNameText.setText(device.getName());
        holder.mIdText.setText(device.getId());

        if (mItemLongClickListener != null) {
            holder.itemView.setOnLongClickListener(v -> {
                if (mItemLongClickListener != null) {
                    mItemLongClickListener.onItemLongClick(device);
                }

                return true;
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDevices.size();
    }

    void replaceDevices(List<DeviceData.Device> devices) {
        mDevices.clear();
        mDevices.addAll(devices);

        notifyDataSetChanged();
    }

    class DeviceViewHolder extends RecyclerView.ViewHolder {

        TextView mNameText;
        TextView mIdText;

        DeviceViewHolder(View itemView) {
            super(itemView);

            mNameText = (TextView) itemView.findViewById(R.id.name_text);
            mIdText = (TextView) itemView.findViewById(R.id.id_text);
        }
    }
}

