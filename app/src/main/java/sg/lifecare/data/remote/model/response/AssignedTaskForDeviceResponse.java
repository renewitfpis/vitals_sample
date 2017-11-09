package sg.lifecare.data.remote.model.response;


import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class AssignedTaskForDeviceResponse extends Response {

    @SerializedName("Data")
    private Data data;

    @Override
    public AssignedTaskForDeviceResponse.Data getData() {
        return data;
    }


    public class Data {

        private String _id;
        private Date create_date;

        public String getId() {
            return _id;
        }

        public Date getCreateDate() {
            return create_date;
        }
    }
}
