
package de.codewild.batterygauge.ui.graphics;

import android.graphics.Color;


/**
 * Interface definition for a class that interpolates between colors.
 */
public interface ColorInterpolator {

    /**
     * Interpolates between the specified colors.
     * <p>
     * Note that colors are {@code int}s containing alpha as well as r,g,b. This 32 bit value
     * is not premultiplied, meaning that its alpha can be any value, regardless of the values of
     * r,g,b. See the {@link Color} class for more details.
     * @param startColor A 32-bit {@code int} value representing colors in the separate bytes of
     * the parameter.
     * @param endColor A 32-bit {@code int} value representing colors in the separate bytes of
     * the parameter.
     * @param proportion The fraction from the starting to the ending color.
     * @return A color that is calculated to be the interpolated result.
     */
    int interpolate(int startColor, int endColor, float proportion);
}
