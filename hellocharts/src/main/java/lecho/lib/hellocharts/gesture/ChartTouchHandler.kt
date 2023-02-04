package lecho.lib.hellocharts.gesture

import android.content.Context
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import android.view.ViewParent
import lecho.lib.hellocharts.computator.ChartComputator
import lecho.lib.hellocharts.gesture.ChartScroller.ScrollResult
import lecho.lib.hellocharts.model.SelectedValue
import lecho.lib.hellocharts.renderer.ChartRenderer
import lecho.lib.hellocharts.view.Chart

/**
 * Default touch handler for most charts. Handles value touch, scroll, fling and zoom.
 */
open class ChartTouchHandler(context: Context?, protected var chart: Chart) {
    protected var gestureDetector: GestureDetector
    protected var scaleGestureDetector: ScaleGestureDetector
    protected var chartScroller: ChartScroller
    protected var chartZoomer: ChartZoomer
    protected var computator: ChartComputator
    protected var renderer: ChartRenderer?

    @JvmField
    var isZoomEnabled = true
    @JvmField
    var isScrollEnabled = true
    @JvmField
    var isValueTouchEnabled = true
    @JvmField
    var isValueSelectionEnabled = false

    /**
     * Used only for selection mode to avoid calling listener multiple times for the same selection. Small thing but it
     * is more intuitive this way.
     */
    protected var selectionModeOldValue = SelectedValue()
    protected var selectedValue = SelectedValue()
    protected var oldSelectedValue = SelectedValue()

    /**
     * ViewParent to disallow touch events interception if chart is within scroll container.
     */
    protected var viewParent: ViewParent? = null

    /**
     * Type of scroll of container, horizontal or vertical.
     */
    protected var containerScrollType: ContainerScrollType? = null

    init {
        computator = chart.getChartComputator()
        renderer = chart.getChartRenderer()
        gestureDetector = GestureDetector(context, ChartGestureListener())
        scaleGestureDetector = ScaleGestureDetector(context!!, ChartScaleGestureListener())
        chartScroller = ChartScroller(context)
        chartZoomer = ChartZoomer(ZoomType.HORIZONTAL_AND_VERTICAL)
    }

    fun resetTouchHandler() {
        computator = chart.getChartComputator()
        renderer = chart.getChartRenderer()
    }

    /**
     * Computes scroll and zoom using [ChartScroller] and [ChartZoomer]. This method returns true if
     * scroll/zoom was computed and chart needs to be invalidated.
     */
    open fun computeScroll(): Boolean {
        var needInvalidate = isScrollEnabled && chartScroller.computeScrollOffset(
            computator
        )
        if (isZoomEnabled && chartZoomer.computeZoom(computator)) {
            needInvalidate = true
        }
        return needInvalidate
    }

    /**
     * Handle chart touch event(gestures, clicks). Return true if gesture was handled and chart needs to be
     * invalidated.
     */
    open fun handleTouchEvent(event: MotionEvent): Boolean {
        var needInvalidate: Boolean

        // TODO: detectors always return true, use class member needInvalidate instead local variable as workaround.
        // This flag should be computed inside gesture listeners methods to avoid invalidation.
        needInvalidate = gestureDetector.onTouchEvent(event)
        needInvalidate = scaleGestureDetector.onTouchEvent(event) || needInvalidate
        if (isZoomEnabled && scaleGestureDetector.isInProgress) {
            // Special case: if view is inside scroll container and user is scaling disable touch interception by
            // parent.
            disallowParentInterceptTouchEvent()
        }
        if (isValueTouchEnabled) {
            needInvalidate = computeTouch(event) || needInvalidate
        }
        return needInvalidate
    }

    /**
     * Handle chart touch event(gestures, clicks). Return true if gesture was handled and chart needs to be
     * invalidated.
     * If viewParent and containerScrollType are not null chart can be scrolled and scaled within horizontal or
     * vertical
     * scroll container like ViewPager.
     */
    fun handleTouchEvent(
        event: MotionEvent, viewParent: ViewParent?,
        containerScrollType: ContainerScrollType?
    ): Boolean {
        this.viewParent = viewParent
        this.containerScrollType = containerScrollType
        return handleTouchEvent(event)
    }

    /**
     * Disallow parent view from intercepting touch events. Use it for chart that is within some scroll container i.e.
     * ViewPager.
     */
    private fun disallowParentInterceptTouchEvent() {
        if (null != viewParent) {
            viewParent!!.requestDisallowInterceptTouchEvent(true)
        }
    }

    /**
     * Allow parent view to intercept touch events if chart cannot be scroll horizontally or vertically according to
     * the
     * current value of [.containerScrollType].
     */
    private fun allowParentInterceptTouchEvent(scrollResult: ScrollResult) {
        if (null != viewParent) {
            if (ContainerScrollType.HORIZONTAL === containerScrollType && !scrollResult.canScrollX
                && !scaleGestureDetector.isInProgress
            ) {
                viewParent!!.requestDisallowInterceptTouchEvent(false)
            } else if (ContainerScrollType.VERTICAL === containerScrollType && !scrollResult.canScrollY
                && !scaleGestureDetector.isInProgress
            ) {
                viewParent!!.requestDisallowInterceptTouchEvent(false)
            }
        }
    }

    private fun computeTouch(event: MotionEvent): Boolean {
        var needInvalidate = false
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val wasTouched = renderer!!.isTouched()
                val isTouched = checkTouch(event.x, event.y)
                if (wasTouched != isTouched) {
                    needInvalidate = true
                    if (isValueSelectionEnabled) {
                        selectionModeOldValue.clear()
                        if (wasTouched && !renderer!!.isTouched()) {
                            chart.callTouchListener()
                        }
                    }
                }
            }

            MotionEvent.ACTION_UP -> if (renderer!!.isTouched()) {
                if (checkTouch(event.x, event.y)) {
                    if (isValueSelectionEnabled) {
                        // For selection mode call listener only if selected value changed,
                        // that means that should be
                        // first(selection) click on given value.
                        if (selectionModeOldValue != selectedValue) {
                            selectionModeOldValue.set(selectedValue)
                            chart.callTouchListener()
                        }
                    } else {
                        chart.callTouchListener()
                        renderer!!.clearTouch()
                    }
                } else {
                    renderer!!.clearTouch()
                }
                needInvalidate = true
            }

            MotionEvent.ACTION_MOVE ->                 // If value was touched and now touch point is outside of value area - clear touch and invalidate, user
                // probably moved finger away from given chart value.
                if (renderer!!.isTouched()) {
                    if (!checkTouch(event.x, event.y)) {
                        renderer!!.clearTouch()
                        needInvalidate = true
                    }
                }

            MotionEvent.ACTION_CANCEL -> if (renderer!!.isTouched()) {
                renderer!!.clearTouch()
                needInvalidate = true
            }
        }
        return needInvalidate
    }

    private fun checkTouch(touchX: Float, touchY: Float): Boolean {
        oldSelectedValue.set(selectedValue)
        selectedValue.clear()
        if (renderer!!.checkTouch(touchX, touchY)) {
            selectedValue.set(renderer!!.getSelectedValue())
        }

        // Check if selection is still on the same value, if not return false.
        return if (oldSelectedValue.isSet && selectedValue.isSet && oldSelectedValue != selectedValue) {
            false
        } else {
            renderer!!.isTouched()
        }
    }

    var zoomType: ZoomType
        get() = chartZoomer.zoomType
        set(zoomType) {
            chartZoomer.zoomType = zoomType
        }

    protected inner class ChartScaleGestureListener : SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            if (isZoomEnabled) {
                var scale = 2.0f - detector.scaleFactor
                if (java.lang.Float.isInfinite(scale)) {
                    scale = 1f
                }
                return chartZoomer.scale(
                    computator = computator,
                    focusX = detector.focusX,
                    focusY = detector.focusY,
                    scale = scale
                )
            }
            return false
        }
    }

    protected open inner class ChartGestureListener : SimpleOnGestureListener() {
        protected var scrollResult = ScrollResult()
        override fun onDown(e: MotionEvent): Boolean {
            if (isScrollEnabled) {
                disallowParentInterceptTouchEvent()
                return chartScroller.startScroll(computator)
            }
            return false
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            return if (isZoomEnabled) {
                chartZoomer.startZoom(e, computator)
            } else false
        }

        override fun onScroll(
            e1: MotionEvent,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            if (isScrollEnabled) {
                val canScroll = chartScroller.scroll(computator, distanceX, distanceY, scrollResult)
                allowParentInterceptTouchEvent(scrollResult)
                return canScroll
            }
            return false
        }

        override fun onFling(
            e1: MotionEvent,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            return if (isScrollEnabled) {
                chartScroller.fling(-velocityX.toInt(), -velocityY.toInt(), computator)
            } else false
        }
    }
}