package lecho.lib.hellocharts.renderer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.Align
import android.graphics.Paint.FontMetricsInt
import android.graphics.PointF
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.text.TextUtils
import lecho.lib.hellocharts.formatter.PieChartValueFormatter
import lecho.lib.hellocharts.model.PieChartData
import lecho.lib.hellocharts.model.SelectedValue
import lecho.lib.hellocharts.model.SelectedValue.SelectedValueType
import lecho.lib.hellocharts.model.SliceValue
import lecho.lib.hellocharts.model.Viewport
import lecho.lib.hellocharts.provider.PieChartDataProvider
import lecho.lib.hellocharts.util.ChartUtils.dp2px
import lecho.lib.hellocharts.util.ChartUtils.sp2px
import lecho.lib.hellocharts.view.Chart
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * Default renderer for PieChart.
 * PieChart doesn't use viewport concept so it a little different than others chart types.
 */
class PieChartRenderer(
    context: Context?,
    chart: Chart?,
    private val dataProvider: PieChartDataProvider
) : AbstractChartRenderer(context!!, chart!!) {
    private var rotation = DEFAULT_START_ROTATION
    private val slicePaint = Paint()
    private var maxSum = 0f
    var circleOval = RectF()
    private val drawCircleOval = RectF()
    private val sliceVector = PointF()
    private val touchAdditional: Int = dp2px(density, DEFAULT_TOUCH_ADDITIONAL_DP)
    private var circleFillRatio = 1.0f

    // Center circle related attributes
    private var hasCenterCircle = false
    private var centerCircleScale = 0f
    private val centerCirclePaint = Paint()

    // Text1
    private val centerCircleText1Paint = Paint()
    private val centerCircleText1FontMetrics = FontMetricsInt()

    // Text2
    private val centerCircleText2Paint = Paint()
    private val centerCircleText2FontMetrics = FontMetricsInt()

    // Separation lines
    private val separationLinesPaint = Paint()
    private var hasLabelsOutside = false
    private var hasLabels = false
    private var hasLabelsOnlyForSelected = false
    private var valueFormatter: PieChartValueFormatter? = null
    private val tempMaximumViewport = Viewport()
    private var softwareBitmap: Bitmap? = null
    private val softwareCanvas = Canvas()

    init {
        slicePaint.isAntiAlias = true
        slicePaint.style = Paint.Style.FILL
        centerCirclePaint.isAntiAlias = true
        centerCirclePaint.style = Paint.Style.FILL
        centerCirclePaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC)
        centerCircleText1Paint.isAntiAlias = true
        centerCircleText1Paint.textAlign = Align.CENTER
        centerCircleText2Paint.isAntiAlias = true
        centerCircleText2Paint.textAlign = Align.CENTER
        separationLinesPaint.isAntiAlias = true
        separationLinesPaint.style = Paint.Style.STROKE
        separationLinesPaint.strokeCap = Paint.Cap.ROUND
        separationLinesPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        separationLinesPaint.color = Color.TRANSPARENT
    }

    override fun onChartSizeChanged() {
        calculateCircleOval()
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
        val data = dataProvider.pieChartData
        hasLabelsOutside = data.hasLabelsOutside()
        hasLabels = data.hasLabels()
        hasLabelsOnlyForSelected = data.hasLabelsOnlyForSelected()
        valueFormatter = data.formatter
        hasCenterCircle = data.hasCenterCircle()
        centerCircleScale = data.centerCircleScale
        centerCirclePaint.color = data.centerCircleColor
        if (null != data.centerText1Typeface) {
            centerCircleText1Paint.typeface = data.centerText1Typeface
        }
        centerCircleText1Paint.textSize =
            sp2px(scaledDensity, data.centerText1FontSize).toFloat()
        centerCircleText1Paint.color = data.centerText1Color
        centerCircleText1Paint.getFontMetricsInt(centerCircleText1FontMetrics)
        if (null != data.centerText2Typeface) {
            centerCircleText2Paint.typeface = data.centerText2Typeface
        }
        centerCircleText2Paint.textSize =
            sp2px(scaledDensity, data.centerText2FontSize).toFloat()
        centerCircleText2Paint.color = data.centerText2Color
        centerCircleText2Paint.getFontMetricsInt(centerCircleText2FontMetrics)
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
        // softwareBitmap can be null if chart is rendered in layout editor. In that case use default canvas and not
        // softwareCanvas.
        val drawCanvas: Canvas?
        if (null != softwareBitmap) {
            drawCanvas = softwareCanvas
            drawCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
        } else {
            drawCanvas = canvas
        }
        drawSlices(drawCanvas)
        drawSeparationLines(drawCanvas)
        if (hasCenterCircle) {
            drawCenterCircle(drawCanvas)
        }
        drawLabels(drawCanvas)
        if (null != softwareBitmap) {
            canvas!!.drawBitmap(softwareBitmap!!, 0f, 0f, null)
        }
    }

    override fun drawUnClipped(canvas: Canvas?) {}
    override fun checkTouch(touchX: Float, touchY: Float): Boolean {
        selectedValue.clear()
        val data = dataProvider.pieChartData
        val centerX = circleOval.centerX()
        val centerY = circleOval.centerY()
        val circleRadius = circleOval.width() / 2f
        sliceVector[touchX - centerX] = touchY - centerY
        // Check if touch is on circle area, if not return false;
        if (sliceVector.length() > circleRadius + touchAdditional) {
            return false
        }
        // Check if touch is not in center circle, if yes return false;
        if (data.hasCenterCircle() && sliceVector.length() < circleRadius * data.centerCircleScale) {
            return false
        }

        // Get touchAngle and align touch 0 degrees with chart 0 degrees, that why I subtracting start angle,
        // adding 360
        // and modulo 360 translates i.e -20 degrees to 340 degrees.
        val touchAngle = (pointToAngle(touchX, touchY, centerX, centerY) - rotation + 360f) % 360f
        val sliceScale = 360f / maxSum
        var lastAngle = 0f // No start angle here, see above
        for ((sliceIndex, sliceValue) in data.getValues().withIndex()) {
            val angle = abs(sliceValue.value) * sliceScale
            if (touchAngle >= lastAngle) {
                selectedValue[sliceIndex, sliceIndex] = SelectedValueType.NONE
            }
            lastAngle += angle
        }
        return isTouched()
    }

    /**
     * Draw center circle with text if [PieChartData.hasCenterCircle] is set true.
     */
    private fun drawCenterCircle(canvas: Canvas?) {
        val data = dataProvider.pieChartData
        val circleRadius = circleOval.width() / 2f
        val centerRadius = circleRadius * data.centerCircleScale
        val centerX = circleOval.centerX()
        val centerY = circleOval.centerY()
        canvas!!.drawCircle(centerX, centerY, centerRadius, centerCirclePaint)

        // Draw center text1 and text2 if not empty.
        if (!TextUtils.isEmpty(data.centerText1)) {
            val text1Height = abs(centerCircleText1FontMetrics.ascent)
            if (!TextUtils.isEmpty(data.centerText2)) {
                // Draw text 2 only if text 1 is not empty.
                val text2Height = abs(centerCircleText2FontMetrics.ascent)
                canvas.drawText(
                    data.centerText1!!,
                    centerX,
                    centerY - text1Height * 0.2f,
                    centerCircleText1Paint
                )
                canvas.drawText(
                    data.centerText2!!,
                    centerX,
                    centerY + text2Height,
                    centerCircleText2Paint
                )
            } else {
                canvas.drawText(
                    data.centerText1!!,
                    centerX,
                    centerY + text1Height / 4f,
                    centerCircleText1Paint
                )
            }
        }
    }

    /**
     * Draw all slices for this PieChart, if mode == [.MODE_HIGHLIGHT]
     * currently selected slices will be redrawn and highlighted.
     *
     * @param canvas canvas
     */
    private fun drawSlices(canvas: Canvas?) {
        val data = dataProvider.pieChartData
        val sliceScale = 360f / maxSum
        var lastAngle = rotation.toFloat()
        for ((sliceIndex, sliceValue) in data.getValues().withIndex()) {
            val angle = abs(sliceValue.value) * sliceScale
            if (isTouched() && selectedValue.firstIndex == sliceIndex) {
                drawSlice(canvas, sliceValue, lastAngle, angle, MODE_HIGHLIGHT)
            } else {
                drawSlice(canvas, sliceValue, lastAngle, angle, MODE_DRAW)
            }
            lastAngle += angle
        }
    }

    private fun drawSeparationLines(canvas: Canvas?) {
        val data = dataProvider.pieChartData
        if (data.getValues().size < 2) {
            //No need for separation lines for 0 or 1 slices.
            return
        }
        val sliceSpacing = dp2px(density, data.slicesSpacing)
        if (sliceSpacing < 1) {
            //No need for separation lines
            return
        }
        val sliceScale = 360f / maxSum
        var lastAngle = rotation.toFloat()
        val circleRadius = circleOval.width() / 2f
        separationLinesPaint.strokeWidth = sliceSpacing.toFloat()
        for (sliceValue in data.getValues()) {
            val angle = abs(sliceValue.value) * sliceScale
            sliceVector[cos(Math.toRadians(lastAngle.toDouble()))
                .toFloat()] = sin(Math.toRadians(lastAngle.toDouble())).toFloat()
            normalizeVector(sliceVector)
            val x1 = sliceVector.x * (circleRadius + touchAdditional) + circleOval.centerX()
            val y1 = sliceVector.y * (circleRadius + touchAdditional) + circleOval.centerY()
            canvas!!.drawLine(
                circleOval.centerX(),
                circleOval.centerY(),
                x1,
                y1,
                separationLinesPaint
            )
            lastAngle += angle
        }
    }

    fun drawLabels(canvas: Canvas?) {
        val data = dataProvider.pieChartData
        val sliceScale = 360f / maxSum
        var lastAngle = rotation.toFloat()
        for ((sliceIndex, sliceValue) in data.getValues().withIndex()) {
            val angle = abs(sliceValue.value) * sliceScale
            if (isTouched()) {
                if (hasLabels) {
                    drawLabel(canvas, sliceValue, lastAngle, angle)
                } else if (hasLabelsOnlyForSelected && selectedValue.firstIndex == sliceIndex) {
                    drawLabel(canvas, sliceValue, lastAngle, angle)
                }
            } else {
                if (hasLabels) {
                    drawLabel(canvas, sliceValue, lastAngle, angle)
                }
            }
            lastAngle += angle
        }
    }

    /**
     * Method draws single slice from lastAngle to lastAngle+angle, if mode = [.MODE_HIGHLIGHT] slice will be
     * darken
     * and will have bigger radius.
     */
    private fun drawSlice(
        canvas: Canvas?,
        sliceValue: SliceValue,
        lastAngle: Float,
        angle: Float,
        mode: Int
    ) {
        sliceVector[cos(Math.toRadians((lastAngle + angle / 2).toDouble())).toFloat()] =
            sin(Math.toRadians((lastAngle + angle / 2).toDouble())).toFloat()
        normalizeVector(sliceVector)
        drawCircleOval.set(circleOval)
        if (MODE_HIGHLIGHT == mode) {
            // Add additional touch feedback by setting bigger radius for that slice and darken color.
            drawCircleOval.inset(-touchAdditional.toFloat(), -touchAdditional.toFloat())
            slicePaint.color = sliceValue.darkenColor
            canvas!!.drawArc(drawCircleOval, lastAngle, angle, true, slicePaint)
        } else {
            slicePaint.color = sliceValue.color
            canvas!!.drawArc(drawCircleOval, lastAngle, angle, true, slicePaint)
        }
    }

    private fun drawLabel(
        canvas: Canvas?,
        sliceValue: SliceValue,
        lastAngle: Float,
        angle: Float
    ) {
        sliceVector[cos(Math.toRadians((lastAngle + angle / 2).toDouble()))
            .toFloat()] = sin(Math.toRadians((lastAngle + angle / 2).toDouble()))
            .toFloat()
        normalizeVector(sliceVector)
        val numChars = valueFormatter!!.formatChartValue(labelBuffer, sliceValue)
        if (numChars == 0) {
            // No need to draw empty label
            return
        }
        val labelWidth = labelPaint.measureText(labelBuffer, labelBuffer.size - numChars, numChars)
        val labelHeight = abs(fontMetrics.ascent)
        val centerX = circleOval.centerX()
        val centerY = circleOval.centerY()
        val circleRadius = circleOval.width() / 2f
        val labelRadius: Float = if (hasLabelsOutside) {
            circleRadius * DEFAULT_LABEL_OUTSIDE_RADIUS_FACTOR
        } else {
            if (hasCenterCircle) {
                circleRadius - (circleRadius - circleRadius * centerCircleScale) / 2
            } else {
                circleRadius * DEFAULT_LABEL_INSIDE_RADIUS_FACTOR
            }
        }
        val rawX = labelRadius * sliceVector.x + centerX
        val rawY = labelRadius * sliceVector.y + centerY
        val left: Float
        val right: Float
        val top: Float
        val bottom: Float
        if (hasLabelsOutside) {
            if (rawX > centerX) {
                // Right half.
                left = rawX + labelMargin
                right = rawX + labelWidth + labelMargin * 3
            } else {
                left = rawX - labelWidth - labelMargin * 3
                right = rawX - labelMargin
            }
            if (rawY > centerY) {
                // Lower half.
                top = rawY + labelMargin
                bottom = rawY + labelHeight + labelMargin * 3
            } else {
                top = rawY - labelHeight - labelMargin * 3
                bottom = rawY - labelMargin
            }
        } else {
            left = rawX - labelWidth / 2f - labelMargin
            right = rawX + labelWidth / 2f + labelMargin
            top = rawY - labelHeight / 2f - labelMargin
            bottom = rawY + labelHeight / 2f + labelMargin
        }
        labelBackgroundRect[left, top, right] = bottom
        drawLabelTextAndBackground(
            canvas!!, labelBuffer, labelBuffer.size - numChars, numChars,
            sliceValue.darkenColor
        )
    }

    private fun normalizeVector(point: PointF) {
        val abs = point.length()
        point[point.x / abs] = point.y / abs
    }

    /**
     * Calculates angle of touched point.
     */
    private fun pointToAngle(x: Float, y: Float, centerX: Float, centerY: Float): Float {
        val diffX = (x - centerX).toDouble()
        val diffY = (y - centerY).toDouble()
        // Pass -diffX to get clockwise degrees order.
        val radian = atan2(-diffX, diffY)
        var angle = (Math.toDegrees(radian).toFloat() + 360) % 360
        // Add 90 because atan2 returns 0 degrees at 6 o'clock.
        angle += 90f
        return angle
    }

    /**
     * Calculates rectangle(square) that will constraint chart circle.
     */
    private fun calculateCircleOval() {
        val contentRect = computator.contentRectMinusAllMargins
        val circleRadius = min(contentRect.width() / 2f, contentRect.height() / 2f)
        val centerX = contentRect.centerX().toFloat()
        val centerY = contentRect.centerY().toFloat()
        val left = centerX - circleRadius + touchAdditional
        val top = centerY - circleRadius + touchAdditional
        val right = centerX + circleRadius - touchAdditional
        val bottom = centerY + circleRadius - touchAdditional
        circleOval[left, top, right] = bottom
        val inest = 0.5f * circleOval.width() * (1.0f - circleFillRatio)
        circleOval.inset(inest, inest)
    }

    /**
     * Viewport is not really important for PieChart, this kind of chart doesn't relay on viewport but uses pixels
     * coordinates instead. This method also calculates sum of all SliceValues.
     */
    private fun calculateMaxViewport() {
        tempMaximumViewport[0f, MAX_WIDTH_HEIGHT, MAX_WIDTH_HEIGHT] = 0f
        maxSum = 0.0f
        for (sliceValue in dataProvider.pieChartData.getValues()) {
            maxSum += abs(sliceValue.value)
        }
    }

    var chartRotation: Int
        get() = rotation
        set(rotation) {
            var rotationValue = rotation
            rotationValue = (rotationValue % 360 + 360) % 360
            this.rotation = rotationValue
        }

    /**
     * Returns SliceValue that is under given angle, selectedValue (if not null) will be hold slice index.
     */
    fun getValueForAngle(angle: Int, selectedValue: SelectedValue?): SliceValue? {
        val data = dataProvider.pieChartData
        val touchAngle = (angle - rotation + 360f) % 360f
        val sliceScale = 360f / maxSum
        var lastAngle = 0f
        for ((sliceIndex, sliceValue) in data.getValues().withIndex()) {
            val tempAngle = abs(sliceValue.value) * sliceScale
            if (touchAngle >= lastAngle) {
                selectedValue?.set(sliceIndex, sliceIndex, SelectedValueType.NONE)
                return sliceValue
            }
            lastAngle += tempAngle
        }
        return null
    }

    /**
     * @see .setCircleFillRatio
     */
    fun getCircleFillRatio(): Float {
        return circleFillRatio
    }

    /**
     * Set how much of view area should be taken by chart circle. Value should be between 0 and 1. Default is 1 so
     * circle will have radius equals min(View.width, View.height).
     */
    fun setCircleFillRatio(fillRatio: Float) {
        var ratio = fillRatio
        if (ratio < 0) {
            ratio = 0f
        } else if (ratio > 1) {
            ratio = 1f
        }
        circleFillRatio = ratio
        calculateCircleOval()
    }

    companion object {
        private const val MAX_WIDTH_HEIGHT = 100f
        private const val DEFAULT_START_ROTATION = 45
        private const val DEFAULT_LABEL_INSIDE_RADIUS_FACTOR = 0.7f
        private const val DEFAULT_LABEL_OUTSIDE_RADIUS_FACTOR = 1.0f
        private const val DEFAULT_TOUCH_ADDITIONAL_DP = 8
        private const val MODE_DRAW = 0
        private const val MODE_HIGHLIGHT = 1
    }
}