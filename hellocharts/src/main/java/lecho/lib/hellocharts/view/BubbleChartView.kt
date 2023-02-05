package lecho.lib.hellocharts.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import androidx.core.view.ViewCompat
import lecho.lib.hellocharts.BuildConfig
import lecho.lib.hellocharts.computator.ChartComputator
import lecho.lib.hellocharts.gesture.ChartTouchHandler
import lecho.lib.hellocharts.listener.BubbleChartOnValueSelectListener
import lecho.lib.hellocharts.listener.DummyBubbleChartOnValueSelectListener
import lecho.lib.hellocharts.model.BubbleChartData
import lecho.lib.hellocharts.model.BubbleChartData.Companion.generateDummyData
import lecho.lib.hellocharts.model.ChartData
import lecho.lib.hellocharts.provider.BubbleChartDataProvider
import lecho.lib.hellocharts.renderer.AxesRenderer
import lecho.lib.hellocharts.renderer.BubbleChartRenderer

/**
 * BubbleChart, supports circle bubbles and square bubbles.
 *
 * @author lecho
 */
@Suppress("unused")
class BubbleChartView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : AbstractChartView(context, attrs, defStyle), BubbleChartDataProvider {

    @JvmField
    var data: BubbleChartData? = null

    @JvmField
    var onValueTouchListener:BubbleChartOnValueSelectListener? = DummyBubbleChartOnValueSelectListener()

    @JvmField
    var bubbleChartRenderer: BubbleChartRenderer

    override var bubbleChartData: BubbleChartData
        get() = data!!
        set(data) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Setting data for BubbleChartView")
            }
            this.data = data
            super.onChartDataChange()
        }


    init {
        bubbleChartRenderer = BubbleChartRenderer(context, this, this)
        setChartRenderer(bubbleChartRenderer)
        bubbleChartData = generateDummyData()
    }


    override fun getChartData(): ChartData {
        return data!!
    }

    override fun callTouchListener() {
        val selectedValue = chartRenderer?.getSelectedValue()
        selectedValue?.let {
            if (it.isSet) {
                val value = data!!.values[selectedValue.firstIndex]
                onValueTouchListener?.onValueSelected(selectedValue.firstIndex, value)
            } else {
                onValueTouchListener?.onValueDeselected()
            }
        }
    }

    fun setOnValueTouchListener(touchListener: BubbleChartOnValueSelectListener?) {
        if (null != touchListener) {
            onValueTouchListener = touchListener
        }
    }

    /**
     * Removes empty spaces, top-bottom for portrait orientation and left-right for landscape. This method has to be
     * called after view View#onSizeChanged() method is called and chart data is set. This method may be inaccurate.
     *
     * @see BubbleChartRenderer.removeMargins
     */
    fun removeMargins() {
        bubbleChartRenderer.removeMargins()
        ViewCompat.postInvalidateOnAnimation(this)
    }

    override fun setChartData(chartData: ChartData) {
        this.chartData = chartData
    }

    override fun setTouchHandler(touchHandler: ChartTouchHandler) {
        this.touchHandler = touchHandler
    }

    override fun getInteractive(): Boolean {
        return isInteractive
    }

    override fun setAxesRenderer(axesRenderer: AxesRenderer) {
        this.axesRenderer = axesRenderer
    }

    override fun setChartComputator(chartComputator: ChartComputator) {
        this.chartComputator = chartComputator
    }

    override fun setContainerScrollEnabled(isEnabled: Boolean) {
        isContainerScrollEnabled = isEnabled
    }

    companion object {
        private const val TAG = "BubbleChartView"
    }
}