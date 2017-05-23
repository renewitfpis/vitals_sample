package sg.lifecare.data.remote.model.response;


import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

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
        private boolean is_repeat;
        private Date last_update;
        private Date start_date;
        private String status;  // P: pending, F: finished, O: overtime
        private String subject;
        private String type;    // C: check list to do, N: just notice, T: question or text input, D: device linked task
        private String type2;   // BP, SW, GM, PO

        public String getId() {
            return _id;
        }

        public Date getStartDate() {
            return start_date;
        }

        public String getSubject() {
            return subject;
        }

        public boolean isBloodGlucose() {
            return "GM".equalsIgnoreCase(type2);
        }

        public boolean isBloodPressure() {
            return "BP".equalsIgnoreCase(type2);
        }

        public boolean isWeightScale() {
            return "SW".equalsIgnoreCase(type2);
        }

        public boolean isSpo2() {
            return "PO".equalsIgnoreCase(type2);
        }

    }

    public class Entity {
        private String _id;
        private String name;
    }
}
