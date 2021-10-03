package com.arcm.scalenumberpicker;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;

import androidx.annotation.ColorInt;

public class Attributes {
    private static final int ORIENTATION_ID = R.styleable.ScaleNumberPicker_snp_orientation;
    private static final int UNIT_LABEL_ID = R.styleable.ScaleNumberPicker_snp_unit_label;
    private static final int UNIT_LABEL_TEXT_COLOUR_ID = R.styleable.ScaleNumberPicker_snp_unit_label_text_colour;
    private static final int UNIT_LABEL_MARGIN_ID = R.styleable.ScaleNumberPicker_snp_unit_label_margin;
    private static final int UNIT_LABEL_TEXT_SIZE_ID = R.styleable.ScaleNumberPicker_snp_unit_label_text_size;
    private static final int VALUE_ID = R.styleable.ScaleNumberPicker_snp_value;
    private static final int MIN_VALUE_ID = R.styleable.ScaleNumberPicker_snp_min_value;
    private static final int MAX_VALUE_ID = R.styleable.ScaleNumberPicker_snp_max_value;
    private static final int VALUE_TEXT_SIZE_ID = R.styleable.ScaleNumberPicker_snp_value_text_size;
    private static final int VALUE_TEXT_MARGIN_ID = R.styleable.ScaleNumberPicker_snp_value_text_margin;
    private static final int VALUE_TEXT_COLOUR_ID = R.styleable.ScaleNumberPicker_snp_value_text_colour;
    private static final int TICK_VALUE_ID = R.styleable.ScaleNumberPicker_snp_tick_value;
    private static final int TICKS_COUNT_PER_SUBDIVISION_ID = R.styleable.ScaleNumberPicker_snp_ticks_count_per_subdivision;
    private static final int SUBDIVISION_WIDTH_ID = R.styleable.ScaleNumberPicker_snp_subdivision_width;
    private static final int SUBDIVISIONS_COUNT_PER_DIVISION_ID = R.styleable.ScaleNumberPicker_snp_subdivisions_count_per_division;
    private static final int SUBDIVISION_LINE_WIDTH_ID = R.styleable.ScaleNumberPicker_snp_subdivision_line_width;
    private static final int SUBDIVISION_LINE_HEIGHT_ID = R.styleable.ScaleNumberPicker_snp_subdivision_line_height;
    private static final int SUBDIVISION_LINE_IN_RANGE_COLOUR_ID = R.styleable.ScaleNumberPicker_snp_subdivision_line_in_range_colour;
    private static final int SUBDIVISION_LINE_OUT_OF_RANGE_COLOUR_ID = R.styleable.ScaleNumberPicker_snp_subdivision_line_out_of_range_colour;
    private static final int DIVISION_TEXT_COLOR_ID = R.styleable.ScaleNumberPicker_snp_division_text_colour;
    private static final int DIVISION_TEXT_SIZE_ID = R.styleable.ScaleNumberPicker_snp_division_text_size;
    private static final int DIVISION_LINE_WIDTH_ID = R.styleable.ScaleNumberPicker_snp_division_line_width;
    private static final int DIVISION_LINE_HEIGHT_ID = R.styleable.ScaleNumberPicker_snp_division_line_height;
    private static final int DIVISION_TEXT_MARGIN_ID = R.styleable.ScaleNumberPicker_snp_division_text_margin;
    private static final int DIVISION_LINE_IN_RANGE_COLOUR_ID = R.styleable.ScaleNumberPicker_snp_division_line_in_range_colour;
    private static final int DIVISION_LINE_OUT_OF_RANGE_COLOUR_ID = R.styleable.ScaleNumberPicker_snp_division_line_out_of_range_colour;
    private static final int BORDER_WIDTH_ID = R.styleable.ScaleNumberPicker_snp_border_width;
    private static final int BORDER_COLOUR_ID = R.styleable.ScaleNumberPicker_snp_border_colour;
    private static final int INDICATOR_TRIANGLE_WIDTH_ID = R.styleable.ScaleNumberPicker_snp_indicator_triangle_width;
    private static final int INDICATOR_OFFSET_ID = R.styleable.ScaleNumberPicker_snp_indicator_offset;
    private static final int INDICATOR_COLOUR_ID = R.styleable.ScaleNumberPicker_snp_indicator_colour;
    private static final int INDICATOR_SHOW_NEEDLE_ID = R.styleable.ScaleNumberPicker_snp_show_indicator_needle;
    private static final int CURVE_HEIGHT_ID = R.styleable.ScaleNumberPicker_snp_curve_height;

    public final int orientation;
    
    public final String unit;
    public final int unitTextSize;
    public final int unitTextMargin;
    public final boolean shouldDrawUnit;
    public final @ColorInt int unitTextColour;

    public final float value;
    public final float minValue;
    public final float maxValue;
    public final int valueTextSize;
    public final int valueTextMargin;
    public final int valueTextOffset;
    public final @ColorInt int valueTextColour;

    public final float tickValue;
    public final int ticksCount;
    
    public final float divisionValue;
    public final int divisionWidth;
    public final int divisionLineWidth;
    public final int divisionLineHeight;
    public final int divisionTextSize;
    public final int divisionTextMargin;
    public final int divisionTextOffset;
    public final @ColorInt int inRangeDivisionColour;
    public final @ColorInt int outOfRangeDivisionColour;
    public final @ColorInt int divisionTextColour;

    public final float subdivisionValue;
    public final int subdivisionsCount;
    public final int subdivisionWidth;
    public final int subdivisionLineWidth;
    public final int subdivisionLineHeight;
    public final @ColorInt int inRangeSubdivisionColour;
    public final @ColorInt int outOfRangeSubdivisionColour;

    public final int indicatorTriangleWidth;
    public final int indicatorTriangleHeight;
    public final int indicatorTriangleOffset;
    public final boolean shouldShowIndicatorNeedle;
    public final @ColorInt int indicatorColour;

    public final int borderWidth;
    public final int borderColour;

    public final int curveHeight;
    
    public Attributes(Context context, AttributeSet attrs, int[] styleableRes, int defStyle, int defStyleRes) {
        final TypedArray a = context.obtainStyledAttributes(attrs, styleableRes, defStyle, defStyleRes);

        this.orientation = a.getInteger(ORIENTATION_ID, 0);
        this.curveHeight = a.getDimensionPixelSize(CURVE_HEIGHT_ID, 15);
        
        this.unit = a.getString(UNIT_LABEL_ID);
        this.unitTextSize = a.getDimensionPixelSize(UNIT_LABEL_TEXT_SIZE_ID, 32);
        this.unitTextColour = a.getColor(UNIT_LABEL_TEXT_COLOUR_ID, Color.BLACK);
        this.unitTextMargin = a.getDimensionPixelSize(UNIT_LABEL_MARGIN_ID, 0);
        this.shouldDrawUnit = (unit != null && !unit.equals(""));
        
        this.tickValue = a.getFloat(TICK_VALUE_ID, 1);
        this.ticksCount = a.getInteger(TICKS_COUNT_PER_SUBDIVISION_ID, 2);
        
        this.subdivisionWidth = a.getDimensionPixelSize(SUBDIVISION_WIDTH_ID, 20);
        this.subdivisionsCount = a.getInteger(SUBDIVISIONS_COUNT_PER_DIVISION_ID, 5);
        this.subdivisionLineWidth = a.getDimensionPixelSize(SUBDIVISION_LINE_WIDTH_ID, 4);
        this.subdivisionLineHeight = a.getDimensionPixelSize(SUBDIVISION_LINE_HEIGHT_ID, 30);
        this.subdivisionValue = tickValue * ticksCount;
        this.inRangeSubdivisionColour = a.getColor(SUBDIVISION_LINE_IN_RANGE_COLOUR_ID, Color.BLACK);
        this.outOfRangeSubdivisionColour = a.getColor(SUBDIVISION_LINE_OUT_OF_RANGE_COLOUR_ID, Color.GRAY);

        this.divisionValue = subdivisionsCount * subdivisionValue;
        this.divisionWidth = subdivisionsCount * subdivisionWidth;
        this.divisionLineWidth = a.getDimensionPixelSize(DIVISION_LINE_WIDTH_ID, 6);
        this.divisionLineHeight = a.getDimensionPixelSize(DIVISION_LINE_HEIGHT_ID, 50);
        this.divisionTextSize = a.getDimensionPixelOffset(DIVISION_TEXT_SIZE_ID, 0);
        this.divisionTextColour = a.getColor(DIVISION_TEXT_COLOR_ID, 0);
        this.divisionTextMargin = a.getDimensionPixelOffset(DIVISION_TEXT_MARGIN_ID, 0);
        this.divisionTextOffset = divisionLineHeight + divisionTextMargin;
        this.inRangeDivisionColour = a.getColor(DIVISION_LINE_IN_RANGE_COLOUR_ID, Color.BLACK);
        this.outOfRangeDivisionColour = a.getColor(DIVISION_LINE_OUT_OF_RANGE_COLOUR_ID, Color.GRAY);
        
        this.indicatorTriangleWidth = a.getDimensionPixelSize(INDICATOR_TRIANGLE_WIDTH_ID, 20);
        this.indicatorTriangleOffset = a.getDimensionPixelSize(INDICATOR_OFFSET_ID, 4);
        this.indicatorColour = a.getColor(INDICATOR_COLOUR_ID, Color.RED);
        this.shouldShowIndicatorNeedle = a.getBoolean(INDICATOR_SHOW_NEEDLE_ID, true);
        this.indicatorTriangleHeight = indicatorTriangleWidth / 2;

        this.borderWidth = a.getDimensionPixelSize(BORDER_WIDTH_ID, 6);
        this.borderColour = a.getColor(BORDER_COLOUR_ID, Color.BLACK);

        this.value = a.getFloat(VALUE_ID, 50);
        this.minValue = a.getFloat(MIN_VALUE_ID, 0);
        this.maxValue = a.getFloat(MAX_VALUE_ID, 100);
        this.valueTextSize = a.getDimensionPixelSize(VALUE_TEXT_SIZE_ID, 48);
        this.valueTextColour = a.getColor(VALUE_TEXT_COLOUR_ID, Color.BLACK);
        this.valueTextMargin = a.getDimensionPixelSize(VALUE_TEXT_MARGIN_ID, 0);
        this.valueTextOffset = indicatorTriangleHeight + indicatorTriangleOffset + valueTextMargin;

        a.recycle();
    }
}
