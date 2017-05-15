package sg.lifecare.data.remote.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class RelatedAlertMessageResponse extends Response {

    @SerializedName("Data")
    private List<RelatedAlertMessageResponse.Data> data;

    @Override
    public List<RelatedAlertMessageResponse.Data> getData() {
        return data;
    }

    public class Data {
        private String _id;
        private Date create_date;
        private Entity entity;
        private String message;
        private Rule rule;
        private String subject;
        private String type;

        public String getEntityName() {
            return entity.name;
        }

        public String getEntityId() {
            return entity._id;
        }

        public String getMessage() {
            return message;
        }

        public Date getCreateDate() {
            return create_date;
        }

        class Entity {
            private String _id;
            private String first_name;
            private String last_name;
            private String name;
        }

        class Rule {
            private String _id;
            private String activity_name;
            private String activity_type;
            private int alert_duration;
            // alert_thresholds
            private String arm_state;
            // auto_disarm_auto
            private Date create_date;
            // current_escalation_level
            private int end_time;
            private String entity;
            // escalation
            private String identification;
            // int_value
            // itl_sync
            private String name;
            private int start_time;
            private String type;
            private String type2;
            private String zone;
        }
    }
}
