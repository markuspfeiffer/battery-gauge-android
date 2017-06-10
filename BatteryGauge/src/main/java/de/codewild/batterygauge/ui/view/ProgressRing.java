package de.codewild.batterygauge.ui.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;

import java.util.TreeMap;

import de.codewild.batterygauge.R;
import de.codewild.batterygauge.ui.graphics.ColorInterpolator;
import de.codewild.batterygauge.ui.graphics.HsvInterpolator;
import de.codewild.batterygauge.ui.graphics.Screen;
import de.codewild.batterygauge.util.Range;


public class ProgressRing extends View {

    private static final int DEFAULT_MAX_ARC_ANGLE = 360;
    private static final int DEFAULT_ARC_ANGLE_OFFSET = 0;

    private static final int DEFAULT_ARC_ANGLE = 90;
    private static final int DEFAULT_ARC_COLOR = 0xff000000;

    private static final float DEFAULT_RING_WIDTH = Screen.dip2px(4);
    private static final int DEFAULT_RING_COLOR = 0x80c0c0c0;

    private static final boolean DEFAULT_IS_STARTED = true;
    private static final boolean DEFAULT_CLOCKWISE = true;
    private static final int DEFAULT_DURATION = 1000;

    private static final int[] VALUES_CW = {0, 360};
    private static final int[] VALUES_CCW = {360, 0};

    private Paint mRingPaint;

    private int mMaxArcAngle;
    private int mArcAngleOffset;

    private int mArcAngle;

    private int mArcColor;
    private Paint mArcPaint;

    private final TreeMap<Integer, Integer> mColorMap = new TreeMap<>();

    private boolean mIsStarted;
    private boolean mIsClockwise;
    private long mDuration;

    private final RectF mOval = new RectF();

    private volatile int mProgress = -1;
    private float mVisualProgress;

    private final ColorInterpolator mInterpolator = new HsvInterpolator();

    private ValueAnimator mSpinningAnimator;
    private ValueAnimator mProgressAnimator;

    private ProgressRingListener mListener;

    public ProgressRing(final Context context) {
        this(context, null);
    }

    public ProgressRing(final Context context, final AttributeSet attrs) {
        super(context, attrs);

        final TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.ProgressRing);

        try {
            this.initialize(attributes);
        } finally {
            attributes.recycle();
        }
    }

    private void initialize(final TypedArray attributes) {
        final float ringWidth = attributes.getDimension(R.styleable.ProgressRing_ringWidth, DEFAULT_RING_WIDTH);
        final int ringColor = attributes.getColor(R.styleable.ProgressRing_ringColor, DEFAULT_RING_COLOR);

        this.mMaxArcAngle = attributes.getInt(R.styleable.ProgressRing_arcAngleMaximum, DEFAULT_MAX_ARC_ANGLE);
        this.mArcAngleOffset = attributes.getInt(R.styleable.ProgressRing_arcAngleOffset, DEFAULT_ARC_ANGLE_OFFSET);

        this.mArcAngle = attributes.getInt(R.styleable.ProgressRing_arcAngle, DEFAULT_ARC_ANGLE);
        this.mArcColor = attributes.getColor(R.styleable.ProgressRing_arcColor, DEFAULT_ARC_COLOR);

        this.mProgress = attributes.getInt(R.styleable.ProgressRing_progress, -1);
        this.setVisualProgress(this.mProgress);

        this.mIsStarted = attributes.getBoolean(R.styleable.ProgressRing_animationStarted, DEFAULT_IS_STARTED);
        this.mIsClockwise = attributes.getBoolean(R.styleable.ProgressRing_clockwise, DEFAULT_CLOCKWISE);
        this.mDuration = attributes.getInt(R.styleable.ProgressRing_durationMillis, DEFAULT_DURATION);

        this.mArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.mArcPaint.setStrokeCap(Paint.Cap.ROUND);
        this.mArcPaint.setStyle(Paint.Style.STROKE);
        this.mArcPaint.setStrokeWidth(ringWidth);
        this.mArcPaint.setColor(this.mArcColor);

        this.mRingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.mRingPaint.setStrokeCap(Paint.Cap.ROUND);
        this.mRingPaint.setStyle(Paint.Style.STROKE);
        this.mRingPaint.setStrokeWidth(ringWidth);
        this.mRingPaint.setColor(ringColor);

        this.mSpinningAnimator = new ValueAnimator();
        this.mSpinningAnimator.setIntValues(this.mIsClockwise ? VALUES_CW : VALUES_CCW);
        this.mSpinningAnimator.setInterpolator(new LinearInterpolator());
        this.mSpinningAnimator.setRepeatCount(ValueAnimator.INFINITE);
        this.mSpinningAnimator.setRepeatMode(ValueAnimator.RESTART);
        this.mSpinningAnimator.setDuration(this.mDuration);

        this.mProgressAnimator = new ValueAnimator();
        this.mProgressAnimator.setInterpolator(new OvershootInterpolator());
        this.mProgressAnimator.addUpdateListener(new ProgressAnimationListener());
        this.mProgressAnimator.setDuration(this.mDuration);
    }

    protected float getVisualProgress() {
        return mVisualProgress;
    }

    private void setVisualProgress(float value) {
        this.mVisualProgress = value;
        if (this.mListener != null) {
            this.mListener.onVisualProgressChanged(value, this.mProgress);
        }
    }

    private void startAnimation() {
        if (this.mSpinningAnimator == null) {
            return;
        }

        if (this.mIsStarted && getVisibility() == View.VISIBLE && !this.mSpinningAnimator.isStarted()) {
            this.mSpinningAnimator.addUpdateListener(new SpinningAnimationListener());
            this.mSpinningAnimator.start();
        }
    }

    private void stopAnimation() {
        if (this.mSpinningAnimator == null) {
            return;
        }

        if (this.mSpinningAnimator.isStarted()) {
            this.mSpinningAnimator.removeAllUpdateListeners();
            this.mSpinningAnimator.cancel();
        }
    }

    @Override
    protected void onSizeChanged(final int w, final int h, final int oldw, final int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        final float width = this.mRingPaint.getStrokeWidth();
        final float offset = (float) Math.ceil(width / 2f) + 1;

        this.mOval.top = offset;
        this.mOval.left = offset;
        this.mOval.right = w - offset;
        this.mOval.bottom = h - offset;
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        draw(canvas, mOval, mArcAngle, mArcAngleOffset, mMaxArcAngle, mArcPaint, mRingPaint, mProgress, mVisualProgress);
    }

    protected void draw(final Canvas canvas, final RectF visibleArea, final int mArcAngle, final int mArcAngleOffset, final int mMaxArcAngle, final Paint mArcPaint, final Paint mRingPaint, final int mProgress, final float mVisualProgress) {
        canvas.drawArc(visibleArea, mArcAngleOffset, mMaxArcAngle, false, mRingPaint);

        if (this.mSpinningAnimator == null && mProgress < 0) {
            return;
        }

        if (mProgress >= 0) {
            this.calculateColor();
            final float value = Range.clamp(mVisualProgress, 0, 100);
            final float percent = value / 100f;
            final float angle = mMaxArcAngle * percent;
            canvas.drawArc(visibleArea, mArcAngleOffset, angle, false, mArcPaint);

        } else if (this.mSpinningAnimator != null) {
            final int value = (int) this.mSpinningAnimator.getAnimatedValue();
            canvas.drawArc(visibleArea, value, mArcAngle, false, mArcPaint);

        }
    }

    protected void calculateColor() {
        if (this.mColorMap.size() == 0) {
            return;
        }

        final int progress = Math.round(this.mVisualProgress);

        Integer startKey = this.mColorMap.floorKey(progress);
        Integer endKey = this.mColorMap.ceilingKey(progress);

        final int startColor;
        final int endColor;

        if (startKey == null) {
            startKey = 0;
            startColor = this.mArcColor;
        } else {
            startColor = this.mColorMap.get(startKey);
        }

        if (endKey == null) {
            endKey = 100;
            endColor = this.mArcColor;
        } else {
            endColor = this.mColorMap.get(endKey);
        }

        if (startColor == endColor) {
            this.mArcPaint.setColor(startColor);
            return;
        }

        final float delta = endKey - startKey;
        final float value = progress - startKey;
        final float proportion = value / delta;

        final int color = this.mInterpolator.interpolate(startColor, endColor, proportion);
        this.mArcPaint.setColor(color);
    }

    @Override
    protected void onVisibilityChanged(@NonNull final View changedView, final int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == View.VISIBLE) {
            this.startAnimation();
        } else {
            this.stopAnimation();
        }
    }

    /**
     * Set the width for stroking the ring and arc. Pass 0 to stroke in hairline mode. Hairlines
     * always draws a single pixel independent of the canvas' matrix.
     *
     * @param width The paint's stroke width.
     */
    public void setRingWidth(final float width) {
        if (width != this.mRingPaint.getStrokeWidth()) {
            this.mRingPaint.setStrokeWidth(width);
            this.mArcPaint.setStrokeWidth(width);
        }
    }

    /**
     * Gets the width, in pixels, used to stroke the ring and arc.
     * <p/>
     * A value of {@code 0} strokes in hairline mode. Hairlines always draws a single pixel
     * independent of the canvas' matrix.
     *
     * @return The paint's stroke width.
     */
    public float getRingWidth() {
        return this.mRingPaint.getStrokeWidth();
    }

    /**
     * Set the color to use for the ring's paint. Note that the color is an {@code int} containing
     * alpha as well as r,g,b. This 32 bit value is not premultiplied, meaning that its alpha can
     * be any value, regardless of the values of r,g,b. See the Color class for more details.
     *
     * @param color The new color (including alpha) to set in the paint.
     */
    public void setRingColor(final int color) {
        if (color != this.mRingPaint.getColor()) {
            this.mRingPaint.setColor(color);
        }
    }

    /**
     * Return the color used for the ring's paint. Note that the color is a 32 bit value containing
     * alpha as well as r,g,b. This 32 bit value is not premultiplied, meaning that its alpha can
     * be any value, regardless of the values of r,g,b. See the Color class for more details.
     *
     * @return The paint's color (and alpha).
     */
    public int getRingColor() {
        return this.mRingPaint.getColor();
    }

    /**
     * Set the color to use for the arc's paint. Note that the color is an {@code int} containing
     * alpha as well as r,g,b. This 32 bit value is not premultiplied, meaning that its alpha can
     * be any value, regardless of the values of r,g,b. See the Color class for more details.
     *
     * @param color The new color (including alpha) to set in the paint.
     */
    public void setArcColor(final int color) {
        if (color != this.mArcPaint.getColor()) {
            this.mArcPaint.setColor(color);
            this.mArcColor = color;
        }
    }

    /**
     * Return the color used for the arc's paint. Note that the color is a 32 bit value containing
     * alpha as well as r,g,b. This 32 bit value is not premultiplied, meaning that its alpha can
     * be any value, regardless of the values of r,g,b. See the Color class for more details.
     *
     * @return The paint's color (and alpha).
     */
    public int getArcColor() {
        return this.mArcColor;
    }

    public void putArcColorResource(final int progress, final int colorId) {
        final int color = ContextCompat.getColor(this.getContext(), colorId);
        this.putArcColor(progress, color);
    }

    public void putArcColor(final int progress, final int color) {
        this.mColorMap.put(progress, color);
        this.invalidate();
    }

    public void removeArcColor(final int progress) {
        this.mColorMap.remove(progress);
    }

    public int getArcColor(final int progress) {
        return this.mColorMap.get(progress);
    }

    public void setMaxArcAngle(final int angle) {
        this.mMaxArcAngle = angle;
    }

    public int getMaxArcAngle() {
        return this.mMaxArcAngle;
    }

    public void setArcAngleOffset(final int angle) {
        this.mArcAngleOffset = angle;
    }

    public int getArcAngleOffset() {
        return this.mArcAngleOffset;
    }

    /**
     * Sets the angle to use when drawing the arc. If the angle is >= 360, then the oval is drawn
     * completely. Note that this differs slightly from SkPath::arcTo, which treats the sweep angle
     * modulo 360. If the sweep angle is negative, the sweep angle is treated as sweep angle modulo
     * 360.
     *
     * @param angle Sweep angle (in degrees) measured clockwise.
     */
    public void setArcAngle(final int angle) {
        this.mArcAngle = angle;
    }

    /**
     * Sets the angle used when drawing the arc.
     *
     * @return Sweep angle (in degrees) measured clockwise.
     */
    public int getArcAngle() {
        return this.mArcAngle;
    }

    /**
     * Starts or stops the animation.
     *
     * @param started {@code true} to start the animation, {@code false} to stop the animation.
     */
    public void setStarted(final boolean started) {
        if (started != this.mIsStarted) {
            if (this.mIsStarted = started) {
                this.startAnimation();
            } else {
                this.stopAnimation();
            }
            this.invalidate();
        }
    }

    /**
     * Gets a value indicating whether the animation is started.
     *
     * @return {@code true} if the animation is started; otherwise, {@code false}.
     */
    public boolean isStarted() {
        return this.mIsStarted;
    }

    /**
     * Sets the direction of the animation's rotation.
     *
     * @param clockwise {@code true} to rotate clockwise, {@code false} to rotate counterclockwise.
     */
    public void setClockwise(final boolean clockwise) {
        if (clockwise != this.mIsClockwise) {
            this.mSpinningAnimator.setIntValues(clockwise ? VALUES_CW : VALUES_CCW);
            this.mIsClockwise = clockwise;
            this.invalidate();
        }
    }

    /**
     * Gets a value indicating the animation's rotation direction.
     *
     * @return {@code true} if the animation is rotating clockwise; otherwise, {@code false}.
     */
    public boolean isClockwise() {
        return this.mIsClockwise;
    }

    /**
     * Sets the duration of one animation cycle, in milliseconds.
     *
     * @param duration The duration to set.
     */
    public void setDuration(final long duration) {
        if (duration != this.mDuration) {
            this.mSpinningAnimator.setDuration(duration);
            this.mProgressAnimator.setDuration(duration);
            this.mDuration = duration;
        }
    }

    /**
     * Gets the duration of one animation cycle, in milliseconds.
     *
     * @return The duration, in milliseconds, before the animation repeats.
     */
    public long getDuration() {
        return this.mDuration;
    }

    /**
     * Gets the progress value that is shown by the ring.
     */
    public int getProgress() {
        return this.mProgress;
    }

    /**
     * Sets the progress value to be shown by the ring.
     *
     * @param progress The progress to set.
     */
    public void setProgress(final int progress, final boolean animated) {
        if (progress != this.mProgress) {
            this.mProgressAnimator.cancel();
            this.mProgress = progress;
            if (animated) {
                this.mProgressAnimator.setFloatValues(this.mVisualProgress, progress);
                this.mProgressAnimator.start();
            } else {
                this.setVisualProgress(progress);
                this.invalidate();
            }
        } else {
            this.setVisualProgress(progress);
        }
    }

    public void setProgress(final int progress) {
        this.setProgress(progress, false);
    }

    public void addProgressListener(ProgressRingListener listener) {
        this.mListener = listener;
    }

    private class SpinningAnimationListener implements ValueAnimator.AnimatorUpdateListener {

        @Override
        public void onAnimationUpdate(final ValueAnimator animation) {
            ProgressRing.this.invalidate();
        }
    }

    private class ProgressAnimationListener implements ValueAnimator.AnimatorUpdateListener {

        @Override
        public void onAnimationUpdate(final ValueAnimator animation) {
            float progress = (float) animation.getAnimatedValue();
            ProgressRing.this.setVisualProgress(progress);
            ProgressRing.this.invalidate();
        }
    }

    public interface ProgressRingListener {

        void onVisualProgressChanged(float visualProgress, int actualProgress);
    }
}
