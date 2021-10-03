package com.arcm.scalenumberpicker;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextPaint;

import java.text.DecimalFormat;

public abstract class ScaleDesign {
    private final Attributes attrs;
    private final DecimalFormat decimalFormat = new DecimalFormat("0.##");

    private final TextPaint divisionTextPaint;
    private final TextPaint valueTextPaint;
    private final TextPaint unitTextPaint;

    private final Paint inRangeSubdivisionPaint;
    private final Paint outOfRangeSubdivisionPaint;
    private final Paint inRangeDivisionPaint;
    private final Paint outOfRangeDivisionPaint;
    private final Paint borderPaint;
    private final Paint indicatorTrianglePaint;
    private final Paint indicatorNeedlePaint;
    private final Path path;

    private int width = 0;
    private int height = 0;
    private int marginLeft;
    private int marginRight;
    private int marginTop;
    private int marginBottom;

    private final int valueTextWidth;
    private final int valueTextHeight;
    private final int divisionTextWidth;
    private final int divisionTextHeight;
    private final int unitTextWidth;
    private final int unitTextHeight;
    private final int unitTextBaseLine;

    private float val;

    private boolean roundedValues = true;

    public ScaleDesign(Attributes attrs) {
        this.attrs = attrs;
        this.val = attrs.value;
        this.path = new Path();

        // Set up default TextPaint objects
        this.divisionTextPaint = newTextPaint(attrs.divisionTextColour, attrs.divisionTextSize);
        this.valueTextPaint = newTextPaint(attrs.valueTextColour, attrs.valueTextSize);
        this.unitTextPaint = newTextPaint(attrs.unitTextColour, attrs.unitTextSize);
        unitTextPaint.setTextAlign(Paint.Align.CENTER);

        // Set up default Paint objects
        this.inRangeSubdivisionPaint = newPaint(Paint.Style.STROKE, attrs.inRangeSubdivisionColour, attrs.subdivisionLineWidth);
        this.outOfRangeSubdivisionPaint = newPaint(Paint.Style.STROKE, attrs.outOfRangeSubdivisionColour, attrs.subdivisionLineWidth);
        this.inRangeDivisionPaint = newPaint(Paint.Style.STROKE, attrs.inRangeDivisionColour, attrs.divisionLineWidth);
        this.outOfRangeDivisionPaint = newPaint(Paint.Style.STROKE, attrs.outOfRangeSubdivisionColour, attrs.divisionLineWidth);
        this.borderPaint = newPaint(Paint.Style.STROKE, attrs.borderColour, attrs.borderWidth);
        this.indicatorTrianglePaint = newPaint(Paint.Style.FILL, attrs.indicatorColour, 0);
        this.indicatorNeedlePaint = newPaint(Paint.Style.STROKE, attrs.indicatorColour, attrs.subdivisionLineWidth);

        Rect bounds = new Rect();
        String text = formatDecimal(attrs.maxValue + attrs.tickValue);
        valueTextPaint.getTextBounds(text, 0, text.length(), bounds);
        valueTextHeight = bounds.height();
        valueTextWidth = bounds.width();
        if (attrs.shouldDrawUnit) {
            unitTextPaint.getTextBounds(attrs.unit, 0, attrs.unit.length(), bounds);
            unitTextHeight = bounds.height();
            unitTextWidth = bounds.width();
            Paint.FontMetrics fm = unitTextPaint.getFontMetrics();
            this.unitTextBaseLine = (int) fm.bottom;
        } else {
            unitTextHeight = 0;
            unitTextWidth = 0;
            unitTextBaseLine = 0;
        }
        divisionTextPaint.getTextBounds(text, 0, text.length(), bounds);
        divisionTextHeight = bounds.height();
        divisionTextWidth = bounds.width();
    }

    private Paint newPaint(Paint.Style style, int colour, int strokeWidth) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(style);
        paint.setColor(colour);
        if (style == Paint.Style.STROKE) paint.setStrokeWidth(strokeWidth);
        return paint;
    }

    private TextPaint newTextPaint(int color, int size) {
        TextPaint tp = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        tp.setColor(color);
        tp.setTextSize(size);
        return tp;
    }

    protected int measureViewContentHeight() {
        int height = marginTop + valueTextHeight + attrs.valueTextOffset;
        height += attrs.divisionTextOffset + divisionTextHeight + marginBottom;
        if (attrs.shouldDrawUnit) {
            height += unitTextHeight + attrs.unitTextMargin;
        }
        return height;
    }

    protected int measureViewContentWidth() {
        int width = marginLeft + getValueTextWidth() + attrs.valueTextOffset;
        width += attrs.borderWidth + attrs.divisionTextOffset + getDivisionTextWidth() + marginRight;
        if (attrs.shouldDrawUnit) {
            width = width - getValueTextWidth() + Math.max(getValueTextWidth(), getUnitTextWidth());
        }
        return width;
    }

    protected void drawInRangeDivision(Canvas canvas, Point p1, Point p2) {
        canvas.drawLine(p1.x, p1.y, p2.x, p2.y, inRangeDivisionPaint);
    }

    protected void drawInRangeDivision(Canvas canvas, HorizontalCircularScale.LineSegment s) {
        canvas.drawLine(s.p1.x, s.p1.y, s.p2.x, s.p2.y, inRangeDivisionPaint);
    }

    protected void drawOutOfRangeDivision(Canvas canvas, Point p1, Point p2) {
        canvas.drawLine(p1.x, p1.y, p2.x, p2.y, outOfRangeDivisionPaint);
    }

    protected void drawOutOfRangeDivision(Canvas canvas, HorizontalCircularScale.LineSegment s) {
        canvas.drawLine(s.p1.x, s.p1.y, s.p2.x, s.p2.y, outOfRangeDivisionPaint);
    }

    protected void drawInRangeSubdivision(Canvas canvas, Point p1, Point p2) {
        canvas.drawLine(p1.x, p1.y, p2.x, p2.y, inRangeSubdivisionPaint);
    }

    protected void drawInRangeSubdivision(Canvas canvas, HorizontalCircularScale.LineSegment s) {
        canvas.drawLine(s.p1.x, s.p1.y, s.p2.x, s.p2.y, inRangeSubdivisionPaint);
    }

    protected void drawOutOfRangeSubdivision(Canvas canvas, Point p1, Point p2) {
        canvas.drawLine(p1.x, p1.y, p2.x, p2.y, outOfRangeSubdivisionPaint);
    }

    protected void drawOutOfRangeSubdivision(Canvas canvas, HorizontalCircularScale.LineSegment s) {
        canvas.drawLine(s.p1.x, s.p1.y, s.p2.x, s.p2.y, outOfRangeSubdivisionPaint);
    }

    protected void drawValueText(Canvas canvas, Point p, String text) {
        canvas.drawText(text, p.x, p.y, valueTextPaint);
    }

    protected void drawValueText(Canvas canvas, PointF p, String text) {
        canvas.drawText(text, p.x, p.y, valueTextPaint);
    }

    protected void drawUnitText(Canvas canvas, Point p, String text) {
        canvas.drawText(text, p.x, p.y, unitTextPaint);
    }

    protected void drawUnitText(Canvas canvas, PointF p, String text) {
        canvas.drawText(text, p.x, p.y, unitTextPaint);
    }

    protected void drawDivisionText(Canvas canvas, Point p, String text) {
        canvas.drawText(text, p.x, p.y, divisionTextPaint);
    }

    protected void drawDivisionText(Canvas canvas, PointF p, String text) {
        canvas.drawText(text, p.x, p.y, divisionTextPaint);
    }

    protected int getUnitTextPaintBaselineShift() {
        return unitTextBaseLine;
    }

    protected void drawBorder(Canvas canvas, Point p1, Point p2, Point p3, Point p4) {
        path.reset();
        path.moveTo(p1.x, p1.y);
        path.lineTo(p2.x, p2.y);
        path.lineTo(p3.x, p3.y);
        path.lineTo(p4.x, p4.y);
        canvas.drawPath(path, borderPaint);
    }
    
    protected void drawIndicator(Canvas canvas, Point p1, Point p2, Point p3, Point p4) {
        path.reset();
        path.moveTo(p1.x, p1.y);
        path.lineTo(p2.x, p2.y);
        path.lineTo(p3.x, p3.y);
        path.close();
        canvas.drawPath(path, indicatorTrianglePaint);
        canvas.drawLine(p3.x, p3.y, p4.x, p4.y, indicatorNeedlePaint);
    }

    protected void drawIndicator(Canvas canvas, PointF p1, PointF p2, PointF p3, PointF p4) {
        path.reset();
        path.moveTo(p1.x, p1.y);
        path.lineTo(p2.x, p2.y);
        path.lineTo(p3.x, p3.y);
        path.close();
        canvas.drawPath(path, indicatorTrianglePaint);
        if (attrs.shouldShowIndicatorNeedle) {
            canvas.drawLine(p3.x, p3.y, p4.x, p4.y, indicatorNeedlePaint);
        }
    }

    protected void drawBorderArc(Canvas canvas, float cx, float cy, double radius, RectF bounds) {
        canvas.save();
        canvas.clipRect(bounds);
        canvas.drawCircle(cx, cy, (float) radius, borderPaint);
        canvas.restore();
    }

    protected void drawBorderEdge(Canvas canvas, HorizontalCircularScale.LineSegment s) {
        canvas.drawLine(s.p1.x, s.p1.y, s.p2.x, s.p2.y, borderPaint);
    }

    protected void setValueTextAlignment(Paint.Align align) {
        valueTextPaint.setTextAlign(align);
    }

    protected void setDivisionTextAlignment(Paint.Align align) {
        divisionTextPaint.setTextAlign(align);
    }

    protected int getDivisionTextWidth() {
        return divisionTextWidth;
    }

    protected int getDivisionTextHeight() {
        return divisionTextHeight;
    }

    protected int getValueTextWidth() {
        return valueTextWidth;
    }

    protected int getValueTextHeight() {
        return valueTextHeight;
    }

    protected int getUnitTextWidth() {
        return unitTextWidth;
    }

    protected int getUnitTextHeight() {
        return unitTextHeight;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public float getValue() {
        return val;
    }

    public void setValue(float val) {
        this.val = val;
    }

    public int getMarginLeft() {
        return marginLeft;
    }

    public void setMarginLeft(int marginLeft) {
        this.marginLeft = marginLeft;
    }

    public int getMarginRight() {
        return marginRight;
    }

    public void setMarginRight(int marginRight) {
        this.marginRight = marginRight;
    }

    public int getMarginTop() {
        return marginTop;
    }

    public void setMarginTop(int marginTop) {
        this.marginTop = marginTop;
    }

    public int getMarginBottom() {
        return marginBottom;
    }

    public void setMargins(int left, int top, int right, int bottom) {
        setMarginLeft(left);
        setMarginRight(right);
        setMarginTop(top);
        setMarginBottom(bottom);
    }

    public void setDimensions(int width, int height) {
        setWidth(width);
        setHeight(height);
    }

    public void setMarginBottom(int marginBottom) {
        this.marginBottom = marginBottom;
    }

    public String formatDecimal(float val) {
        return decimalFormat.format(val);
    }

    public String formatValue(float val) {
        if (!roundedValues) {
            return formatDecimal(val);
        }
        val = Math.round(val / attrs.tickValue) * attrs.tickValue;
        if (attrs.tickValue % 1 == 0) {
            return String.valueOf((int) val);
        }
        return formatDecimal(val);
    }

    public String formatDecimal(double val) {
        return decimalFormat.format(val);
    }

    public void setRoundedValues(boolean rounded) {
        this.roundedValues = rounded;
    }

    protected abstract void notifyDimensionsChanged();

    protected abstract void draw(Canvas canvas);
}
