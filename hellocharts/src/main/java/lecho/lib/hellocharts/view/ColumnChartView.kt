package lecho.lib.hellocharts.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import lecho.lib.hellocharts.BuildConfig
import lecho.lib.hellocharts.computator.ChartComputator
import lecho.lib.hellocharts.gesture.ChartTouchHandler
import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener
import lecho.lib.hellocharts.listener.DummyColumnChartOnValueSelectListener
import lecho.lib.hellocharts.model.ChartData
import lecho.lib.hellocharts.model.ColumnChartData
import lecho.lib.hellocharts.model.ColumnChartData.Companion.generateDummyData
import lecho.lib.hellocharts.provider.ColumnChartDataProvider
import lecho.lib.hellocharts.renderer.AxesRenderer
import lecho.lib.hellocharts.renderer.ColumnChartRenderer

/**
 * ColumnChart/BarChart, supports subColumns, stacked columns and negative values.
 *
 * @author Leszek Wach
 */
@Suppress("unused")
open class ColumnChartView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : AbstractChartView(context, attrs, defStyle), ColumnChartDataProvider {


    @JvmField
    var data: ColumnChartData? = null

    @JvmField
    var onValueTouchListener: ColumnChartOnValueSelectListener =
        DummyColumnChartOnValueSelectListener()

    final override var columnChartData: ColumnChartData
        get() = data!!
        set(data) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Setting data for ColumnChartView")
            }
            this.data = data
            super.onChartDataChange()
        }

    init {
        setChartRenderer(ColumnChartRenderer(context, this, this))
        columnChartData = generateDummyData()
    }

    override fun getChartData(): ColumnChartData {
        return data!!
    }

    override fun callTouchListener() {
        val selectedValue = chartRenderer.getSelectedValue()
        if (selectedValue.isSet) {
            val value = data!!.columns[selectedValue.firstIndex].values[selectedValue.secondIndex]
            onValueTouchListener.onValueSelected(
                selectedValue.firstIndex,
                selectedValue.secondIndex,
                value
            )
        } else {
            onValueTouchListener.onValueDeselected()
        }
    }

    fun getOnValueTouchListener(): ColumnChartOnValueSelectListener {
        return onValueTouchListener
    }

    fun setOnValueTouchListener(touchListener: ColumnChartOnValueSelectListener?) {
        if (null != touchListener) {
            onValueTouchListener = touchListener
        }
    }

    override fun setChartData(chartData: ChartData) {
        this.chartData = chartData
    }

    override fun setTouchHandler(touchHandler: ChartTouchHandler) {
        this.touchHandler = touchHandler
    }

    override fun getInteractive(): Boolean {
        return isInteractive()
    }

    override fun setAxesRenderer(axesRenderer: AxesRenderer) {
        this.axesRenderer = axesRenderer
    }

    override fun setChartComputator(chartComputator: ChartComputator) {
        this.chartComputator = chartComputator
    }

    override fun setContainerScrollEnabled(isEnabled: Boolean) {}

    companion object {
        private const val TAG = "ColumnChartView"
    }
}