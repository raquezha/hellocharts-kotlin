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
import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.listener.DummyColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.model.ChartData;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SelectedValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.provider.ColumnChartDataProvider;
import lecho.lib.hellocharts.renderer.AxesRenderer;
import lecho.lib.hellocharts.renderer.ColumnChartRenderer;

/**
 * ColumnChart/BarChart, supports subColumns, stacked columns and negative values.
 *
 * @author Leszek Wach
 */
@SuppressWarnings("unused")
public class ColumnChartView extends AbstractChartView implements ColumnChartDataProvider {
    private static final String TAG = "ColumnChartView";
    private ColumnChartData data;
    private ColumnChartOnValueSelectListener onValueTouchListener = new DummyColumnChartOnValueSelectListener();

    public ColumnChartView(Context context) {
        this(context, null, 0);
    }

    public ColumnChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColumnChartView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setChartRenderer(new ColumnChartRenderer(context, this, this));
        setColumnChartData(ColumnChartData.generateDummyData());
    }

    @NonNull
    @Override
    public ColumnChartData getColumnChartData() {
        return data;
    }

    @Override
    public void setColumnChartData(@NonNull ColumnChartData data) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Setting data for ColumnChartView");
       }
        this.data = data;
        super.onChartDataChange();

    }

    @NonNull
    @Override
    public ColumnChartData getChartData() {
        return data;
    }

    @Override
    public void callTouchListener() {
        SelectedValue selectedValue = chartRenderer.getSelectedValue();

        if (selectedValue.isSet()) {
            SubcolumnValue value = data.getColumns().get(selectedValue.firstIndex).values
                    .get(selectedValue.secondIndex);
            onValueTouchListener.onValueSelected(selectedValue.firstIndex, selectedValue.secondIndex, value);
        } else {
            onValueTouchListener.onValueDeselected();
        }
    }

    public ColumnChartOnValueSelectListener getOnValueTouchListener() {
        return onValueTouchListener;
    }

    public void setOnValueTouchListener(ColumnChartOnValueSelectListener touchListener) {
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
        return isInteractive();
    }

    @Override
    public void setContainerScrollEnabled(boolean isEnabled) {

    }
}
