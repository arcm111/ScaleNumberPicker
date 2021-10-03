package com.arcm.scalenumberpicker;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

public class VerticalLeftScale extends ScaleDesign {
    public static final String TAG = "VerticalLeftScale";

    private final Attributes attrs;
    private final Point p1 = new Point();
    private final Point p2 = new Point();
    private final Point p3 = new Point();
    private final Point p4 = new Point();

    private final Point valueLocation = new Point();
    private final Point unitLocation = new Point();
    private int cx, cy, y0, y1;

    public VerticalLeftScale(Attributes attrs) {
        super(attrs);
        this.attrs = attrs;
        setValueTextAlignment(Paint.Align.CENTER);
        setDivisionTextAlignment(Paint.Align.RIGHT);
    }

    @Override
    protected void draw(Canvas canvas) {
        float centerOffset = (getValue() % attrs.divisionValue) / attrs.subdivisionValue * attrs.subdivisionWidth;
        drawValueText(canvas, valueLocation, formatValue(getValue()));
        if (attrs.shouldDrawUnit) {
            drawUnitText(canvas, unitLocation, attrs.unit);
        }
        drawRulerTicks(canvas, cx, cy, y0, y1, (int) centerOffset);
        drawRulerBoundaries(canvas, cx, y0, y1);
        drawRulerIndicator(canvas, cx, cy);
    }

    @Override
    protected int measureViewContentHeight() {
        return getMarginTop() + getHeight() + getMarginBottom();
    }

    private void drawRulerTicks(Canvas canvas, int cx, int cy, int y0, int y1, int centerOffset) {
        float curVal = getValue() - (getValue() % attrs.divisionValue);
        for (int d = cy + centerOffset; d > y0; d -= attrs.divisionWidth) {
            p1.set(cx, d);
            p2.set(cx - attrs.divisionLineHeight, d);
            if (curVal > attrs.maxValue) {
                drawOutOfRangeDivision(canvas, p1, p2);
            } else drawInRangeDivision(canvas, p1, p2);
            for (int t = d - attrs.subdivisionWidth; t > Math.max(d - attrs.divisionWidth, y0); t -= attrs.subdivisionWidth) {
                p1.set(cx, t);
                p2.set(cx - attrs.subdivisionLineHeight, t);
                if (curVal >= attrs.maxValue) {
                    drawOutOfRangeSubdivision(canvas, p1, p2);
                } else drawInRangeSubdivision(canvas, p1, p2);
            }
            p1.set(cx - attrs.divisionTextOffset, d + getDivisionTextHeight() / 2);
            if (curVal <= attrs.maxValue) drawDivisionText(canvas, p1, formatDecimal(curVal));
            curVal += attrs.divisionValue;
        }

        curVal = getValue() - (getValue() % attrs.divisionValue);
        for (int d = cy + centerOffset; d < y1; d += attrs.divisionWidth) {
            p1.set(cx, d);
            p2.set(cx - attrs.divisionLineHeight, d);
            if (curVal < attrs.minValue) {
                drawOutOfRangeDivision(canvas, p1, p2);
            } else drawInRangeDivision(canvas, p1, p2);
            for (int t = d + attrs.subdivisionWidth; t < Math.min(d + attrs.divisionWidth, y1); t += attrs.subdivisionWidth) {
                p1.set(cx, t);
                p2.set(cx - attrs.subdivisionLineHeight, t);
                if (curVal <= attrs.minValue) {
                    drawOutOfRangeSubdivision(canvas, p1, p2);
                } else drawInRangeSubdivision(canvas, p1, p2);
            }
            p1.set(cx - attrs.divisionTextOffset, d + getDivisionTextHeight() / 2);
            if (curVal >= attrs.minValue) drawDivisionText(canvas, p1, formatDecimal(curVal));
            curVal -= attrs.divisionValue;
        }
    }

    private void drawRulerIndicator(Canvas canvas, int cx, int cy) {
        int d = attrs.indicatorTriangleWidth / 2;
        p1.set(cx + attrs.indicatorTriangleOffset + attrs.indicatorTriangleHeight, cy - d);
        p2.set(cx + attrs.indicatorTriangleOffset + attrs.indicatorTriangleHeight, cy + d);
        p3.set(cx + attrs.indicatorTriangleOffset, cy);
        p4.set(cx - attrs.divisionLineHeight, cy);
        drawIndicator(canvas, p1, p2, p3, p4);
    }

    private void drawRulerBoundaries(Canvas canvas, int cx, int y0, int y1) {
        p1.set(cx - attrs.divisionLineHeight, y0);
        p2.set(cx, y0);
        p3.set(cx, y1);
        p4.set(cx - attrs.divisionLineHeight, y1);
        drawBorder(canvas, p1, p2, p3, p4);
    }

    @Override
    protected void notifyDimensionsChanged() {
        int width = getWidth() - getMarginLeft() - getMarginRight();
        int contentWidth = measureViewContentWidth();
        if (width > contentWidth) {
            setWidth(width);
            setMarginLeft(getMarginLeft() + (width - contentWidth) / 2);
            setMarginRight(getMarginRight() + (width - contentWidth) / 2);
        }

        this.cx = getMarginLeft() + getDivisionTextWidth() + attrs.divisionTextOffset;
        this.cy = getMarginTop() + getHeight() / 2;

        int offsetX = Math.max(getValueTextWidth(), getUnitTextWidth()) / 2 + attrs.valueTextOffset;
        valueLocation.set(cx + offsetX, cy + getValueTextHeight() / 2);
         if (attrs.shouldDrawUnit) {
            int h = attrs.unitTextMargin + getUnitTextHeight() - getUnitTextPaintBaselineShift();
            unitLocation.set(cx + offsetX, cy + h + getValueTextHeight() / 2);
        }

        this.y0 = getMarginTop() + attrs.borderWidth / 2;
        this.y1 = getMarginTop() + getHeight() - attrs.borderWidth / 2;
    }
}
