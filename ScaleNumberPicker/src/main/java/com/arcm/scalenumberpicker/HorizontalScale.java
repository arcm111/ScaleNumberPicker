package com.arcm.scalenumberpicker;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

public class HorizontalScale extends ScaleDesign {
    public static final String TAG = "HorizontalScale";

    private final Attributes attrs;
    private final Point p1 = new Point();
    private final Point p2 = new Point();
    private final Point p3 = new Point();
    private final Point p4 = new Point();

    private int cx, cy, x0, x1;
    private final Point valueLocation = new Point();
    private final Point unitLocation = new Point();

    public HorizontalScale(Attributes attrs) {
        super(attrs);
        this.attrs = attrs;
        setValueTextAlignment(Paint.Align.CENTER);
        setDivisionTextAlignment(Paint.Align.CENTER);
    }

    @Override
    protected void draw(Canvas canvas) {
        // distance between pointer and previous division line
        float centerOffset = ((getValue() % attrs.divisionValue) / attrs.subdivisionValue) * attrs.subdivisionWidth;
        drawValueText(canvas, valueLocation, formatValue(getValue()));
        if (attrs.shouldDrawUnit) {
            drawUnitText(canvas, unitLocation, attrs.unit);
        }
        drawRulerTicks(canvas, cx, cy, x0, x1, (int) centerOffset);
        drawRulerBoundaries(canvas, x0, x1, cy);
        drawRulerIndicator(canvas, cx, cy);
    }

    @Override
    protected int measureViewContentWidth() {
        return getMarginLeft() + getWidth() + getMarginRight();
    }

    /**
     * Draws division and subdivision lines.
     * Using centerOffset as a pivot it draws left half first then the right half.
     * Always starts by drawing the division line located at the pivot and then moves right or left.
     * @param canvas canvas to be drawn upon
     * @param cx horizontal center of the ruler
     * @param cy vertical center of the ruler
     * @param x0 leftmost point on the ruler
     * @param x1 rightmost point on the ruler
     * @param centerOffset the pivot location
     */
    private void drawRulerTicks(Canvas canvas, int cx, int cy, int x0, int x1, int centerOffset) {
        float curVal = getValue() - (getValue() % attrs.divisionValue);
        for (int d = cx - centerOffset; d > x0; d -= attrs.divisionWidth) {
            p1.set(d, cy);
            p2.set(d, cy + attrs.divisionLineHeight);
            if (curVal < attrs.minValue) {
                drawOutOfRangeDivision(canvas, p1, p2);
            } else drawInRangeDivision(canvas, p1, p2);
            for (int t = d - attrs.subdivisionWidth; t > Math.max(d - attrs.divisionWidth, x0); t -= attrs.subdivisionWidth) {
                p1.set(t, cy);
                p2.set(t, cy + attrs.subdivisionLineHeight);
                if (curVal <= attrs.minValue) {
                    drawOutOfRangeSubdivision(canvas, p1, p2);
                } else drawInRangeSubdivision(canvas, p1, p2);
            }
            p1.set(d, cy + attrs.divisionTextOffset);
            if (curVal >= attrs.minValue) drawDivisionText(canvas, p1, formatDecimal(curVal));
            curVal -= attrs.divisionValue;
        }

        curVal = getValue() - (getValue() % attrs.divisionValue);
        for (int d = cx - centerOffset; d < x1; d += attrs.divisionWidth) {
            p1.set(d, cy);
            p2.set(d, cy + attrs.divisionLineHeight);
            if (curVal > attrs.maxValue) {
                drawOutOfRangeDivision(canvas, p1, p2);
            } else drawInRangeDivision(canvas, p1, p2);
            for (int t = d + attrs.subdivisionWidth; t < Math.min(d + attrs.divisionWidth, x1); t += attrs.subdivisionWidth) {
                p1.set(t, cy);
                p2.set(t, cy + attrs.subdivisionLineHeight);
                if (curVal >= attrs.maxValue) {
                    drawOutOfRangeSubdivision(canvas, p1, p2);
                } else drawInRangeSubdivision(canvas, p1, p2);
            }
            p1.set(d, cy + attrs.divisionTextOffset);
            if (curVal <= attrs.maxValue) drawDivisionText(canvas, p1, formatDecimal(curVal));
            curVal += attrs.divisionValue;
        }
    }

    private void drawRulerIndicator(Canvas canvas, int cx, int cy) {
        int d = attrs.indicatorTriangleWidth / 2;
        p1.set(cx - d, cy - attrs.indicatorTriangleOffset - attrs.indicatorTriangleHeight);
        p2.set(cx + d, cy - attrs.indicatorTriangleOffset - attrs.indicatorTriangleHeight);
        p3.set(cx, cy - attrs.indicatorTriangleOffset);
        p4.set(cx, cy + attrs.divisionLineHeight);
        drawIndicator(canvas, p1, p2, p3, p4);
    }

    private void drawRulerBoundaries(Canvas canvas, int x0, int x1, int cy) {
        p1.set(x0, cy + attrs.divisionLineHeight);
        p2.set(x0, cy);
        p3.set(x1, cy);
        p4.set(x1, cy + attrs.divisionLineHeight);
        drawBorder(canvas, p1, p2, p3, p4);
    }

    @Override
    protected void notifyDimensionsChanged() {
        int height = getHeight() - getMarginTop() + getMarginBottom();
        int contentHeight = measureViewContentHeight();
        if (height > contentHeight) {
            setHeight(contentHeight);
            setMarginTop(getMarginTop() + (height - contentHeight) / 2);
            setMarginBottom(getMarginBottom() + (height - contentHeight) / 2);
        }

        this.cx = getMarginLeft() + getWidth() / 2;
        int cy = getMarginTop() + getValueTextHeight() + attrs.valueTextOffset;
        if (attrs.shouldDrawUnit) {
            cy += getUnitTextHeight() + attrs.unitTextMargin;
        }
        this.cy = cy;

        // Draw scale's value text and unit
        valueLocation.set(cx, getMarginTop() + getValueTextHeight());
        if (attrs.shouldDrawUnit) {
            int unitY = getMarginTop() + getValueTextHeight() + attrs.valueTextMargin + getUnitTextHeight();
            unitLocation.set(cx, unitY);
        }

        // Draw divisions and subdivisions
        this.x0 = Math.max(getMarginLeft(), attrs.divisionLineWidth / 2);
        this.x1 = getMarginLeft() + getWidth();
    }
}
