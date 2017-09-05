package sg.lifecare.data.local.database;


import java.util.Date;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;
import timber.log.Timber;

public class Migration implements RealmMigration {
    @Override
    public void migrate(DynamicRealm dynamicRealm, long oldVesion, long newVersion) {
        RealmSchema schema = dynamicRealm.getSchema();

        Timber.d("migrate: oldVersion=%d, newVersion=%d", oldVesion, newVersion);

        // migrate from version 0 to version 1
        if (oldVesion == 0) {

            // add BodyTemperature model
            RealmObjectSchema bodyTemperatureSchema = schema.create("BodyTemperature");
            bodyTemperatureSchema.addField("entityId", String.class);
            bodyTemperatureSchema.addField("deviceId", String.class);
            bodyTemperatureSchema.addField("temperature", Float.class);
            bodyTemperatureSchema.addField("takenTime", Date.class);
            bodyTemperatureSchema.addField("takerId", String.class);
            bodyTemperatureSchema.addField("patientId", String.class);
            bodyTemperatureSchema.addField("isUploaded", Boolean.class);
            bodyTemperatureSchema.addField("uploadedTime", Date.class);

            oldVesion++;
        } else if (oldVesion == 1) {
            RealmObjectSchema spo2Schema = schema.create("Spo2");
            spo2Schema.addField("entityId", String.class);
            spo2Schema.addField("deviceId", String.class);
            spo2Schema.addField("spo2", Integer.class);
            spo2Schema.addField("pulse", Integer.class);
            spo2Schema.addField("takenTime", Date.class);
            spo2Schema.addField("takerId", String.class);
            spo2Schema.addField("patientId", String.class);
            spo2Schema.addField("isUploaded", Boolean.class);
            spo2Schema.addField("uploadedTime", Date.class);

            oldVesion++;

        }
    }
}
