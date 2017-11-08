package sg.lifecare.data.remote.model.data;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import sg.lifecare.data.remote.LifecareUtils;

public class BloodGlucoseEventData extends EventData {

    private transient float mConcentration;
    private transient String mRemarks;
    private transient String mType;   // before/after
    private transient String mUnit = "mmol/L";

    public BloodGlucoseEventData() {
        super(LifecareUtils.EVENT_ID_BLOOD_GLUCOSE, "Gluco Update Data");
    }

    public float getConcentration() {
        return mConcentration;
    }

    public void setConcentration(float concentration) {
        mConcentration = concentration;
        updateExtraData();
    }

    public void setMetricUnit() {
        mUnit = "mmol/L";
        updateExtraData();
    }

    public void setRemarks(@NonNull String remarks) {
        mRemarks = remarks;
        updateExtraData();
    }

    public void setTypeAfterMeal() {
        mType = "AfterMeal";
        updateExtraData();
    }

    public void setTypeBeforeMeal() {
        mType = "BeforeMeal";
        updateExtraData();
    }

    @Override
    protected void updateExtraData() {
        StringBuilder sb = new StringBuilder();
        sb.append("Concentration:").append(mConcentration);
        sb.append("&Unit:").append(mUnit);
        sb.append("&Type:").append(mType);

        if (!TextUtils.isEmpty(mRemarks)) {
            sb.append("&Remarks:").append(mRemarks);
        }

        setExtraData(sb.toString());
    }



}
