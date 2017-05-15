package sg.lifecare.data.remote.model.data;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import sg.lifecare.data.remote.LifecareConfig;
import sg.lifecare.data.remote.model.response.EntityData;


public class ProfileData {

    private String EntityId;
    private String FirstName;
    private String LastName;
    //private String YearOfBirth;
    //private String Gender;
    //private String Kinship;
    //private String EntityRelationshipId;
    private List<ProfileData.Phone> Phones = new ArrayList<>();

    public static ProfileData build(EntityData entityData) {
        ProfileData post = new ProfileData(entityData.getId());
        post.FirstName = entityData.getFirstName();
        post.LastName = entityData.getLastName();

        if (entityData.getPhones().size() > 0) {
            ProfileData.Phone phone = new Phone(entityData.getPhones().get(0).getDigits(),
                    entityData.getPhones().get(0).getCode(),
                    entityData.getPhones().get(0).getCountryCode(),
                    entityData.getPhones().get(0).getType(),
                    entityData.getPhones().get(0).getType2(),
                    entityData.getPhones().get(0).getId());
            post.Phones.add(phone);
        } else {
            ProfileData.Phone phone = new Phone("",
                    LifecareConfig.PHONE_CODE_DEFAULT,
                    LifecareConfig.PHONE_COUNTRY_CODE_DEFAULT,
                    LifecareConfig.PHONE_TYPE_DEFAULT,
                    LifecareConfig.PHONE_TYPE2_DEFAULT,
                    "");
            post.Phones.add(phone);
        }

        return post;
    }

    private ProfileData(String entityId) {
        EntityId = entityId;
    }



    //public void setEditUserEntityId(String entityId) {
    //    EntityRelationshipId = entityId;
    //}

    public String getPhoneNumber() {
        return Phones.get(0).PhoneDigits;
    }

    public void setPhoneNumber(String number) {
        Phones.get(0).setDigits(number);
    }

    public static class Phone {
        private String PhoneDigits;
        private String Digits;
        private String Code;
        private String CountryCode;
        private String Type;
        private String Type2;
        private String PhoneId;

        public Phone() {
        }

        Phone(String digits, String code, String countryCode, String type, String type2, String phoneId) {
            Digits = digits;
            Code = code;
            CountryCode = countryCode;
            Type = type;
            Type2 = type;
            PhoneId = phoneId;

            setPhoneDigits(digits);

        }

        void setDigits(String digits) {
            Digits = digits;
            setPhoneDigits(digits);
        }

        private void setPhoneDigits(String digits) {
            PhoneDigits = digits;
            if (!TextUtils.isEmpty(CountryCode)) {
                if (CountryCode.startsWith("+")) {
                    PhoneDigits = CountryCode + digits;
                } else {
                    PhoneDigits = "+" + CountryCode + digits;
                }
            }
        }
    }
}
