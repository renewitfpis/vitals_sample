package sg.lifecare.data.local.database;


import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class User extends RealmObject {

    @PrimaryKey
    private String entityId;
    private String name;
    private String id;
    private int role;   // 1 = patient,  2 = nurse, 3 = doctor

    public String getEntityId() {
        return entityId;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setRole(int role) {
        this.role = role;
    }
}
