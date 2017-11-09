package sg.lifecare.vitals2.ui.bloodpressure;

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

public class BloodPressureManualFragment extends BaseFragment implements BloodPressureManualMvpView{

    static final String TAG = BloodPressureManualFragment.class.getSimpleName();

    private static final int RESULT_CALENDAR = 1;

    private static final int SYSTOLIC_INITIAL_VALUE = 120;
    private static final int DIASTOLIC_INITIAL_VALUE = 80;
    private static final int PULSE_INITIAL_VALUE = 75;

    @Inject
    BloodPressureManualMvpPresenter<BloodPressureManualMvpView> mPresenter;

    @BindView(R.id.timestamp_text)
    TextView mTimestamp;

    @BindView(R.id.systolic_value_text)
    TextView mSystolicValueText;

    @BindView(R.id.systolic_valuepicker)
    ScrollingValuePicker mSystolicValuePicker;

    @BindView(R.id.diastolic_value_text)
    TextView mDiastolicValueText;

    @BindView(R.id.diastolic_valuepicker)
    ScrollingValuePicker mDiastolicValuePicker;

    @BindView(R.id.pulse_rate_value_text)
    TextView mPulseValueText;

    @BindView(R.id.pulse_rate_valuepicker)
    ScrollingValuePicker mPulseValuePicker;

    private DateTime mDateTime;

    static BloodPressureManualFragment newInstance() {
        BloodPressureManualFragment fragment = new BloodPressureManualFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blood_pressure_manual, container, false);

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

        mSystolicValueText.setText(String.format(Locale.getDefault(), "%d", SYSTOLIC_INITIAL_VALUE));
        mSystolicValuePicker.setMinMaxValue(0, 200);
        mSystolicValuePicker.setValueMultiple(1f);
        mSystolicValuePicker.setValueTypeMultiple(5);
        mSystolicValuePicker.setViewMultipleSize(10.0f);
        mSystolicValuePicker.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
        mSystolicValuePicker.setTextSize(20);
        mSystolicValuePicker.setInitValue(SYSTOLIC_INITIAL_VALUE);
        mSystolicValuePicker.getScrollView().setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                mSystolicValuePicker.getScrollView().startScrollerTask();
            }
            return false;
        });
        mSystolicValuePicker.setOnScrollChangedListener(
                new ObservableHorizontalScrollView.OnScrollChangedListener() {
                    @Override
                    public void onScrollChanged(ObservableHorizontalScrollView view, int l, int t) {

                    }

                    @Override
                    public void onScrollStopped(int l, int t) {
                        Timber.d("onScrollStopped: %d %d %f", l, t, mSystolicValuePicker.getValueAndScrollItemToCenter(l, t));

                        mSystolicValueText.setText(String.format(Locale.getDefault(), "%d",
                                (int)mSystolicValuePicker.getValueAndScrollItemToCenter(l, t)));
                    }
                });

        mDiastolicValueText.setText(String.format(Locale.getDefault(), "%d", DIASTOLIC_INITIAL_VALUE));
        mDiastolicValuePicker.setMinMaxValue(0, 200);
        mDiastolicValuePicker.setValueMultiple(1);
        mDiastolicValuePicker.setValueTypeMultiple(5);
        mDiastolicValuePicker.setViewMultipleSize(10.0f);
        mDiastolicValuePicker.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
        mDiastolicValuePicker.setTextSize(20);
        mDiastolicValuePicker.setInitValue(DIASTOLIC_INITIAL_VALUE);
        mDiastolicValuePicker.getScrollView().setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                mDiastolicValuePicker.getScrollView().startScrollerTask();
            }
            return false;
        });
        mDiastolicValuePicker.setOnScrollChangedListener(
                new ObservableHorizontalScrollView.OnScrollChangedListener() {
                    @Override
                    public void onScrollChanged(ObservableHorizontalScrollView view, int l, int t) {

                    }

                    @Override
                    public void onScrollStopped(int l, int t) {
                        Timber.d("onScrollStopped: %d %d %f", l, t, mDiastolicValuePicker.getValueAndScrollItemToCenter(l, t));

                        mDiastolicValueText.setText(String.format(Locale.getDefault(), "%d",
                                (int)mDiastolicValuePicker.getValueAndScrollItemToCenter(l, t)));
                    }
                });

        mPulseValueText.setText(String.format(Locale.getDefault(), "%d", PULSE_INITIAL_VALUE));
        mPulseValuePicker.setMinMaxValue(0, 200);
        mPulseValuePicker.setValueMultiple(1);
        mPulseValuePicker.setValueTypeMultiple(5);
        mPulseValuePicker.setViewMultipleSize(8.0f);
        mPulseValuePicker.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
        mPulseValuePicker.setTextSize(20);
        mPulseValuePicker.setInitValue(PULSE_INITIAL_VALUE);
        mPulseValuePicker.getScrollView().setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                mPulseValuePicker.getScrollView().startScrollerTask();
            }
            return false;
        });
        mPulseValuePicker.setOnScrollChangedListener(
                new ObservableHorizontalScrollView.OnScrollChangedListener() {
                    @Override
                    public void onScrollChanged(ObservableHorizontalScrollView view, int l, int t) {

                    }

                    @Override
                    public void onScrollStopped(int l, int t) {
                        Timber.d("onScrollStopped: %d %d %f", l, t, mPulseValuePicker.getValueAndScrollItemToCenter(l, t));

                        mPulseValueText.setText(String.format(Locale.getDefault(), "%d",
                                (int)mPulseValuePicker.getValueAndScrollItemToCenter(l, t)));
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
