package sg.lifecare.data.remote.model.response;


import com.google.gson.annotations.SerializedName;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

public class ServiceResponse extends Response {

    public static final String MEDICAL_CARE = "MC";
    public static final String HOME_CARE = "HC";
    public static final String CLEANING = "CL";
    public static final String COOKING = "CK";

    @SerializedName("Data")
    private List<ServiceResponse.Data> data;

    @Override
    public List<ServiceResponse.Data> getData() {
        return data;
    }

    public static List<ServiceResponse.Data> findServicesByType(List<ServiceResponse.Data> services, String type) {
        Timber.d("findServicesByType: type=%s", type);
        List<ServiceResponse.Data> list = new ArrayList<>();
        if ((services != null) && (services.size() > 0) && !TextUtils.isEmpty(type)) {
            for (ServiceResponse.Data service : services) {
                //Timber.d("findServicesByType: type2=%s", service.type2);
                if (type.equals(service.type2)) {
                    list.add(service);
                }
            }
        }

        return list;
    }

    public class Data {
        private String _id;
        private int amount;
        private String code;
        private Date create_date;
        private String currency;
        private String description;
        private Enterprise enterprise;
        private String interval;
        private int interval_count;
        private Date last_update;
        private String name;
        private int rating;
        private int rating_times;
        // services
        // status
        private String tag;
        private String type;
        private String type2;

        public int getAmount() {
            return amount;
        }

        public String getCurrency() {
            return currency;
        }

        public String getDescription() {
            return description;
        }

        public String getId() {
            return _id;
        }

        public int getIntervalCount() {
            return interval_count;
        }

        public String getName() {
            return name;
        }

        public int getRating() {
            return rating;
        }

        public int getRatingTimes() {
            return rating_times;
        }

        public String getDefaultEnterpriseMediaUrl() {
            if (enterprise != null) {
                if ((enterprise.medias != null) && (enterprise.medias.size() > 0)) {
                    return enterprise.medias.get(0).getMediaUrl();
                }
            }
            return "";
        }

        public boolean isMedicalCare() {
            return MEDICAL_CARE.equals(type2);
        }

        public boolean isHomeCare() {
            return HOME_CARE.equals(type2);
        }

        public boolean isCleaning() {
            return CLEANING.equals(type2);
        }

        public boolean isCooking() {
            return COOKING.equals(type2);
        }
    }

    class Enterprise {
        private String _id;
        // cards
        private String could_access;
        private String code;
        // code2
        // code3
        // code4
        // code5
        // code6
        // code7
        // code8
        private Date create_date;
        // description
        // devices
        // entities
        // host
        private Date last_update;
        private List<Media> medias;
        // modules
        private List<String> related_enterprises;
        // supplied_products
        // type
        private List<String> types;
        // urls

    }
}
