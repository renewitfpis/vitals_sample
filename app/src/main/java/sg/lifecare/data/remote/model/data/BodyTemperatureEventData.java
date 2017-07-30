package sg.lifecare.data.remote.model.data;

import android.text.TextUtils;

import sg.lifecare.data.remote.LifecareUtils;

public class BodyTemperatureEventData extends EventData {

    private transient float mTemperature;
    private transient String mUnit = "°C";

    public BodyTemperatureEventData() {
        super(LifecareUtils.EVENT_ID_BODY_TEMPERATURE, "Temperature Update Data");
    }

    public float getTemperature() {
        return mTemperature;
    }

    public void setTemperature(float temperature) {
        mTemperature = temperature;
        updateExtraData();
    }

    @Override
    protected void updateExtraData() {
        StringBuilder sb = new StringBuilder();
        sb.append("Temperature:").append(mTemperature);
        sb.append("&Unit:").append(mUnit);

        if (!TextUtils.isEmpty(mRemarks)) {
            sb.append("&Remarks:").append(mRemarks);
        }

        if (!TextUtils.isEmpty(mNurseId)) {
            sb.append("&NurseId:").append(mNurseId);
        }

        if (!TextUtils.isEmpty(mPatientId)) {
            sb.append("&PatientId:").append(mPatientId);
        }

        if (mReadTime != null) {
            sb.append("&RecordTime:").append(getIsoTimestamp(mReadTime));
        }

        setExtraData(sb.toString());
    }
}
