package sg.lifecare.data.remote.model.response;


import java.util.Date;

public class VitalEventData extends EventData {

    private String patient_id;
    private String nurse_id;
    private Date record_time1; // invalid date format

    public String getPatientId() {
        return patient_id;
    }

    public String getTakerId() {
        return nurse_id;
    }

    public Date getTakenTime() {
        return record_time1;
    }
}
