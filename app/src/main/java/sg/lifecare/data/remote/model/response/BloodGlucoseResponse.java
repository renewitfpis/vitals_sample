package sg.lifecare.data.remote.model.response;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BloodGlucoseResponse extends Response {

    @SerializedName("Data")
    private List<BloodGlucoseResponse.Data> data;

    @Override
    public List<BloodGlucoseResponse.Data> getData() {
        return data;
    }

    public class Data extends VitalEventData {

        private float concentration;

        public float getConcentration() {
            return concentration;
        }
    }
}
