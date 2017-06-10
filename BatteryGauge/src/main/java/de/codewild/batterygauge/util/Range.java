
package de.codewild.batterygauge.util;

/**
 * Provides basic range operations.
 */
public class Range {

    /**
     * Restricts the specified value to the defined minimum and maximum.
     * <p/>
     * If the specified value is below the defined minimum, the minimum value is returned. If
     * the specified value exceeds the defined maximum, the maximum value is returned. Otherwise
     * the specified value is returned.
     *
     * @param value The value to clamp.
     * @param min   The minimum allowable value.
     * @param max   The maximum allowable value.
     * @return The specified value, or either <i>min</i> or <i>max</i> if the value exceeds the
     * allowable range defined by these values.
     */
    public static float clamp(final float value, final float min, final float max) {
        return value < min ? min : value > max ? max : value;
    }
}
