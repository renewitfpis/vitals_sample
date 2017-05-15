package sg.lifecare.utils;

import android.text.TextUtils;
import android.util.Patterns;


public class CommonUtils {

    public static boolean isValidEmail(String email) {
        if (!TextUtils.isEmpty(email)) {
            return Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }

        return false;
    }
}
