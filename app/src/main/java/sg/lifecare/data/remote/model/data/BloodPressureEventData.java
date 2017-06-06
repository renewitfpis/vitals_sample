package sg.lifecare.data.remote.model.data;

import android.text.TextUtils;

import sg.lifecare.data.remote.LifecareUtils;

public class BloodPressureEventData extends EventData {

    private transient int mSystolic;
    private transient int mDiastolic;
    private transient int mPulse;
    private transient String mRemarks;

    public BloodPressureEventData() {
        super(LifecareUtils.EVENT_ID_BLOOD_PRESSURE, "BloodPressure Update Data");
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

    @Override
    protected void updateExtraData() {
        StringBuilder sb = new StringBuilder();
        sb.append("HighBlood:").append(mSystolic);
        sb.append("&LowBlood:").append(mDiastolic);
        sb.append("&HeartBeat:").append(mPulse);

        if (!TextUtils.isEmpty(mRemarks)) {
            sb.append("&Remarks:").append(mRemarks);
        }

        setExtraData(sb.toString());
    }
}
