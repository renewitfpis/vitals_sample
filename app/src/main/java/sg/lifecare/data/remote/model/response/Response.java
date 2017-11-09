package sg.lifecare.data.remote.model.response;


import java.util.Date;

public abstract class Response {

    private int TotalRows;
    private int RowsReturned;
    private boolean Error;
    private String ErrorDesc;
    private int ErrorCode;

    public abstract Object getData();

    public int getTotalRows() {
        return TotalRows;
    }

    public int getRowsReturned() {
        return RowsReturned;
    }

    public boolean isError() {
        return Error;
    }

    public String getErrorDesc() {
        return ErrorDesc;
    }

    public int getErrorCode() {
        return ErrorCode;
    }

    public class Media {
        private String media_url;
        private String type;

        public String getMediaUrl() {
            return media_url;
        }
    }

    public class Phone {
        private String _id;
        private String code;
        private String country_code;
        private Date created_date;
        private String digits;
        private String itl_sync;
        private Date last_update;
        private String phone_digits;
        private String type;
        private String type2;

        public String getId() {
            return _id;
        }

        public String getCountryCode() {
            return country_code;
        }

        public String getDigits() {
            return digits;
        }

        public String getPhoneDigits() {
            return phone_digits;
        }

        public String getCode() {
            return code;
        }

        public String getType() {
            return type;
        }

        public String getType2() {
            return type2;
        }
    }

    public class Module {
        private String _id;
        private String name;
    }
}
