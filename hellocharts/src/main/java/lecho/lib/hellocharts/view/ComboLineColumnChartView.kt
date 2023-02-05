package lecho.lib.hellocharts.view

import android.content.Context
import android.util.AttributeSet
import lecho.lib.hellocharts.computator.ChartComputator
import lecho.lib.hellocharts.gesture.ChartTouchHandler
import lecho.lib.hellocharts.listener.ComboLineColumnChartOnValueSelectListener
import lecho.lib.hellocharts.listener.DummyCompoLineColumnChartOnValueSelectListener
import lecho.lib.hellocharts.model.ChartData
import lecho.lib.hellocharts.model.ColumnChartData
import lecho.lib.hellocharts.model.ComboLineColumnChartData
import lecho.lib.hellocharts.model.ComboLineColumnChartData.Companion.generateDummyData
import lecho.lib.hellocharts.model.LineChartData
import lecho.lib.hellocharts.model.SelectedValue.SelectedValueType
import lecho.lib.hellocharts.provider.ColumnChartDataProvider
import lecho.lib.hellocharts.provider.ComboLineColumnChartDataProvider
import lecho.lib.hellocharts.provider.LineChartDataProvider
import lecho.lib.hellocharts.renderer.AxesRenderer
import lecho.lib.hellocharts.renderer.ColumnChartRenderer
import lecho.lib.hellocharts.renderer.ComboLineColumnChartRenderer
import lecho.lib.hellocharts.renderer.LineChartRenderer

/**
 * ComboChart, supports ColumnChart combined with LineChart. Lines are always drawn on top.
 *
 * @author Leszek Wach
 */
@Suppress("unused")
class ComboLineColumnChartView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : AbstractChartView(context, attrs, defStyle), ComboLineColumnChartDataProvider {

    @JvmField
    var data: ComboLineColumnChartData? = null

    @JvmField
    var columnChartDataProvider: ColumnChartDataProvider = ComboColumnChartDataProvider()

    @JvmField
    var lineChartDataProvider: LineChartDataProvider = ComboLineChartDataProvider()

    @JvmField
    var onValueTouchListener: ComboLineColumnChartOnValueSelectListener =
        DummyCompoLineColumnChartOnValueSelectListener()

    override var comboLineColumnChartData: ComboLineColumnChartData
        get() = data!!
        set(data) {
            this.data = data // generateDummyData();
            super.onChartDataChange()
        }

    init {
        setChartRenderer(
            ComboLineColumnChartRenderer(
                context!!, this, columnChartDataProvider,
                lineChartDataProvider
            )
        )
        comboLineColumnChartData = generateDummyData()
    }


    override fun getChartData(): ChartData {
        return data!!
    }

    override fun callTouchListener() {
        val selectedValue = chartRenderer!!.getSelectedValue()
        if (selectedValue.isSet) {
            if (SelectedValueType.COLUMN == selectedValue.type) {
                val value =
                    data!!.columnChartData.columns[selectedValue.firstIndex].values[selectedValue.secondIndex]
                onValueTouchListener.onColumnValueSelected(
                    selectedValue.firstIndex,
                    selectedValue.secondIndex, value
                )
            } else if (SelectedValueType.LINE == selectedValue.type) {
                val value =
                    data!!.lineChartData.getLines()[selectedValue.firstIndex].getValues()[selectedValue.secondIndex]
                onValueTouchListener.onPointValueSelected(
                    selectedValue.firstIndex, selectedValue.secondIndex,
                    value
                )
            } else {
                throw IllegalArgumentException(
                    "Invalid selected value type " + if (selectedValue.type != null) selectedValue.type!!.name else null
                )
            }
        } else {
            onValueTouchListener.onValueDeselected()
        }
    }

    fun getOnValueTouchListener(): ComboLineColumnChartOnValueSelectListener {
        return onValueTouchListener
    }

    fun setOnValueTouchListener(touchListener: ComboLineColumnChartOnValueSelectListener?) {
        if (null != touchListener) {
            onValueTouchListener = touchListener
        }
    }

    fun setColumnChartRenderer(context: Context?, columnChartRenderer: ColumnChartRenderer?) {
        setChartRenderer(
            ComboLineColumnChartRenderer(
                context!!,
                this,
                columnChartRenderer!!,
                lineChartDataProvider
            )
        )
    }

    fun setLineChartRenderer(context: Context?, lineChartRenderer: LineChartRenderer?) {
        setChartRenderer(
            ComboLineColumnChartRenderer(
                context!!,
                this,
                columnChartDataProvider,
                lineChartRenderer!!
            )
        )
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

    private inner class ComboLineChartDataProvider : LineChartDataProvider {
        override var lineChartData: LineChartData
            get() = data!!.lineChartData
            set(data) {
                this@ComboLineColumnChartView.data!!.lineChartData = data
            }
    }

    private inner class ComboColumnChartDataProvider : ColumnChartDataProvider {
        override var columnChartData: ColumnChartData
            get() = data!!.columnChartData
            set(data) {
                this@ComboLineColumnChartView.data!!.columnChartData = data
            }
    }
}