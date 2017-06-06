package sg.lifecare.data.local;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import sg.lifecare.framework.di.ApplicationContext;
import sg.lifecare.utils.CookieUtils;
import timber.log.Timber;


@Singleton
public class PreferencesHelper {

    private static final String PREF_FILE_NAME = "smartears_app_pref_file";

    private static final String PREF_KEY_DEVICE_ID = "DEVICE_ID";
    private static final String PREF_KEY_FCM_TOKEN = "FCM_TOKEN";
    private static final String PREF_KEY_ENTITY_ID = "ENTITY_ID";

    private static final String GCM_SENDER_ID = "1076112719492";

    private DeviceData mDeviceData;

    private final SharedPreferences mPref;

    @Inject
    public PreferencesHelper(@ApplicationContext final Context context) {
        mPref = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        mDeviceData = DeviceData.getInstance(context);

        if (TextUtils.isEmpty(getDeviceId())) {
            setDeviceId(context);
        }

        if (TextUtils.isEmpty(getFcmToken())) {
            Observable.create(aSubscriber -> {
                InstanceID instanceID = InstanceID.getInstance(context);
                try {
                    String token = instanceID.getToken(GCM_SENDER_ID, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                    aSubscriber.onNext(token);
                } catch (IOException e) {
                    Timber.e(e, e.getMessage());
                    aSubscriber.onError(e);
                }
                aSubscriber.onComplete();
            })
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(token -> putFcmToken((String) token));
        }

    }

    public void clear(Context context) {
        CookieUtils.getCookieJar(context).clear();
        setEntityId("");
    }

    private void putFcmToken(String token) {
        mPref.edit().putString(PREF_KEY_FCM_TOKEN, token).apply();
    }

    public String getFcmToken() {
        return mPref.getString(PREF_KEY_FCM_TOKEN, null);
    }

    private void setDeviceId(Context context) {
        String id = InstanceID.getInstance(context).getId();
        mPref.edit().putString(PREF_KEY_DEVICE_ID, id).apply();
    }

    public String getDeviceId() {
        return mPref.getString(PREF_KEY_DEVICE_ID, null);
    }

    public String getEntityId() {
        return mPref.getString(PREF_KEY_ENTITY_ID, null);
    }

    public void setEntityId(String entityId) {
        mPref.edit().putString(PREF_KEY_ENTITY_ID, entityId).apply();
    }

    public DeviceData getDeviceData() {
        return mDeviceData;
    }

}