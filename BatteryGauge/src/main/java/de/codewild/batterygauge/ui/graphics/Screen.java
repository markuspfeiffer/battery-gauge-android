
package de.codewild.batterygauge.ui.graphics;

import android.content.res.Resources;
import android.util.DisplayMetrics;


/**
 * Provides constants and static methods to perform unit conversions based on a device's display
 * metrics.
 */
public final class Screen {

    /**
     * Converts the specified dimension, in density-independent pixel (dip), to pixel (px).
     * <p/>
     * A density-independent pixel is equivalent to one physical pixel on a 160 dpi screen, which
     * is the baseline density assumed by the system for a "medium" density screen. On systems with
     * a higher density a density-independent pixel is equivalent to multiple physical pixels and
     * on system with a lower density multiple density-independent pixels are equivalent to one
     * physical pixel.
     *
     * @param dip The dimension to convert, in dip.
     * @return The specified dimension, converted to pixels.
     * @see DisplayMetrics#density
     * @see #px2dip(int)
     * @see #sp2px(float)
     */
    public static int dip2px(final float dip) {
        final float density = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dip * density);
    }
}
