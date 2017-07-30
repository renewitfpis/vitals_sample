package sg.lifecare.data.remote.model.data;

import android.text.TextUtils;

import sg.lifecare.data.remote.LifecareUtils;

public class BodyWeightEventData extends EventData {

    private transient float mWeight;
    private transient String mUnit = "kg";

    public BodyWeightEventData() {
        super(LifecareUtils.EVENT_ID_BODY_WEIGHT, "Weight Update Data");
    }

    public float getWeight() {
        return mWeight;
    }

    public void setWeight(float weight) {
        mWeight = weight;
        updateExtraData();
    }


    @Override
    protected void updateExtraData() {
        StringBuilder sb = new StringBuilder();
        sb.append("Weight:").append(mWeight);
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
