package sg.lifecare.views;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import sg.lifecare.utils.CommonUtils;
import sg.lifecare.vitals2.R;
import timber.log.Timber;

public class TimelineView extends View {

    public static final int VERTICAL = 1;
    public static final int HORIZONTAL = 0;

    public static final int LINE_TYPE_NORMAL = 0;
    public static final int LINE_TYPE_BEGIN = 1;
    public static final int LINE_TYPE_END = 2;
    public static final int LINE_TYPE_ONLYONE = 3;

    private static final float DEFAULT_MARKER_SIZE = 10f;
    private static final float DEFAULT_LINE_SIZE = 2f;


    private Drawable mMarker;
    private Drawable mStartLine;
    private Drawable mEndLine;
    private int mMarkerSize;
    private int mLineSize;
    private int mLineOrientation;
    private int mLinePadding;
    private boolean mMarkerInCenter;
    private int mMarkerYOffset;

    private Rect mBounds;

    public TimelineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.timeline_style);
        mMarker = typedArray.getDrawable(R.styleable.timeline_style_marker);
        mStartLine = typedArray.getDrawable(R.styleable.timeline_style_line);
        mEndLine = typedArray.getDrawable(R.styleable.timeline_style_line);
        mMarkerSize = typedArray.getDimensionPixelSize(R.styleable.timeline_style_markerSize,
                CommonUtils.dpToPx(getContext(), DEFAULT_MARKER_SIZE));
        mLineSize = typedArray.getDimensionPixelSize(R.styleable.timeline_style_lineSize,
                CommonUtils.dpToPx(getContext(), DEFAULT_LINE_SIZE));
        mLineOrientation = typedArray.getInt(R.styleable.timeline_style_lineOrientation, VERTICAL);
        mLinePadding = typedArray.getDimensionPixelSize(R.styleable.timeline_style_linePadding, 0);
        mMarkerInCenter = typedArray.getBoolean(R.styleable.timeline_style_markerInCenter, false);
        mMarkerYOffset = typedArray.getDimensionPixelOffset(R.styleable.timeline_style_markerYOffset, 0);
        typedArray.recycle();

        if (mMarker == null) {
            mMarker = ContextCompat.getDrawable(getContext(), R.drawable.timeline_marker);
        }

        if (mStartLine == null && mEndLine == null) {
            mStartLine = new ColorDrawable(ContextCompat.getColor(getContext(), android.R.color.darker_gray));
            mEndLine = new ColorDrawable(ContextCompat.getColor(getContext(), android.R.color.darker_gray));
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int w = mMarkerSize + getPaddingLeft() + getPaddingRight();
        int h = mMarkerSize + getPaddingTop() + getPaddingBottom();

        int widthSize = resolveSizeAndState(w, widthMeasureSpec, 0);
        int heightSize = resolveSizeAndState(h, heightMeasureSpec, 0);

        setMeasuredDimension(widthSize, heightSize);
        initDrawable();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        initDrawable();
    }

    private void initDrawable() {
        int pLeft = getPaddingLeft();
        int pRight = getPaddingRight();
        int pTop = getPaddingTop();
        int pBottom = getPaddingBottom();

        int width = getWidth();
        int height = getHeight();

        int cWidth = width - pLeft - pRight;
        int cHeight = height - pTop - pBottom;

        int markerSize = Math.min(mMarkerSize, Math.min(cWidth, cHeight));

        if (mMarker != null) {
            if (mMarkerInCenter) {
                mMarker.setBounds(
                        (width / 2) - (markerSize / 2), (height / 2) - (markerSize / 2),
                        (width / 2) + (markerSize / 2), (height / 2) + (markerSize / 2));
                mBounds = mMarker.getBounds();
            } else {
                mMarker.setBounds(pLeft, pTop + mMarkerYOffset, pLeft + markerSize, pTop + markerSize + mMarkerYOffset);
                mBounds = mMarker.getBounds();
            }
        }

        int centerX = mBounds.centerX();
        int lineLeft = centerX - (mLineSize >> 1);

        if (mLineOrientation == HORIZONTAL) {
            if (mStartLine != null) {
                mStartLine.setBounds(0, pTop + (mBounds.height()/2), mBounds.left - mLinePadding,
                        (mBounds.height()/2) + pTop + mLineSize);
            }

            if (mEndLine != null) {
                mEndLine.setBounds(mBounds.right + mLinePadding, pTop + (mBounds.height()/2),
                        width, (mBounds.height()/2) + pTop + mLineSize);
            }
        } else {
            if (mStartLine != null) {
                mStartLine.setBounds(lineLeft, 0, mLineSize + lineLeft, mBounds.top - mLinePadding);
            }

            if (mEndLine != null) {
                mEndLine.setBounds(lineLeft, mBounds.bottom + mLinePadding, mLineSize + lineLeft, height);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mMarker != null) {
            mMarker.draw(canvas);
        }

        if (mStartLine != null) {
            mStartLine.draw(canvas);
        }

        if (mEndLine != null) {
            mEndLine.draw(canvas);
        }
    }

    /**
     * Sets marker
     *
     * @param marker will set marker drawable to timeline
     */
    public void setMarker(Drawable marker) {
        mMarker = marker;
        initDrawable();
    }

    /**
     * Sets marker
     *
     * @param marker will set marker drawable to timeline
     * @param color with a color
     */
    public void setMarker(Drawable marker, int color) {
        mMarker = marker;
        mMarker.setColorFilter(color, PorterDuff.Mode.SRC);
        initDrawable();
    }

    /**
     * Sets marker color
     *
     * @param color the color
     */
    public void setMarkerColor(int color) {
        mMarker.setColorFilter(color, PorterDuff.Mode.SRC);
        initDrawable();
    }

    /**
     * Sets start line
     *
     * @param color     the color
     * @param viewType  the view type
     */
    public void setStartLine(int color, int viewType) {
        mStartLine = new ColorDrawable(color);
        initLine(viewType);
    }

    /**
     * Sets end line
     * @param color     the color
     * @param viewType  the view type
     */
    public void setEndLine(int color, int viewType) {
        mEndLine = new ColorDrawable(color);
        initLine(viewType);
    }

    /**
     * Sets marker size
     *
     * @param size      the marker size
     */
    public void setMarkerSize(int size) {
        mMarkerSize = size;
        initDrawable();
    }

    /**
     * Sets line size
     * @param size      the line size
     */
    public void setLineSize(int size) {
        mLineSize = size;
        initDrawable();
    }

    /**
     * Sets line padding
     * @param padding   the line padding
     */
    public void setLinePadding(int padding) {
        mLinePadding = padding;
        initDrawable();
    }

    /**
     * Sets start line
     * @param line      the start line
     */
    public void setStartLine(Drawable line) {
        mStartLine = line;
        initDrawable();
    }

    /**
     * Sets end line
     * @param line      the end line
     */
    public void setEndLine(Drawable line) {
        mEndLine = line;
        initDrawable();
    }

    /**
     * Init line
     *
     * @param viewType  the view type
     */
    public void initLine(int viewType) {
        if (viewType == LINE_TYPE_BEGIN) {
            setStartLine(null);
        } else if (viewType == LINE_TYPE_END) {
            setEndLine(null);
        } else if (viewType == LINE_TYPE_ONLYONE) {
            setStartLine(null);
            setEndLine(null);
        }

        initDrawable();
    }

    public static int getTimeLineViewType(int position, int totalSize) {
        if (totalSize == 1) {
            return LINE_TYPE_ONLYONE;
        } else if (position == 0) {
            return LINE_TYPE_BEGIN;
        } else if (position == (totalSize-1)) {
            return LINE_TYPE_END;
        }

        return LINE_TYPE_NORMAL;
    }
}
