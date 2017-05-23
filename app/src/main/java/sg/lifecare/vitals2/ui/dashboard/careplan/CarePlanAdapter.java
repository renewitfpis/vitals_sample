package sg.lifecare.vitals2.ui.dashboard.careplan;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import sg.lifecare.data.remote.model.response.AssignedTaskResponse;
import sg.lifecare.utils.DateUtils;
import sg.lifecare.vitals2.R;
import sg.lifecare.vitals2.R2;

class CarePlanAdapter extends RecyclerView.Adapter<CarePlanAdapter.BaseHolder>{

    interface OnItemClickListener {
        void onItemClick(AssignedTaskResponse.Data task);
    }

    private OnItemClickListener mListener;
    private List<AssignedTaskResponse.Data> mTasks = new ArrayList<>();

    CarePlanAdapter(OnItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public BaseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == BaseHolder.TYPE_BLOOD_GLUCOSE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_careplan_blood_glucose,
                    parent, false);
            return new BloodGlucoseHolder(view);
        } else if (viewType == BaseHolder.TYPE_BLOOD_PRESSURE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_careplan_blood_pressure,
                    parent, false);
            return new BloodPressureHolder(view);
        } else if (viewType == BaseHolder.TYPE_WEIGHT_SCALE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_careplan_weight_scale,
                    parent, false);
            return new WeighScaleHolder(view);
        }

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_careplan,
                parent, false);
        return new BaseHolder(view, viewType);
    }

    @Override
    public int getItemViewType(int position) {
        AssignedTaskResponse.Data task = mTasks.get(position);

        if (task.isBloodGlucose()) {
            return BaseHolder.TYPE_BLOOD_GLUCOSE;
        } else if (task.isBloodPressure()) {
            return BaseHolder.TYPE_BLOOD_PRESSURE;
        } else if (task.isWeightScale()) {
            return BaseHolder.TYPE_WEIGHT_SCALE;
        }


        return BaseHolder.TYPE_UNDEFINED;
    }

    @Override
    public void onBindViewHolder(BaseHolder holder, int position) {
        AssignedTaskResponse.Data task = mTasks.get(position);

        holder.bindView(task);

        if (mListener != null) {
            holder.itemView.setOnClickListener(v -> {
                mListener.onItemClick(task);
            });
        }
    }

    @Override
    public int getItemCount() {
        return mTasks.size();
    }

    void replaceAll(List<AssignedTaskResponse.Data> tasks) {
        mTasks.clear();

        if ((tasks != null) && (tasks.size() > 0)) {
            sortByTime(tasks);
            mTasks.addAll(tasks);
        }
    }

    private void sortByTime(List<AssignedTaskResponse.Data> tasks) {
        Collections.sort(tasks, (first, second) -> {
            Date date1 = first.getStartDate();
            Date date2 = second.getStartDate();

            if (date1 != null && date2 != null) {
                return date2.compareTo(date1);
            }

            return 0;
        });
    }

    class BloodGlucoseHolder extends BaseHolder {

        @BindView(R.id.name_text)
        TextView mName;

        BloodGlucoseHolder(View itemView) {
            super(itemView, TYPE_BLOOD_GLUCOSE);

            ButterKnife.bind(this, itemView);
        }

        @Override
        void bindView(AssignedTaskResponse.Data task) {
            super.bindView(task);

            mName.setText(task.getSubject());
        }
    }

    class BloodPressureHolder extends BaseHolder {

        @BindView(R.id.name_text)
        TextView mName;

        BloodPressureHolder(View itemView) {
            super(itemView, TYPE_BLOOD_PRESSURE);

            ButterKnife.bind(this, itemView);
        }

        @Override
        void bindView(AssignedTaskResponse.Data task) {
            super.bindView(task);

            mName.setText(task.getSubject());
        }
    }

    class WeighScaleHolder extends BaseHolder {

        @BindView(R.id.name_text)
        TextView mName;

        WeighScaleHolder(View itemView) {
            super(itemView, TYPE_BLOOD_PRESSURE);

            ButterKnife.bind(this, itemView);
        }

        @Override
        void bindView(AssignedTaskResponse.Data task) {
            super.bindView(task);

            mName.setText(task.getSubject());
        }
    }

    class BaseHolder extends RecyclerView.ViewHolder {

        static final int TYPE_UNDEFINED = 0;
        static final int TYPE_BLOOD_GLUCOSE = 1;
        static final int TYPE_BLOOD_PRESSURE = 2;
        static final int TYPE_WEIGHT_SCALE = 3;

        final int type;

        @BindView(R2.id.time_text)
        TextView timeText;

        BaseHolder(View itemView, int type) {
            super(itemView);
            this.type = type;

            ButterKnife.bind(this, itemView);
        }

        void bindView(AssignedTaskResponse.Data task) {
            timeText.setText(DateUtils.getDisplayTime(task.getStartDate()));
        }
    }
}
