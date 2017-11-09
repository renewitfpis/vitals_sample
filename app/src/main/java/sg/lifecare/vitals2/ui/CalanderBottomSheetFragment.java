package sg.lifecare.vitals2.ui;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.NumberPicker;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import org.joda.time.DateTime;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import sg.lifecare.vitals2.R;
import timber.log.Timber;

public class CalanderBottomSheetFragment extends BottomSheetDialogFragment {

    private static final String PARAM_YEAR = "year";
    private static final String PARAM_MONTH = "month";
    private static final String PARAM_DAY = "day";
    private static final String PARAM_HOUR = "hour";
    private static final String PARAM_MINUTE = "minute";

    private static final String[] HOUR_24_FORMAT = {
            "00", "01", "02", "03", "04", "05", "06", "07", "08", "09",
            "10", "11", "12", "13", "14", "15", "16", "17", "18", "19",
            "20", "21", "22", "23"};

    private static final String[] MINUTE_FORMAT = {
            "00", "01", "02", "03", "04", "05", "06", "07", "08", "09",
            "10", "11", "12", "13", "14", "15", "16", "17", "18", "19",
            "20", "21", "22", "23", "24", "25", "26", "27", "28", "29",
            "30", "31", "32", "33", "34", "35", "36", "37", "38", "39",
            "40", "41", "42", "43", "44", "45", "46", "47", "48", "49",
            "50", "51", "52", "53", "54", "55", "56", "57", "58", "59"};

    @BindView(R.id.calendar_view)
    MaterialCalendarView mCalendarView;

    @BindView(R.id.hour_picker)
    NumberPicker mHourPicker;

    @BindView(R.id.minute_picker)
    NumberPicker mMinutePicker;

    public static CalanderBottomSheetFragment newInstance(int year, int month, int day,
            int hour, int minute) {
        CalanderBottomSheetFragment fragment = new CalanderBottomSheetFragment();

        Bundle data = new Bundle();
        data.putInt(PARAM_YEAR, year);
        data.putInt(PARAM_MONTH, month);
        data.putInt(PARAM_DAY, day);
        data.putInt(PARAM_HOUR, hour);
        data.putInt(PARAM_MINUTE, minute);

        fragment.setArguments(data);

        return fragment;
    }

    public static DateTime getDateTimeFromIntent(Intent intent) {
        DateTime dateTime = new DateTime()
                .withYear(intent.getIntExtra(PARAM_YEAR, 0))
                .withMonthOfYear(intent.getIntExtra(PARAM_MONTH, 0))
                .withDayOfMonth(intent.getIntExtra(PARAM_DAY, 0))
                .withHourOfDay(intent.getIntExtra(PARAM_HOUR, 0))
                .withMinuteOfHour(intent.getIntExtra(PARAM_MINUTE, 0));

        Timber.d("getDateTimeFromIntent: %d, %d, %d", intent.getIntExtra(PARAM_YEAR, 0), intent.getIntExtra(PARAM_MONTH, 0), intent.getIntExtra(PARAM_DAY, 0));

        return dateTime;
    }

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetCallback =
            new BottomSheetBehavior.BottomSheetCallback() {

                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                        dismiss();
                    }
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {

                }
            };

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View view  = View.inflate(getContext(), R.layout.bottomsheet_calendar, null);

        dialog.setContentView(view);

        CoordinatorLayout.LayoutParams params =
                (CoordinatorLayout.LayoutParams) ((View) view.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();
        if ((behavior != null) && (behavior instanceof BottomSheetBehavior)) {
            Timber.d("behavior");
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetCallback);
            ((BottomSheetBehavior) behavior).setHideable(false);
            ((BottomSheetBehavior) behavior).setPeekHeight(1200);
        }

        ButterKnife.bind(this, view);

        Bundle data = getArguments();
        if (data != null) {
            int year = data.getInt(PARAM_YEAR);
            int month = data.getInt(PARAM_MONTH);
            int day = data.getInt(PARAM_DAY);
            int hour = data.getInt(PARAM_HOUR);
            int minute = data.getInt(PARAM_MINUTE);

            DateTime dateTime = new DateTime(year, month, day, hour, minute);
            DateTime today = new DateTime();

            mCalendarView.setSelectedDate(dateTime.toDate());
            mCalendarView.setCurrentDate(dateTime.toDate());

            mCalendarView.state().edit()
                    .setMaximumDate(today.toDate())
                    .commit();

            mHourPicker.setMinValue(0);
            mHourPicker.setMaxValue(23);
            mHourPicker.setValue(hour > 23 ? 0 : hour);
            mHourPicker.setDisplayedValues(HOUR_24_FORMAT);
            setDividerColor(mHourPicker, ContextCompat.getColor(getContext(), android.R.color.transparent));

            mMinutePicker.setMinValue(0);
            mMinutePicker.setMaxValue(59);
            mMinutePicker.setValue(minute > 59 ? 0 : minute);
            mMinutePicker.setDisplayedValues(MINUTE_FORMAT);
            setDividerColor(mMinutePicker, ContextCompat.getColor(getContext(), android.R.color.transparent));
        }

    }

    private void setDividerColor(NumberPicker picker, int color) {
        java.lang.reflect.Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (java.lang.reflect.Field field : pickerFields) {
            if (field.getName().equals("mSelectionDivider")) {
                ColorDrawable colorDrawable = new ColorDrawable(color);

                try {
                    field.setAccessible(true);
                    field.set(picker, colorDrawable);
                } catch (IllegalAccessException e) {
                    Timber.e(e.getMessage(), e);
                }
                break;
            }
        }
    }

    @OnClick(R.id.done_button)
    public void onDoneButtonClick() {
        Intent intent = new Intent();
        CalendarDay calendarDay = mCalendarView.getSelectedDate();
        intent.putExtra(PARAM_YEAR, calendarDay.getYear());
        intent.putExtra(PARAM_MONTH, calendarDay.getMonth());
        intent.putExtra(PARAM_DAY, calendarDay.getDay());
        intent.putExtra(PARAM_HOUR, mHourPicker.getValue());
        intent.putExtra(PARAM_MINUTE, mMinutePicker.getValue());
        Timber.d("onDoneButtonClick: %d, %d, %d", calendarDay.getYear(),calendarDay.getMonth(), calendarDay.getDay());
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
        dismiss();
    }
}
