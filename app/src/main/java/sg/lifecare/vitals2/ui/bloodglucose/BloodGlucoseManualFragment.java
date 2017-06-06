package sg.lifecare.vitals2.ui.bloodglucose;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import sg.lifecare.utils.DateUtils;
import sg.lifecare.views.valuepicker.ObservableHorizontalScrollView;
import sg.lifecare.views.valuepicker.ScrollingValuePicker;
import sg.lifecare.vitals2.R;
import sg.lifecare.vitals2.ui.CalanderBottomSheetFragment;
import sg.lifecare.vitals2.ui.base.BaseFragment;
import timber.log.Timber;

public class BloodGlucoseManualFragment extends BaseFragment implements BloodGlucoseManualMvpView {

    public static final String TAG = BloodGlucoseManualFragment.class.getSimpleName();

    private static final int RESULT_CALENDAR = 1;

    private static final float GLUCOSE_INITIAL_VALUE = 5.5f;

    @Inject
    BloodGlucoseManualMvpPresenter<BloodGlucoseManualMvpView> mPresenter;

    @BindView(R.id.timestamp_text)
    TextView mTimestamp;

    @BindView(R.id.glucose_value_text)
    TextView mGlucoseValueText;

    @BindView(R.id.glucose_valuepicker)
    ScrollingValuePicker mGlucoseValuePicker;


    private DateTime mDateTime;

    public static BloodGlucoseManualFragment newInstance() {
        return new BloodGlucoseManualFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blood_glucose_manual, container, false);

        Timber.d("onCreateView");

        getActivityComponent().inject(this);
        setUnbinder(ButterKnife.bind(this, view));

        mPresenter.onAttach(this);

        setupViews(view);

        return view;
    }

    @Override
    public void onDestroyView() {
        mPresenter.onDetach();
        super.onDestroyView();
    }

    @Override
    protected void setupViews(View view) {
        mDateTime = new DateTime();

        mTimestamp.setText(DateUtils.FULL_DATETIME_FORMAT.print(mDateTime));

        mGlucoseValueText.setText(String.format(Locale.getDefault(), "%.1f", GLUCOSE_INITIAL_VALUE));

        mGlucoseValuePicker.setMinMaxValue(0, 30f);
        mGlucoseValuePicker.setValueMultiple(0.1f);
        mGlucoseValuePicker.setValueTypeMultiple(0.5f);
        mGlucoseValuePicker.setViewMultipleSize(3.0f);
        mGlucoseValuePicker.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
        mGlucoseValuePicker.setTextSize(20);
        mGlucoseValuePicker.setInitValue(GLUCOSE_INITIAL_VALUE);
        mGlucoseValuePicker.getScrollView().setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    mGlucoseValuePicker.getScrollView().startScrollerTask();
                }
                return false;
            }
        });
        mGlucoseValuePicker.setOnScrollChangedListener(
                new ObservableHorizontalScrollView.OnScrollChangedListener() {
                    @Override
                    public void onScrollChanged(ObservableHorizontalScrollView view, int l, int t) {

                    }

                    @Override
                    public void onScrollStopped(int l, int t) {
                        Timber.d("onScrollStopped: %d %d %.1f", l, t, mGlucoseValuePicker.getValueAndScrollItemToCenter(l, t));

                        mGlucoseValueText.setText(String.format(Locale.getDefault(), "%.1f",
                                mGlucoseValuePicker.getValueAndScrollItemToCenter(l, t)));
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Timber.d("onActivityResult: requestCode=%d", requestCode);
        if (requestCode == RESULT_CALENDAR) {
            mDateTime = CalanderBottomSheetFragment.getDateTimeFromIntent(data);
            Timber.d("onActivityResult: date %s", mDateTime.toString());
            mTimestamp.setText(DateUtils.FULL_DATETIME_FORMAT.print(mDateTime));
        }
    }

    @OnClick(R.id.timestamp_text)
    public void onTimestampClick() {
        DateTime today = new DateTime();
        CalanderBottomSheetFragment fragment =
                CalanderBottomSheetFragment.newInstance(
                        today.getYear(),
                        today.getMonthOfYear(),
                        today.getDayOfMonth(),
                        today.getHourOfDay(),
                        today.getMinuteOfHour());
        fragment.setTargetFragment(this, RESULT_CALENDAR);
        fragment.show(getFragmentManager(), TAG);
    }

}
