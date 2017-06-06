package sg.lifecare.data.remote.model.response.extradata;


public class BloodPressureExtraData extends ExtraData {

    private int HeartBeat;
    private int HighBlood;
    private int LowBlood;

    public int getDiastolic() {
        return LowBlood;
    }

    public int getPulse() {
        return HeartBeat;
    }

    public int getSystolic() {
        return HighBlood;
    }
}
