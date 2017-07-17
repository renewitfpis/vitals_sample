package sg.lifecare.data.remote.model.response;

import java.util.Date;
import java.util.List;

public class EntityData {

    private String _id;
    private List<Response.Media> medias;
    private String name;
    private String first_name;
    private String last_name;
    private String location_name;
    private String authentication_string_lower;
    private int authorization_level;
    private double latitude;
    private double longitude;
    private List<Response.Phone> phones;
    private Date last_update;

    public String getId() {
        return _id;
    }

    public List<Response.Media> getMedias() {
        return medias;
    }

    public String getName() {
        return name;
    }

    public String getFirstName() {
        return first_name;
    }

    public String getLastName() {
        return last_name;
    }

    public String getLocationName() {
        return location_name;
    }

    public String getEmail() {
        return authentication_string_lower;
    }

    public List<Response.Phone> getPhones() {
        return phones;
    }

    public Date getLastUpdate() {
        return last_update;
    }

    public boolean hasValidLocation() {
        return (latitude != 0) && (longitude != 0);
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLocationName(String locationName){
        location_name = locationName;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) {
        this.longitude = latitude;
    }

    public String getDefaultMediaUrl() {
        if ((medias != null) && (medias.size() > 0)) {
            return medias.get(0).getMediaUrl();
        }

        return "";
    }

    public String getDefaultPhoneNumber() {
        if ((phones != null) && (phones.size() > 0)) {
            return phones.get(0).getPhoneDigits();
        }

        return "";
    }

    public int getAuthorizationLevel() {
        return authorization_level;
    }

    public boolean isNormalUser() {
        return 300 == authorization_level;
    }
}
