package sg.lifecare.data.remote.model.response;


import java.util.Date;

public class EventData {

    private String _id;
    private Date create_date;
    private String event_type_id;
    private String event_type_name;
    private String extra_data;
    private String node_name;

    public Date getCreateDate() {
        return create_date;
    }

    public String getId() {
        return _id;
    }
}
