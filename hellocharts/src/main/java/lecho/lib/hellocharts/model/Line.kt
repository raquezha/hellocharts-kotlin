package lecho.lib.hellocharts.model

import android.graphics.PathEffect
import lecho.lib.hellocharts.formatter.LineChartValueFormatter
import lecho.lib.hellocharts.formatter.SimpleLineChartValueFormatter
import lecho.lib.hellocharts.util.ChartUtils
import lecho.lib.hellocharts.util.ChartUtils.darkenColor

/**
 * Single line for line chart.
 */
@Suppress("unused")
class Line {
    var color = ChartUtils.DEFAULT_COLOR

    private var pointColor = UNINITIALIZED

    var darkenColor = ChartUtils.DEFAULT_DARKEN_COLOR


    /**
     * Transparency of area when line is filled. *
     */
    var areaTransparency = DEFAULT_AREA_TRANSPARENCY

    var strokeWidth = DEFAULT_LINE_STROKE_WIDTH_DP

    var pointRadius = DEFAULT_POINT_RADIUS_DP

    var gradientToTransparent = false

    var hasPoints = true

    var hasLines = true

    var hasLabels = false

    var hasLabelsOnlyForSelected = false

    var isCubic = false

    var isSquare = false

    var isFilled = false

    var shape = ValueShape.CIRCLE

    /**
     * Set path effect for this line, note: it will slow down drawing, try to not use complicated effects,
     * DashPathEffect should be safe choice.
     */
    @JvmField
    var pathEffect: PathEffect? = null

    var formatter: LineChartValueFormatter = SimpleLineChartValueFormatter()

    private var values: MutableList<PointValue> = ArrayList()

    constructor()

    constructor(values: MutableList<PointValue>?) {
        setValues(values)
    }

    constructor(line: Line) {
        color = line.color
        pointColor = line.pointColor
        darkenColor = line.darkenColor
        areaTransparency = line.areaTransparency
        strokeWidth = line.strokeWidth
        pointRadius = line.pointRadius
        gradientToTransparent = line.gradientToTransparent
        hasPoints = line.hasPoints
        hasLines = line.hasLines
        hasLabels = line.hasLabels
        hasLabelsOnlyForSelected = line.hasLabelsOnlyForSelected
        isSquare = line.isSquare
        isCubic = line.isCubic
        isFilled = line.isFilled
        shape = line.shape
        pathEffect = line.pathEffect
        formatter = line.formatter

        for (pointValue in line.values) {
            values.add(PointValue(pointValue))
        }
    }

    fun update(scale: Float) {
        for (value in values) {
            value.update(scale)
        }
    }

    fun finish() {
        for (value in values) {
            value.finish()
        }
    }

    fun getValues(): List<PointValue> {
        return values
    }

    fun setValues(values: MutableList<PointValue>?) {
        this.values = values ?: ArrayList()
    }

    fun setColor(color: Int): Line {
        this.color = color
        if (pointColor == UNINITIALIZED) {
            darkenColor = darkenColor(color)
        }
        return this
    }

    fun getPointColor(): Int {
        return if (pointColor == UNINITIALIZED) {
            color
        } else {
            pointColor
        }
    }

    fun setPointColor(pointColor: Int): Line {
        this.pointColor = pointColor
        darkenColor = if (pointColor == UNINITIALIZED) {
            darkenColor(color)
        } else {
            darkenColor(pointColor)
        }
        return this
    }

    /**
     * Set area transparency(255 is full opacity) for filled lines
     *
     * @param areaTransparency transparency
     * @return Line
     */
    fun setAreaTransparency(areaTransparency: Int): Line {
        this.areaTransparency = areaTransparency
        return this
    }

    fun setStrokeWidth(strokeWidth: Int): Line {
        this.strokeWidth = strokeWidth
        return this
    }

    fun hasPoints(): Boolean {
        return hasPoints
    }

    fun setHasPoints(hasPoints: Boolean): Line {
        this.hasPoints = hasPoints
        return this
    }

    fun hasLines(): Boolean {
        return hasLines
    }

    fun setHasLines(hasLines: Boolean): Line {
        this.hasLines = hasLines
        return this
    }

    fun hasLabels(): Boolean {
        return hasLabels
    }

    fun setHasLabels(hasLabels: Boolean): Line {
        this.hasLabels = hasLabels
        if (hasLabels) {
            hasLabelsOnlyForSelected = false
        }
        return this
    }

    /**
     * @see .setHasLabelsOnlyForSelected
     */
    fun hasLabelsOnlyForSelected(): Boolean {
        return hasLabelsOnlyForSelected
    }

    /**
     * Set true if you want to show value labels only for selected value, works best when chart has
     * isValueSelectionEnabled set to true
     */
    fun setHasLabelsOnlyForSelected(hasLabelsOnlyForSelected: Boolean): Line {
        this.hasLabelsOnlyForSelected = hasLabelsOnlyForSelected
        if (hasLabelsOnlyForSelected) {
            hasLabels = false
        }
        return this
    }

    /**
     * Set radius for points for this line.
     *
     * @param pointRadius radius
     * @return line
     */
    fun setPointRadius(pointRadius: Int): Line {
        this.pointRadius = pointRadius
        return this
    }

    fun setHasGradientToTransparent(hasGradientToTransparent: Boolean): Line {
        gradientToTransparent = hasGradientToTransparent
        return this
    }

    fun setCubic(isCubic: Boolean): Line {
        this.isCubic = isCubic
        if (isSquare) setSquare(false)
        return this
    }

    fun setSquare(isSquare: Boolean): Line {
        this.isSquare = isSquare
        if (isCubic) setCubic(false)
        return this
    }

    fun setFilled(isFilled: Boolean): Line {
        this.isFilled = isFilled
        return this
    }

    /**
     * Set shape for points, possible values: SQUARE, CIRCLE
     *
     * @param shape shape
     * @return line
     */
    fun setShape(shape: ValueShape): Line {
        this.shape = shape
        return this
    }

    fun setFormatter(formatter: LineChartValueFormatter?): Line {
        if (null != formatter) {
            this.formatter = formatter
        }
        return this
    }

    companion object {
        private const val DEFAULT_LINE_STROKE_WIDTH_DP = 3
        private const val DEFAULT_POINT_RADIUS_DP = 6
        private const val DEFAULT_AREA_TRANSPARENCY = 64
        const val UNINITIALIZED = 0
    }
}