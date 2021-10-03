package com.arcm.scalenumberpicker;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.Nullable;

public class ScaleNumberPicker extends View implements ValueAnimator.AnimatorUpdateListener {
    public static final String TAG = "ScaleNumberPicker";
    public final int DEFSTYLE_RES = R.style.ScaleNumberPicker;
    private final int[] STYLEABLE_RES = R.styleable.ScaleNumberPicker;
    private static final String EXTRA_VALUE = "scale_value";
    private static final String EXTRA_SUPER_STATE = "super_extra_state";
    private static final int HORIZONTAL = 0;
    private static final int VERTICAL_LEFT = 1;
    private static final int VERTICAL_RIGHT = 2;
    private static final int HORIZONTAL_CIRCULAR = 3;

    private VelocityTracker velocityTracker;
    private float val;
    private float oldVal;
    private float lastUpdatedValue;
    private float dragStartX;
    private float dragStartY;

    private Attributes attrs;
    private ScaleDesign design;
    private OnValueChangedListener listener;
    private ValueAnimator valueAnimator;

    private static final int RANGE_MULTIPLIER = 10;
    private final int MAX_SPEED;
    private final int MIN_SPEED;
    private int range;

    public ScaleNumberPicker(Context context) {
        super(context);
        init(null, 0);
    }

    public ScaleNumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public ScaleNumberPicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    {
        ViewConfiguration viewConf = ViewConfiguration.get(getContext());
        this.MAX_SPEED = viewConf.getScaledMaximumFlingVelocity();
        this.MIN_SPEED = viewConf.getScaledMinimumFlingVelocity();
    }

    private void init(AttributeSet attrSet, int defStyle) {
        // Load attributes
        this.attrs = new Attributes(getContext(), attrSet, STYLEABLE_RES, defStyle, DEFSTYLE_RES);
        this.val = attrs.value;
        this.range = (int) (attrs.maxValue - attrs.minValue);
        if (attrs.orientation == HORIZONTAL) {
            this.design = new HorizontalScale(attrs);
        } else if (attrs.orientation == VERTICAL_LEFT) {
            this.design = new VerticalLeftScale(attrs);
        } else if (attrs.orientation == VERTICAL_RIGHT){
            this.design = new VerticalRightScale(attrs);
        } else if (attrs.orientation == HORIZONTAL_CIRCULAR) {
            this.design = new HorizontalCircularScale(attrs);
        }
        this.valueAnimator = ValueAnimator.ofFloat(attrs.minValue, attrs.maxValue);
        valueAnimator.addUpdateListener(this);
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                Log.i(TAG, "value changed: " + oldVal + " => " + val);
                if (listener != null) listener.onValueChanged(oldVal, val);
            }
        });
        valueAnimator.setInterpolator(new DecelerateInterpolator());
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle state = new Bundle();
        state.putParcelable(EXTRA_SUPER_STATE, super.onSaveInstanceState());
        state.putFloat(EXTRA_VALUE, val);
        return state;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable parcel) {
        Bundle state = (Bundle) parcel;
        super.onRestoreInstanceState(state.getParcelable(EXTRA_SUPER_STATE));
        this.val = state.getFloat(EXTRA_VALUE);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        design.setValue(val);
        design.draw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredWidth = design.measureViewContentWidth();
        int desiredHeight = design.measureViewContentHeight();
        
        if (attrs.orientation == HORIZONTAL || attrs.orientation == HORIZONTAL_CIRCULAR) {
            desiredWidth = MeasureSpec.getSize(widthMeasureSpec);
        } else desiredHeight = MeasureSpec.getSize(heightMeasureSpec);

        int width = getMeasurement(widthMeasureSpec, desiredWidth);
        int height = getMeasurement(heightMeasureSpec, desiredHeight);
        int pLeft = getPaddingLeft();
        int pRight = getPaddingRight();
        int pTop = getPaddingTop();
        int pBottom = getPaddingBottom();
        
        int contentWidth = width - pLeft - pRight;
        int contentHeight = height - pTop - pBottom;

        if (attrs.orientation == HORIZONTAL) {
            int margin = (contentWidth / 2) % attrs.subdivisionWidth;
            design.setMargins(pLeft + margin, pTop, pRight + margin, pBottom);
            design.setDimensions(contentWidth - margin * 2, contentHeight);
            this.range = (int) ((contentWidth / attrs.divisionWidth) * attrs.divisionValue) * RANGE_MULTIPLIER;
        } else if (attrs.orientation == HORIZONTAL_CIRCULAR) {
            design.setMargins(pLeft, pTop, pRight, pBottom);
            design.setDimensions(contentWidth, contentHeight);
            this.range = (int) ((contentWidth / attrs.divisionWidth) * attrs.divisionValue) * RANGE_MULTIPLIER;
        } else {
            int margin = (contentHeight / 2) % attrs.subdivisionWidth;
            design.setMargins(pLeft, pTop + margin, pRight, pBottom + margin);
            design.setDimensions(contentWidth, contentHeight - margin * 2);
            this.range = (int) ((contentHeight / attrs.divisionWidth) * attrs.divisionValue) * RANGE_MULTIPLIER;
        }
        design.notifyDimensionsChanged();

        // MUST CALL THIS
        setMeasuredDimension(width, height);
    }
    
    private int getMeasurement(int measureSpec, int desiredMeasurement) {
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        if (mode == MeasureSpec.EXACTLY) return size;
        return desiredMeasurement;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
                onDown(event);
                break;
            case MotionEvent.ACTION_MOVE:
                onMove(event);
                break;
            case MotionEvent.ACTION_UP:
                getParent().requestDisallowInterceptTouchEvent(false);
                onUp(event);
                performClick();
                break;
            case MotionEvent.ACTION_CANCEL:
                getParent().requestDisallowInterceptTouchEvent(false);
                onCancel();
                break;
            default:
                return false;
        }
        return true;
    }

    private void onDown(MotionEvent event) {
        this.oldVal = val;
        if (valueAnimator.isRunning()) valueAnimator.cancel();
        if (velocityTracker == null) {
            this.velocityTracker = VelocityTracker.obtain();
        } else velocityTracker.clear();
        this.dragStartX = event.getX();
        this.dragStartY = event.getY();
        velocityTracker.addMovement(event);
    }

    private void onMove(MotionEvent event) {
        velocityTracker.addMovement(event);
        float dx = event.getX() - dragStartX;
        float dy = event.getY() - dragStartY;
        float newVal = oldVal - (dx / attrs.subdivisionWidth) * attrs.subdivisionValue;
        if (attrs.orientation == VERTICAL_LEFT || attrs.orientation == VERTICAL_RIGHT) {
            newVal = oldVal + (dy / attrs.subdivisionWidth) * attrs.subdivisionValue;
        }
        this.val = Math.min(Math.max(attrs.minValue, newVal), attrs.maxValue);
        if (val != lastUpdatedValue) invalidate();
        this.lastUpdatedValue = val;
    }

    private void onUp(MotionEvent event) {
        velocityTracker.addMovement(event);
        // When you want to determine the velocity, call
        // computeCurrentVelocity(). Then call getXVelocity()
        // and getYVelocity() to retrieve the velocity for each pointer ID.
        velocityTracker.computeCurrentVelocity(1000);
        float speed = velocityTracker.getXVelocity();
        if (attrs.orientation == VERTICAL_LEFT || attrs.orientation == VERTICAL_RIGHT) {
            speed = -velocityTracker.getYVelocity();
        }
        float dv = (speed / MAX_SPEED) * range;
        float end = Math.round((val - dv) / attrs.tickValue) * attrs.tickValue;
        float endValue = Math.max(attrs.minValue, Math.min(end, attrs.maxValue));
        Log.i(TAG, val + ":" + endValue + " max: " + MAX_SPEED + " min: " + MIN_SPEED);
        valueAnimator.setFloatValues(val, endValue);
        valueAnimator.start();
        resetVelocityTracker();
    }

    private void onCancel() {
        this.val = oldVal;
        resetVelocityTracker();
        invalidate();
    }

    private void resetVelocityTracker() {
        velocityTracker.recycle();
        // throws {@code illegalStateException} without this line!
        this.velocityTracker = null;
    }

    @Override
    public boolean performClick() {
        super.performClick();
        return true;
    }

    public void setOnValueChangedListener(OnValueChangedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animator) {
        if (animator == valueAnimator) {
            float newVal = trimValue((float) valueAnimator.getAnimatedValue());
            if (newVal != val) {
                this.val = (float) valueAnimator.getAnimatedValue();
                invalidate();
            }
        }
    }

    private float trimValue(float value) {
        return Math.round(value / attrs.tickValue) * attrs.tickValue;
    }

    public interface OnValueChangedListener {
        void onValueChanged(float oldValue, float newValue);
    }

    public float getValue() {
        return val;
    }

    public void setValue(float val) {
        this.val = val;
        design.setRoundedValues(false);
        invalidate();
        design.setRoundedValues(true);
    }
}
