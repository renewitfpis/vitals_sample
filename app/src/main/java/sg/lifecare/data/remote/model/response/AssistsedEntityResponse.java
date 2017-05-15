package sg.lifecare.data.remote.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class AssistsedEntityResponse extends Response {

    @SerializedName("Data")
    private List<AssistsedEntityResponse.Data> data;

    @Override
    public List<AssistsedEntityResponse.Data> getData() {
        return data;
    }

    public class Data extends EntityData {
        private boolean approved;
        private Date date_established;
        private List<Device> devices;
        private boolean disabled;
        private int gps_int_value;
        private List<Module> modules;
        private String status;
        private String type;

        public int getGpsBatteryLevel() {
            return gps_int_value;
        }

    }

    class Device {
        private String _id;
        private String name;
        private String type;
    }

    class Module {
        private String _id;
        private String code;
        private String name;
    }
}


