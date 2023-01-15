package lecho.lib.hellocharts.computator

import lecho.lib.hellocharts.model.Viewport

/**
 * Version of ChartComputator for preview charts.
 * It always uses maxViewport as visible viewport and currentViewport as preview area.
 */
class PreviewChartComputator : ChartComputator() {
    override fun computeRawX(valueX: Float): Float {
        val pixelOffset =
            (valueX - maxViewport.left) * (contentRectMinusAllMargins.width() / maxViewport
                .width())
        return contentRectMinusAllMargins.left + pixelOffset
    }

    override fun computeRawY(valueY: Float): Float {
        val pixelOffset =
            (valueY - maxViewport.bottom) * (contentRectMinusAllMargins.height() / maxViewport
                .height())
        return contentRectMinusAllMargins.bottom - pixelOffset
    }

    override var visibleViewport: Viewport
        get() = maxViewport
        set(visibleViewport) {
            setMaximumViewport(visibleViewport)
        }

    override fun constrainViewport(left: Float, top: Float, right: Float, bottom: Float) {
        super.constrainViewport(left, top, right, bottom)
        viewportChangeListener.onViewportChanged(currentViewPort)
    }
}