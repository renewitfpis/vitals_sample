package sg.lifecare.vitals2.di.module;

import android.app.Activity;
import android.content.Context;

import dagger.Module;
import dagger.Provides;
import io.reactivex.disposables.CompositeDisposable;
import sg.lifecare.framework.di.ActivityContext;
import sg.lifecare.framework.di.PerActivity;
import sg.lifecare.vitals2.ui.bloodpressure.BloodPressureDeviceMvpPresenter;
import sg.lifecare.vitals2.ui.bloodpressure.BloodPressureDeviceMvpView;
import sg.lifecare.vitals2.ui.bloodpressure.BloodPressureDevicePresenter;
import sg.lifecare.vitals2.ui.bloodpressure.BloodPressureManualMvpPresenter;
import sg.lifecare.vitals2.ui.bloodpressure.BloodPressureManualMvpView;
import sg.lifecare.vitals2.ui.bloodpressure.BloodPressureManualPresenter;
import sg.lifecare.vitals2.ui.bodyweight.BodyWeightDeviceMvpPresenter;
import sg.lifecare.vitals2.ui.bodyweight.BodyWeightDeviceMvpView;
import sg.lifecare.vitals2.ui.bodyweight.BodyWeightDevicePresenter;
import sg.lifecare.vitals2.ui.dashboard.DashboardMvpPresenter;
import sg.lifecare.vitals2.ui.dashboard.DashboardMvpView;
import sg.lifecare.vitals2.ui.dashboard.DashboardPresenter;
import sg.lifecare.vitals2.ui.bloodglucose.BloodGlucoseManualMvpPresenter;
import sg.lifecare.vitals2.ui.bloodglucose.BloodGlucoseManualMvpView;
import sg.lifecare.vitals2.ui.bloodglucose.BloodGlucoseManualPresenter;
import sg.lifecare.vitals2.ui.dashboard.careplan.CarePlanMvpPresenter;
import sg.lifecare.vitals2.ui.dashboard.careplan.CarePlanMvpView;
import sg.lifecare.vitals2.ui.dashboard.careplan.CarePlanPresenter;
import sg.lifecare.vitals2.ui.dashboard.nurse.NurseMainMvpPresenter;
import sg.lifecare.vitals2.ui.dashboard.nurse.NurseMainMvpView;
import sg.lifecare.vitals2.ui.dashboard.nurse.NurseMainPresenter;
import sg.lifecare.vitals2.ui.dashboard.nurse.NurseScanMvpPresenter;
import sg.lifecare.vitals2.ui.dashboard.nurse.NurseScanMvpView;
import sg.lifecare.vitals2.ui.dashboard.nurse.NurseScanPresenter;
import sg.lifecare.vitals2.ui.dashboard.patient.PatientMainMvpPresenter;
import sg.lifecare.vitals2.ui.dashboard.patient.PatientMainMvpView;
import sg.lifecare.vitals2.ui.dashboard.patient.PatientMainPresenter;
import sg.lifecare.vitals2.ui.dashboard.vital.VitalMvpPresenter;
import sg.lifecare.vitals2.ui.dashboard.vital.VitalMvpView;
import sg.lifecare.vitals2.ui.dashboard.vital.VitalPresenter;
import sg.lifecare.vitals2.ui.device.ble.jumper.JumperOximeterMvpPresenter;
import sg.lifecare.vitals2.ui.device.ble.jumper.JumperOximeterMvpView;
import sg.lifecare.vitals2.ui.device.ble.jumper.JumperOximeterPresenter;
import sg.lifecare.vitals2.ui.device.ble.jumper.JumperThermometerMvpPresenter;
import sg.lifecare.vitals2.ui.device.ble.jumper.JumperThermometerMvpView;
import sg.lifecare.vitals2.ui.device.ble.jumper.JumperThermometerPresenter;
import sg.lifecare.vitals2.ui.device.ble.urion.UrionMvpPresenter;
import sg.lifecare.vitals2.ui.device.ble.urion.UrionMvpView;
import sg.lifecare.vitals2.ui.device.ble.urion.UrionPresenter;
import sg.lifecare.vitals2.ui.device.list.DeviceListMvpPresenter;
import sg.lifecare.vitals2.ui.device.list.DeviceListMvpView;
import sg.lifecare.vitals2.ui.device.list.DeviceListPresenter;
import sg.lifecare.vitals2.ui.device.ble.and.ANDMvpPresenter;
import sg.lifecare.vitals2.ui.device.ble.and.ANDUC352MvpView;
import sg.lifecare.vitals2.ui.device.scanner.BleScannerMvpPresenter;
import sg.lifecare.vitals2.ui.device.scanner.BleScannerMvpView;
import sg.lifecare.vitals2.ui.device.scanner.BleScannerPresenter;
import sg.lifecare.vitals2.ui.login.ForgotPasswordMvpPresenter;
import sg.lifecare.vitals2.ui.login.ForgotPasswordMvpView;
import sg.lifecare.vitals2.ui.login.ForgotPasswordPresenter;
import sg.lifecare.vitals2.ui.login.LoginMvpPresenter;
import sg.lifecare.vitals2.ui.login.LoginMvpView;
import sg.lifecare.vitals2.ui.login.LoginPresenter;
import sg.lifecare.vitals2.ui.qn.QNMvpPresenter;
import sg.lifecare.vitals2.ui.qn.QNMvpView;
import sg.lifecare.vitals2.ui.qn.QNPresenter;

@Module
public class ActivityModule {

    private Activity mActivity;

    public ActivityModule(Activity activity) {
        mActivity = activity;
    }

    @Provides
    @ActivityContext
    Context provideContext() {
        return mActivity;
    }

    @Provides
    CompositeDisposable provideCompositeDisposible() {
        return new CompositeDisposable();
    }

    @Provides
    @PerActivity
    LoginMvpPresenter<LoginMvpView> provideLoginPresenter(LoginPresenter<LoginMvpView> presenter) {
        return presenter;
    }

    @Provides
    @PerActivity
    ForgotPasswordMvpPresenter<ForgotPasswordMvpView> provideForgotPasswordPresenter(
            ForgotPasswordPresenter<ForgotPasswordMvpView> presenter) {
        return presenter;
    }

    @Provides
    @PerActivity
    DashboardMvpPresenter<DashboardMvpView> provideDashboardPresenter(
            DashboardPresenter<DashboardMvpView> presenter) {
        return presenter;
    }

    @Provides
    @PerActivity
    CarePlanMvpPresenter<CarePlanMvpView> provideCarePlanPresenter(
            CarePlanPresenter<CarePlanMvpView> presenter) {
        return presenter;
    }

    @Provides
    @PerActivity
    BloodGlucoseManualMvpPresenter<BloodGlucoseManualMvpView> provideBloodGlucoseManualPresenter(
            BloodGlucoseManualPresenter<BloodGlucoseManualMvpView> presenter) {
        return presenter;
    }

    @Provides
    @PerActivity
    BloodPressureManualMvpPresenter<BloodPressureManualMvpView> provideBloodPressureManualPresenter(
            BloodPressureManualPresenter<BloodPressureManualMvpView> presenter) {
        return presenter;
    }

    @Provides
    @PerActivity
    BloodPressureDeviceMvpPresenter<BloodPressureDeviceMvpView> provideBloodPressureDevicePresenter(
            BloodPressureDevicePresenter<BloodPressureDeviceMvpView> presenter) {
        return presenter;
    }

    @Provides
    @PerActivity
    BodyWeightDeviceMvpPresenter<BodyWeightDeviceMvpView> provideBodyWeightDevicePresenter(
            BodyWeightDevicePresenter<BodyWeightDeviceMvpView> presenter) {
        return presenter;
    }

    @Provides
    @PerActivity
    DeviceListMvpPresenter<DeviceListMvpView> provideDeviceListPresenter(
            DeviceListPresenter<DeviceListMvpView> presenter) {
        return presenter;
    }

    @Provides
    @PerActivity
    BleScannerMvpPresenter<BleScannerMvpView> provideBleDeviceAddPresenter(
            BleScannerPresenter<BleScannerMvpView> presenter) {
        return presenter;
    }

    @Provides
    @PerActivity
    NurseScanMvpPresenter<NurseScanMvpView> provideNurseScanPresenter(
            NurseScanPresenter<NurseScanMvpView> presenter) {
        return presenter;
    }

    @Provides
    @PerActivity
    NurseMainMvpPresenter<NurseMainMvpView> provideNurseMainPresenter(
            NurseMainPresenter<NurseMainMvpView> presenter) {
        return presenter;
    }

    @Provides
    @PerActivity
    PatientMainMvpPresenter<PatientMainMvpView> providePatientMainPresenter(
            PatientMainPresenter<PatientMainMvpView> presenter) {
        return presenter;
    }


    @Provides
    @PerActivity
    QNMvpPresenter<QNMvpView> proviceQNPresenter(QNPresenter<QNMvpView> presenter) {
        return presenter;
    }

    @Provides
    @PerActivity
    UrionMvpPresenter<UrionMvpView> provideUrionPresenter(UrionPresenter<UrionMvpView> presenter) {
        return presenter;
    }

    @Provides
    @PerActivity
    JumperThermometerMvpPresenter<JumperThermometerMvpView> provideJumperThermometerPresenter(
            JumperThermometerPresenter<JumperThermometerMvpView> presenter) {
        return presenter;
    }

    @Provides
    @PerActivity
    JumperOximeterMvpPresenter<JumperOximeterMvpView> provideJumperOximeterPresenter(
            JumperOximeterPresenter<JumperOximeterMvpView> presenter) {
        return presenter;
    }

    @Provides
    @PerActivity
    VitalMvpPresenter<VitalMvpView> provideVitalPresenter(VitalPresenter<VitalMvpView> presenter) {
        return presenter;
    }
}
