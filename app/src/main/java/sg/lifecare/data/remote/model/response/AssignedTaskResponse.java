package sg.lifecare.data.remote.model.response;


import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

import sg.lifecare.data.remote.LifecareUtils;
import sg.lifecare.data.remote.model.response.extradata.BloodGlucoseExtraData;
import sg.lifecare.data.remote.model.response.extradata.BloodPressureExtraData;
import sg.lifecare.data.remote.model.response.extradata.BodyWeightExtraData;
import sg.lifecare.data.remote.model.response.extradata.ExtraData;
import sg.lifecare.data.remote.model.response.extradata.SpO2ExtraData;
import timber.log.Timber;

public class AssignedTaskResponse extends Response {

    @SerializedName("Data")
    private List<AssignedTaskResponse.Data> data;

    @Override
    public List<AssignedTaskResponse.Data> getData() {
        return data;
    }

    public class Data {

        private String _id;
        private Date create_date;
        private Date end_date;
        private Entity entity;
        private Event event;
        private boolean is_repeat;
        private Date last_update;
        private Date start_date;
        private String status;  // P: pending, F: finished, O: overtime
        private String subject;
        private String type;    // C: check list to do, N: just notice, T: question or text input, D: device linked task
        private String type2;   // BP, SW, GM, PO

        public Event getEvent() {
            return event;
        }

        public String getId() {
            return _id;
        }

        public Date getLastUpdateDate() {
            return last_update;
        }

        public Date getStartDate() {
            return start_date;
        }

        public String getSubject() {
            return subject;
        }

        public boolean isCheckListTask() {
            return LifecareUtils.isCheckListTask(type);
        }

        public boolean isNoticeTask() {
            return LifecareUtils.isNoticeTask(type);
        }

        public boolean isQuestionTask() {
            return LifecareUtils.isQuestionTask(type);
        }

        public boolean isDeviceTask() {
            return LifecareUtils.isDeviceTask(type);
        }

        public boolean isBloodGlucose() {
            return LifecareUtils.isBloodGlucoseType(type2);
        }

        public boolean isBloodPressure() {
            return LifecareUtils.isBloodPressureType(type2);
        }

        public boolean isBodyWeight() {
            return LifecareUtils.isBodyWeightType(type2);
        }

        public boolean isSpo2() {
            return LifecareUtils.isSpO2Type(type2);
        }

    }

    public class Entity {
        private String _id;
        private String name;
    }

    public class Event {
        private String event_type_id;
        private JsonElement extra_data;

        private transient BloodGlucoseExtraData mBloodGlucoseExtraData;
        private transient BloodPressureExtraData mBloodPressureExtraData;
        private transient BodyWeightExtraData mBodyWeightExtraData;
        private transient SpO2ExtraData mSpO2ExtraData;

        public ExtraData getExtraData() {
            if (extra_data != null) {
                if (LifecareUtils.isBloodGlucoseEventId(event_type_id)) {
                    if (mBloodGlucoseExtraData == null) {
                        Gson gson = new Gson();
                        mBloodGlucoseExtraData = gson.fromJson(extra_data,
                                BloodGlucoseExtraData.class);
                    }
                    return mBloodGlucoseExtraData;
                } else if (LifecareUtils.isBloodPressureEventId(event_type_id)) {
                    if (mBloodPressureExtraData == null) {
                        Gson gson = new Gson();
                        mBloodPressureExtraData = gson.fromJson(extra_data,
                                BloodPressureExtraData.class);
                    }

                    return mBloodPressureExtraData;
                } else if (LifecareUtils.isBodyWeightEventId(event_type_id)) {
                    if (mBodyWeightExtraData == null) {
                        Gson gson = new Gson();
                        mBodyWeightExtraData = gson.fromJson(extra_data,
                                BodyWeightExtraData.class);
                    }

                    return mBodyWeightExtraData;
                } else if (LifecareUtils.isSpO2EventId(event_type_id)) {
                    if (mSpO2ExtraData == null) {
                        Gson gson = new Gson();
                        mSpO2ExtraData = gson.fromJson(extra_data,
                                SpO2ExtraData.class);
                    }
                    return mSpO2ExtraData;
                }
            }

            return null;
        }
    }

}
