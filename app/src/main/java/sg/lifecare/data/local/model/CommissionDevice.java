package sg.lifecare.data.local.model;

import java.io.Serializable;

public abstract class CommissionDevice implements Serializable {

    private final int mType;

    public CommissionDevice(int type) {
        mType = type;
    }

    public abstract int getName();
    public abstract int getDesc();
    public abstract int getType();
    public abstract int getImage();
    public abstract String getProductId();
    public abstract String getPlanId();

    /*public static final int TYPE_PERSONAL = 1;
    public static final int TYPE_PAWS = 2;
    public static final int TYPE_KIDS = 3;

    private static final String PRODUCT_ID_PERSONAL = "582420cc1c3b06a08c0edc0e";
    private static final String PRODUCT_ID_PAWS = "58749131a5316908ccc53e49";
    private static final String PRODUCT_ID_KIDS = "587490c1a5316908ccc53e32";

    private static final String PLAN_ID_PERSONAL = "589e8db90c3fe6f50065abd7";
    private static final String PLAN_ID_PAWS = "589e8d8a0c3fe6f50065ab83";
    private static final String PLAN_ID_KIDS = "589e8d6b0c3fe6f50065ab49";

    private final int mType;

    public CommissionDevice(int type) {
        mType = type;
    }

    public int getName() {
        switch (mType) {
            case TYPE_PERSONAL:
                return R.string.tracker_personal;

            case TYPE_PAWS:
                return R.string.tracker_paws;

            case TYPE_KIDS:
                return R.string.tracker_kids;
        }

        return 0;
    }

    public int getDesc() {
        switch (mType) {
            case TYPE_PERSONAL:
                return R.string.tracker_personal_desc;

            case TYPE_PAWS:
                return R.string.tracker_paws_desc;

            case TYPE_KIDS:
                return R.string.tracker_kids_desc;
        }

        return 0;
    }

    public int getImage() {
        switch (mType) {
            case TYPE_PERSONAL:
                return R.drawable.ic_tracker_personal;

            case TYPE_PAWS:
                return R.drawable.ic_tracker_paws;

            case TYPE_KIDS:
                return R.drawable.ic_tracker_kids;
        }

        return 0;
    }

    public String getProductId() {
        switch (mType) {
            case TYPE_PERSONAL:
                return PRODUCT_ID_PERSONAL;

            case TYPE_PAWS:
                return PRODUCT_ID_PAWS;

            case TYPE_KIDS:
                return PRODUCT_ID_KIDS;
        }

        return "";
    }

    public String getPlanId() {
        switch (mType) {
            case TYPE_PERSONAL:
                return PLAN_ID_PERSONAL;

            case TYPE_PAWS:
                return PLAN_ID_PAWS;

            case TYPE_KIDS:
                return PLAN_ID_KIDS;
        }

        return "";
    }*/
}
