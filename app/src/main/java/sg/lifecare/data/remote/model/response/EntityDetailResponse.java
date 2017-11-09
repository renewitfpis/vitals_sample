package sg.lifecare.data.remote.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class EntityDetailResponse extends Response {

    @SerializedName("Data")
    private List<EntityDetailResponse.Data> data;

    @Override
    public List<EntityDetailResponse.Data> getData() {
        return data;
    }

    public class Data extends EntityData {
        // modules
        private List<RelatedEntity> related_entities;
        // installations
        // devices
        // addresses
        // phones
        private String government_id;
        private String an_name;
        private Date date_established;
        private String type;
        private String status;
        private String default_language;
        private Enterprise enterprise;
        private String referal_code;

        class RelatedEntity {
            private String _id;
            private String related_entity;
            private String entity;
            private Date create_date;
            private String type;
            private String status;
            private String type3;
        }

        class Enterprise {
            private String _id;
            private String name;
        }
    }
}
