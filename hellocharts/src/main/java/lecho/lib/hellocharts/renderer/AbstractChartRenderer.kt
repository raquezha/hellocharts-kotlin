package lecho.lib.hellocharts.renderer

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.Align
import android.graphics.Paint.FontMetricsInt
import android.graphics.RectF
import android.graphics.Typeface
import lecho.lib.hellocharts.computator.ChartComputator
import lecho.lib.hellocharts.model.SelectedValue
import lecho.lib.hellocharts.model.Viewport
import lecho.lib.hellocharts.util.ChartUtils.dp2px
import lecho.lib.hellocharts.util.ChartUtils.sp2px
import lecho.lib.hellocharts.view.Chart

/**
 * Abstract renderer implementation, every chart renderer extends this class(although it is not required it helps).
 */
abstract class AbstractChartRenderer(
    context: Context,
    @JvmField protected var chart: Chart
) : ChartRenderer {

    @JvmField
    var computator: ChartComputator

    /**
     * Paint for value labels.
     */
    @JvmField
    var labelPaint = Paint()

    /**
     * Paint for labels background.
     */
    @JvmField
    var labelBackgroundPaint = Paint()

    /**
     * Holds coordinates for label background rect.
     */
    @JvmField
    var labelBackgroundRect = RectF()

    /**
     * Font metrics for label paint, used to determine text height.
     */
    @JvmField
    var fontMetrics = FontMetricsInt()

    /**
     * If true maximum and current viewport will be calculated when chart data change or during data animations.
     */
    @JvmField
    var isViewportCalculationEnabled = true

    @JvmField
    var density: Float

    @JvmField
    var scaledDensity: Float

    @JvmField
    var selectedValue = SelectedValue()

    @JvmField
    var labelBuffer = CharArray(64)

    @JvmField
    var labelOffset: Int

    @JvmField
    var labelMargin: Int

    @JvmField
    var isValueLabelBackgroundEnabled = false

    @JvmField
    var isValueLabelBackgroundAuto = false

    init {
        density = context.resources.displayMetrics.density
        scaledDensity = context.resources.displayMetrics.scaledDensity
        computator = chart.getChartComputator()
        labelMargin = dp2px(density, DEFAULT_LABEL_MARGIN_DP)
        labelOffset = labelMargin
        labelPaint.isAntiAlias = true
        labelPaint.style = Paint.Style.FILL
        labelPaint.textAlign = Align.LEFT
        labelPaint.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
        labelPaint.color = Color.WHITE
        labelBackgroundPaint.isAntiAlias = true
        labelBackgroundPaint.style = Paint.Style.FILL
    }

    override fun resetRenderer() {
        computator = chart.getChartComputator()
    }

    override fun onChartDataChanged() {
        val data = chart.getChartData()
        val typeface = chart.getChartData().getValueLabelTypeface()
        if (null != typeface) {
            labelPaint.typeface = typeface
        }
        labelPaint.color = data.getValueLabelTextColor()
        labelPaint.textSize = sp2px(scaledDensity, data.getValueLabelTextSize()).toFloat()
        labelPaint.getFontMetricsInt(fontMetrics)
        isValueLabelBackgroundEnabled = data.isValueLabelBackgroundEnabled()
        isValueLabelBackgroundAuto = data.isValueLabelBackgroundAuto()
        labelBackgroundPaint.color = data.getValueLabelBackgroundColor()

        // Important - clear selection when data changed.
        selectedValue.clear()
    }

    /**
     * Draws label text and label background if isValueLabelBackgroundEnabled is true.
     */
    protected fun drawLabelTextAndBackground(
        canvas: Canvas, labelBuffer: CharArray?, startIndex: Int, numChars: Int,
        autoBackgroundColor: Int
    ) {
        val textX: Float
        val textY: Float
        if (isValueLabelBackgroundEnabled) {
            if (isValueLabelBackgroundAuto) {
                labelBackgroundPaint.color = autoBackgroundColor
            }
            canvas.drawRect(labelBackgroundRect, labelBackgroundPaint)
            textX = labelBackgroundRect.left + labelMargin
            textY = labelBackgroundRect.bottom - labelMargin
        } else {
            textX = labelBackgroundRect.left
            textY = labelBackgroundRect.bottom
        }
        canvas.drawText(labelBuffer!!, startIndex, numChars, textX, textY, labelPaint)
    }

    override fun isTouched(): Boolean {
        return selectedValue.isSet
    }

    override fun clearTouch() {
        selectedValue.clear()
    }

    override fun getMaximumViewport(): Viewport? {
        return computator.maxViewport
    }

    override fun setMaximumViewport(maxViewport: Viewport?) {
        if (null != maxViewport) {
            computator.setMaximumViewport(maxViewport)
        }
    }

    override fun getCurrentViewport(): Viewport? {
        return computator.getCurrentViewport()
    }

    override fun setCurrentViewport(viewport: Viewport?) {
        if (null != viewport) {
            computator.setCurrentViewport(viewport)
        }
    }

    override fun isViewportCalculationEnabled(): Boolean {
        return isViewportCalculationEnabled
    }

    override fun setViewportCalculationEnabled(isEnabled: Boolean) {
        isViewportCalculationEnabled = isEnabled
    }

    override fun selectValue(selectedValue: SelectedValue) {
        this.selectedValue.set(selectedValue)
    }

    override fun getSelectedValue(): SelectedValue {
        return selectedValue
    }

    companion object {
        const val DEFAULT_LABEL_MARGIN_DP = 4
    }
}