@file:Suppress("MemberVisibilityCanBePrivate")

package lecho.lib.hellocharts.computator

import android.graphics.Point
import android.graphics.PointF
import android.graphics.Rect
import lecho.lib.hellocharts.listener.DummyViewportChangeListener
import lecho.lib.hellocharts.listener.ViewportChangeListener
import lecho.lib.hellocharts.model.Viewport
import kotlin.math.max
import kotlin.math.min

/**
 * Computes raw points coordinates(in pixels), holds content area dimensions and chart viewport.
 */
open class ChartComputator {
    protected var maxZoom = DEFAULT_MAXIMUM_ZOOM
    var chartWidth = 0
        protected set
    var chartHeight = 0
        protected set

    /**
     * Returns content rectangle in pixels.
     *
     * @see setContentRect
     */
    //contentRectMinusAllMargins <= contentRectMinusAxesMargins <= maxContentRect
    var contentRectMinusAllMargins = Rect()
        protected set

    /**
     * Returns content rectangle with chart internal margins, for example
     * for LineChart contentRectMinusAxesMargins is bigger than contentRectMinusAllMargins
     * by point radius, thanks to that points are not cut on edges.
     *
     * @see setContentRect
     */
    var contentRectMinusAxesMargins = Rect()
        protected set
    private var maxContentRect = Rect()

    /**
     * This rectangle represents the currently visible chart values ranges.
     * The currently visible chart X values are from this rectangle's left to its right.
     * The currently visible chart Y values are from this rectangle's top to its bottom.
     */
    protected var currentViewPort = Viewport()

    /**
     * Returns maximum viewport - values ranges extremes.
     */
    var maxViewport = Viewport()
        protected set
    var minViewportWidth = 0f
        protected set
    var minViewportHeight = 0f
        protected set

    /**
     * Warning! Viewport listener is disabled for all charts beside preview charts
     * to avoid additional method calls during animations.
     */
    protected var viewportChangeListener: ViewportChangeListener =
        DummyViewportChangeListener()

    /**
     * Calculates available width and height. Should be called when chart dimensions change.
     * ContentRect is relative to chart view not the device's screen.
     */
    fun setContentRect(
        width: Int,
        height: Int,
        paddingLeft: Int,
        paddingTop: Int,
        paddingRight: Int,
        paddingBottom: Int
    ) {
        chartWidth = width
        chartHeight = height
        maxContentRect[paddingLeft, paddingTop, width - paddingRight] = height - paddingBottom
        contentRectMinusAxesMargins.set(maxContentRect)
        contentRectMinusAllMargins.set(maxContentRect)
    }

    fun resetContentRect() {
        contentRectMinusAxesMargins.set(maxContentRect)
        contentRectMinusAllMargins.set(maxContentRect)
    }

    fun insetContentRect(deltaLeft: Int, deltaTop: Int, deltaRight: Int, deltaBottom: Int) {
        contentRectMinusAxesMargins.left = contentRectMinusAxesMargins.left + deltaLeft
        contentRectMinusAxesMargins.top = contentRectMinusAxesMargins.top + deltaTop
        contentRectMinusAxesMargins.right = contentRectMinusAxesMargins.right - deltaRight
        contentRectMinusAxesMargins.bottom = contentRectMinusAxesMargins.bottom - deltaBottom
        insetContentRectByInternalMargins(deltaLeft, deltaTop, deltaRight, deltaBottom)
    }

    fun insetContentRectByInternalMargins(
        deltaLeft: Int,
        deltaTop: Int,
        deltaRight: Int,
        deltaBottom: Int
    ) {
        contentRectMinusAllMargins.left = contentRectMinusAllMargins.left + deltaLeft
        contentRectMinusAllMargins.top = contentRectMinusAllMargins.top + deltaTop
        contentRectMinusAllMargins.right = contentRectMinusAllMargins.right - deltaRight
        contentRectMinusAllMargins.bottom = contentRectMinusAllMargins.bottom - deltaBottom
    }

    /**
     * Checks if new viewport doesn't exceed max available viewport.
     */
    open fun constrainViewport(left: Float, top: Float, right: Float, bottom: Float) {
        var cLeft = left
        var cTop = top
        var cRight = right
        var cBottom = bottom
        if (cRight - cLeft < minViewportWidth) {
            // Minimum width - constrain horizontal zoom!
            cRight = cLeft + minViewportWidth
            if (cLeft < maxViewport.left) {
                cLeft = maxViewport.left
                cRight = cLeft + minViewportWidth
            } else if (cRight > maxViewport.right) {
                cRight = maxViewport.right
                cLeft = cRight - minViewportWidth
            }
        }
        if (cTop - cBottom < minViewportHeight) {
            // Minimum height - constrain vertical zoom!
            cBottom = cTop - minViewportHeight
            if (cTop > maxViewport.top) {
                cTop = maxViewport.top
                cBottom = cTop - minViewportHeight
            } else if (cBottom < maxViewport.bottom) {
                cBottom = maxViewport.bottom
                cTop = cBottom + minViewportHeight
            }
        }
        currentViewPort.left = max(maxViewport.left, cLeft)
        currentViewPort.top = min(maxViewport.top, cTop)
        currentViewPort.right = min(maxViewport.right, cRight)
        currentViewPort.bottom = max(maxViewport.bottom, cBottom)
        viewportChangeListener.onViewportChanged(currentViewPort)
    }

    /**
     * Sets the current viewport (defined by [currentViewPort]) to the given X and Y positions.
     */
    fun setViewportTopLeft(left: Float, top: Float) {
        /**
         * Constrains within the scroll range.
         * The scroll range is simply the viewport extremes (AXIS_X_MAX, etc.)
         * minus the viewport size. For example, if the extrema were 0 and 10,
         * and the viewport size was 2, the scroll range would be 0 to 8.
         */
        var cLeft = left
        var cTop = top
        val curWidth = currentViewPort.width()
        val curHeight = currentViewPort.height()
        cLeft = max(maxViewport.left, min(cLeft, maxViewport.right - curWidth))
        cTop = max(maxViewport.bottom + curHeight, min(cTop, maxViewport.top))
        constrainViewport(cLeft, cTop, cLeft + curWidth, cTop - curHeight)
    }

    /**
     * Translates chart value into raw pixel value.
     * Returned value is absolute pixel X coordinate.
     * If this method return 0 that means left most pixel of the screen.
     */
    open fun computeRawX(valueX: Float): Float {
        // TODO: (contentRectMinusAllMargins.width() / currentViewport.width())
        //  can be recalculated only when viewport change.
        val pixelOffset = (valueX - currentViewPort.left) * (contentRectMinusAllMargins.width() /
            currentViewPort.width())
        return contentRectMinusAllMargins.left + pixelOffset
    }

    /**
     * Translates chart value into raw pixel value. Returned value is absolute pixel Y coordinate.
     * If this method return 0 that means top most pixel of the screen.
     */
    open fun computeRawY(valueY: Float): Float {
        val pixelOffset = (valueY - currentViewPort.bottom) * (contentRectMinusAllMargins.height() /
            currentViewPort.height())

        // if currentViewport.height() == 0  for example the line inLineChart is not visiable.
        return if(pixelOffset.isNaN()) {
            contentRectMinusAllMargins.bottom.toFloat()
        } else{
            contentRectMinusAllMargins.bottom - pixelOffset
        }
    }

    /**
     * Translates viewport distance int pixel distance for X coordinates.
     */
    fun computeRawDistanceX(distance: Float): Float {
        return distance * (contentRectMinusAllMargins.width() / currentViewPort.width())
    }

    /**
     * Translates viewport distance int pixel distance for X coordinates.
     */
    fun computeRawDistanceY(distance: Float): Float {
        return distance * (contentRectMinusAllMargins.height() / currentViewPort.height())
    }

    /**
     * Finds the chart point (i.e. within the chart's domain and range) represented
     * by the given pixel coordinates, if that pixel is within the chart region
     * described by [contentRectMinusAllMargins]. If the point is found, the "dest" argument
     * is set to the point and this function returns true. Otherwise, this function
     * returns false and "dest" is unchanged.
     */
    fun rawPixelsToDataPoint(x: Float, y: Float, dest: PointF): Boolean {
        if (!contentRectMinusAllMargins.contains(x.toInt(), y.toInt())) {
            return false
        }
        dest[currentViewPort.left + (x - contentRectMinusAllMargins.left) * currentViewPort.width() /
            contentRectMinusAllMargins.width()] =
            currentViewPort.bottom + (y - contentRectMinusAllMargins.bottom) * currentViewPort.height() /
                -contentRectMinusAllMargins.height()
        return true
    }

    /**
     * Computes the current scrollable surface size, in pixels.
     * For example, if the entire chart area is visible, this is simply the current
     * size of [contentRectMinusAllMargins]. If the chart is zoomed in 200% in both directions,
     * the returned size will be twice as large horizontally and vertically.
     */
    fun computeScrollSurfaceSize(out: Point) {
        out[(maxViewport.width() * contentRectMinusAllMargins.width() / currentViewPort.width()).toInt()] =
            (maxViewport.height() * contentRectMinusAllMargins.height() / currentViewPort.height()).toInt()
    }

    /**
     * Check if given coordinates lies inside contentRectMinusAllMargins.
     */
    fun isWithinContentRect(x: Float, y: Float, precision: Float): Boolean {
        if (x >= contentRectMinusAllMargins.left - precision && x <= contentRectMinusAllMargins.right + precision) {
            if (y <= contentRectMinusAllMargins.bottom + precision && y >= contentRectMinusAllMargins.top -
                precision
            ) {
                return true
            }
        }
        return false
    }

    /**
     * Returns current chart viewport, returned object is mutable but should not be modified.
     *
     * @return [lecho.lib.hellocharts.model.Viewport]
     */
    fun getCurrentViewport(): Viewport {
        return currentViewPort
    }

    /**
     * Set current viewport to the same values as viewport passed in parameter.
     * This method use deep copy so parameter can be safely modified later.
     * Current viewport must be equal or smaller than maximum viewport.
     *
     * @param viewport [lecho.lib.hellocharts.model.Viewport]
     */
    fun setCurrentViewport(viewport: Viewport) {
        constrainViewport(viewport.left, viewport.top, viewport.right, viewport.bottom)
    }

    /**
     * Set new values for current viewport, that will change what part of chart is visible.
     * Current viewport must be equal or smaller than maximum viewport.
     */
    fun setCurrentViewport(left: Float, top: Float, right: Float, bottom: Float) {
        constrainViewport(left, top, right, bottom)
    }

    /**
     * Set maximum viewport to the same values as viewport passed in parameter.
     * This method use deep copy so parameter can be safely modified later.
     *
     * @param maxViewport
     */
    fun setMaximumViewport(maxViewport: Viewport) {
        setMaximumViewport(maxViewport.left, maxViewport.top, maxViewport.right, maxViewport.bottom)
    }

    /**
     * Set new values for maximum viewport, that will change what part of chart is visible.
     */
    fun setMaximumViewport(left: Float, top: Float, right: Float, bottom: Float) {
        maxViewport[left, top, right] = bottom
        computeMinimumWidthAndHeight()
    }

    /**
     * Returns viewport for visible part of chart, for most charts it is equal to current viewport.
     *
     * @return [lecho.lib.hellocharts.model.Viewport]
     */
    open var visibleViewport: Viewport
        get() = currentViewPort
        set(visibleViewport) {
            setCurrentViewport(visibleViewport)
        }

    fun setViewPortChangeListener(viewportChangeListener: ViewportChangeListener?) {
        if (null == viewportChangeListener) {
            this.viewportChangeListener =
                DummyViewportChangeListener()
        } else {
            this.viewportChangeListener = viewportChangeListener
        }
    }

    fun getMaximumZoom(): Float {
        return maxZoom
    }

    /**
     * Set maximum zoom level, default is 20.
     *
     * @param maxZoom
     */
    fun setMaximumZoom(maxZoom: Float) {
        var cMaxZoom = maxZoom
        if (cMaxZoom < 1) {
            cMaxZoom = 1f
        }
        this.maxZoom = cMaxZoom
        computeMinimumWidthAndHeight()
        setCurrentViewport(currentViewPort)
    }

    private fun computeMinimumWidthAndHeight() {
        minViewportWidth = maxViewport.width() / maxZoom
        minViewportHeight = maxViewport.height() / maxZoom
    }

    companion object {
        /**
         * Maximum chart zoom.
         */
        protected const val DEFAULT_MAXIMUM_ZOOM = 20f
    }
}