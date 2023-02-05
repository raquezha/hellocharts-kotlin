package lecho.lib.hellocharts.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.core.view.ViewCompat;

import lecho.lib.hellocharts.computator.PreviewChartComputator;
import lecho.lib.hellocharts.listener.ViewportChangeListener;
import lecho.lib.hellocharts.gesture.PreviewChartTouchHandler;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.renderer.PreviewColumnChartRenderer;


/**
 * Preview chart that can be used as overview for other ColumnChart. When you change Viewport of this chart, visible
 * area of other chart will change. For that you need also to use
 * {@link Chart#setViewportChangeListener(ViewportChangeListener)}
 *
 * @author Leszek Wach
 */
public class PreviewColumnChartView extends ColumnChartView {

    protected PreviewColumnChartRenderer previewChartRenderer;

    public PreviewColumnChartView(Context context) {
        this(context, null, 0);
    }

    public PreviewColumnChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PreviewColumnChartView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        chartComputator = new PreviewChartComputator();
        previewChartRenderer = new PreviewColumnChartRenderer(context, this, this);
        touchHandler = new PreviewChartTouchHandler(context, this);
        setChartRenderer(previewChartRenderer);
        setColumnChartData(ColumnChartData.generateDummyData());
    }

    public int getPreviewColor() {
        return previewChartRenderer.getPreviewColor();
    }

    public void setPreviewColor(int color) {
//        if (BuildConfig.DEBUG) {
//            Log.d(TAG, "Changing preview area color");
//        }

        previewChartRenderer.setPreviewColor(color);
        ViewCompat.postInvalidateOnAnimation(this);
    }

    @Override
    public boolean canScrollHorizontally(int direction) {
        final int offset = computeHorizontalScrollOffset();
        final int range = computeHorizontalScrollRange() - computeHorizontalScrollExtent();
        if (range == 0) return false;
        if (direction < 0) {
            return offset > 0;
        } else {
            return offset < range - 1;
        }
    }
}
