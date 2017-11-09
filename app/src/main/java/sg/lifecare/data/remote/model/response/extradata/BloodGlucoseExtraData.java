package sg.lifecare.data.remote.model.response.extradata;


public class BloodGlucoseExtraData extends ExtraData {

    private float Concentration;
    private String Type;
    private String Unit;

    public float getConcentration() {
        return Concentration;
    }

    public String getType() {
        return Type;
    }

    public String getUnit() {
        return Unit;
    }
}
