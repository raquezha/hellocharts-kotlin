package lecho.lib.hellocharts.gesture

import android.graphics.PointF
import android.view.MotionEvent
import lecho.lib.hellocharts.computator.ChartComputator
import lecho.lib.hellocharts.model.Viewport

/**
 * Encapsulates zooming functionality.
 */
class ChartZoomer(zoomType: ZoomType) {
    private val zoomer: ZoomerCompat
    var zoomType: ZoomType
    private val zoomFocalPoint = PointF() // Used for double tap zoom
    private val viewportFocus = PointF()
    private val scrollerStartViewport = Viewport() // Used only for zooms and flings

    init {
        zoomer = ZoomerCompat()
        this.zoomType = zoomType
    }

    fun startZoom(e: MotionEvent, computator: ChartComputator): Boolean {
        zoomer.forceFinished(true)
        scrollerStartViewport.set(computator.getCurrentViewport())
        if (!computator.rawPixelsToDataPoint(e.x, e.y, zoomFocalPoint)) {
            // Focus point is not within content area.
            return false
        }
        zoomer.startZoom(ZOOM_AMOUNT)
        return true
    }

    fun computeZoom(computator: ChartComputator): Boolean {
        if (zoomer.computeZoom()) {
            // Performs the zoom since a zoom is in progress.
            val newWidth = (1.0f - zoomer.currZoom) * scrollerStartViewport.width()
            val newHeight = (1.0f - zoomer.currZoom) * scrollerStartViewport.height()
            val pointWithinViewportX = ((zoomFocalPoint.x - scrollerStartViewport.left)
                / scrollerStartViewport.width())
            val pointWithinViewportY = ((zoomFocalPoint.y - scrollerStartViewport.bottom)
                / scrollerStartViewport.height())
            val left = zoomFocalPoint.x - newWidth * pointWithinViewportX
            val top = zoomFocalPoint.y + newHeight * (1 - pointWithinViewportY)
            val right = zoomFocalPoint.x + newWidth * (1 - pointWithinViewportX)
            val bottom = zoomFocalPoint.y - newHeight * pointWithinViewportY
            setCurrentViewport(computator, left, top, right, bottom)
            return true
        }
        return false
    }

    fun scale(computator: ChartComputator, focusX: Float, focusY: Float, scale: Float): Boolean {
        /*
          Smaller viewport means bigger zoom so for zoomIn scale should have value <1, for zoomOout >1
         */
        val newWidth = scale * computator.getCurrentViewport().width()
        val newHeight = scale * computator.getCurrentViewport().height()
        if (!computator.rawPixelsToDataPoint(focusX, focusY, viewportFocus)) {
            // Focus point is not within content area.
            return false
        }
        val contentMarginLeft = (focusX - computator.contentRectMinusAllMargins.left)
        val contentMarginTop = (focusY - computator.contentRectMinusAllMargins.top)
        val newWidthAllMargin = (newWidth / computator.contentRectMinusAllMargins.width())
        val newHeightAllMargin = (newHeight / computator.contentRectMinusAllMargins.height())
        val left = viewportFocus.x - contentMarginLeft * newWidthAllMargin
        val top = viewportFocus.y + contentMarginTop * newHeightAllMargin
        val right = left + newWidth
        val bottom = top - newHeight
        setCurrentViewport(computator, left, top, right, bottom)
        return true
    }

    private fun setCurrentViewport(
        computator: ChartComputator,
        left: Float,
        top: Float,
        right: Float,
        bottom: Float
    ) {
        val currentViewport = computator.getCurrentViewport()
        if (ZoomType.HORIZONTAL_AND_VERTICAL === zoomType) {
            computator.setCurrentViewport(left, top, right, bottom)
        } else if (ZoomType.HORIZONTAL === zoomType) {
            computator.setCurrentViewport(left, currentViewport.top, right, currentViewport.bottom)
        } else if (ZoomType.VERTICAL === zoomType) {
            computator.setCurrentViewport(currentViewport.left, top, currentViewport.right, bottom)
        }
    }

    companion object {
        const val ZOOM_AMOUNT = 0.25f
    }
}