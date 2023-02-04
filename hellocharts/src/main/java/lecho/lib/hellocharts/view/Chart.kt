package lecho.lib.hellocharts.view

import lecho.lib.hellocharts.animation.ChartAnimationListener
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

/**
 * Interface for all charts. Every chart must implements this interface but chart doesn't really
 * have to extends View or ViewGroup class. It can be any java class for example chart that only
 * draw on in-memory bitmap and saves it on sd card.
 */
@Suppress("unused")
interface Chart {

    /**
     * Returns generic chart data. For specific class call get*ChartData method from data provider
     * implementation.
     */
    fun getChartData(): ChartData?

    fun setChartData(chartData: ChartData?)

    fun getChartRenderer(): ChartRenderer?

    fun setChartRenderer(chartRenderer: ChartRenderer?)

    fun getAxesRenderer(): AxesRenderer?

    fun setAxesRenderer(axesRenderer: AxesRenderer?)

    fun setChartComputator(chartComputator: ChartComputator?)

    fun getChartComputator(): ChartComputator?

    fun getTouchHandler(): ChartTouchHandler?

    fun setTouchHandler(): ChartTouchHandler?

    /**
     * Updates chart data with given scale. Called during chart data animation update.
     */
    fun animationDataUpdate(scale: Float)

    /**
     * Called when data animation finished.
     */
    fun animationDataFinished()

    /**
     * Starts chart data animation for given duration. Before you call this method you should change
     * target values of chart data.
     */
    fun startDataAnimation()

    /**
     * Starts chart data animation for given duration. If duration is negative the default value of
     * 500ms will be used. Before you call this method you should change target values of chart data.
     */
    fun startDataAnimation(duration: Long)

    /**
     * Stops chart data animation. All chart data values are set to their target values.
     */
    fun cancelDataAnimation()
    /**
     * Return true if auto viewports recalculations are enabled, false otherwise.
     */
    /**
     * Set true to enable viewports(max and current) recalculations during animations or after
     * setChartData method is called. If you disable viewports calculations viewports will not
     * change until you change them manually or enable calculations again. Disabled viewport
     * calculations is usefully if you want show only part of chart by setting custom viewport and
     * don't want any operation to change that viewport
     */
    fun isViewportCalculationEnabled(): Boolean

    fun setViewportCalculationEnabled(isEnabled: Boolean)

    /**
     * Set listener for data animation to be notified when data animation started and finished.
     * By default that flag is set to true so be careful with animation and custom viewports.
     */
    fun setDataAnimationListener(animationListener: ChartAnimationListener?)

    /**
     * Set listener for viewport animation to be notified when viewport animation started and finished.
     */
    fun setViewportAnimationListener(animationListener: ChartAnimationListener?)

    /**
     * Set listener for current viewport changes. It will be called when viewport change either
     * by gesture or programmatically. Note! This method works only for preview charts.
     * It is intentionally disabled for other types of charts to avoid unnecessary method calls
     * during invalidation.
     */
    fun setViewportChangeListener(viewportChangeListener: ViewportChangeListener?)

    fun callTouchListener()

    fun getInteractive(): Boolean

    /**
     * Set true to allow user use touch gestures. If set to false user will not be able zoom,
     * scroll or select/touch value. By default true.
     */
    fun setInteractive(isInteractive: Boolean)

    fun isInteractive(): Boolean

    /**
     * Set true to enable zoom, false to disable, by default true;
     */
    fun isZoomEnabled(): Boolean

    fun setZoomEnabled(isEnabled: Boolean)

    /**
     * Set true to enable touch scroll/fling, false to disable touch scroll/fling, by default true;
     */
    fun isScrollEnabled(): Boolean

    fun setScrollEnabled(isEnabled: Boolean)

    /**
     * Move Scroll viewport to position x,y(that position must be within maximum chart viewport).
     * If possible viewport will be centered at this point.
     * Width and height of viewport will not be modified.
     */
    fun moveTo(x: Float, y: Float)

    /**
     * Animate viewport to position x,y(that position must be within maximum chart viewport).
     * If possible viewport will be centered at this point. Width and height of viewport will not
     * be modified.
     *
     * @see .setCurrentViewport
     */
    fun moveToWithAnimation(x: Float, y: Float)

    /**
     * Set zoom type, available options:
     * ZoomType.HORIZONTAL_AND_VERTICAL,
     * ZoomType.HORIZONTAL, ZoomType.VERTICAL.
     * By default HORIZONTAL_AND_VERTICAL.
     */
    fun getZoomType(): ZoomType?

    fun setZoomType(zoomType: ZoomType?)

    /**
     * Set max zoom value. Default maximum zoom is 20.
     */
    fun getMaxZoom(): Float

    fun setMaxZoom(zoomLevel: Float)

    /**
     * Returns current zoom level.
     */
    fun getZoomLevel(): Float

    /**
     * Programmatically zoom chart to given point(viewport point). Call this method after
     * chart data had been set.
     *
     * @param x         x within chart maximum viewport
     * @param y         y within chart maximum viewport
     * @param zoomLevel value from 1 to maxZoom(default 20). 1 means chart has no zoom.
     */
    fun setZoomLevel(x: Float, y: Float, zoomLevel: Float)

    /**
     * Programmatically zoom chart to given point(viewport point) with animation. Call this method
     * after chart data had been set.
     *
     * @param x         x within chart maximum viewport
     * @param y         y within chart maximum viewport
     * @param zoomLevel value from 1 to maxZoom(default 20). 1 means chart has no zoom.
     */
    fun setZoomLevelWithAnimation(x: Float, y: Float, zoomLevel: Float)

    /**
     * Set true if you want allow user to click value on chart, set false to disable that option.
     * By default true.
     */
    fun isValueTouchEnabled(): Boolean

    fun setValueTouchEnabled(isEnabled: Boolean)

    /**
     * Set maximum viewport. If you set bigger maximum viewport data will be more concentrate and
     * there will be more empty spaces on sides. Note. MaxViewport have to be set after chartData
     * has been set.
     */
    fun getMaximumViewport(): Viewport?

    fun setMaximumViewport(maxViewport: Viewport?)

    /**
     * Sets current viewport. Note. viewport have to be set after chartData has been set.
     */
    fun getCurrentViewport(): Viewport?

    fun setCurrentViewport(viewport: Viewport?)

    /**
     * Sets current viewport with animation. Note. viewport have to be set after chartData has been set.
     */
    fun setCurrentViewportWithAnimation(targetViewport: Viewport?)

    /**
     * Sets current viewport with animation. Note. viewport have to be set after chartData has been set.
     */
    fun setCurrentViewportWithAnimation(targetViewport: Viewport?, duration: Long)

    /**
     * Reset maximum viewport and current viewport. Values for both viewports will be auto-calculated
     * using current chart data ranges.
     */
    fun resetViewports()

    /**
     * Set true if you want value selection with touch - value will stay selected until you touch
     * somewhere else on the chart area. By default false and value is automatically unselected
     * when user stop pressing on it.
     */
    fun isValueSelectionEnabled(): Boolean

    fun setValueSelectionEnabled(isEnabled: Boolean)

    /**
     * Select single value on chart.
     * If indexes are not valid IndexOutOfBoundsException will be thrown.
     */
    fun setSelectedValue(selectedValue: SelectedValue?)

    /**
     * Return currently selected value indexes.
     */
    fun getSelectedValue(): SelectedValue?

    fun isContainerScrollEnabled(): Boolean

    fun setContainerScrollEnabled(isEnabled: Boolean)

    /**
     * Set isContainerScrollEnabled to true and containerScrollType to HORIZONTAL or VERTICAL
     * if you are using chart within scroll container.
     */
    fun setContainerScrollEnabled(
        isContainerScrollEnabled: Boolean,
        containerScrollType: ContainerScrollType?
    )
}