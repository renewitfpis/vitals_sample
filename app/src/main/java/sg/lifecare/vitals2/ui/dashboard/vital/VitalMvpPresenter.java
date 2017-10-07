package sg.lifecare.vitals2.ui.dashboard.vital;

import io.realm.Realm;
import sg.lifecare.ble.parser.BloodPressureMeasurement;
import sg.lifecare.ble.parser.BodyTemperatureMeasurement;
import sg.lifecare.ble.parser.BodyWeightMeasurement;
import sg.lifecare.data.remote.model.response.AssistsedEntityResponse;
import sg.lifecare.data.remote.model.response.EntityDetailResponse;
import sg.lifecare.framework.di.PerActivity;
import sg.lifecare.vitals2.ui.base.MvpPresenter;
import sg.lifecare.vitals2.ui.base.MvpView;

@PerActivity
public interface VitalMvpPresenter<V extends MvpView> extends MvpPresenter<V> {

    AssistsedEntityResponse.Data getMember(int position);

    EntityDetailResponse.Data getUser();

    Realm getDatabase();

    void postBloodPressureData(BloodPressureMeasurement bp, String nurseId, String patientId, String deviceId);

    void postBodyWeightData(BodyWeightMeasurement bw, String nurseId, String patientId, String deviceId);

    void postBodyTemperatureData(BodyTemperatureMeasurement bt, String nurseId, String patientId, String deviceId);
}
