package lecho.lib.hellocharts.renderer

import android.content.Context
import android.graphics.Canvas
import lecho.lib.hellocharts.model.Viewport
import lecho.lib.hellocharts.view.Chart

open class ComboChartRenderer(context: Context?, chart: Chart?) : AbstractChartRenderer(
    context!!, chart!!
) {
    @JvmField
    var renderers: MutableList<ChartRenderer> = ArrayList()

    @JvmField
    var unionViewport = Viewport()

    override fun onChartSizeChanged() {
        for (renderer in renderers) {
            renderer.onChartSizeChanged()
        }
    }

    override fun onChartDataChanged() {
        super.onChartDataChanged()
        for (renderer in renderers) {
            renderer.onChartDataChanged()
        }
        onChartViewportChanged()
    }

    override fun onChartViewportChanged() {
        if (isViewportCalculationEnabled) {
            for ((rendererIndex, renderer) in renderers.withIndex()) {
                renderer.onChartViewportChanged()
                if (rendererIndex == 0) {
                    val maximumViewport = renderer.getMaximumViewport()
                    if (maximumViewport != null) {
                        unionViewport.set(maximumViewport)
                    }
                } else {
                    val maximumViewport = renderer.getMaximumViewport()
                    if (maximumViewport != null) {
                        unionViewport.union(maximumViewport)
                    }
                }
            }
            computator.setMaximumViewport(unionViewport)
            computator.setCurrentViewport(unionViewport)
        }
    }

    override fun draw(canvas: Canvas?) {
        for (renderer in renderers) {
            renderer.draw(canvas)
        }
    }

    override fun drawUnClipped(canvas: Canvas?) {
        for (renderer in renderers) {
            renderer.drawUnClipped(canvas)
        }
    }

    override fun checkTouch(touchX: Float, touchY: Float): Boolean {
        selectedValue.clear()
        var rendererIndex = renderers.size - 1
        while (rendererIndex >= 0) {
            val renderer = renderers[rendererIndex]
            if (renderer.checkTouch(touchX, touchY)) {
                selectedValue.set(
                    renderer.getSelectedValue().apply {
                        this.selectedY = touchY
                        this.selectedX = touchX
                    }
                )
                break
            }
            rendererIndex--
        }

        //clear the rest of renderers if value was selected, if value was not selected this loop
        // will not be executed.
        rendererIndex--
        while (rendererIndex >= 0) {
            val renderer = renderers[rendererIndex]
            renderer.clearTouch()
            rendererIndex--
        }
        return isTouched()
    }

    override fun clearTouch() {
        for (renderer in renderers) {
            renderer.clearTouch()
        }
        selectedValue.clear()
    }
}