package sg.lifecare.vitals2.ui.dashboard.patient;

import sg.lifecare.ble.parser.BloodPressureMeasurement;
import sg.lifecare.data.local.database.BloodPressure;
import sg.lifecare.framework.di.PerActivity;
import sg.lifecare.vitals2.ui.base.MvpPresenter;
import sg.lifecare.vitals2.ui.base.MvpView;

@PerActivity
public interface PatientMainMvpPresenter<V extends MvpView> extends MvpPresenter<V> {

    void postBloodPressureData(BloodPressureMeasurement bp, String nurseId, String patientId);

    BloodPressure getLatestBloodPressure(String patientId);
}
