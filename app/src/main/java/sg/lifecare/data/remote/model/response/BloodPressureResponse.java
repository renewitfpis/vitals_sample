package sg.lifecare.data.remote.model.response;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BloodPressureResponse extends Response {

    @SerializedName("Data")
    private List<BloodPressureResponse.Data> data;

    @Override
    public List<BloodPressureResponse.Data> getData() {
        return data;
    }

    public class Data extends EventData {

        private int diastolic;
        private int heart_rate;
        private int systolic;

        public int getDiastolic() {
            return diastolic;
        }

        public int getPulse() {
            return heart_rate;
        }

        public int getSystolic() {
            return systolic;
        }
    }
}
