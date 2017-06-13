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
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import sg.lifecare.data.remote.model.response.AssignedTaskResponse;
import sg.lifecare.data.remote.model.response.extradata.BloodGlucoseExtraData;
import sg.lifecare.data.remote.model.response.extradata.BloodPressureExtraData;
import sg.lifecare.data.remote.model.response.extradata.BodyWeightExtraData;
import sg.lifecare.data.remote.model.response.extradata.ExtraData;
import sg.lifecare.data.remote.model.response.extradata.SpO2ExtraData;
import sg.lifecare.utils.DateUtils;
import sg.lifecare.views.TimelineView;
import sg.lifecare.vitals2.R;
import sg.lifecare.vitals2.R2;
import timber.log.Timber;

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
        } else if (viewType == BaseHolder.TYPE_BODY_WEIGHT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_careplan_weight_scale,
                    parent, false);
            return new BodyWeightHolder(view);
        } else if (viewType == BaseHolder.TYPE_NOTICE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_careplan_notice,
                    parent, false);
            return new NoticeHolder(view);
        } else if (viewType == BaseHolder.TYPE_SPO2) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_careplan_spo2,
                    parent, false);
            return new SpO2Holder(view);
        }

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_careplan,
                parent, false);
        return new BaseHolder(view, viewType);
    }

    @Override
    public int getItemViewType(int position) {
        AssignedTaskResponse.Data task = mTasks.get(position);

        if (task.isDeviceTask()) {
            if (task.isBloodGlucose()) {
                return BaseHolder.TYPE_BLOOD_GLUCOSE;
            } else if (task.isBloodPressure()) {
                return BaseHolder.TYPE_BLOOD_PRESSURE;
            } else if (task.isBodyWeight()) {
                return BaseHolder.TYPE_BODY_WEIGHT;
            } else if (task.isSpo2()) {
                return BaseHolder.TYPE_SPO2;
            }
        } else if (task.isNoticeTask()) {
            return BaseHolder.TYPE_NOTICE;
        }


        return BaseHolder.TYPE_UNDEFINED;
    }

    @Override
    public void onBindViewHolder(BaseHolder holder, int position) {
        AssignedTaskResponse.Data task = mTasks.get(position);

        holder.bindView(task);
        holder.mTimelineView.initLine(TimelineView.getTimeLineViewType(position, getItemCount()));

        if (task.getEvent() != null) {
            Timber.d("extra_data %s", task.getEvent().getExtraData());
        }

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

    class NoticeHolder extends BaseHolder {
        @BindView(R.id.name_text)
        TextView mNameText;

        NoticeHolder(View itemView) {
            super(itemView, TYPE_BLOOD_GLUCOSE);

            ButterKnife.bind(this, itemView);
        }

        @Override
        void bindView(AssignedTaskResponse.Data task) {
            super.bindView(task);

            mNameText.setText(task.getSubject());
        }
    }

    class SpO2Holder extends BaseHolder {
        @BindView(R.id.name_text)
        TextView mNameText;

        @BindView(R.id.value_text)
        TextView mValueText;

        @BindView(R.id.unit_text)
        TextView mUnitText;

        @BindView(R.id.timestamp_text)
        TextView mTimestampText;

        SpO2Holder(View itemView) {
            super(itemView, TYPE_BLOOD_GLUCOSE);

            ButterKnife.bind(this, itemView);
        }

        @Override
        void bindView(AssignedTaskResponse.Data task) {
            super.bindView(task);

            mNameText.setText(task.getSubject());

            mValueText.setVisibility(View.GONE);
            mUnitText.setVisibility(View.GONE);
            mTimestampText.setVisibility(View.GONE);

            AssignedTaskResponse.Event event = task.getEvent();
            if (event != null) {
                ExtraData extraData = event.getExtraData();

                if ((extraData != null) && (extraData instanceof SpO2ExtraData)) {
                    SpO2ExtraData data = (SpO2ExtraData) extraData;
                    mValueText.setText(String.format(Locale.getDefault(), "%d", data.getSpO2()));
                    mUnitText.setText(R.string.unit_percent);
                    mTimestampText.setText(DateUtils.getLocalDisplayTime(task.getLastUpdateDate()));

                    mValueText.setVisibility(View.VISIBLE);
                    mUnitText.setVisibility(View.VISIBLE);
                    mTimestampText.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    class BloodGlucoseHolder extends BaseHolder {

        @BindView(R.id.name_text)
        TextView mNameText;

        @BindView(R.id.value_text)
        TextView mValueText;

        @BindView(R.id.unit_text)
        TextView mUnitText;

        @BindView(R.id.timestamp_text)
        TextView mTimestampText;

        BloodGlucoseHolder(View itemView) {
            super(itemView, TYPE_BLOOD_GLUCOSE);

            ButterKnife.bind(this, itemView);
        }

        @Override
        void bindView(AssignedTaskResponse.Data task) {
            super.bindView(task);

            mNameText.setText(task.getSubject());

            mValueText.setVisibility(View.GONE);
            mUnitText.setVisibility(View.GONE);
            mTimestampText.setVisibility(View.GONE);

            AssignedTaskResponse.Event event = task.getEvent();
            if (event != null) {
                ExtraData extraData = event.getExtraData();

                if ((extraData != null) && (extraData instanceof BloodGlucoseExtraData)) {
                    BloodGlucoseExtraData data = (BloodGlucoseExtraData) extraData;
                    mValueText.setText(String.format(Locale.getDefault(), "%.1f", data.getConcentration()));
                    mUnitText.setText(R.string.unit_mmol_l);
                    mTimestampText.setText(DateUtils.getLocalDisplayTime(task.getLastUpdateDate()));

                    mValueText.setVisibility(View.VISIBLE);
                    mUnitText.setVisibility(View.VISIBLE);
                    mTimestampText.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    class BloodPressureHolder extends BaseHolder {

        @BindView(R.id.name_text)
        TextView mName;

        @BindView(R.id.stolic_text)
        TextView mStolicText;

        @BindView(R.id.stolic_unit_text)
        TextView mStolicUnitText;

        @BindView(R.id.pulse_text)
        TextView mPulseText;

        @BindView(R.id.pulse_unit_text)
        TextView mPulseUnitText;

        @BindView(R.id.timestamp_text)
        TextView mTimestampText;

        BloodPressureHolder(View itemView) {
            super(itemView, TYPE_BLOOD_PRESSURE);

            ButterKnife.bind(this, itemView);
        }

        @Override
        void bindView(AssignedTaskResponse.Data task) {
            super.bindView(task);

            mName.setText(task.getSubject());

            mStolicText.setVisibility(View.GONE);
            mStolicUnitText.setVisibility(View.GONE);
            mPulseText.setVisibility(View.GONE);
            mPulseUnitText.setVisibility(View.GONE);
            mTimestampText.setVisibility(View.GONE);

            AssignedTaskResponse.Event event = task.getEvent();
            if (event != null) {
                ExtraData extraData = event.getExtraData();

                if ((extraData != null) && (extraData instanceof BloodPressureExtraData)) {
                    BloodPressureExtraData data = (BloodPressureExtraData) extraData;
                    mStolicText.setText(String.format(Locale.getDefault(), "%d/%d", data.getSystolic(), data.getDiastolic()));
                    mStolicUnitText.setText(R.string.unit_mmhg);
                    mPulseText.setText(String.format(Locale.getDefault(), "%d", data.getPulse()));
                    mPulseUnitText.setText(R.string.unit_per_min);
                    mTimestampText.setText(DateUtils.getLocalDisplayTime(task.getLastUpdateDate()));

                    mStolicText.setVisibility(View.VISIBLE);
                    mStolicUnitText.setVisibility(View.VISIBLE);
                    mPulseText.setVisibility(View.VISIBLE);
                    mPulseUnitText.setVisibility(View.VISIBLE);
                    mTimestampText.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    class BodyWeightHolder extends BaseHolder {

        @BindView(R.id.name_text)
        TextView mName;

        @BindView(R.id.value_text)
        TextView mValueText;

        @BindView(R.id.unit_text)
        TextView mUnitText;

        @BindView(R.id.timestamp_text)
        TextView mTimestampText;

        BodyWeightHolder(View itemView) {
            super(itemView, TYPE_BLOOD_PRESSURE);

            ButterKnife.bind(this, itemView);
        }

        @Override
        void bindView(AssignedTaskResponse.Data task) {
            super.bindView(task);

            mName.setText(task.getSubject());

            mValueText.setVisibility(View.GONE);
            mUnitText.setVisibility(View.GONE);
            mTimestampText.setVisibility(View.GONE);

            AssignedTaskResponse.Event event = task.getEvent();
            if (event != null) {
                ExtraData extraData = event.getExtraData();

                if ((extraData != null) && (extraData instanceof BodyWeightExtraData)) {
                    BodyWeightExtraData data = (BodyWeightExtraData) extraData;
                    mValueText.setText(String.format(Locale.getDefault(), "%.1f", data.getWeight()));
                    mUnitText.setText(R.string.unit_kg);
                    mTimestampText.setText(DateUtils.getLocalDisplayTime(task.getLastUpdateDate()));

                    mValueText.setVisibility(View.VISIBLE);
                    mUnitText.setVisibility(View.VISIBLE);
                    mTimestampText.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    class BaseHolder extends RecyclerView.ViewHolder {

        static final int TYPE_UNDEFINED = 0;
        static final int TYPE_BLOOD_GLUCOSE = 1;
        static final int TYPE_BLOOD_PRESSURE = 2;
        static final int TYPE_BODY_WEIGHT = 3;
        static final int TYPE_SPO2 = 4;
        static final int TYPE_NOTICE = 5;

        final int type;

        @BindView(R2.id.time_text)
        TextView timeText;

        @BindView(R2.id.timeline_view)
        TimelineView mTimelineView;

        BaseHolder(View itemView, int type) {
            super(itemView);
            this.type = type;

            ButterKnife.bind(this, itemView);
        }

        void bindView(AssignedTaskResponse.Data task) {
            timeText.setText(DateUtils.getLocalDisplayTime(task.getStartDate()));
        }
    }
}
