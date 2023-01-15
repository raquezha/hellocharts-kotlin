package lecho.lib.hellocharts.gesture

import android.content.Context
import android.graphics.Point
import android.widget.OverScroller
import lecho.lib.hellocharts.computator.ChartComputator
import lecho.lib.hellocharts.model.Viewport

/**
 * Encapsulates scrolling functionality.
 */
class ChartScroller(context: Context?) {
    private val scrollerStartViewport = Viewport() // Used only for zooms and flings
    private val surfaceSizeBuffer = Point() // Used for scroll and flings
    private val scroller: OverScroller

    init {
        scroller = OverScroller(context)
    }

    fun startScroll(computator: ChartComputator): Boolean {
        scroller.abortAnimation()
        scrollerStartViewport.set(computator.getCurrentViewport())
        return true
    }
    /**
     * Scrolling uses math based on the viewport (as opposed to math using pixels).
     * Pixel offset is the offset in screen pixels, while viewport offset is the offset within
     * the current viewport. For additional information on surface sizes and pixel offsets,
     * see the docs for @link[lecho.lib.hellocharts.computator.ChartComputator.computeScrollSurfaceSize]
     * For additional information about the viewport,
     * see the comments for {@link #mCurrentViewport}.
     **/
    fun scroll(
        computator: ChartComputator,
        distanceX: Float,
        distanceY: Float,
        scrollResult: ScrollResult
    ): Boolean {


        val maxViewport = computator.maxViewport
        val visibleViewport = computator.visibleViewport
        val currentViewport = computator.getCurrentViewport()
        val contentRect = computator.contentRectMinusAllMargins
        val canScrollLeft = currentViewport.left > maxViewport.left
        val canScrollRight = currentViewport.right < maxViewport.right
        val canScrollTop = currentViewport.top < maxViewport.top
        val canScrollBottom = currentViewport.bottom > maxViewport.bottom
        var canScrollX = false
        var canScrollY = false
        if (canScrollLeft && distanceX <= 0) {
            canScrollX = true
        } else if (canScrollRight && distanceX >= 0) {
            canScrollX = true
        }
        if (canScrollTop && distanceY <= 0) {
            canScrollY = true
        } else if (canScrollBottom && distanceY >= 0) {
            canScrollY = true
        }
        if (canScrollX || canScrollY) {
            computator.computeScrollSurfaceSize(surfaceSizeBuffer)
            val viewportOffsetX = distanceX * visibleViewport.width() / contentRect.width()
            val viewportOffsetY = -distanceY * visibleViewport.height() / contentRect.height()
            computator
                .setViewportTopLeft(
                    currentViewport.left + viewportOffsetX,
                    currentViewport.top + viewportOffsetY
                )
        }
        scrollResult.canScrollX = canScrollX
        scrollResult.canScrollY = canScrollY
        return canScrollX || canScrollY
    }

    fun computeScrollOffset(computator: ChartComputator): Boolean {
        if (scroller.computeScrollOffset()) {
            // The scroller isn't finished, meaning a fling or programmatic pan operation is
            // currently active.
            val maxViewport = computator.maxViewport
            computator.computeScrollSurfaceSize(surfaceSizeBuffer)
            val currXRange = maxViewport.left + maxViewport.width() * scroller.currX /
                surfaceSizeBuffer.x
            val currYRange = maxViewport.top - maxViewport.height() * scroller.currY /
                surfaceSizeBuffer.y
            computator.setViewportTopLeft(currXRange, currYRange)
            return true
        }
        return false
    }

    fun fling(velocityX: Int, velocityY: Int, computator: ChartComputator): Boolean {
        // Flings use math in pixels (as opposed to math based on the viewport).
        computator.computeScrollSurfaceSize(surfaceSizeBuffer)
        scrollerStartViewport.set(computator.getCurrentViewport())
        val startX =
            (surfaceSizeBuffer.x * (scrollerStartViewport.left - computator.maxViewport.left)
                / computator.maxViewport.width()).toInt()
        val startY =
            (surfaceSizeBuffer.y * (computator.maxViewport.top - scrollerStartViewport.top) /
                computator.maxViewport.height()).toInt()

        scroller.forceFinished(true)
        val width = computator.contentRectMinusAllMargins.width()
        val height = computator.contentRectMinusAllMargins.height()
        scroller.fling(
            /* startX = */ startX,
            /* startY = */ startY,
            /* velocityX = */ velocityX,
            /* velocityY = */ velocityY,
            /* minX = */ 0,
            /* maxX = */ surfaceSizeBuffer.x - width + 1,
            /* minY = */ 0,
            /* maxY = */ surfaceSizeBuffer.y - height + 1
        )
        return true
    }

    data class ScrollResult(
        var canScrollX: Boolean = false,
        var canScrollY:Boolean = false
    )
}