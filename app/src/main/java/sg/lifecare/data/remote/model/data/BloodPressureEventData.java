package sg.lifecare.data.remote.model.data;

import android.text.TextUtils;

import java.util.Date;

import sg.lifecare.data.remote.LifecareUtils;

public class BloodPressureEventData extends EventData {

    private transient int mSystolic;
    private transient int mDiastolic;
    private transient int mPulse;
    private transient String mNurseId;
    private transient String mPatientId;
    private transient Date mReadTime;
    private transient String mRemarks;

    public BloodPressureEventData() {
        super(LifecareUtils.EVENT_ID_BLOOD_PRESSURE, "BloodPressure Update Data");
    }

    public int getSystolic() {
        return mSystolic;
    }

    public int getDiastolic() {
        return mDiastolic;
    }

    public int getPulse() {
        return mPulse;
    }

    public String getNurseId() {
        return mNurseId;
    }

    public String getPatientId() {
        return mPatientId;
    }

    public Date getReadTime() {
        return mReadTime;
    }

    public String getRemarks() {
        return mRemarks;
    }

    public void setDistolic(int diastolic) {
        mDiastolic = diastolic;
        updateExtraData();
    }

    public void setPulse(int pulse) {
        mPulse = pulse;
        updateExtraData();
    }

    public void setRemarks(String remarks) {
        mRemarks = remarks;
        updateExtraData();
    }

    public void setSystolic(int systolic) {
        mSystolic = systolic;
        updateExtraData();
    }

    public void setReadTime(Date time) {
        mReadTime = time;
        updateExtraData();
    }

    public void setNurseId(String id) {
        mNurseId = id;
        updateExtraData();
    }

    public void setPatientId(String id) {
        mPatientId = id;
        updateExtraData();
    }

    @Override
    protected void updateExtraData() {
        StringBuilder sb = new StringBuilder();
        sb.append("HighBlood:").append(mSystolic);
        sb.append("&LowBlood:").append(mDiastolic);
        sb.append("&HeartBeat:").append(mPulse);

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
