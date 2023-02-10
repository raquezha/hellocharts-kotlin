package lecho.lib.hellocharts.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import androidx.core.view.ViewCompat
import lecho.lib.hellocharts.BuildConfig
import lecho.lib.hellocharts.computator.PreviewChartComputator
import lecho.lib.hellocharts.gesture.PreviewChartTouchHandler
import lecho.lib.hellocharts.model.LineChartData.Companion.generateDummyData
import lecho.lib.hellocharts.renderer.PreviewLineChartRenderer

/**
 * Preview chart that can be used as overview for other LineChart. When you change Viewport of this chart, visible area
 * of other chart will change. For that you need also to use
 * [Chart.setViewportChangeListener]
 *
 * @author Leszek Wach
 */
class PreviewLineChartView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : LineChartView(context, attrs, defStyle) {

    @JvmField
    var previewChartRenderer: PreviewLineChartRenderer

    init {
        chartComputator = PreviewChartComputator()
        previewChartRenderer = PreviewLineChartRenderer(context, this, this)
        touchHandler = PreviewChartTouchHandler(context!!, this)
        setChartRenderer(previewChartRenderer)
        lineChartData = generateDummyData()
    }

    var previewColor: Int
        get() = previewChartRenderer.previewColor
        set(color) {
        if (BuildConfig.DEBUG) {
            Log.d("PreviewLineChartView", "Changing preview area color")
        }
            previewChartRenderer.previewColor = color
            ViewCompat.postInvalidateOnAnimation(this)
        }

    override fun canScrollHorizontally(direction: Int): Boolean {
        val offset = computeHorizontalScrollOffset()
        val range = computeHorizontalScrollRange() - computeHorizontalScrollExtent()
        if (range == 0) return false
        return if (direction < 0) {
            offset > 0
        } else {
            offset < range - 1
        }
    }
}