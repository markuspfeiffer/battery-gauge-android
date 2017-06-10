
package de.codewild.batterygauge.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;


public class SquareFrameLayout extends FrameLayout {

    public SquareFrameLayout(final Context context) {
        super(context);
    }

    public SquareFrameLayout(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareFrameLayout(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    @SuppressWarnings("SuspiciousNameCombination")
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        // Simply use the width as height, to make it square.
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
