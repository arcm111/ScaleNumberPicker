package com.arcm.scalenumberpicker;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;

public class HorizontalCircularScale extends ScaleDesign {
    public static final String TAG = "HorizontalCircularScale";

    private final Attributes attrs;
    private final PointF p1;
    private final PointF p2;
    private final PointF p3;
    private final PointF p4;
    private final LineSegment segment;
    private final RectF arcBounds;

    private float radius;
    private double angle;
    private float originX;
    private float originY;
    private double tickAngle;
    private double subdivisionAngle;
    private double divisionAngle;

    private int cx, cy;
    private final PointF valueLocation = new PointF();
    private final PointF unitLocation = new PointF();

    public HorizontalCircularScale(Attributes attrs) {
        super(attrs);
        this.attrs = attrs;
        this.p1 = new PointF();
        this.p2 = new PointF();
        this.p3 = new PointF();
        this.p4 = new PointF();
        this.segment = new LineSegment(p1, p2);
        this.arcBounds = new RectF();
        setValueTextAlignment(Paint.Align.CENTER);
        setDivisionTextAlignment(Paint.Align.CENTER);
    }

    private void setParameters() {
        int width = getWidth() - attrs.borderWidth;
        int contentHeight = measureViewContentHeight();
        int height = getHeight() - getMarginTop() - getMarginBottom();
        if (height > contentHeight) {
            setHeight(contentHeight);
            setMarginTop(getMarginTop() + (height - contentHeight) / 2);
            setMarginBottom(getMarginBottom() + (height - contentHeight) / 2);
        }
        this.radius = calculateRadius(width, attrs.curveHeight);
        this.angle = calculateAngle(width, attrs.curveHeight);
        this.originX = getMarginLeft() + getWidth() / 2.0f;
        this.originY = getMarginTop() + attrs.indicatorTriangleHeight + attrs.indicatorTriangleOffset + radius;
        // angle = arcLength / radius
        this.tickAngle = (attrs.subdivisionWidth / radius) / attrs.ticksCount;
        this.subdivisionAngle = tickAngle * attrs.ticksCount;
        this.divisionAngle = subdivisionAngle * attrs.subdivisionsCount;
        updateArcBounds();
    }

    private void updateArcBounds() {
        arcBounds.left = getMarginLeft() + attrs.borderWidth / 2.0f;
        arcBounds.right = getMarginLeft() + getWidth() - attrs.borderWidth / 2.0f;
        arcBounds.top = getMarginTop();
        arcBounds.bottom = getMarginTop() + getHeight();
    }

    @Override
    protected void draw(Canvas canvas) {
        double centerOffset = -((getValue() % attrs.divisionValue) / attrs.tickValue) * tickAngle;
        drawValueText(canvas, valueLocation, formatValue(getValue()));
        if (attrs.shouldDrawUnit) {
            drawUnitText(canvas, unitLocation, attrs.unit);
        }
        drawRulerTicks(canvas, centerOffset, -angle, angle);
        drawRulerBoundaries(canvas, originX, originY, radius, angle);
        drawRulerIndicator(canvas, cx, cy);
    }

    private float calculateRadius(int curveWidth, int curveHeight) {
        return (curveHeight / 2.0f) + ((curveWidth * curveWidth) / (8.0f * curveHeight));
    }

    private double calculateAngle(int curveWidth, int curveHeight) {
        return 2 * Math.atan2(curveHeight, curveWidth / 2.0);
    }

    private void relativeCoords(PointF p) {
        p.set(p.x + originX, originY - p.y);
    }

    private LineSegment createSegment(LineSegment s, double a, int length) {
        s.p1.x = (float) ((radius + attrs.borderWidth / 2.0) * Math.sin(a));
        s.p1.y = (float) ((radius + attrs.borderWidth / 2.0) * Math.cos(a));
        relativeCoords(s.p1);
        s.p2.x = (float) ((radius - length) * Math.sin(a));
        s.p2.y = (float) ((radius - length) * Math.cos(a));
        relativeCoords(s.p2);
        return s;
    }
    
    private PointF createCoords(PointF p, double a, double r) {
        p.x = (float) (r * Math.sin(a));
        p.y = (float) (r * Math.cos(a));
        relativeCoords(p);
        return p;
    }

    @Override
    protected int measureViewContentWidth() {
        return getMarginLeft() + getWidth() + getMarginRight();
    }

    @Override
    protected int measureViewContentHeight() {
        int height = getMarginTop() + attrs.indicatorTriangleHeight + attrs.indicatorTriangleOffset;
        int v = attrs.divisionTextOffset + getDivisionTextHeight() + attrs.valueTextMargin;
        v += getValueTextHeight() + attrs.unitTextMargin + getUnitTextHeight();
        height += Math.max(attrs.curveHeight + attrs.divisionLineHeight, v) + getMarginBottom();
        return height;
    }

    private void drawRulerTicks(Canvas canvas, double angleOffset, double a0, double a1) {
        double curVal = getValue() - (getValue() % attrs.divisionValue);
        for (double d = angleOffset; d > a0; d -= divisionAngle) {
            if (curVal < attrs.minValue) {
                drawOutOfRangeDivision(canvas, createSegment(segment, d, attrs.divisionLineHeight));
            } else drawInRangeDivision(canvas, createSegment(segment, d, attrs.divisionLineHeight));
            for (double t = d - subdivisionAngle; t > Math.max(d - divisionAngle, a0); t -= subdivisionAngle) {
                if (curVal <= attrs.minValue) {
                    drawOutOfRangeSubdivision(canvas, createSegment(segment, t, attrs.subdivisionLineHeight));
                } else drawInRangeSubdivision(canvas, createSegment(segment, t, attrs.subdivisionLineHeight));
            }
            PointF p = createCoords(p1, d, radius - attrs.divisionTextOffset);
            if (curVal >= attrs.minValue) drawDivisionText(canvas, p, formatDecimal(curVal));
            curVal -= attrs.divisionValue;
        }

        curVal = getValue() - (getValue() % attrs.divisionValue);
        for (double d = angleOffset; d < a1; d += divisionAngle) {
            if (curVal > attrs.maxValue) {
                drawOutOfRangeDivision(canvas, createSegment(segment, d, attrs.divisionLineHeight));
            } else drawInRangeDivision(canvas, createSegment(segment, d, attrs.divisionLineHeight));
            for (double t = d + subdivisionAngle; t < Math.min(d + divisionAngle, a1); t += subdivisionAngle) {
                if (curVal >= attrs.maxValue) {
                    drawOutOfRangeSubdivision(canvas, createSegment(segment, t, attrs.subdivisionLineHeight));
                } else drawInRangeSubdivision(canvas, createSegment(segment, t, attrs.subdivisionLineHeight));
            }
            PointF p = createCoords(p1, d, radius - attrs.divisionTextOffset);
            if (curVal <= attrs.maxValue) drawDivisionText(canvas, p, formatDecimal(curVal));
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

    private void drawRulerBoundaries(Canvas canvas, float cx, float cy, double radius, double angle) {
        drawBorderArc(canvas, cx, cy, radius, arcBounds);
        drawBorderEdge(canvas, createSegment(segment, -angle, attrs.divisionLineHeight));
        drawBorderEdge(canvas, createSegment(segment, angle, attrs.divisionLineHeight));
    }

    public static class LineSegment {
        public final PointF p1;
        public final PointF p2;

        public LineSegment(PointF p1, PointF p2) {
            this.p1 = p1;
            this.p2 = p2;
        }
    }

    @Override
    protected void notifyDimensionsChanged() {
        setParameters();
        this.cx = getMarginLeft() + getWidth() / 2;
        this.cy = getMarginTop() + attrs.indicatorTriangleOffset + attrs.indicatorTriangleHeight;

        // Draw scale's value text
        int vy = cy + attrs.divisionTextOffset + getDivisionTextHeight() + attrs.valueTextMargin;
        valueLocation.set(cx, vy + getValueTextHeight());
        if (attrs.shouldDrawUnit) {
            vy += getValueTextHeight() + attrs.unitTextMargin;
            unitLocation.set(cx, vy + getUnitTextHeight() - getUnitTextPaintBaselineShift());
        }
    }
}
