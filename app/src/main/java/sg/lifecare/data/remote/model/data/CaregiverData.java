package sg.lifecare.data.remote.model.data;

public class CaregiverData {

    private String AuthenticationString;
    private String EntityId;
    private String FirstName;
    private String LastName;
    private String Type3;
    private String Enterprise;

    public CaregiverData(String entityId) {
        EntityId = entityId;
    }

    public void setFirstName(String name) {
        FirstName = name;
    }

    public void setLastName(String name) {
        LastName = name;
    }

    public void setLevel(int level) {
        Type3 = String.valueOf(level);
    }

    public void setEnterprise(String enterprise) {
        Enterprise = enterprise;
    }

    public void setEmail(String email) {
        AuthenticationString = email;
    }
}
