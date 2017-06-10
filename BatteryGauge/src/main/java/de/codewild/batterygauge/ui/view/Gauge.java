package de.codewild.batterygauge.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;


public class Gauge extends ProgressRing {

    public Gauge(final Context context) {
        this(context, null);
    }

    public Gauge(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void draw(final Canvas canvas, final RectF visibleArea, final int mArcAngle, final int mArcAngleOffset, final int mMaxArcAngle, final Paint mArcPaint, final Paint mRingPaint, final int mProgress, final float mVisualProgress) {
        this.calculateColor();
        final float value = this.getVisualProgress();
        final float percent = value / 100f;
        final float angle = mArcAngleOffset + mMaxArcAngle * percent;

        float cX = visibleArea.centerX();
        float cY = visibleArea.centerY();

        float x = (float) (visibleArea.width() / 2 * Math.cos(angle * Math.PI / 180f));
        float y = (float) (visibleArea.height() / 2 * Math.sin(angle * Math.PI / 180f));

        canvas.drawLine(
                cX,
                cY,
                cX + x,
                cY + y,
                mArcPaint);
    }
}
