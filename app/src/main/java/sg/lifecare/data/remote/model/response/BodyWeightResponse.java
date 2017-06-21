package sg.lifecare.data.remote.model.response;


import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import timber.log.Timber;

public class BodyWeightResponse extends Response {

    @SerializedName("Data")
    private List<BodyWeightResponse.Data> data;

    @Override
    public List<Data> getData() {
        return data;
    }

    public class Data extends EventData {

        private String weight;

        public float getWeight() {
            if (!TextUtils.isEmpty(weight)) {
                try {
                    return Float.valueOf(weight);
                } catch (NumberFormatException e) {
                    Timber.e(e.getMessage());
                }
            }
            return 0f;
        }
    }
}
