package sg.lifecare.data.local.database;

import android.text.TextUtils;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.Sort;
import io.realm.annotations.PrimaryKey;
import timber.log.Timber;

public class Patient extends RealmObject {

    @PrimaryKey
    private String id;
    private Date lastUpdateTime;

    private RealmList<BloodPressure> bloodPressures;

    public static RealmResults<Patient> getPatients(Realm realm) {
        return realm.where(Patient.class).findAllSortedAsync("lastUpdateTime", Sort.DESCENDING);
    }

    public static void addOrUpdatePatient(Realm realm, String patientId, Date upateTime) {
        Timber.d("addPatient: %s", patientId);
        if (!TextUtils.isEmpty(patientId)) {

            Patient patient = realm.where(Patient.class).equalTo("id", patientId).findFirst();

            if (patient == null) {
                patient = realm.createObject(Patient.class, patientId);
                if (upateTime != null) {
                    patient.setLastUpdateTime(upateTime);
                }
            } else {
                if (upateTime != null) {
                    if (patient.getLastUpdateTime() == null) {
                        patient.setLastUpdateTime(upateTime);
                    } else if (patient.getLastUpdateTime().before(upateTime)) {
                        patient.setLastUpdateTime(upateTime);
                    }
                }
            }
        }
    }

    public String getId() {
        return id;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setLastUpdateTime(Date time) {
        this.lastUpdateTime = time;
    }

}
