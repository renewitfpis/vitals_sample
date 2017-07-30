package sg.lifecare.data.remote;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.JsonDeserializer;

import android.content.Context;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import sg.lifecare.data.remote.model.data.AcknowledgeData;
import sg.lifecare.data.remote.model.data.BloodGlucoseEventData;
import sg.lifecare.data.remote.model.data.BloodPressureEventData;
import sg.lifecare.data.remote.model.data.BodyTemperatureEventData;
import sg.lifecare.data.remote.model.data.BodyWeightEventData;
import sg.lifecare.data.remote.model.data.CaregiverData;
import sg.lifecare.data.remote.model.data.CommissionData;
import sg.lifecare.data.remote.model.data.ProfileData;
import sg.lifecare.data.remote.model.data.SpO2EventData;
import sg.lifecare.data.remote.model.response.AcknowledgeResponse;
import sg.lifecare.data.remote.model.response.AddAlertRuleResponse;
import sg.lifecare.data.remote.model.response.AlertRuleResponse;
import sg.lifecare.data.remote.model.response.AssignedTaskForDeviceResponse;
import sg.lifecare.data.remote.model.response.AssignedTaskResponse;
import sg.lifecare.data.remote.model.response.AssistsedEntityResponse;
import sg.lifecare.data.remote.model.response.BloodGlucoseResponse;
import sg.lifecare.data.remote.model.response.BloodPressureResponse;
import sg.lifecare.data.remote.model.response.BodyWeightResponse;
import sg.lifecare.data.remote.model.response.CommissionDeviceResponse;
import sg.lifecare.data.remote.model.response.ConnectedDeviceResponse;
import sg.lifecare.data.remote.model.response.EntityDetailResponse;
import sg.lifecare.data.remote.model.response.InviteCaregiverResponse;
import sg.lifecare.data.remote.model.response.LocationResponse;
import sg.lifecare.data.remote.model.response.LoginResponse;
import sg.lifecare.data.remote.model.response.LogoutResponse;
import sg.lifecare.data.remote.model.response.RegisterAccountResponse;
import sg.lifecare.data.remote.model.response.RelatedAlertMessageResponse;
import sg.lifecare.data.remote.model.response.ResetPasswordResponse;
import sg.lifecare.data.remote.model.response.Response;
import sg.lifecare.data.remote.model.response.ServiceResponse;
import sg.lifecare.data.remote.model.response.UpdateProfileResponse;
import sg.lifecare.utils.CookieUtils;
import sg.lifecare.vitals2.BuildConfig;
import timber.log.Timber;

public interface LifecareService {

    String ENDPOINT = "https://www.lifecare.sg";

    @FormUrlEncoded
    @POST("mlifecare/authentication/appLogin")
    Observable<LoginResponse> login(@Field("AuthenticationString") String username,
                                    @Field("Password") String password,
                                    @Field("Token") String fcm,
                                    @Field("DeviceId") String deviceId,
                                    @Field("DeviceType") String deviceType);
    @FormUrlEncoded
    @POST("mlifecare/authentication/appLogout")
    Observable<LogoutResponse> logout(@Field("DeviceId") String deviceId);

    @FormUrlEncoded
    @POST("mlifecare/authentication/forgotPasswordRequest")
    Observable<ResetPasswordResponse> resetPassword(@Field("AuthenticationString") String email);

    @FormUrlEncoded
    @POST("mlifecare/authentication/smarthomeUserSignup")
    Observable<RegisterAccountResponse> registerAccount(@Field("FirstName") String firstname,
                                                        @Field("LastName") String lastname,
                                                        @Field("Password") String password,
                                                        @Field("AuthenticationString") String email);

    @GET("mlifecare/entity/getEntityDetail")
    Observable<EntityDetailResponse> getEntityDetail(@Query("EntityId") String entityId);

    @GET("mlifecare/entityRelationship/getAssisted")
    Observable<AssistsedEntityResponse> getAsisteds(@Query("CaregiverId") String entityId);

    @GET("/mlifecare/message/getRelatedAlertMessages")
    Observable<RelatedAlertMessageResponse> getRelatedAlertMessages(@Query("EntityId")String entityId,
                                                                    @Query("PageSize")int pageSize,
                                                                    @Query("SkipSize")int skipSize);

    @GET("/mlifecare/device/getConnectedSmartDevices")
    Observable<ConnectedDeviceResponse> getConnectedDevices(@Query("EntityId")String entityId);

    @GET("/mlifecare/rule/getRules")
    Observable<AlertRuleResponse> getAlertRules(@Query("EntityId")String entityId);

    @GET("/atthings/event/getLocationData")
    Observable<LocationResponse> getLocations(@Query("EntityId") String entityId,
                                              @Query("StartDateTime") String startDate,
                                              @Query("EndDateTime") String endDate);

    @GET("/mlifecare/service/getServicesDetails")
    Observable<ServiceResponse> getServices();

    @GET("/atthings/taskAssign/getTaskAssign")
    Observable<AssignedTaskResponse> getAssignedTasks(@Query("EntityId") String entityId);

    @POST("/mlifecare/device/commission")
    Observable<CommissionDeviceResponse> postCommissionDevice(@Body CommissionData post);

    @FormUrlEncoded
    @POST("/mlifecare/rule/addRule")
    Observable<AddAlertRuleResponse> postAddRule(@Field("EntityId") String entityId,
                                                 @Field("Latitude") double latitude,
                                                 @Field("Longitude") double longitude,
                                                 @Field("IntValue") int radius,
                                                 @Field("StartTime") String startTime,
                                                 @Field("EndTime") String endTime,
                                                 @Field("ArmState") String armState,
                                                 @Field("ActivityType") String activityType,
                                                 @Field("Name") String name,
                                                 @Field("Type") String type);

    @POST("/mlifecare/entity/updateUserProfile")
    Observable<UpdateProfileResponse> postUpdateProfile(@Body ProfileData post);

    @POST("/mlifecare/entityRelationship/inviteAssistance")
    Observable<InviteCaregiverResponse> postInviteCaregiver(@Body CaregiverData post);

    @POST("/mlifecare/rule/acknowledgeRule")
    Observable<AcknowledgeResponse> postAcknowledge(@Body AcknowledgeData post);

    @GET("/mlifecare/event/getBloodPressureReading")
    Flowable<BloodPressureResponse> getBloodPressures(
            @Query("EntityId") String entityId,
            @Query("StartDay") String startDate,
            @Query("EndDay") String endDay);

    @GET("/mlifecare/event/getGlucoseReading")
    Flowable<BloodGlucoseResponse> getBloodGlucoses(
            @Query("EntityId") String entityId,
            @Query("StartDay") String startDate,
            @Query("EndDay") String endDay);

    @GET("/mlifecare/event/getWeighingScaleReading")
    Flowable<BodyWeightResponse> getBodyWeights(
            @Query("EntityId") String entityId,
            @Query("StartDay") String startDate,
            @Query("EndDay") String endDay);

    @POST("/mlifecare/event/addEvent")
    Observable<AssignedTaskForDeviceResponse> postAssignedTaskForDevice(@Body BloodGlucoseEventData post);

    @POST("/mlifecare/event/addEvent")
    Observable<AssignedTaskForDeviceResponse> postAssignedTaskForDevice(@Body BloodPressureEventData post);

    @POST("/mlifecare/event/addEvent")
    Observable<AssignedTaskForDeviceResponse> postAssignedTaskForDevice(@Body BodyWeightEventData post);

    @POST("/mlifecare/event/addEvent")
    Observable<AssignedTaskForDeviceResponse> postAssignedTaskForDevice(@Body SpO2EventData post);

    @POST("/mlifecare/event/addEvent")
    Observable<AssignedTaskForDeviceResponse> postAssignedTaskForDevice(@Body BodyTemperatureEventData post);

    /**
     * Factory class that sets up new Lifecare Service
     */
    class Factory {

        public static LifecareService makeLifecareService(Context context) {
            Timber.d("makeLifecareService");
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY
                    : HttpLoggingInterceptor.Level.NONE);
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .cookieJar(CookieUtils.getCookieJar(context))
                    .addInterceptor(new UnauthorizedInterceptor())
                    .addNetworkInterceptor(new StethoInterceptor())
                    .addInterceptor(logging)
                    .build();

            Gson gson = new GsonBuilder()
                    .serializeNulls()
                    .registerTypeAdapter(Date.class,
                            (JsonDeserializer<Date>) (jsonElement, type, jsonDeserializationContext) -> {
                                if (jsonElement == null) {
                                    return null;
                                }

                                DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                                return formatter.parseDateTime(jsonElement.getAsString()).toDate();
                            })
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                    .create();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(LifecareService.ENDPOINT)
                    .client(okHttpClient)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

            return retrofit.create(LifecareService.class);

        }
    }
}
