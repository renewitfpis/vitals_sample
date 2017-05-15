package sg.lifecare.data.remote.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class ConnectedDeviceResponse extends Response {

    @SerializedName("Data")
    private List<ConnectedDeviceResponse.Data> data;

    @Override
    public List<Data> getData() {
        return data;
    }

    public class Data {

        private String _id;
        private Product product;
        private Date last_update;
        private String type;
        private int value;
        private String status;
        private String name;

        public String getName() {
            return name;
        }

        public boolean isOnline() {
            return "N".equalsIgnoreCase(status);
        }

        public int getValue() {
            return value;
        }

        public String getImageUrl() {
            if ((product != null) && (product.getMedias() != null) && (product.getMedias().size() > 0)) {
                return product.getMedias().get(0).getMediaUrl();
            }

            return "";
        }

        public Date getLastUpdate() {
            return last_update;
        }
    }

    class Product {
        private String _id;
        private List<Media> medias;
        private boolean gsm;
        private boolean battery_level_compatibitity;
        private boolean uptime_compatibility;
        private String name;

        List<Media> getMedias() {
            return medias;
        }
    }
}
