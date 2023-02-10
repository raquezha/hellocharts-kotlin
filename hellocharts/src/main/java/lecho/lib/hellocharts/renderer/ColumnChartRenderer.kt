package lecho.lib.hellocharts.renderer

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.Cap
import android.graphics.PointF
import android.graphics.RectF
import lecho.lib.hellocharts.model.Column
import lecho.lib.hellocharts.model.ColumnChartData
import lecho.lib.hellocharts.model.SelectedValue.SelectedValueType
import lecho.lib.hellocharts.model.SubcolumnValue
import lecho.lib.hellocharts.model.Viewport
import lecho.lib.hellocharts.provider.ColumnChartDataProvider
import lecho.lib.hellocharts.util.ChartUtils.dp2px
import lecho.lib.hellocharts.view.Chart
import kotlin.math.abs

/**
 * Magic renderer for ColumnChart.
 */
open class ColumnChartRenderer(
    context: Context?,
    chart: Chart?,
    private val dataProvider: ColumnChartDataProvider
) : AbstractChartRenderer(
    context!!, chart!!
) {
    /**
     * Additional width for highlighted column, used to give touch feedback.
     */
    @JvmField
    val touchAdditionalWidth: Int = dp2px(density, DEFAULT_COLUMN_TOUCH_ADDITIONAL_WIDTH_DP)

    /**
     * Spacing between sub-columns.
     */
    @JvmField
    val subcolumnSpacing: Int = dp2px(density, DEFAULT_SUBCOLUMN_SPACING_DP)

    /**
     * Paint used to draw every column.
     */
    @JvmField
    val columnPaint = Paint()

    /**
     * Holds coordinates for currently processed column/sub-column.
     */
    @JvmField
    val drawRect = RectF()

    /**
     * Coordinated of user tauch.
     */
    @JvmField
    val touchedPoint = PointF()

    @JvmField
    var fillRatio = 0f

    @JvmField
    var baseValue = 0f

    @JvmField
    val tempMaximumViewport = Viewport()

    init {
        columnPaint.isAntiAlias = true
        columnPaint.style = Paint.Style.FILL
        columnPaint.strokeCap = Cap.ROUND
    }

    override fun onChartSizeChanged() {}
    override fun onChartDataChanged() {
        super.onChartDataChanged()
        val data = dataProvider.columnChartData
        fillRatio = data.fillRatio
        baseValue = data.baseValue
        onChartViewportChanged()
    }

    override fun onChartViewportChanged() {
        if (isViewportCalculationEnabled) {
            calculateMaxViewport()
            computator.setMaximumViewport(tempMaximumViewport)
            computator.setCurrentViewport(computator.maxViewport)
        }
    }

    override fun draw(canvas: Canvas?) {
        val data = dataProvider.columnChartData
        if (data.isStacked) {
            drawColumnForStacked(canvas)
            if (isTouched()) {
                highlightColumnForStacked(canvas)
            }
        } else {
            drawColumnsForSubcolumns(canvas)
            if (isTouched()) {
                highlightColumnsForSubcolumns(canvas)
            }
        }
    }

    override fun drawUnClipped(canvas: Canvas?) {
        // Do nothing, for this kind of chart there is nothing to draw beyond clipped area
    }

    override fun checkTouch(touchX: Float, touchY: Float): Boolean {
        selectedValue.clear()
        val data = dataProvider.columnChartData
        if (data.isStacked) {
            checkTouchForStacked(touchX, touchY)
        } else {
            checkTouchForSubcolumns(touchX, touchY)
        }
        return isTouched()
    }

    private fun calculateMaxViewport() {
        val data = dataProvider.columnChartData
        // Column chart always has X values from 0 to numColumns-1, to add some margin on the left and right I added
        // extra 0.5 to the each side, that margins will be negative scaled according to number of columns, so for more
        // columns there will be less margin.
        tempMaximumViewport[-0.5f, baseValue, data.columns.size - 0.5f] = baseValue
        if (data.isStacked) {
            calculateMaxViewportForStacked(data)
        } else {
            calculateMaxViewportForSubcolumns(data)
        }
    }

    private fun calculateMaxViewportForSubcolumns(data: ColumnChartData) {
        for (column in data.columns) {
            for (columnValue in column.values) {
                if (columnValue.value >= baseValue && columnValue.value > tempMaximumViewport.top) {
                    tempMaximumViewport.top = columnValue.value
                }
                if (columnValue.value < baseValue && columnValue.value < tempMaximumViewport.bottom) {
                    tempMaximumViewport.bottom = columnValue.value
                }
            }
        }
    }

    private fun calculateMaxViewportForStacked(data: ColumnChartData) {
        for (column in data.columns) {
            var sumPositive = baseValue
            var sumNegative = baseValue
            for (columnValue in column.values) {
                if (columnValue.value >= baseValue) {
                    sumPositive += columnValue.value
                } else {
                    sumNegative += columnValue.value
                }
            }
            if (sumPositive > tempMaximumViewport.top) {
                tempMaximumViewport.top = sumPositive
            }
            if (sumNegative < tempMaximumViewport.bottom) {
                tempMaximumViewport.bottom = sumNegative
            }
        }
    }

    private fun drawColumnsForSubcolumns(canvas: Canvas?) {
        val data = dataProvider.columnChartData
        val columnWidth = calculateColumnWidth()
        for ((columnIndex, column) in data.columns.withIndex()) {
            processColumnForSubcolumns(
                canvas,
                column,
                columnWidth,
                columnIndex,
                MODE_DRAW,
                data.isRoundedCorner
            )
        }
    }

    private fun highlightColumnsForSubcolumns(canvas: Canvas?) {
        val data = dataProvider.columnChartData
        val columnWidth = calculateColumnWidth()
        val column = data.columns[selectedValue.firstIndex]
        processColumnForSubcolumns(
            canvas,
            column,
            columnWidth,
            selectedValue.firstIndex,
            MODE_HIGHLIGHT,
            data.isRoundedCorner,
            data.enableTouchAdditionalWidth
        )
    }

    private fun checkTouchForSubcolumns(touchX: Float, touchY: Float) {
        // Using member variable to hold touch point to avoid too much parameters in methods.
        touchedPoint.x = touchX
        touchedPoint.y = touchY
        val data = dataProvider.columnChartData
        val columnWidth = calculateColumnWidth()
        for ((columnIndex, column) in data.columns.withIndex()) {
            // canvas is not needed for checking touch
            processColumnForSubcolumns(
                null,
                column,
                columnWidth,
                columnIndex,
                MODE_CHECK_TOUCH,
                data.isRoundedCorner,
                data.enableTouchAdditionalWidth
            )
        }
    }

    private fun processColumnForSubcolumns(
        canvas: Canvas?,
        column: Column,
        columnWidth: Float,
        columnIndex: Int,
        mode: Int,
        isRoundedCorners: Boolean,
        enableTouchAdditionalWidth: Boolean = true
    ) {
        // For n subColumns there will be n-1 spacing and there will be one
        // subColumn for every columnValue
        var subcolumnWidth = ((columnWidth - subcolumnSpacing * (column.values.size - 1))
            / column.values.size)
        if (subcolumnWidth < 1) {
            subcolumnWidth = 1f
        }
        // Columns are indexes from 0 to n, column index is also column X value
        val rawX = computator.computeRawX(columnIndex.toFloat())
        val halfColumnWidth = columnWidth / 2
        val baseRawY = computator.computeRawY(baseValue)
        // First subColumn will starts at the left edge of current column,
        // rawValueX is horizontal center of that column
        var subcolumnRawX = rawX - halfColumnWidth
        var valueIndex = 0
        for (columnValue in column.values) {
            columnPaint.color = columnValue.color
            if (subcolumnRawX > rawX + halfColumnWidth) {
                break
            }
            val rawY = computator.computeRawY(columnValue.value)
            calculateRectToDraw(
                columnValue,
                subcolumnRawX,
                subcolumnRawX + subcolumnWidth,
                baseRawY,
                rawY
            )
            when (mode) {
                MODE_DRAW -> drawSubcolumn(canvas, column, columnValue, false, isRoundedCorners)
                MODE_HIGHLIGHT -> highlightSubcolumn(
                    canvas,
                    column,
                    columnValue,
                    valueIndex,
                    false,
                    isRoundedCorners,
                    enableTouchAdditionalWidth
                )

                MODE_CHECK_TOUCH -> checkRectToDraw(columnIndex, valueIndex)
                else -> throw IllegalStateException("Cannot process column in mode: $mode")
            }
            subcolumnRawX += subcolumnWidth + subcolumnSpacing
            ++valueIndex
        }
    }

    private fun drawColumnForStacked(canvas: Canvas?) {
        val data = dataProvider.columnChartData
        val columnWidth = calculateColumnWidth()
        // Columns are indexes from 0 to n, column index is also column X value
        for ((columnIndex, column) in data.columns.withIndex()) {
            processColumnForStacked(
                canvas,
                column,
                columnWidth,
                columnIndex,
                MODE_DRAW,
                data.isRoundedCorner,
                data.enableTouchAdditionalWidth
            )
        }
    }

    private fun highlightColumnForStacked(canvas: Canvas?) {
        val data = dataProvider.columnChartData
        val columnWidth = calculateColumnWidth()
        // Columns are indexes from 0 to n, column index is also column X value
        val column = data.columns[selectedValue.firstIndex]
        processColumnForStacked(
            canvas,
            column,
            columnWidth,
            selectedValue.firstIndex,
            MODE_HIGHLIGHT,
            data.isRoundedCorner,
            data.enableTouchAdditionalWidth
        )
    }

    private fun checkTouchForStacked(touchX: Float, touchY: Float) {
        touchedPoint.x = touchX
        touchedPoint.y = touchY
        val data = dataProvider.columnChartData
        val columnWidth = calculateColumnWidth()
        for ((columnIndex, column) in data.columns.withIndex()) {
            // canvas is not needed for checking touch
            processColumnForStacked(
                null,
                column,
                columnWidth,
                columnIndex,
                MODE_CHECK_TOUCH,
                data.isRoundedCorner,
                data.enableTouchAdditionalWidth
            )
        }
    }

    private fun processColumnForStacked(
        canvas: Canvas?,
        column: Column,
        columnWidth: Float,
        columnIndex: Int,
        mode: Int,
        isRoundedCorners: Boolean,
        enableTouchAdditionalWidth: Boolean = true
    ) {
        val rawX = computator.computeRawX(columnIndex.toFloat())
        val halfColumnWidth = columnWidth / 2
        var mostPositiveValue = baseValue
        var mostNegativeValue = baseValue
        var subcolumnBaseValue: Float
        for ((valueIndex, columnValue) in column.values.withIndex()) {
            columnPaint.color = columnValue.color
            if (columnValue.value >= baseValue) {
                // Using values instead of raw pixels make code easier to
                // understand(for me)
                subcolumnBaseValue = mostPositiveValue
                mostPositiveValue += columnValue.value
            } else {
                subcolumnBaseValue = mostNegativeValue
                mostNegativeValue += columnValue.value
            }
            val rawBaseY = computator.computeRawY(subcolumnBaseValue)
            val rawY = computator.computeRawY(subcolumnBaseValue + columnValue.value)
            calculateRectToDraw(
                columnValue,
                rawX - halfColumnWidth,
                rawX + halfColumnWidth,
                rawBaseY,
                rawY
            )
            when (mode) {
                MODE_DRAW -> drawSubcolumn(canvas, column, columnValue, true, isRoundedCorners)
                MODE_HIGHLIGHT -> highlightSubcolumn(
                    canvas,
                    column,
                    columnValue,
                    valueIndex,
                    true,
                    isRoundedCorners,
                    enableTouchAdditionalWidth
                )

                MODE_CHECK_TOUCH -> checkRectToDraw(columnIndex, valueIndex)
                else -> throw IllegalStateException("Cannot process column in mode: $mode")
            }
        }
    }

    private fun drawSubcolumn(
        canvas: Canvas?,
        column: Column,
        columnValue: SubcolumnValue,
        isStacked: Boolean,
        isRoundedCorners: Boolean
    ) {
        if (isRoundedCorners) {
            canvas!!.drawRoundRect(drawRect, 100f, 100f, columnPaint)
        } else {
            canvas!!.drawRect(drawRect, columnPaint)
        }
        if (column.hasLabels()) {
            drawLabel(canvas, column, columnValue, isStacked, labelOffset.toFloat())
        }
    }

    private fun highlightSubcolumn(
        canvas: Canvas?,
        column: Column,
        columnValue: SubcolumnValue,
        valueIndex: Int,
        isStacked: Boolean,
        isRoundedCorners: Boolean,
        enableTouchAdditionalWidth: Boolean = true
    ) {
        if (selectedValue.secondIndex == valueIndex) {
            columnPaint.color = columnValue.darkenColor
            val left = drawRect.left.takeIf {
                !enableTouchAdditionalWidth
            } ?: (drawRect.left - touchAdditionalWidth)

            val right = drawRect.right.takeIf {
                !enableTouchAdditionalWidth
            } ?: (drawRect.right + touchAdditionalWidth)
            if (isRoundedCorners) {
                canvas!!.drawRoundRect(
                    /* left = */ left,
                    /* top = */ drawRect.top,
                    /* right = */ right,
                    /* bottom = */ drawRect.bottom,
                    /* rx = */ 100f,
                    /* ry = */ 100f,
                    /* paint = */ columnPaint
                )
            } else {
                canvas!!.drawRect(
                    /* left = */ left,
                    /* top = */ drawRect.top,
                    /* right = */ right,
                    /* bottom = */ drawRect.bottom,
                    /* paint = */ columnPaint
                )
            }
            if (column.hasLabels() || column.hasLabelsOnlyForSelected()) {
                drawLabel(canvas, column, columnValue, isStacked, labelOffset.toFloat())
            }
        }
    }

    private fun checkRectToDraw(columnIndex: Int, valueIndex: Int) {
        if (drawRect.contains(touchedPoint.x, touchedPoint.y)) {
            selectedValue[columnIndex, valueIndex] = SelectedValueType.COLUMN
        }
    }

    private fun calculateColumnWidth(): Float {
        // columnWidth should be at least 2 px
        var columnWidth = fillRatio * computator.contentRectMinusAllMargins.width() / computator
            .visibleViewport.width()
        if (columnWidth < 2) {
            columnWidth = 2f
        }
        return columnWidth
    }

    private fun calculateRectToDraw(
        columnValue: SubcolumnValue,
        left: Float,
        right: Float,
        rawBaseY: Float,
        rawY: Float
    ) {
        // Calculate rect that will be drawn as column, subColumn or label background.
        drawRect.left = left
        drawRect.right = right
        if (columnValue.value >= baseValue) {
            drawRect.top = rawY
            drawRect.bottom = rawBaseY - subcolumnSpacing
        } else {
            drawRect.bottom = rawY
            drawRect.top = rawBaseY + subcolumnSpacing
        }
    }

    private fun drawLabel(
        canvas: Canvas?,
        column: Column,
        columnValue: SubcolumnValue,
        isStacked: Boolean,
        offset: Float
    ) {
        val numChars = column.formatter.formatChartValue(labelBuffer, columnValue)
        if (numChars == 0) {
            // No need to draw empty label
            return
        }
        val labelWidth = labelPaint.measureText(labelBuffer, labelBuffer.size - numChars, numChars)
        val labelHeight = abs(fontMetrics.ascent)
        val left = drawRect.centerX() - labelWidth / 2 - labelMargin
        val right = drawRect.centerX() + labelWidth / 2 + labelMargin
        var top: Float
        var bottom: Float
        if (isStacked && labelHeight < drawRect.height() - 2 * labelMargin) {
            // For stacked columns draw label only if label height is less than subColumn height - (2 * labelMargin).
            if (columnValue.value >= baseValue) {
                top = drawRect.top
                bottom = drawRect.top + labelHeight + labelMargin * 2
            } else {
                top = drawRect.bottom - labelHeight - labelMargin * 2
                bottom = drawRect.bottom
            }
        } else if (!isStacked) {
            // For not stacked draw label at the top for positive and at the bottom for negative values
            if (columnValue.value >= baseValue) {
                top = drawRect.top - offset - labelHeight - labelMargin * 2
                if (top < computator.contentRectMinusAllMargins.top) {
                    top = drawRect.top + offset
                    bottom = drawRect.top + offset + labelHeight + labelMargin * 2
                } else {
                    bottom = drawRect.top - offset
                }
            } else {
                bottom = drawRect.bottom + offset + labelHeight + labelMargin * 2
                if (bottom > computator.contentRectMinusAllMargins.bottom) {
                    top = drawRect.bottom - offset - labelHeight - labelMargin * 2
                    bottom = drawRect.bottom - offset
                } else {
                    top = drawRect.bottom + offset
                }
            }
        } else {
            // Draw nothing.
            return
        }
        labelBackgroundRect[left, top, right] = bottom
        drawLabelTextAndBackground(
            canvas!!, labelBuffer, labelBuffer.size - numChars, numChars,
            columnValue.darkenColor
        )
    }

    companion object {
        const val DEFAULT_SUBCOLUMN_SPACING_DP = 1
        const val DEFAULT_COLUMN_TOUCH_ADDITIONAL_WIDTH_DP = 4
        private const val MODE_DRAW = 0
        private const val MODE_CHECK_TOUCH = 1
        private const val MODE_HIGHLIGHT = 2
    }
}