package lecho.lib.hellocharts.renderer

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import lecho.lib.hellocharts.computator.ChartComputator
import lecho.lib.hellocharts.formatter.BubbleChartValueFormatter
import lecho.lib.hellocharts.model.BubbleValue
import lecho.lib.hellocharts.model.RoundedCorner
import lecho.lib.hellocharts.model.SelectedValue
import lecho.lib.hellocharts.model.ValueShape
import lecho.lib.hellocharts.model.Viewport
import lecho.lib.hellocharts.provider.BubbleChartDataProvider
import lecho.lib.hellocharts.util.ChartUtils.dp2px
import lecho.lib.hellocharts.view.Chart
import kotlin.math.abs
import kotlin.math.sqrt

class BubbleChartRenderer(
    context: Context?,
    chart: Chart?,
    private val dataProvider: BubbleChartDataProvider
) : AbstractChartRenderer(
    context!!, chart!!
) {
    /**
     * Additional value added to bubble radius when drawing highlighted bubble, used to give tauch feedback.
     */
    @JvmField
    val touchAdditional: Int = dp2px(density, DEFAULT_TOUCH_ADDITIONAL_DP)

    /**
     * Scales for bubble radius value, only one is used depending on screen orientation;
     */
    @JvmField
    var bubbleScaleX = 0f

    @JvmField
    var bubbleScaleY = 0f

    /**
     * True if bubbleScale = bubbleScaleX so the renderer should used [ChartComputator.computeRawDistanceX]
     * , if false bubbleScale = bubbleScaleY and renderer should use
     * [ChartComputator.computeRawDistanceY].
     */
    @JvmField
    var isBubbleScaledByX = true

    /**
     * Maximum bubble radius.
     */
    @JvmField
    var maxRadius = 0f

    /**
     * Minimal bubble radius in pixels.
     */
    @JvmField
    var minRawRadius = 0f

    @JvmField
    val bubbleCenter = PointF()

    @JvmField
    val bubblePaint = Paint()

    /**
     * Rect used for drawing bubbles with SHAPE_SQUARE.
     */

    @JvmField
    val bubbleRect = RectF()

    @JvmField
    var hasLabels = false

    @JvmField
    var hasLabelsOnlyForSelected = false

    @JvmField
    var valueFormatter: BubbleChartValueFormatter? = null

    @JvmField
    val tempMaximumViewport = Viewport()

    init {
        bubblePaint.isAntiAlias = true
        bubblePaint.style = Paint.Style.FILL
    }

    override fun onChartSizeChanged() {
        val computator = chart.getChartComputator()
        val contentRect = computator.contentRectMinusAllMargins
        isBubbleScaledByX = contentRect.width() < contentRect.height()
    }

    override fun onChartDataChanged() {
        super.onChartDataChanged()
        val data = dataProvider.bubbleChartData
        hasLabels = data.hasLabels()
        hasLabelsOnlyForSelected = data.hasLabelsOnlyForSelected()
        valueFormatter = data.formatter
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
        drawBubbles(canvas)
        if (isTouched()) {
            highlightBubbles(canvas)
        }
    }

    override fun drawUnClipped(canvas: Canvas?) {}
    override fun checkTouch(touchX: Float, touchY: Float): Boolean {
        selectedValue.clear()
        val data = dataProvider.bubbleChartData
        for ((valueIndex, bubbleValue) in data.values.withIndex()) {
            val rawRadius = processBubble(bubbleValue)
            if (ValueShape.SQUARE == bubbleValue.shape) {
                if (bubbleRect.contains(touchX, touchY)) {
                    selectedValue[valueIndex, valueIndex] = SelectedValue.SelectedValueType.NONE
                }
            } else if (ValueShape.CIRCLE == bubbleValue.shape) {
                val diffX = touchX - bubbleCenter.x
                val diffY = touchY - bubbleCenter.y
                val touchDistance = sqrt((diffX * diffX + diffY * diffY).toDouble()).toFloat()
                if (touchDistance <= rawRadius) {
                    selectedValue[valueIndex, valueIndex] = SelectedValue.SelectedValueType.NONE
                }
            } else {
                throw IllegalArgumentException("Invalid bubble shape: " + bubbleValue.shape)
            }
        }
        return isTouched()
    }

    /**
     * Removes empty spaces on sides of chart(left-right for landscape, top-bottom for portrait). *This method should be
     * called after layout had been drawn*. Because most often chart is drawn as rectangle with proportions other than
     * 1:1 and bubbles have to be drawn as circles not ellipses I am unable to calculate correct margins based on chart
     * data only. I need to know chart dimension to remove extra empty spaces, that bad because viewport depends a
     * little on contentRectMinusAllMargins.
     */
    fun removeMargins() {
        val contentRect = computator.contentRectMinusAllMargins
        if (contentRect.height() == 0 || contentRect.width() == 0) {
            // View probably not yet measured, skip removing margins.
            return
        }
        val pxX = computator.computeRawDistanceX(maxRadius * bubbleScaleX)
        val pxY = computator.computeRawDistanceY(maxRadius * bubbleScaleY)
        val scaleX = computator.maxViewport.width() / contentRect.width()
        val scaleY = computator.maxViewport.height() / contentRect.height()
        var dx = 0f
        var dy = 0f
        if (isBubbleScaledByX) {
            dy = (pxY - pxX) * scaleY * 0.75f
        } else {
            dx = (pxX - pxY) * scaleX * 0.75f
        }
        val maxViewport = computator.maxViewport
        maxViewport.inset(dx, dy)
        val currentViewport = computator.getCurrentViewport()
        currentViewport.inset(dx, dy)
        computator.setMaximumViewport(maxViewport)
        computator.setCurrentViewport(currentViewport)
    }

    private fun drawBubbles(canvas: Canvas?) {
        val data = dataProvider.bubbleChartData
        for (bubbleValue in data.values) {
            drawBubble(canvas, bubbleValue)
        }
    }

    private fun drawBubble(canvas: Canvas?, bubbleValue: BubbleValue) {
        var rawRadius = processBubble(bubbleValue)
        // Not touched bubbles are a little smaller than touched to give user touch feedback.
        rawRadius -= touchAdditional.toFloat()
        bubbleRect.inset(touchAdditional.toFloat(), touchAdditional.toFloat())
        bubblePaint.color = bubbleValue.color
        drawBubbleShapeAndLabel(canvas, bubbleValue, rawRadius, MODE_DRAW)
    }

    // TODO: support rounder corner
    private fun drawBubbleShapeAndLabel(
        canvas: Canvas?,
        bubbleValue: BubbleValue,
        rawRadius: Float,
        mode: Int,
        roundedCorner: RoundedCorner? = null
    ) {
        if (ValueShape.SQUARE == bubbleValue.shape) {
            canvas!!.drawRect(bubbleRect, bubblePaint)
        } else if (ValueShape.CIRCLE == bubbleValue.shape) {
            canvas!!.drawCircle(bubbleCenter.x, bubbleCenter.y, rawRadius, bubblePaint)
        } else {
            throw IllegalArgumentException("Invalid bubble shape: " + bubbleValue.shape)
        }
        if (MODE_HIGHLIGHT == mode) {
            if (hasLabels || hasLabelsOnlyForSelected) {
                drawLabel(
                    canvas = canvas,
                    bubbleValue = bubbleValue,
                    rawX = bubbleCenter.x,
                    rawY = bubbleCenter.y,
                    roundedCorner = roundedCorner
                )
            }
        } else if (MODE_DRAW == mode) {
            if (hasLabels) {
                drawLabel(
                    canvas = canvas,
                    bubbleValue = bubbleValue,
                    rawX = bubbleCenter.x,
                    rawY = bubbleCenter.y,
                    roundedCorner = roundedCorner
                )
            }
        } else {
            throw IllegalStateException("Cannot process bubble in mode: $mode")
        }
    }

    private fun highlightBubbles(canvas: Canvas?) {
        val data = dataProvider.bubbleChartData
        val bubbleValue = data.values[selectedValue.firstIndex]
        highlightBubble(canvas, bubbleValue)
    }

    private fun highlightBubble(canvas: Canvas?, bubbleValue: BubbleValue) {
        val rawRadius = processBubble(bubbleValue)
        bubblePaint.color = bubbleValue.darkenColor
        drawBubbleShapeAndLabel(canvas, bubbleValue, rawRadius, MODE_HIGHLIGHT)
    }

    /**
     * Calculate bubble radius and center x and y coordinates. Center x and x will be stored in point parameter, radius
     * will be returned as float value.
     */
    private fun processBubble(bubbleValue: BubbleValue): Float {
        val rawX = computator.computeRawX(bubbleValue.x)
        val rawY = computator.computeRawY(bubbleValue.y)
        var radius = sqrt(abs(bubbleValue.z) / Math.PI).toFloat()
        var rawRadius: Float
        if (isBubbleScaledByX) {
            radius *= bubbleScaleX
            rawRadius = computator.computeRawDistanceX(radius)
        } else {
            radius *= bubbleScaleY
            rawRadius = computator.computeRawDistanceY(radius)
        }
        if (rawRadius < minRawRadius + touchAdditional) {
            rawRadius = minRawRadius + touchAdditional
        }
        bubbleCenter[rawX] = rawY
        if (ValueShape.SQUARE == bubbleValue.shape) {
            bubbleRect[rawX - rawRadius, rawY - rawRadius, rawX + rawRadius] = rawY + rawRadius
        }
        return rawRadius
    }

    private fun drawLabel(
        canvas: Canvas?,
        bubbleValue: BubbleValue,
        rawX: Float,
        rawY: Float,
        roundedCorner: RoundedCorner?
    ) {
        val contentRect = computator.contentRectMinusAllMargins
        val numChars = valueFormatter!!.formatChartValue(labelBuffer, bubbleValue)
        if (numChars == 0) {
            // No need to draw empty label
            return
        }
        val labelWidth = labelPaint.measureText(labelBuffer, labelBuffer.size - numChars, numChars)
        val labelHeight = abs(fontMetrics.ascent)
        var left = rawX - labelWidth / 2 - labelMargin
        var right = rawX + labelWidth / 2 + labelMargin
        var top = rawY - (labelHeight + 0f) / 2 - labelMargin
        var bottom = rawY + (labelHeight + 0f) / 2 + labelMargin
        if (top < contentRect.top) {
            top = rawY
            bottom = rawY + labelHeight + labelMargin * 2
        }
        if (bottom > contentRect.bottom) {
            top = rawY - labelHeight - labelMargin * 2
            bottom = rawY
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
            canvas = canvas!!, labelBuffer = labelBuffer,
            startIndex = labelBuffer.size - numChars, numChars = numChars,
            autoBackgroundColor = bubbleValue.darkenColor,
            roundedCorner = roundedCorner
        )
    }

    private fun calculateMaxViewport() {
        var maxZ = Float.MIN_VALUE
        tempMaximumViewport[Float.MAX_VALUE, Float.MIN_VALUE, Float.MIN_VALUE] = Float.MAX_VALUE
        val data = dataProvider.bubbleChartData
        // TODO: Optimize.
        for (bubbleValue in data.values) {
            if (abs(bubbleValue.z) > maxZ) {
                maxZ = abs(bubbleValue.z)
            }
            if (bubbleValue.x < tempMaximumViewport.left) {
                tempMaximumViewport.left = bubbleValue.x
            }
            if (bubbleValue.x > tempMaximumViewport.right) {
                tempMaximumViewport.right = bubbleValue.x
            }
            if (bubbleValue.y < tempMaximumViewport.bottom) {
                tempMaximumViewport.bottom = bubbleValue.y
            }
            if (bubbleValue.y > tempMaximumViewport.top) {
                tempMaximumViewport.top = bubbleValue.y
            }
        }
        maxRadius = sqrt(maxZ / Math.PI).toFloat()

        // Number 4 is determined by trials and errors method, no magic behind it:).
        bubbleScaleX = tempMaximumViewport.width() / (maxRadius * 4)
        if (bubbleScaleX == 0f) {
            // case for 0 viewport width.
            bubbleScaleX = 1f
        }
        bubbleScaleY = tempMaximumViewport.height() / (maxRadius * 4)
        if (bubbleScaleY == 0f) {
            // case for 0 viewport height.
            bubbleScaleY = 1f
        }

        // For cases when user sets different than 1 bubble scale in BubbleChartData.
        bubbleScaleX *= data.bubbleScale
        bubbleScaleY *= data.bubbleScale

        // Prevent cutting of bubbles on the edges of chart area.
        tempMaximumViewport.inset(-maxRadius * bubbleScaleX, -maxRadius * bubbleScaleY)
        minRawRadius = dp2px(density, dataProvider.bubbleChartData.minBubbleRadius).toFloat()
    }

    companion object {
        private const val DEFAULT_TOUCH_ADDITIONAL_DP = 4
        private const val MODE_DRAW = 0
        private const val MODE_HIGHLIGHT = 1
    }
}