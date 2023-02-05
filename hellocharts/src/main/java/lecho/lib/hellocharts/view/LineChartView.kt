package lecho.lib.hellocharts.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import lecho.lib.hellocharts.BuildConfig
import lecho.lib.hellocharts.computator.ChartComputator
import lecho.lib.hellocharts.gesture.ChartTouchHandler
import lecho.lib.hellocharts.listener.DummyLineChartOnValueSelectListener
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener
import lecho.lib.hellocharts.model.ChartData
import lecho.lib.hellocharts.model.LineChartData
import lecho.lib.hellocharts.model.LineChartData.Companion.generateDummyData
import lecho.lib.hellocharts.provider.LineChartDataProvider
import lecho.lib.hellocharts.renderer.AxesRenderer
import lecho.lib.hellocharts.renderer.LineChartRenderer

/**
 * LineChart, supports cubic lines, filled lines, circle and square points. Point radius and stroke width can be
 * adjusted using LineChartData attributes.
 *
 * @author Leszek Wach
 */
@Suppress("unused")
open class LineChartView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : AbstractChartView(context, attrs, defStyle), LineChartDataProvider {

    final override var lineChartData: LineChartData
        get() = data!!
        set(data) {
            if (BuildConfig.DEBUG) {
                Log.d("LineChartView", "Setting data for LineChartView")
            }
            this.data = data
            super.onChartDataChange()
        }


    init {
        setChartRenderer(
            LineChartRenderer(
                context,
                this,
                this
            )
        )
        lineChartData = generateDummyData()
    }
    @JvmField
    var data: LineChartData? = null

    @JvmField
    var onValueTouchListener: LineChartOnValueSelectListener =
        DummyLineChartOnValueSelectListener()


    override fun getChartData(): ChartData {
        return lineChartData
    }

    override fun callTouchListener() {
        chartRenderer?.getSelectedValue()?.let { selectedValue ->
            if (selectedValue.isSet) {
                data?.getLines()
                    ?.get(selectedValue.firstIndex)
                    ?.getValues()
                    ?.get(selectedValue.secondIndex)?.let {
                        onValueTouchListener.onValueSelected(
                            selectedValue.firstIndex,
                            selectedValue.secondIndex,
                            it
                        )
                    }
            } else {
                onValueTouchListener.onValueDeselected()
            }
        }
    }

    fun getOnValueTouchListener(): LineChartOnValueSelectListener {
        return onValueTouchListener
    }

    fun setOnValueTouchListener(touchListener: LineChartOnValueSelectListener?) {
        if (null != touchListener) {
            onValueTouchListener = touchListener
        }
    }

    override fun setChartData(chartData: ChartData) {
        this.chartData = chartData
    }

    override fun setAxesRenderer(axesRenderer: AxesRenderer) {
        this.axesRenderer = axesRenderer
    }

    override fun setChartComputator(chartComputator: ChartComputator) {
        this.chartComputator = chartComputator
    }

    override fun setTouchHandler(touchHandler: ChartTouchHandler) {
        this.touchHandler = touchHandler
    }

    override fun getInteractive(): Boolean {
        return isInteractive
    }

    override fun setContainerScrollEnabled(isEnabled: Boolean) {
        isContainerScrollEnabled = isEnabled
    }
}