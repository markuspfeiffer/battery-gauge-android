
package de.codewild.batterygauge.ui.graphics;

import android.graphics.Color;


/**
 * Interpolates between colors using the HSV color space.
 */
public class HsvInterpolator implements ColorInterpolator {

    private final float[] hsvA = new float[3];
    private final float[] hsvB = new float[3];

    /**
     * Initializes a new instance of the {@link HsvInterpolator} class.
     */
    public HsvInterpolator() {
    }

    private void swap(final float[] a, final float[] b, final int index) {
        final float temp = a[index];
        a[index] = b[index];
        b[index] = temp;
    }

    private float interpolate(final float a, final float b, final float proportion) {
        return (a + ((b - a) * proportion));
    }

    @Override
    public int interpolate(final int startColor, final int endColor, float proportion) {
        Color.colorToHSV(startColor, this.hsvA);
        Color.colorToHSV(endColor, this.hsvB);

        float distance = this.hsvB[0] - this.hsvA[0];
        final float hue;

        if (this.hsvA[0] > this.hsvB[0]) {
            this.swap(this.hsvA, this.hsvB, 0);
            distance = -distance;
            proportion = 1 - proportion;
        }

        if (distance > 180) {
            this.hsvA[0] = this.hsvA[0] + 360;
            hue = (this.hsvA[0] + proportion * (this.hsvB[0] - this.hsvA[0])) % 360;
        } else {
            hue = this.hsvA[0] + proportion * distance;
        }

        this.hsvB[0] = hue;

        for (int i = 1; i < 3; i++) {
            this.hsvB[i] = this.interpolate(this.hsvA[i], this.hsvB[i], proportion);
        }

        return Color.HSVToColor(this.hsvB);
    }
}
