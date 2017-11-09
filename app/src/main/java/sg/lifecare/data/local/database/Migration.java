package sg.lifecare.data.local.database;


import io.realm.DynamicRealm;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

public class Migration implements RealmMigration {
    @Override
    public void migrate(DynamicRealm dynamicRealm, long oldVesion, long newVersion) {
        RealmSchema schema = dynamicRealm.getSchema();

        // migrate from version 0 to version 1
        if (oldVesion == 0) {
            oldVesion++;
        }
    }
}
