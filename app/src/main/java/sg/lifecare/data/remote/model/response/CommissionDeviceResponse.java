package sg.lifecare.data.remote.model.response;

import com.google.gson.annotations.SerializedName;

public class CommissionDeviceResponse extends Response {

    @SerializedName("Data")
    private String data;

    @Override
    public String getData() {
        return data;
    }
}
