package sg.lifecare.data.local.database;


import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Vital extends RealmObject {
    @PrimaryKey
    private String id;

    private boolean isAllUploaded;
    private Date mLastUploadTime;

    private RealmList<BloodGlucose> bloodGlucoses;
    private RealmList<BodyWeight> bodyWeights;
    private RealmList<BloodPressure> bloodPressures;

}
