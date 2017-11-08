package sg.lifecare.vitals2.di.component;

import dagger.Component;
import sg.lifecare.framework.di.PerActivity;
import sg.lifecare.vitals2.TestActivity;
import sg.lifecare.vitals2.di.module.ActivityModule;
import sg.lifecare.vitals2.ui.bloodglucose.InoSmartFragment;
import sg.lifecare.vitals2.ui.bloodpressure.BloodPressureDeviceFragment;
import sg.lifecare.vitals2.ui.bloodpressure.BloodPressureManualFragment;
import sg.lifecare.vitals2.ui.bodyweight.BodyWeightDeviceFragment;
import sg.lifecare.vitals2.ui.dashboard.DashboardActivity;
import sg.lifecare.vitals2.ui.bloodglucose.BloodGlucoseManualFragment;
import sg.lifecare.vitals2.ui.dashboard.careplan.CarePlanFragment;
import sg.lifecare.vitals2.ui.dashboard.member.MemberListFragment;
import sg.lifecare.vitals2.ui.dashboard.nurse.NurseMainFragment;
import sg.lifecare.vitals2.ui.dashboard.nurse.NurseScanFragment;
import sg.lifecare.vitals2.ui.dashboard.patient.PatientMainFragment;
import sg.lifecare.vitals2.ui.dashboard.vital.VitalFragment;
import sg.lifecare.vitals2.ui.device.ble.BleDeviceAddFragment;
import sg.lifecare.vitals2.ui.device.list.DeviceListFragment;
import sg.lifecare.vitals2.ui.jumper.JumperOximeterFragment;
import sg.lifecare.vitals2.ui.bodytemperature.JumperThermometerFragment;
import sg.lifecare.vitals2.ui.login.ForgotPasswordFragment;
import sg.lifecare.vitals2.ui.login.LoginActivity;
import sg.lifecare.vitals2.ui.panic.PanicFragment;
import sg.lifecare.vitals2.ui.bodyweight.QNFragment;
import sg.lifecare.vitals2.ui.bloodpressure.UrionFragment;

@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(TestActivity testActivity);

    void inject(LoginActivity loginActivity);
    void inject(ForgotPasswordFragment forgotPasswordFragment);

    void inject(DashboardActivity dashboardActivity);
    void inject(CarePlanFragment carePlanFragment);

    void inject(BloodGlucoseManualFragment bloodGlucoseManualFragment);

    void inject(BodyWeightDeviceFragment bodyWeightDeviceFragment);

    void inject(BloodPressureManualFragment bloodPressureManualFragment);
    void inject(BloodPressureDeviceFragment bloodPressureDeviceFragment);

    void inject(DeviceListFragment deviceListFragment);
    void inject(BleDeviceAddFragment deviceAddFragment);

    void inject(NurseScanFragment nurseScanFragment);
    void inject(NurseMainFragment nurseMainFragment);
    void inject(PatientMainFragment patientMainFragment);

    void inject(PanicFragment panicFragment);

    void inject(QNFragment qnFragment);

    void inject(UrionFragment urionFragment);

    void inject(JumperThermometerFragment jumperFragment);
    void inject(JumperOximeterFragment jumperFragment);

    void inject(VitalFragment vitalFragment);

    void inject(MemberListFragment memberListFragment);

    void inject(InoSmartFragment inoSmartFragment);

}
