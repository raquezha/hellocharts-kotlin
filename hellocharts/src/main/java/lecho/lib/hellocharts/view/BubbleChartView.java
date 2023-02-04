package lecho.lib.hellocharts.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

import lecho.lib.hellocharts.BuildConfig;
import lecho.lib.hellocharts.computator.ChartComputator;
import lecho.lib.hellocharts.gesture.ChartTouchHandler;
import lecho.lib.hellocharts.listener.BubbleChartOnValueSelectListener;
import lecho.lib.hellocharts.listener.DummyBubbleChartOnValueSelectListener;
import lecho.lib.hellocharts.model.BubbleChartData;
import lecho.lib.hellocharts.model.BubbleValue;
import lecho.lib.hellocharts.model.ChartData;
import lecho.lib.hellocharts.model.SelectedValue;
import lecho.lib.hellocharts.provider.BubbleChartDataProvider;
import lecho.lib.hellocharts.renderer.AxesRenderer;
import lecho.lib.hellocharts.renderer.BubbleChartRenderer;

/**
 * BubbleChart, supports circle bubbles and square bubbles.
 *
 * @author lecho
 */
@SuppressWarnings("unused")
public class BubbleChartView extends AbstractChartView implements BubbleChartDataProvider {
    private static final String TAG = "BubbleChartView";
    protected BubbleChartData data;
    protected BubbleChartOnValueSelectListener onValueTouchListener = new DummyBubbleChartOnValueSelectListener();

    protected BubbleChartRenderer bubbleChartRenderer;

    public BubbleChartView(Context context) {
        this(context, null, 0);
    }

    public BubbleChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BubbleChartView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        bubbleChartRenderer = new BubbleChartRenderer(context, this, this);
        setChartRenderer(bubbleChartRenderer);
        setBubbleChartData(BubbleChartData.generateDummyData());
    }

    @NonNull
    @Override
    public BubbleChartData getBubbleChartData() {
        return data;
    }

    @Override
    public void setBubbleChartData(@NonNull BubbleChartData data) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Setting data for BubbleChartView");
        }
        this.data = data;
        super.onChartDataChange();
    }

    @Override
    public ChartData getChartData() {
        return data;
    }

    @Override
    public void callTouchListener() {
        SelectedValue selectedValue = chartRenderer.getSelectedValue();

        if (selectedValue.isSet()) {
            BubbleValue value = data.values.get(selectedValue.firstIndex);
            onValueTouchListener.onValueSelected(selectedValue.firstIndex, value);
        } else {
            onValueTouchListener.onValueDeselected();
        }
    }

    public BubbleChartOnValueSelectListener getOnValueTouchListener() {
        return onValueTouchListener;
    }

    public void setOnValueTouchListener(BubbleChartOnValueSelectListener touchListener) {
        if (null != touchListener) {
            this.onValueTouchListener = touchListener;
        }
    }

    /**
     * Removes empty spaces, top-bottom for portrait orientation and left-right for landscape. This method has to be
     * called after view View#onSizeChanged() method is called and chart data is set. This method may be inaccurate.
     *
     * @see BubbleChartRenderer#removeMargins()
     */
    public void removeMargins() {
        bubbleChartRenderer.removeMargins();
        ViewCompat.postInvalidateOnAnimation(this);
    }

    @Override
    public void setChartData(@Nullable ChartData chartData) {
        this.chartData = chartData;
    }

    @Override
    public void setAxesRenderer(@Nullable AxesRenderer axesRenderer) {
        this.axesRenderer = axesRenderer;
    }

    @Override
    public void setChartComputator(@Nullable ChartComputator chartComputator) {
        this.chartComputator = chartComputator;
    }

    @Nullable
    @Override
    public ChartTouchHandler setTouchHandler() {
        return touchHandler;
    }

    @Override
    public boolean getInteractive() {
        return isInteractive;
    }

    @Override
    public void setContainerScrollEnabled(boolean isEnabled) {
        this.isContainerScrollEnabled = isEnabled;
    }
}
