package sg.lifecare.data.remote.model.data;

import java.util.ArrayList;
import java.util.List;

import sg.lifecare.data.local.model.CommissionDevice;


public class CommissionData {

    private String CaregiverId;
    private String FirstName;
    private String LastName;
    private List<String> PlanIds;
    private List<SmartDevice> SmartDevices;

    public CommissionData(String caregiverId, String trackerId, String firstName,
                                String lastName, String phoneNumber, CommissionDevice commissionDevice) {
        CaregiverId = caregiverId;
        FirstName = firstName;
        LastName = lastName;

        PlanIds = new ArrayList<>();
        PlanIds.add(commissionDevice.getPlanId());

        Phone phone = new Phone(phoneNumber);
        SmartDevice smartDevice = new SmartDevice(commissionDevice.getProductId(), trackerId, phone);
        SmartDevices = new ArrayList<>();
        SmartDevices.add(smartDevice);

    }

    class SmartDevice {
        private String DeviceName;
        private List<Phone> Phones;
        private String ProductId;
        private String Serial;
        private String Zone;
        private String ZoneCode;

        SmartDevice(String productId, String serial, Phone phone) {
            ProductId = productId;
            Serial = serial;
            DeviceName = "Personal Tracker";
            Zone = "All Zones / Others";
            ZoneCode = "OT";

            Phones = new ArrayList<>();
            Phones.add(phone);
        }
    }

    class Phone {
        private String Code;
        private boolean OverridePrimaryPhone;
        private String CountryCode;
        private String Digits;
        private String PhoneDigits;
        private String Type2;

        Phone(String digits) {
            Code = "SG";
            OverridePrimaryPhone = true;
            CountryCode = "65";
            Digits = digits;
            PhoneDigits = "+" + CountryCode + Digits;
            Type2 = "M";
        }
    }

}
