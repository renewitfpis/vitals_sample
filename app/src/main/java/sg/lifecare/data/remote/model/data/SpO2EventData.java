package sg.lifecare.data.remote.model.data;


import android.text.TextUtils;

import sg.lifecare.data.remote.LifecareUtils;

public class SpO2EventData extends EventData {

    private transient int mPulse;
    private transient int mSpO2;
    private transient double mPi;
    private transient String mRemarks;

    public SpO2EventData() {
        super(LifecareUtils.EVENT_ID_SPO2, "SpO2 Data");
    }

    public int getSpO2() {
        return mSpO2;
    }

    public int getPulse() {
        return mPulse;
    }

    public double getPi() {
        return mPi;
    }

    public void setPulse(int pulse) {
        mPulse = pulse;
        updateExtraData();
    }

    public void setRemarks(String remarks) {
        mRemarks = remarks;
        updateExtraData();
    }

    public void setSpO2(int spO2) {
        mSpO2 = spO2;
        updateExtraData();
    }

    public void setPi(double pi) {
        mPi = pi;
        updateExtraData();
    }

    @Override
    protected void updateExtraData() {
        StringBuilder sb = new StringBuilder();
        sb.append("Pulse:").append(mPulse);
        sb.append("&SpO2:").append(mSpO2);
        sb.append("&pi:").append(mPi);

        if (!TextUtils.isEmpty(mRemarks)) {
            sb.append("&Remarks:").append(mRemarks);
        }

        setExtraData(sb.toString());
    }
}
