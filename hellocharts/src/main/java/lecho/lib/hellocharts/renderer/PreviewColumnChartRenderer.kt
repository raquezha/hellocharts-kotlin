package lecho.lib.hellocharts.renderer

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import lecho.lib.hellocharts.provider.ColumnChartDataProvider
import lecho.lib.hellocharts.util.ChartUtils.dp2px
import lecho.lib.hellocharts.view.Chart

/**
 * Renderer for preview chart based on ColumnChart.
 * In addition to drawing chart data it also draw current viewport as preview area.
 */
class PreviewColumnChartRenderer(
    context: Context?,
    chart: Chart?,
    dataProvider: ColumnChartDataProvider?
) : ColumnChartRenderer(context, chart, dataProvider!!) {
    private val previewPaint = Paint()

    init {
        previewPaint.isAntiAlias = true
        previewPaint.color = Color.LTGRAY
        previewPaint.strokeWidth = dp2px(
            density,
            DEFAULT_PREVIEW_STROKE_WIDTH_DP
        ).toFloat()
    }

    override fun drawUnClipped(canvas: Canvas?) {
        super.drawUnClipped(canvas)
        val currentViewport = computator.getCurrentViewport()
        val left = computator.computeRawX(currentViewport.left)
        val top = computator.computeRawY(currentViewport.top)
        val right = computator.computeRawX(currentViewport.right)
        val bottom = computator.computeRawY(currentViewport.bottom)
        previewPaint.alpha = DEFAULT_PREVIEW_TRANSPARENCY
        previewPaint.style = Paint.Style.FILL
        canvas!!.drawRect(left, top, right, bottom, previewPaint)
        previewPaint.style = Paint.Style.STROKE
        previewPaint.alpha = FULL_ALPHA
        canvas.drawRect(left, top, right, bottom, previewPaint)
    }

    var previewColor: Int
        get() = previewPaint.color
        set(color) {
            previewPaint.color = color
        }

    companion object {
        private const val DEFAULT_PREVIEW_TRANSPARENCY = 64
        private const val FULL_ALPHA = 255
        private const val DEFAULT_PREVIEW_STROKE_WIDTH_DP = 2
    }
}