package sg.lifecare.data;

import android.content.Context;
import android.text.TextUtils;

import org.joda.time.DateTime;
import org.reactivestreams.Publisher;

import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.realm.Realm;
import io.realm.RealmResults;
import sg.lifecare.data.local.PreferencesHelper;
import sg.lifecare.data.local.database.AppDatabase;
import sg.lifecare.data.local.database.BloodGlucose;
import sg.lifecare.data.local.database.BloodPressure;
import sg.lifecare.data.local.database.BodyTemperature;
import sg.lifecare.data.local.database.Patient;
import sg.lifecare.data.remote.LifecareUtils;
import sg.lifecare.data.remote.model.data.BloodGlucoseEventData;
import sg.lifecare.data.remote.model.data.BloodPressureEventData;
import sg.lifecare.data.remote.model.data.BodyTemperatureEventData;
import sg.lifecare.data.remote.model.data.BodyWeightEventData;
import sg.lifecare.data.remote.model.data.EventData;
import sg.lifecare.data.remote.model.data.SpO2EventData;
import sg.lifecare.data.remote.model.response.AssignedTaskForDeviceResponse;
import sg.lifecare.data.remote.model.response.AssignedTaskResponse;
import sg.lifecare.data.remote.model.response.BloodGlucoseResponse;
import sg.lifecare.data.remote.model.response.BloodPressureResponse;
import sg.lifecare.data.remote.model.response.BodyTemperatureResponse;
import sg.lifecare.data.remote.model.response.BodyWeightResponse;
import sg.lifecare.data.remote.model.response.LogoutResponse;
import sg.lifecare.framework.di.ApplicationContext;
import sg.lifecare.utils.CookieUtils;
import sg.lifecare.utils.DateUtils;
import sg.lifecare.data.remote.LifecareService;
import sg.lifecare.data.remote.model.data.CommissionData;
import sg.lifecare.data.remote.model.response.AlertRuleResponse;
import sg.lifecare.data.remote.model.response.AssistsedEntityResponse;
import sg.lifecare.data.remote.model.response.CommissionDeviceResponse;
import sg.lifecare.data.remote.model.response.EntityData;
import sg.lifecare.data.remote.model.response.EntityDetailResponse;
import sg.lifecare.data.remote.model.response.LocationResponse;
import sg.lifecare.data.remote.model.response.LoginResponse;
import sg.lifecare.data.remote.model.response.RelatedAlertMessageResponse;
import sg.lifecare.data.remote.model.response.ResetPasswordResponse;
import sg.lifecare.data.remote.model.response.ServiceResponse;


@Singleton
public class DataManager {

    private final Context mContext;
    private final LifecareService mLifecareService;
    private final PreferencesHelper mPreferencesHelper;

    private final AppDatabase mAppDatabase;

    // login user entity detail
    private EntityDetailResponse.Data mUserEntity;

    // members entity detail
    private List<AssistsedEntityResponse.Data> mMembersEntity;

    // selected member (for navigation)
    private int mSelectedMemberPosition;

    @Inject
    public DataManager(@ApplicationContext Context context, LifecareService lifecareService,
                       PreferencesHelper preferencesHelper, AppDatabase appDatabase) {
        mContext = context;
        mLifecareService = lifecareService;
        mPreferencesHelper = preferencesHelper;
        mAppDatabase = appDatabase;
    }

    public Context getContext() {
        return mContext;
    }

    public PreferencesHelper getPreferencesHelper() {
        return mPreferencesHelper;
    }

    public EntityDetailResponse.Data getUserEntity() {
        return mUserEntity;
    }

    public List<AssistsedEntityResponse.Data> getMembersEntity() {
        return mMembersEntity;
    }

    public void setUserEntity(EntityDetailResponse.Data userEntity) {
        mUserEntity = userEntity;

        mPreferencesHelper.setUserEntity(userEntity);
    }

    public void setMembersEntity(List<AssistsedEntityResponse.Data> membersEntity) {
        mMembersEntity = membersEntity;
    }

    public void setSelectedMemberEntity(EntityData member) {

        mSelectedMemberPosition = -1;

        if (mMembersEntity != null) {
            for (int i = 0; i < mMembersEntity.size(); i++) {
                //Timber.d("setSelectedMemberEntity: i=%d, id1=%s, id2=%s", i,
                //        mMembersEntity.get(i).getId(), member.getId());
                if (TextUtils.equals(mMembersEntity.get(i).getId(), member.getId())) {
                    mSelectedMemberPosition = i;
                    break;
                }
            }
        }
    }

    public AssistsedEntityResponse.Data getSelectedMemberEntity() {
        if ((mMembersEntity != null) && (mSelectedMemberPosition >= 0) &&
                (mSelectedMemberPosition < mMembersEntity.size())) {
            return mMembersEntity.get(mSelectedMemberPosition);
        }

        return null;
    }

    public boolean loadOfflineData() {
        EntityDetailResponse.Data user = mPreferencesHelper.getUserEntity();
        if (user == null) {
            return false;
        }

        setUserEntity(user);

        if (LifecareUtils.isCaregiver(user.getAuthorizationLevel())) {
            List<AssistsedEntityResponse.Data> members = mPreferencesHelper.getMembersEntity();
            setMembersEntity(members);
        }

        return true;
    }

    public Observable<LoginResponse> login(final String email, final String password) {
        // need to clear the previous cookie
        CookieUtils.getCookieJar(mContext).clear();

        return mLifecareService.login(email, password, mPreferencesHelper.getFcmToken(),
                mPreferencesHelper.getDeviceId(), "A");
    }

    public Observable<LogoutResponse> logout() {
        return mLifecareService.logout(mPreferencesHelper.getDeviceId())
                .doOnComplete(() -> {
                   mPreferencesHelper.clear(mContext);
                });
    }

    public Observable<ResetPasswordResponse> resetPassword(final String email) {
        // need to clear the previous cookie
        CookieUtils.getCookieJar(mContext).clear();

        return mLifecareService.resetPassword(email);
    }

    public Observable<EntityDetailResponse> getEntityObservable(final String id) {
        return mLifecareService.getEntityDetail(id);
    }

    public Observable<AssistsedEntityResponse> getMembersEntityObservable(String entityId) {
        return mLifecareService.getAsisteds(entityId);
    }

    public Observable<LocationResponse> getLocations(String entityId, Calendar start, Calendar end) {
        return mLifecareService.getLocations(entityId, DateUtils.getIsoTimestamp(start),
                DateUtils.getIsoTimestamp(end));
    }

    public Observable<AlertRuleResponse> getRules(String entityId) {
        return mLifecareService.getAlertRules(entityId);
    }

    public Observable<ServiceResponse> getServices() {
        return mLifecareService.getServices();
    }

    public Observable<RelatedAlertMessageResponse> getRelatedAlertMessages(String entityId,
                                                                           int pageSize, int skipSize) {
        return mLifecareService.getRelatedAlertMessages(entityId, pageSize, skipSize);
    }

    public Observable<AssignedTaskResponse> getAssignedTasks(String entityId) {
        return mLifecareService.getAssignedTasks(entityId);
    }

    public Observable<CommissionDeviceResponse> postCommissionDevice(CommissionData data) {
        return mLifecareService.postCommissionDevice(data);
    }

    public Flowable<AssignedTaskForDeviceResponse> postAssignedTaskForDevice(BloodGlucoseEventData data) {
        return mLifecareService.postAssignedTaskForDevice(data);
    }

    public Flowable<AssignedTaskForDeviceResponse> postAssignedTaskForDevice(BloodPressureEventData data) {
        return mLifecareService.postAssignedTaskForDevice(data);
    }

    public Flowable<AssignedTaskForDeviceResponse> postAssignedTaskForDevice(BodyWeightEventData data) {
        return mLifecareService.postAssignedTaskForDevice(data);
    }

    public Flowable<AssignedTaskForDeviceResponse> postAssignedTaskForDevice(SpO2EventData data) {
        return mLifecareService.postAssignedTaskForDevice(data);
    }

    public Flowable<AssignedTaskForDeviceResponse> postAssignedTaskForDevice(BodyTemperatureEventData data) {
        return mLifecareService.postAssignedTaskForDevice(data);
    }

    public Flowable<BloodPressureResponse> getBloodPressures(String entityId, DateTime start,
            DateTime end) {
        return mLifecareService.getBloodPressures(entityId, LifecareUtils.convertToDayFormat(start),
                LifecareUtils.convertToDayFormat(end));
    }

    public Flowable<BloodGlucoseResponse> getBloodGlucoses(String entityId, DateTime start,
            DateTime end) {
        return mLifecareService.getBloodGlucoses(entityId, LifecareUtils.convertToDayFormat(start),
                LifecareUtils.convertToDayFormat(end));
    }

    public Flowable<BodyWeightResponse> getBodyWeights(String entityId, DateTime start, DateTime end) {
        return mLifecareService.getBodyWeights(entityId, LifecareUtils.convertToDayFormat(start),
                LifecareUtils.convertToDayFormat(end));
    }

    public Flowable<BodyTemperatureResponse> getBodyTemperatures(String entityId, DateTime start, DateTime end) {
        return mLifecareService.getBodyTemperatures(entityId, LifecareUtils.convertToDayFormat(start),
                LifecareUtils.convertToDayFormat(end));
    }

    /**************************************************************************
     * Database
     *************************************************************************/
    public Realm getRealm() {
        return mAppDatabase.getRealm();
    }
}
