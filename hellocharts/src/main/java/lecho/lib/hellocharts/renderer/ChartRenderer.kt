package lecho.lib.hellocharts.renderer

import android.graphics.Canvas
import lecho.lib.hellocharts.model.SelectedValue
import lecho.lib.hellocharts.model.Viewport

/**
 * Interface for all chart renderer.
 */
interface ChartRenderer {

    fun onChartSizeChanged()

    fun onChartDataChanged()

    fun onChartViewportChanged()

    fun resetRenderer()

    /**
     * Draw chart data.
     */
    fun draw(canvas: Canvas?)

    /**
     * Draw chart data that should not be clipped to contentRect area.
     */
    fun drawUnClipped(canvas: Canvas?)

    /**
     * Checks if given pixel coordinates corresponds to any chart value. If yes return true and set selectedValue, if
     * not selectedValue should be *cleared* and method should return false.
     */
    fun checkTouch(touchX: Float, touchY: Float): Boolean

    /**
     * Returns true if there is value selected.
     */

    fun isTouched(): Boolean

    /**
     * Clear value selection.
     */
    fun clearTouch()

    fun getMaximumViewport(): Viewport?

    fun setMaximumViewport(maxViewport: Viewport?)

    fun getCurrentViewport(): Viewport?

    fun setCurrentViewport(viewport: Viewport?)

    fun isViewportCalculationEnabled(): Boolean

    fun setViewportCalculationEnabled(isEnabled: Boolean)

    fun selectValue(selectedValue: SelectedValue)

    fun getSelectedValue(): SelectedValue
}