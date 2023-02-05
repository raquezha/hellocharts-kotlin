package lecho.lib.hellocharts.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import lecho.lib.hellocharts.BuildConfig;
import lecho.lib.hellocharts.computator.ChartComputator;
import lecho.lib.hellocharts.gesture.ChartTouchHandler;
import lecho.lib.hellocharts.listener.DummyLineChartOnValueSelectListener;
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.ChartData;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SelectedValue;
import lecho.lib.hellocharts.provider.LineChartDataProvider;
import lecho.lib.hellocharts.renderer.AxesRenderer;
import lecho.lib.hellocharts.renderer.LineChartRenderer;

/**
 * LineChart, supports cubic lines, filled lines, circle and square points. Point radius and stroke width can be
 * adjusted using LineChartData attributes.
 *
 * @author Leszek Wach
 */
@SuppressWarnings("unused")
public class LineChartView extends AbstractChartView implements LineChartDataProvider {
    protected LineChartData data;
    protected LineChartOnValueSelectListener onValueTouchListener = new DummyLineChartOnValueSelectListener();

    public LineChartView(Context context) {
        this(context, null, 0);
    }

    public LineChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LineChartView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setChartRenderer(new LineChartRenderer(context, this, this));
        setLineChartData(LineChartData.generateDummyData());
    }

    @NonNull
    @Override
    public LineChartData getLineChartData() {
        return data;
    }

    @Override
    public void setLineChartData(@NonNull LineChartData data) {
        if (BuildConfig.DEBUG) {
            Log.d("LineChartView", "Setting data for LineChartView");
        }
        this.data = data;
        super.onChartDataChange();
    }

    @NonNull
    @Override
    public ChartData getChartData() {
        return data;
    }

    @Override
    public void callTouchListener() {
        SelectedValue selectedValue = chartRenderer.getSelectedValue();

        if (selectedValue.isSet()) {

            PointValue point = data.getLines().get(selectedValue.firstIndex).getValues()
                    .get(selectedValue.secondIndex);
            onValueTouchListener.onValueSelected(selectedValue.firstIndex, selectedValue.secondIndex, point);
        } else {
            onValueTouchListener.onValueDeselected();
        }
    }

    public LineChartOnValueSelectListener getOnValueTouchListener() {
        return onValueTouchListener;
    }

    public void setOnValueTouchListener(LineChartOnValueSelectListener touchListener) {
        if (null != touchListener) {
            this.onValueTouchListener = touchListener;
        }
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

    @Override
    public void setTouchHandler(@NotNull ChartTouchHandler touchHandler) {
        this.touchHandler = touchHandler;
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
