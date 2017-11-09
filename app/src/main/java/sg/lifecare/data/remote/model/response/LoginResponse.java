package sg.lifecare.data.remote.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class LoginResponse extends Response {

    @SerializedName("Data")
    private LoginResponse.Data data;

    @Override
    public LoginResponse.Data getData() {
        return data;
    }

    public class Data  {
        private String _id;
        private List<RelatedEntity> related_entities;
        private List<Module> modules;
        private List<Device> devices;
        // medias
        // addresses
        // phones
        private String an_name;
        private boolean disabled;
        private Date date_established;
        private String type;
        private boolean approved;
        private String status;
        private String name;
        private String last_name;
        private String first_name;
        private int authorization_level;
        private String authentication_string_lower;
        private Enterprise enterprise;
        private String referral_code;

        public String getId() {
            return _id;
        }

    }

    class RelatedEntity {
        private String _id;
        private List<RelatedRelatedEntity> related_entities;
    }

    class RelatedRelatedEntity {
        private String _id;
        private List<String> modules;
        private String name;
        private String authentication_string;
    }

    class Device {
        private String _id;
        private String entity;
        private String zone;
        private String product;
        private String hub;
        // phones
        // devices
        private String secret;
        private String prefix;
        private String secured;
        private String token;
        private Date last_update;
        private Date create_date;
        private Date removal_date;
        private Date deployment_date;
        private String description;
        private String type2;
        private String type;
        private String value;
        private String status;
        private String name;
        private String enterprise;
    }

    class Enterprise {
        private String _id;
        private List<String> supplied_product;
        private List<String> products;
        private List<Media> medias;
        private List<String> cards;
        private List<String> devices;
        private List<String> entities;
        // url
        private String host;
        private Date last_update;
        private Date create_date;
        private String description;
        private String type;
        private String code2;
        private String code;
        private String name;
        private List<String> types;
        private List<String> related_enterprises;
        private List<String> addresses;
    }

    class Media {
        private String _id;
        private String media_url;
        private String type;
        private String title;
    }
}
