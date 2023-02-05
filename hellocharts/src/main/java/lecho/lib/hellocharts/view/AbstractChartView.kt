package lecho.lib.hellocharts.view

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.view.ViewCompat
import lecho.lib.hellocharts.animation.ChartAnimationListener
import lecho.lib.hellocharts.animation.ChartDataAnimator
import lecho.lib.hellocharts.animation.ChartDataAnimatorV14
import lecho.lib.hellocharts.animation.ChartViewportAnimator
import lecho.lib.hellocharts.animation.ChartViewportAnimatorV14
import lecho.lib.hellocharts.computator.ChartComputator
import lecho.lib.hellocharts.gesture.ChartTouchHandler
import lecho.lib.hellocharts.gesture.ContainerScrollType
import lecho.lib.hellocharts.gesture.ZoomType
import lecho.lib.hellocharts.listener.ViewportChangeListener
import lecho.lib.hellocharts.model.ChartData
import lecho.lib.hellocharts.model.SelectedValue
import lecho.lib.hellocharts.model.Viewport
import lecho.lib.hellocharts.renderer.AxesRenderer
import lecho.lib.hellocharts.renderer.ChartRenderer
import lecho.lib.hellocharts.util.ChartUtils

/**
 * Abstract class for charts views.
 *
 * @author Leszek Wach
 */
abstract class AbstractChartView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), Chart {

    @JvmField
    var chartComputator: ChartComputator = ChartComputator()

    @JvmField
    var axesRenderer: AxesRenderer

    @JvmField
    var touchHandler: ChartTouchHandler

    @JvmField
    var chartRenderer: ChartRenderer? = null

    @JvmField
    var chartData: ChartData? = null

    @JvmField
    var dataAnimator: ChartDataAnimator

    @JvmField
    var viewportAnimator: ChartViewportAnimator

    @JvmField
    var isInteractive = true

    @JvmField
    var isContainerScrollEnabled = false

    @JvmField
    var containerScrollType: ContainerScrollType? = null

    init {
        touchHandler = ChartTouchHandler(context, this)
        axesRenderer = AxesRenderer(context!!, this)
        viewportAnimator = ChartViewportAnimatorV14(this)
        dataAnimator = ChartDataAnimatorV14(this)
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        chartComputator.setContentRect(
            getWidth(), getHeight(), paddingLeft, paddingTop, paddingRight,
            paddingBottom
        )
        chartRenderer?.onChartSizeChanged()
        axesRenderer.onChartSizeChanged()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (isEnabled) {
            axesRenderer.drawInBackground(canvas)
            val clipRestoreCount = canvas.save()
            canvas.clipRect(chartComputator.contentRectMinusAllMargins)
            chartRenderer?.draw(canvas)
            canvas.restoreToCount(clipRestoreCount)
            chartRenderer?.drawUnClipped(canvas)
            axesRenderer.drawInForeground(canvas)
        } else {
            canvas.drawColor(ChartUtils.DEFAULT_COLOR)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event)
        return if (isInteractive()) {
            val needInvalidate: Boolean = if (isContainerScrollEnabled()) {
                getTouchHandler().handleTouchEvent(event, parent, containerScrollType)
            } else {
                getTouchHandler().handleTouchEvent(event)
            }
            if (needInvalidate) {
                ViewCompat.postInvalidateOnAnimation(this)
            }
            true
        } else {
            false
        }
    }

    override fun computeScroll() {
        super.computeScroll()
        if (isInteractive) {
            if (touchHandler.computeScroll()) {
                ViewCompat.postInvalidateOnAnimation(this)
            }
        }
    }

    override fun startDataAnimation() {
        dataAnimator.startAnimation(Long.MIN_VALUE)
    }

    override fun startDataAnimation(duration: Long) {
        dataAnimator.startAnimation(duration)
    }

    override fun cancelDataAnimation() {
        dataAnimator.cancelAnimation()
    }

    override fun animationDataUpdate(scale: Float) {
        getChartData().update(scale)
        getChartRenderer()?.onChartViewportChanged()
        ViewCompat.postInvalidateOnAnimation(this)
    }

    override fun animationDataFinished() {
        getChartData().finish()
        getChartRenderer()?.onChartViewportChanged()
        ViewCompat.postInvalidateOnAnimation(this)
    }

    override fun setDataAnimationListener(animationListener: ChartAnimationListener?) {
        dataAnimator.setChartAnimationListener(animationListener)
    }

    override fun setViewportAnimationListener(animationListener: ChartAnimationListener?) {
        viewportAnimator.setChartAnimationListener(animationListener)
    }

    override fun setViewportChangeListener(viewportChangeListener: ViewportChangeListener?) {
        chartComputator.setViewPortChangeListener(viewportChangeListener)
    }

    override fun getChartRenderer(): ChartRenderer? {
        return chartRenderer
    }

    final override fun setChartRenderer(chartRenderer: ChartRenderer) {
        this.chartRenderer = chartRenderer
        resetRendererAndTouchHandler()
        ViewCompat.postInvalidateOnAnimation(this)
    }

    override fun getAxesRenderer(): AxesRenderer {
        return axesRenderer
    }

    override fun getChartComputator(): ChartComputator {
        return chartComputator
    }

    override fun getTouchHandler(): ChartTouchHandler {
        return touchHandler
    }

    override fun isInteractive(): Boolean {
        return isInteractive
    }

    override fun setInteractive(isInteractive: Boolean) {
        this.isInteractive = isInteractive
    }

    override fun isZoomEnabled(): Boolean {
        return touchHandler.isZoomEnabled
    }

    override fun setZoomEnabled(isEnabled: Boolean) {
        touchHandler.isZoomEnabled = isEnabled
    }

    override fun isScrollEnabled(): Boolean {
        return touchHandler.isScrollEnabled
    }

    override fun setScrollEnabled(isEnabled: Boolean) {
        touchHandler.isScrollEnabled = isEnabled
    }

    override fun moveTo(x: Float, y: Float) {
        val scrollViewport = computeScrollViewport(x, y)
        setCurrentViewport(scrollViewport)
    }

    override fun moveToWithAnimation(x: Float, y: Float) {
        val scrollViewport = computeScrollViewport(x, y)
        setCurrentViewportWithAnimation(scrollViewport)
    }

    private fun computeScrollViewport(x: Float, y: Float): Viewport {
        val maxViewport = getMaximumViewport()
        val currentViewport = getCurrentViewport()
        val scrollViewport = Viewport(currentViewport)
        if (maxViewport != null && maxViewport.contains(x, y)) {
            val width: Float = currentViewport?.width() ?: 0f
            val height: Float = currentViewport?.height() ?: 0f
            val halfWidth = width / 2
            val halfHeight = height / 2
            var left = x - halfWidth
            var top = y + halfHeight
            left = maxViewport.left.coerceAtLeast(left.coerceAtMost(maxViewport.right - width))
            top = (maxViewport.bottom + height).coerceAtLeast(top.coerceAtMost(maxViewport.top))
            scrollViewport[left, top, left + width] = top - height
        }
        return scrollViewport
    }

    override fun isValueTouchEnabled(): Boolean {
        return touchHandler.isValueTouchEnabled
    }

    override fun setValueTouchEnabled(isEnabled: Boolean) {
        touchHandler.isValueTouchEnabled = isEnabled
    }

    override fun getZoomType(): ZoomType? {
        return touchHandler.zoomType
    }

    override fun setZoomType(zoomType: ZoomType?) {
        touchHandler.zoomType = zoomType!!
    }

    override fun getMaxZoom(): Float {
        return chartComputator.getMaximumZoom()
    }

    override fun setMaxZoom(zoomLevel: Float) {
        chartComputator.setMaximumZoom(zoomLevel)
        ViewCompat.postInvalidateOnAnimation(this)
    }

    override fun getZoomLevel(): Float {
        val maxViewport = getMaximumViewport()
        val currentViewport = getCurrentViewport()
        return ((maxViewport?.width() ?: 0f) / (currentViewport?.width() ?: 0f)).coerceAtLeast(
            ((maxViewport?.height() ?: 0f)) / ((currentViewport?.height() ?: 0f))
        )
    }

    override fun setZoomLevel(x: Float, y: Float, zoomLevel: Float) {
        val zoomViewport = computeZoomViewport(x, y, zoomLevel)
        setCurrentViewport(zoomViewport)
    }

    override fun setZoomLevelWithAnimation(x: Float, y: Float, zoomLevel: Float) {
        val zoomViewport = computeZoomViewport(x, y, zoomLevel)
        setCurrentViewportWithAnimation(zoomViewport)
    }

    private fun computeZoomViewport(x: Float, y: Float, zoomLevel: Float): Viewport {
        var level = zoomLevel
        val maxViewport = getMaximumViewport()
        val zoomViewport = Viewport(getMaximumViewport())
        if (maxViewport != null && maxViewport.contains(x, y)) {
            if (level < 1) {
                level = 1f
            } else if (level > getMaxZoom()) {
                level = getMaxZoom()
            }
            val newWidth = zoomViewport.width() / level
            val newHeight = zoomViewport.height() / level
            val halfWidth = newWidth / 2
            val halfHeight = newHeight / 2
            var left = x - halfWidth
            var right = x + halfWidth
            var top = y + halfHeight
            var bottom = y - halfHeight
            if (left < maxViewport.left) {
                left = maxViewport.left
                right = left + newWidth
            } else if (right > maxViewport.right) {
                right = maxViewport.right
                left = right - newWidth
            }
            if (top > maxViewport.top) {
                top = maxViewport.top
                bottom = top - newHeight
            } else if (bottom < maxViewport.bottom) {
                bottom = maxViewport.bottom
                top = bottom + newHeight
            }
            val zoomType = getZoomType()
            if (ZoomType.HORIZONTAL_AND_VERTICAL === zoomType) {
                zoomViewport[left, top, right] = bottom
            } else if (ZoomType.HORIZONTAL === zoomType) {
                zoomViewport.left = left
                zoomViewport.right = right
            } else if (ZoomType.VERTICAL === zoomType) {
                zoomViewport.top = top
                zoomViewport.bottom = bottom
            }
        }
        return zoomViewport
    }

    override fun getMaximumViewport(): Viewport? {
        return chartRenderer?.getMaximumViewport()
    }

    override fun setMaximumViewport(maxViewport: Viewport?) {
        chartRenderer?.setMaximumViewport(maxViewport)
        ViewCompat.postInvalidateOnAnimation(this)
    }

    override fun setCurrentViewportWithAnimation(targetViewport: Viewport?) {
        if (null != targetViewport && getCurrentViewport() != null) {
            viewportAnimator.cancelAnimation()
            viewportAnimator.startAnimation(getCurrentViewport()!!, targetViewport)
        }
        ViewCompat.postInvalidateOnAnimation(this)
    }

    override fun setCurrentViewportWithAnimation(targetViewport: Viewport?, duration: Long) {
        if (null != targetViewport && getCurrentViewport() != null) {
            viewportAnimator.cancelAnimation()
            viewportAnimator.startAnimation(getCurrentViewport()!!, targetViewport, duration)
        }
        ViewCompat.postInvalidateOnAnimation(this)
    }

    override fun getCurrentViewport(): Viewport? {
        return getChartRenderer()?.getCurrentViewport()
    }

    override fun setCurrentViewport(viewport: Viewport?) {
        if (null != viewport) {
            chartRenderer?.setCurrentViewport(viewport)
        }
        ViewCompat.postInvalidateOnAnimation(this)
    }

    override fun resetViewports() {
        chartRenderer?.setMaximumViewport(null)
        chartRenderer?.setCurrentViewport(null)
    }

    override fun isViewportCalculationEnabled(): Boolean {
        return chartRenderer?.isViewportCalculationEnabled() ?: false
    }

    override fun setViewportCalculationEnabled(isEnabled: Boolean) {
        chartRenderer?.setViewportCalculationEnabled(isEnabled)
    }

    override fun isValueSelectionEnabled(): Boolean {
        return touchHandler.isValueSelectionEnabled
    }

    override fun setValueSelectionEnabled(isEnabled: Boolean) {
        touchHandler.isValueSelectionEnabled = isEnabled
    }

    override fun setSelectedValue(selectedValue: SelectedValue?) {
        chartRenderer?.selectValue(selectedValue!!)
        callTouchListener()
        ViewCompat.postInvalidateOnAnimation(this)
    }

    override fun getSelectedValue(): SelectedValue? {
        return chartRenderer?.getSelectedValue()
    }

    override fun isContainerScrollEnabled(): Boolean {
        return isContainerScrollEnabled
    }

    override fun setContainerScrollEnabled(
        isContainerScrollEnabled: Boolean,
        containerScrollType: ContainerScrollType?
    ) {
        this.isContainerScrollEnabled = isContainerScrollEnabled
        this.containerScrollType = containerScrollType
    }

    protected fun onChartDataChange() {
        chartComputator.resetContentRect()
        chartRenderer?.onChartDataChanged()
        axesRenderer.onChartDataChanged()
        ViewCompat.postInvalidateOnAnimation(this)
    }

    /**
     * You should call this method in derived classes, most likely from constructor if you changed chart/axis renderer,
     * touch handler or chart computator
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun resetRendererAndTouchHandler() {
        chartRenderer?.resetRenderer()
        axesRenderer.resetRenderer()
        touchHandler.resetTouchHandler()
    }

    /**
     * When embedded in a ViewPager, this will be called in order to know if we can scroll.
     * If this returns true, the ViewPager will ignore the drag so that we can scroll our content.
     * If this return false, the ViewPager will assume we won't be able to scroll and will consume the drag
     *
     * @param direction Amount of pixels being scrolled (x axis)
     * @return true if the chart can be scrolled (ie. zoomed and not against the edge of the chart)
     */
    override fun canScrollHorizontally(direction: Int): Boolean {
        if (getZoomLevel() <= 1.0) {
            return false
        }
        val currentViewport = getCurrentViewport()
        val maximumViewport = getMaximumViewport()
        return if (direction < 0) {
            if (currentViewport != null && maximumViewport != null) {
                currentViewport.left > maximumViewport.left
            } else {
                false
            }
        } else if (currentViewport != null && maximumViewport != null) {
            currentViewport.right < maximumViewport.right
        } else {
            false
        }
    }
}