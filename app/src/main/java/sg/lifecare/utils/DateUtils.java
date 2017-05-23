package sg.lifecare.utils;

import android.content.Context;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import sg.lifecare.vitals2.R;
import timber.log.Timber;

public class DateUtils {

    public static final String ISO8601_TIMESTAMP = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static final DateFormat ISO8601_TIMESTAMP_FORMAT = new SimpleDateFormat(ISO8601_TIMESTAMP, Locale.getDefault());

    private static final DateFormat DAY_DATE_FORMAT = new SimpleDateFormat("EEE, d MMM", Locale.getDefault());
    private static final DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm", Locale.getDefault());

    public static String getIsoTimestamp(Calendar timestamp) {
        try {
            ISO8601_TIMESTAMP_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
            return ISO8601_TIMESTAMP_FORMAT.format(timestamp.getTime());
        } catch (Exception e) {
            Timber.e(e, e.getMessage());
        }
        return "";
    }

    public static boolean isToday(Calendar day) {
        Calendar today = Calendar.getInstance();

        return day.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                day.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                day.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH);
    }

    public static String getDisplayTimestamp(Context context, Date date) {
        if (date == null) {
            return "";
        }

        Calendar current = Calendar.getInstance();
        Calendar input = Calendar.getInstance();
        input.setTime(date);

        if (input.after(current)) {
            return context.getString(R.string.date_a_few_seconds_ago);
        }

        long diff = (current.getTime().getTime() - input.getTime().getTime())/1000;
        long diffHours = diff / (3600); // 60 * 60
        int diffMinutes = (int) (diff / (60) % 60);

        if (diffHours == 0) {
            if (diffMinutes == 0) {
                return context.getString(R.string.date_a_few_seconds_ago);
            } else {
                return context.getResources().getQuantityString(R.plurals.date_minute_ago,
                        diffMinutes, diffMinutes);
            }
        } else {
            boolean sameDay = current.get(Calendar.DAY_OF_MONTH) == input.get(Calendar.DAY_OF_MONTH);
            DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault());
            if (sameDay) {
                return timeFormat.format(input.getTime());
            } else {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM", Locale.getDefault());
                return String.format(context.getResources().getString(R.string.date_day),
                        dateFormat.format(input.getTime()), timeFormat.format(input.getTime()));
            }
        }

    }

    public static String getDisplayDay(Context context, Calendar day) {
        if (isToday(day)) {
            return context.getString(R.string.date_today);
        }

        try {
            return DAY_DATE_FORMAT.format(day.getTime());
        } catch (Exception e) {
            Timber.e(e, e.getMessage());
        }


        return "";

    }

    public static String getDisplayTime(Date time) {
        try {
            return TIME_FORMAT.format(time);
        } catch (Exception e) {
            Timber.e(e, e.getMessage());
        }

        return "";

    }
}
