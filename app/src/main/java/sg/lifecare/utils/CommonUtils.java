package sg.lifecare.utils;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Patterns;
import android.util.TypedValue;

public class CommonUtils {

    public static boolean isValidEmail(String email) {
        if (!TextUtils.isEmpty(email)) {
            return Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }

        return false;
    }

    public static int dpToPx(Context context, float dp) {
        return dpToPx(context.getResources(), dp);
    }

    public static int dpToPx(Resources resources, float dp) {
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
        return (int) px;
    }

    public static int spTopPx(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
}
