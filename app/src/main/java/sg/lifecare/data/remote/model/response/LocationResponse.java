package sg.lifecare.data.remote.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class LocationResponse extends Response {

    @SerializedName("Data")
    private List<LocationResponse.Data> data;

    @Override
    public List<LocationResponse.Data> getData() {
        return data;
    }

    public class Data {
        private double latitude;
        private double longitude;
        private Date show_data;
        private String locationName;

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public Date getTimestamp() {
            return show_data;
        }

        public String getLocationName() {
            return locationName;
        }

        public void setLocationName(String name) {
            locationName = name;
        }
    }
}
