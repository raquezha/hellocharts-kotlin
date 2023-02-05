package lecho.lib.hellocharts.renderer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Paint.Cap
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.Shader
import lecho.lib.hellocharts.model.Line
import lecho.lib.hellocharts.model.PointValue
import lecho.lib.hellocharts.model.SelectedValue.SelectedValueType
import lecho.lib.hellocharts.model.ValueShape
import lecho.lib.hellocharts.model.Viewport
import lecho.lib.hellocharts.provider.LineChartDataProvider
import lecho.lib.hellocharts.util.ChartUtils.dp2px
import lecho.lib.hellocharts.view.Chart
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

/**
 * Renderer for line chart. Can draw lines, cubic lines, filled area chart and scattered chart.
 */
open class LineChartRenderer(
    context: Context?,
    chart: Chart?,
    private val dataProvider: LineChartDataProvider
) : AbstractChartRenderer(
    context!!, chart!!
) {
    private val checkPrecision: Int
    private var baseValue = 0f
    private val touchToleranceMargin: Int = dp2px(density, DEFAULT_TOUCH_TOLERANCE_MARGIN_DP)
    private val path = Path()
    private val linePaint = Paint()
    private val pointPaint = Paint()
    private var softwareBitmap: Bitmap? = null
    private val softwareCanvas = Canvas()
    private val tempMaximumViewport = Viewport()

    init {
        linePaint.isAntiAlias = true
        linePaint.style = Paint.Style.STROKE
        linePaint.strokeCap = Cap.ROUND
        linePaint.strokeWidth =
            dp2px(density, DEFAULT_LINE_STROKE_WIDTH_DP)
                .toFloat()
        pointPaint.isAntiAlias = true
        pointPaint.style = Paint.Style.FILL
        checkPrecision = dp2px(density, 2)
    }

    override fun onChartSizeChanged() {
        val internalMargin = calculateContentRectInternalMargin()
        computator.insetContentRectByInternalMargins(
            internalMargin, internalMargin,
            internalMargin, internalMargin
        )
        if (computator.chartWidth > 0 && computator.chartHeight > 0) {
            softwareBitmap = Bitmap.createBitmap(
                computator.chartWidth, computator.chartHeight,
                Bitmap.Config.ARGB_8888
            )
            softwareCanvas.setBitmap(softwareBitmap)
        }
    }

    override fun onChartDataChanged() {
        super.onChartDataChanged()
        val internalMargin = calculateContentRectInternalMargin()
        computator.insetContentRectByInternalMargins(
            internalMargin, internalMargin,
            internalMargin, internalMargin
        )
        baseValue = dataProvider.lineChartData.baseValue
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
        val data = dataProvider.lineChartData
        val drawCanvas: Canvas?

        // softwareBitmap can be null if chart is rendered in layout editor. In that case use default canvas and not
        // softwareCanvas.
        if (null != softwareBitmap) {
            drawCanvas = softwareCanvas
            drawCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
        } else {
            drawCanvas = canvas
        }
        for (line in data.getLines()) {
            if (line.hasLines()) {
                if (line.isCubic) {
                    drawSmoothPath(drawCanvas, line)
                } else if (line.isSquare) {
                    drawSquarePath(drawCanvas, line)
                } else {
                    drawPath(drawCanvas, line)
                }
            }
        }
        if (null != softwareBitmap) {
            canvas!!.drawBitmap(softwareBitmap!!, 0f, 0f, null)
        }
    }

    override fun drawUnClipped(canvas: Canvas?) {
        val data = dataProvider.lineChartData
        for ((lineIndex, line) in data.getLines().withIndex()) {
            if (checkIfShouldDrawPoints(line)) {
                drawPoints(canvas, line, lineIndex, MODE_DRAW)
            }
        }
        if (isTouched()) {
            // Redraw touched point to bring it to the front
            highlightPoints(canvas)
        }
    }

    private fun checkIfShouldDrawPoints(line: Line): Boolean {
        return line.hasPoints() || line.getValues().size == 1
    }

    override fun checkTouch(touchX: Float, touchY: Float): Boolean {
        selectedValue.clear()
        val data = dataProvider.lineChartData
        for ((lineIndex, line) in data.getLines().withIndex()) {
            if (checkIfShouldDrawPoints(line)) {
                val pointRadius = dp2px(density, line.pointRadius)
                for ((valueIndex, pointValue) in line.getValues().withIndex()) {
                    val rawValueX = computator.computeRawX(pointValue.x)
                    val rawValueY = computator.computeRawY(pointValue.y)
                    if (isInArea(
                            rawValueX,
                            rawValueY,
                            touchX,
                            touchY,
                            (pointRadius + touchToleranceMargin).toFloat()
                        )
                    ) {
                        selectedValue[lineIndex, valueIndex] = SelectedValueType.LINE
                    }
                }
            }
        }
        return isTouched()
    }

    private fun calculateMaxViewport() {
        tempMaximumViewport[Float.MAX_VALUE, Float.MIN_VALUE, Float.MIN_VALUE] = Float.MAX_VALUE
        val data = dataProvider.lineChartData
        for (line in data.getLines()) {
            // Calculate max and min for viewport.
            for (pointValue in line.getValues()) {
                if (pointValue.x < tempMaximumViewport.left) {
                    tempMaximumViewport.left = pointValue.x
                }
                if (pointValue.x > tempMaximumViewport.right) {
                    tempMaximumViewport.right = pointValue.x
                }
                if (pointValue.y < tempMaximumViewport.bottom) {
                    tempMaximumViewport.bottom = pointValue.y
                }
                if (pointValue.y > tempMaximumViewport.top) {
                    tempMaximumViewport.top = pointValue.y
                }
            }
        }
    }

    private fun calculateContentRectInternalMargin(): Int {
        var contentAreaMargin = 0
        val data = dataProvider.lineChartData
        for (line in data.getLines()) {
            if (checkIfShouldDrawPoints(line)) {
                val margin = line.pointRadius + DEFAULT_TOUCH_TOLERANCE_MARGIN_DP
                if (margin > contentAreaMargin) {
                    contentAreaMargin = margin
                }
            }
        }
        return dp2px(density, contentAreaMargin)
    }

    /**
     * Draws lines, uses path for drawing filled area on software canvas. Line is drawn with canvas.drawLines() method.
     */
    private fun drawPath(canvas: Canvas?, line: Line) {
        prepareLinePaint(line)
        for ((valueIndex, pointValue) in line.getValues().withIndex()) {
            val rawX = computator.computeRawX(pointValue.x)
            val rawY = computator.computeRawY(pointValue.y)
            if (valueIndex == 0) {
                path.moveTo(rawX, rawY)
            } else {
                path.lineTo(rawX, rawY)
            }
        }
        canvas!!.drawPath(path, linePaint)
        if (line.isFilled) {
            drawArea(canvas, line)
        }
        path.reset()
    }

    private fun drawSquarePath(canvas: Canvas?, line: Line) {
        prepareLinePaint(line)
        var previousRawY = 0f
        for ((valueIndex, pointValue) in line.getValues().withIndex()) {
            val rawX = computator.computeRawX(pointValue.x)
            val rawY = computator.computeRawY(pointValue.y)
            if (valueIndex == 0) {
                path.moveTo(rawX, rawY)
            } else {
                path.lineTo(rawX, previousRawY)
                path.lineTo(rawX, rawY)
            }
            previousRawY = rawY
        }
        canvas!!.drawPath(path, linePaint)
        if (line.isFilled) {
            drawArea(canvas, line)
        }
        path.reset()
    }

    private fun drawSmoothPath(canvas: Canvas?, line: Line) {
        prepareLinePaint(line)
        val lineSize = line.getValues().size
        var prePreviousPointX = Float.NaN
        var prePreviousPointY = Float.NaN
        var previousPointX = Float.NaN
        var previousPointY = Float.NaN
        var currentPointX = Float.NaN
        var currentPointY = Float.NaN
        var nextPointX: Float
        var nextPointY: Float
        for (valueIndex in 0 until lineSize) {
            if (java.lang.Float.isNaN(currentPointX)) {
                val linePoint = line.getValues()[valueIndex]
                currentPointX = computator.computeRawX(linePoint.x)
                currentPointY = computator.computeRawY(linePoint.y)
            }
            if (java.lang.Float.isNaN(previousPointX)) {
                if (valueIndex > 0) {
                    val linePoint = line.getValues()[valueIndex - 1]
                    previousPointX = computator.computeRawX(linePoint.x)
                    previousPointY = computator.computeRawY(linePoint.y)
                } else {
                    previousPointX = currentPointX
                    previousPointY = currentPointY
                }
            }
            if (java.lang.Float.isNaN(prePreviousPointX)) {
                if (valueIndex > 1) {
                    val linePoint = line.getValues()[valueIndex - 2]
                    prePreviousPointX = computator.computeRawX(linePoint.x)
                    prePreviousPointY = computator.computeRawY(linePoint.y)
                } else {
                    prePreviousPointX = previousPointX
                    prePreviousPointY = previousPointY
                }
            }

            // nextPoint is always new one or it is equal currentPoint.
            if (valueIndex < lineSize - 1) {
                val linePoint = line.getValues()[valueIndex + 1]
                nextPointX = computator.computeRawX(linePoint.x)
                nextPointY = computator.computeRawY(linePoint.y)
            } else {
                nextPointX = currentPointX
                nextPointY = currentPointY
            }
            if (valueIndex == 0) {
                // Move to start point.
                path.moveTo(currentPointX, currentPointY)
            } else {
                // Calculate control points.
                val firstDiffX = currentPointX - prePreviousPointX
                val firstDiffY = currentPointY - prePreviousPointY
                val secondDiffX = nextPointX - previousPointX
                val secondDiffY = nextPointY - previousPointY
                val firstControlPointX = previousPointX + LINE_SMOOTHNESS * firstDiffX
                val firstControlPointY = previousPointY + LINE_SMOOTHNESS * firstDiffY
                val secondControlPointX = currentPointX - LINE_SMOOTHNESS * secondDiffX
                val secondControlPointY = currentPointY - LINE_SMOOTHNESS * secondDiffY
                path.cubicTo(
                    firstControlPointX,
                    firstControlPointY,
                    secondControlPointX,
                    secondControlPointY,
                    currentPointX,
                    currentPointY
                )
            }

            // Shift values by one back to prevent recalculation of values that have
            // been already calculated.
            prePreviousPointX = previousPointX
            prePreviousPointY = previousPointY
            previousPointX = currentPointX
            previousPointY = currentPointY
            currentPointX = nextPointX
            currentPointY = nextPointY
        }
        canvas!!.drawPath(path, linePaint)
        if (line.isFilled) {
            drawArea(canvas, line)
        }
        path.reset()
    }

    private fun prepareLinePaint(line: Line) {
        linePaint.strokeWidth = dp2px(density, line.strokeWidth).toFloat()
        linePaint.color = line.color
        linePaint.pathEffect = line.pathEffect
        linePaint.shader = null
    }

    // TODO Drawing points can be done in the same loop as drawing lines but it
    // may cause problems in the future with
    // implementing point styles.
    private fun drawPoints(canvas: Canvas?, line: Line, lineIndex: Int, mode: Int) {
        pointPaint.color = line.getPointColor()
        for ((valueIndex, pointValue) in line.getValues().withIndex()) {
            val pointRadius = dp2px(density, line.pointRadius)
            val rawX = computator.computeRawX(pointValue.x)
            val rawY = computator.computeRawY(pointValue.y)
            if (computator.isWithinContentRect(rawX, rawY, checkPrecision.toFloat())) {
                // Draw points only if they are within contentRectMinusAllMargins, using contentRectMinusAllMargins
                // instead of viewport to avoid some
                // float rounding problems.
                if (MODE_DRAW == mode) {
                    drawPoint(canvas, line, pointValue, rawX, rawY, pointRadius.toFloat())
                    if (line.hasLabels()) {
                        drawLabel(
                            canvas,
                            line,
                            pointValue,
                            rawX,
                            rawY,
                            (pointRadius + labelOffset).toFloat()
                        )
                    }
                } else if (MODE_HIGHLIGHT == mode) {
                    highlightPoint(canvas, line, pointValue, rawX, rawY, lineIndex, valueIndex)
                } else {
                    throw IllegalStateException("Cannot process points in mode: $mode")
                }
            }
        }
    }

    private fun drawPoint(
        canvas: Canvas?, line: Line, pointValue: PointValue, rawX: Float, rawY: Float,
        pointRadius: Float
    ) {
        if (ValueShape.SQUARE == line.shape) {
            canvas!!.drawRect(
                rawX - pointRadius, rawY - pointRadius, rawX + pointRadius, rawY + pointRadius,
                pointPaint
            )
        } else if (ValueShape.CIRCLE == line.shape) {
            canvas!!.drawCircle(rawX, rawY, pointRadius, pointPaint)
        } else if (ValueShape.DIAMOND == line.shape) {
            canvas!!.save()
            canvas.rotate(45f, rawX, rawY)
            canvas.drawRect(
                rawX - pointRadius, rawY - pointRadius, rawX + pointRadius, rawY + pointRadius,
                pointPaint
            )
            canvas.restore()
        } else {
            throw IllegalArgumentException("Invalid point shape: " + line.shape)
        }
    }

    private fun highlightPoints(canvas: Canvas?) {
        val lineIndex = selectedValue.firstIndex
        val line = dataProvider.lineChartData.getLines()[lineIndex]
        drawPoints(canvas, line, lineIndex, MODE_HIGHLIGHT)
    }

    private fun highlightPoint(
        canvas: Canvas?,
        line: Line,
        pointValue: PointValue,
        rawX: Float,
        rawY: Float,
        lineIndex: Int,
        valueIndex: Int
    ) {
        if (selectedValue.firstIndex == lineIndex && selectedValue.secondIndex == valueIndex) {
            val pointRadius = dp2px(density, line.pointRadius)
            pointPaint.color = line.darkenColor
            drawPoint(
                canvas,
                line,
                pointValue,
                rawX,
                rawY,
                (pointRadius + touchToleranceMargin).toFloat()
            )
            if (line.hasLabels() || line.hasLabelsOnlyForSelected()) {
                drawLabel(
                    canvas,
                    line,
                    pointValue,
                    rawX,
                    rawY,
                    (pointRadius + labelOffset).toFloat()
                )
            }
        }
    }

    private fun drawLabel(
        canvas: Canvas?,
        line: Line,
        pointValue: PointValue,
        rawX: Float,
        rawY: Float,
        offset: Float
    ) {
        val contentRect = computator.contentRectMinusAllMargins
        val numChars = line.formatter.formatChartValue(labelBuffer, pointValue)
        if (numChars == 0) {
            // No need to draw empty label
            return
        }
        val labelWidth = labelPaint.measureText(labelBuffer, labelBuffer.size - numChars, numChars)
        val labelHeight = abs(fontMetrics.ascent)
        var left = rawX - labelWidth / 2 - labelMargin
        var right = rawX + labelWidth / 2 + labelMargin
        var top: Float
        var bottom: Float
        if (pointValue.y >= baseValue) {
            top = rawY - offset - labelHeight - labelMargin * 2
            bottom = rawY - offset
        } else {
            top = rawY + offset
            bottom = rawY + offset + labelHeight + labelMargin * 2
        }
        if (top < contentRect.top) {
            top = rawY + offset
            bottom = rawY + offset + labelHeight + labelMargin * 2
        }
        if (bottom > contentRect.bottom) {
            top = rawY - offset - labelHeight - labelMargin * 2
            bottom = rawY - offset
        }
        if (left < contentRect.left) {
            left = rawX
            right = rawX + labelWidth + labelMargin * 2
        }
        if (right > contentRect.right) {
            left = rawX - labelWidth - labelMargin * 2
            right = rawX
        }
        labelBackgroundRect[left, top, right] = bottom
        drawLabelTextAndBackground(
            canvas!!,
            labelBuffer,
            labelBuffer.size - numChars,
            numChars,
            line.darkenColor
        )
    }

    private fun drawArea(canvas: Canvas?, line: Line) {
        val lineSize = line.getValues().size
        if (lineSize < 2) {
            //No point to draw area for one point or empty line.
            return
        }
        val contentRect = computator.contentRectMinusAllMargins
        val baseRawValue = min(
            contentRect.bottom.toFloat(), max(
                computator.computeRawY(baseValue),
                contentRect.top.toFloat()
            )
        )
        //That checks works only if the last point is the right most one.
        val left = max(computator.computeRawX(line.getValues()[0].x), contentRect.left.toFloat())
        val right = min(
            computator.computeRawX(line.getValues()[lineSize - 1].x),
            contentRect.right.toFloat()
        )
        path.lineTo(right, baseRawValue)
        path.lineTo(left, baseRawValue)
        path.close()
        linePaint.style = Paint.Style.FILL
        linePaint.alpha = line.areaTransparency
        linePaint.shader = if (line.gradientToTransparent) LinearGradient(
            0f,
            0f,
            0f,
            canvas!!.height.toFloat(),
            line.color,
            line.color and 0x00ffffff,
            Shader.TileMode.MIRROR
        ) else null
        canvas!!.drawPath(path, linePaint)
        linePaint.style = Paint.Style.STROKE
    }

    private fun isInArea(x: Float, y: Float, touchX: Float, touchY: Float, radius: Float): Boolean {
        val diffX = touchX - x
        val diffY = touchY - y
        return diffX.toDouble().pow(2.0) + diffY.toDouble().pow(2.0) <= 2 * radius.toDouble()
            .pow(2.0)
    }

    companion object {
        private const val LINE_SMOOTHNESS = 0.16f
        private const val DEFAULT_LINE_STROKE_WIDTH_DP = 3
        private const val DEFAULT_TOUCH_TOLERANCE_MARGIN_DP = 4
        private const val MODE_DRAW = 0
        private const val MODE_HIGHLIGHT = 1
    }
}