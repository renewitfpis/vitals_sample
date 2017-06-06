package sg.lifecare.views.valuepicker;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import timber.log.Timber;

public class ScrollingValuePicker extends FrameLayout {

    public static final int DISPLAY_NUMBER_TYPE_SPACIAL_COUNT = LineRulerView.DISPLAY_NUMBER_TYPE_SPACIAL_COUNT;
    public static final int DISPLAY_NUMBER_TYPE_MULTIPLE = LineRulerView.DISPLAY_NUMBER_TYPE_MULTIPLE;

    private View mLeftSpacer;
    private View mRightSpacer;
    private LineRulerView lineRulerView;
    private ObservableHorizontalScrollView mScrollView;
    private float viewMultipleSize = 3f;

    private float maxValue = 0;
    private float minValue = 0;

    private float initValue = 0f;

    private float valueMultiple = 1f;

    private float valueTypeMultiple = 5;

    private Paint paint = new Paint();
    private Path path = new Path();

    public ScrollingValuePicker(Context context) {
        super(context);
        init(context);
    }

    public ScrollingValuePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ScrollingValuePicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ScrollingValuePicker(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    public void setOnScrollChangedListener(final ObservableHorizontalScrollView.OnScrollChangedListener onScrollChangedListener) {
        mScrollView.setOnScrollChangedListener(onScrollChangedListener);
    }

    public void setMinMaxValue(float minValue, float maxValue) {
        setMaxValue(minValue, maxValue, 1);
    }

    private void setMaxValue(float minValue, float maxValue, float valueMultiple) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.valueMultiple = valueMultiple;
        lineRulerView.setMaxValue(this.maxValue);
        lineRulerView.setMinValue(this.minValue);
        lineRulerView.setValueMultiple(this.valueMultiple);
    }

    public void setValueMultiple(float valueMultiple) {
        this.valueMultiple = valueMultiple;
        lineRulerView.setValueMultiple(this.valueMultiple);
    }

    public void setValueTypeMultiple(float valueTypeMultiple) {
        this.valueTypeMultiple = valueTypeMultiple;
        lineRulerView.setMultipleTypeValue(valueTypeMultiple);
    }


    public void setViewMultipleSize(float size) {
        this.viewMultipleSize = size;
    }

    public void setInitValue(float initValue) {
        this.initValue = initValue;
    }

    public float getViewMultipleSize() {
        return this.viewMultipleSize;
    }


    private void init(Context context) {
        mScrollView = new ObservableHorizontalScrollView(context);
        mScrollView.setHorizontalScrollBarEnabled(false);
        addView(mScrollView);

        final LinearLayout container = new LinearLayout(context);
        mScrollView.addView(container);

        mLeftSpacer = new View(context);
        mRightSpacer = new View(context);

        lineRulerView = new LineRulerView(context);
        container.addView(lineRulerView);
        container.addView(mLeftSpacer, 0);
        container.addView(mRightSpacer);

        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (getWidth() != 0) {
                    scrollToValue(getScrollView(), initValue);
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });

    }

    private void scrollToValue(ObservableHorizontalScrollView view, float value) {
        float oneValue = (float) view.getWidth() * viewMultipleSize / ((maxValue - minValue) * valueMultiple);
        float valueWidth = oneValue * (value - minValue);

        view.scrollBy((int) valueWidth, 0);
    }


    public ObservableHorizontalScrollView getScrollView() {
        return mScrollView;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        paint.setColor(Color.parseColor("#ffffff"));
        paint.setStrokeWidth(5f);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);

        path.moveTo(getWidth() / 2 - 30, 0);
        path.lineTo(getWidth() / 2, 40);
        path.lineTo(getWidth() / 2 + 30, 0);
        canvas.drawPath(path, paint);
        super.onDraw(canvas);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (changed) {

            final int width = getWidth();

            final ViewGroup.LayoutParams leftParams = mLeftSpacer.getLayoutParams();
            leftParams.width = width / 2;
            mLeftSpacer.setLayoutParams(leftParams);

            final ViewGroup.LayoutParams rulerViewParams = lineRulerView.getLayoutParams();
            rulerViewParams.width = (int) (width * viewMultipleSize);  // set RulerView Width
            lineRulerView.setLayoutParams(rulerViewParams);
            lineRulerView.invalidate();


            final ViewGroup.LayoutParams rightParams = mRightSpacer.getLayoutParams();
            rightParams.width = width / 2;
            mRightSpacer.setLayoutParams(rightParams);

            invalidate();

        }
    }

    public void setTextColor(int color) {
        lineRulerView.setTextColor(color);
    }

    public void setTextSize(int size) {
        lineRulerView.setTextSize(size);
    }

    public float getValueAndScrollItemToCenter(int l, int t) {
        float oneValue = mScrollView.getWidth() * viewMultipleSize / (maxValue - minValue);
        float value;
        float offset;

        if (valueMultiple >= 1f) {
            value = (int)(l / oneValue) + (int)minValue;
            offset = (int) (l % oneValue);

            if (offset > oneValue / 2) {
                value += 1;
                mScrollView.smoothScrollBy((int) (oneValue - offset), 0);

            } else {
                mScrollView.smoothScrollBy((int)-offset, 0);
            }
        } else {
            value = ((l / oneValue) + minValue) * valueMultiple;
            offset = (int) (l % oneValue);

            if (offset > oneValue / 2) {
                //value += 1;
                mScrollView.smoothScrollBy((int) (oneValue - offset), 0);

            } else {
                mScrollView.smoothScrollBy((int)-offset, 0);
            }
        }

        //Timber.d("getValueAndScrollItemToCenter: width=%d, min=%f, max=%f", mScrollView.getWidth(), minValue, maxValue);
        //Timber.d("getValueAndScrollItemToCenter: l=%d, oneValue=%f, value=%f, offset=%f", l, oneValue, value, offset);
        //Timber.d("getValueAndScrollItemToCenter: viewMultipleSize=%f, valueMultiple=%f", viewMultipleSize, valueMultiple);

        if (value > maxValue) {
            value = maxValue;
        }

        return value;
    }
}
