package sg.lifecare.data.remote.model.response;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import timber.log.Timber;

public class BodyTemperatureResponse extends Response {

    @SerializedName("Data")
    private List<Data> data;

    @Override
    public List<Data> getData() {
        return data;
    }

    public class Data extends VitalEventData {

        private String temperature;

        public float getTemperature() {
            if (!TextUtils.isEmpty(temperature)) {
                try {
                    return Float.valueOf(temperature);
                } catch (NumberFormatException e) {
                    Timber.e(e.getMessage());
                }
            }
            return 0f;
        }
    }
}
