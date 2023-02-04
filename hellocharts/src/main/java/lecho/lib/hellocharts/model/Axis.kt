package lecho.lib.hellocharts.model

import android.graphics.Color
import android.graphics.Typeface
import lecho.lib.hellocharts.formatter.AxisValueFormatter
import lecho.lib.hellocharts.formatter.SimpleAxisValueFormatter
import lecho.lib.hellocharts.util.ChartUtils

/**
 * Single axis model. By default axis is auto-generated. Use [.setAutoGenerated] to disable axis values
 * generation and set values manually using [.setValues]. If Axis is auto-generated [.setValues]
 * will be ignored but if you set some values Axis will switch to manual mode. Change how axis labels are displayed by
 * changing formatter [.setFormatter]. Axis can have a name
 * that should be displayed next to
 * labels(that depends on renderer implementation), you can change name using [.setName], by default axis
 * name is empty and therefore not displayed.
 */
@Suppress("unused")
class Axis {
    /**
     * Text size for axis labels and name.
     */
    var textSize = DEFAULT_TEXT_SIZE_SP
        private set

    /**
     * Maximum number of characters used for this axis. Used to determine axis dimensions.
     */
    var maxLabelChars = DEFAULT_MAX_AXIS_LABEL_CHARS
        private set

    /**
     * Axis values, each value will be used to calculate its label position.
     */
    private var values: MutableList<AxisValue> = ArrayList()

    /**
     * Name for this axis.
     */
    var name: String? = null
        private set

    /**
     * If true axis will be generated to automatically fit chart ranges. *
     */
    var isAutoGenerated = true
        private set

    /**
     * If true renderer will draw lines(grid) for this axis.
     */
    private var hasLines = false
    /**
     * @see .setInside
     */
    /**
     * If true axis labels will be drown inside chart area.
     */
    var isInside = false
        private set

    /**
     * Axis labels and name text color.
     */
    var textColor = Color.LTGRAY
        private set

    /**
     * Axis grid lines color.
     */
    var lineColor = ChartUtils.DEFAULT_DARKEN_COLOR
        private set

    /**
     * Typeface for labels and name text.
     */
    var typeface: Typeface? = null
        private set

    /**
     * Formatter used to format labels.
     */
    var formatter: AxisValueFormatter = SimpleAxisValueFormatter()
        private set

    /**
     * If true draws a line between the labels and the graph *
     */
    private var hasSeparationLine = true
    private var hasTiltedLabels = false

    /**
     * Creates auto-generated axis without name and with default formatter.
     */
    constructor()

    /**
     * Creates axis with given values(not auto-generated) without name and with default formatter.
     */
    constructor(values: MutableList<AxisValue>?) {
        setValues(values)
    }

    constructor(axis: Axis) {
        name = axis.name
        isAutoGenerated = axis.isAutoGenerated
        hasLines = axis.hasLines
        isInside = axis.isInside
        textColor = axis.textColor
        lineColor = axis.lineColor
        textSize = axis.textSize
        maxLabelChars = axis.maxLabelChars
        typeface = axis.typeface
        formatter = axis.formatter
        hasSeparationLine = axis.hasSeparationLine
        hasTiltedLabels = axis.hasTiltedLabels
        for (axisValue in axis.values) {
            values.add(AxisValue(axisValue))
        }
    }

    fun getValues(): List<AxisValue> {
        return values
    }

    fun setValues(values: MutableList<AxisValue>?): Axis {
        this.values = values ?: ArrayList()
        isAutoGenerated = false
        return this
    }

    fun setName(name: String?): Axis {
        this.name = name
        return this
    }

    fun setAutoGenerated(isAutoGenerated: Boolean): Axis {
        this.isAutoGenerated = isAutoGenerated
        return this
    }

    fun hasLines(): Boolean {
        return hasLines
    }

    fun setHasLines(hasLines: Boolean): Axis {
        this.hasLines = hasLines
        return this
    }

    fun setTextColor(color: Int): Axis {
        textColor = color
        return this
    }

    /**
     * Set to true if you want axis values to be drawn inside chart area(axis name still will be drawn outside), by
     * default this is set to false and axis is drawn outside chart area.
     */
    fun setInside(isInside: Boolean): Axis {
        this.isInside = isInside
        return this
    }

    fun setLineColor(lineColor: Int): Axis {
        this.lineColor = lineColor
        return this
    }

    fun setTextSize(textSize: Int): Axis {
        this.textSize = textSize
        return this
    }

    /**
     * Set maximum number of characters for axis labels, min 0, max 32.
     */
    fun setMaxLabelChars(maxLabelChars: Int): Axis {
        var maxChars = maxLabelChars
        if (maxChars < 0) {
            maxChars = 0
        } else if (maxChars > 32) {
            maxChars = 32
        }
        this.maxLabelChars = maxChars
        return this
    }

    fun setTypeface(typeface: Typeface?): Axis {
        this.typeface = typeface
        return this
    }

    fun setFormatter(formatter: AxisValueFormatter?): Axis {
        this.formatter = formatter ?: SimpleAxisValueFormatter()
        return this
    }

    /**
     * Set true if you want to draw separation line for this axis,
     * set false to hide separation line, by default true.
     */
    fun setHasSeparationLine(hasSeparationLine: Boolean): Axis {
        this.hasSeparationLine = hasSeparationLine
        return this
    }

    fun hasSeparationLine(): Boolean {
        return hasSeparationLine
    }

    fun hasTiltedLabels(): Boolean {
        return hasTiltedLabels
    }

    fun setHasTiltedLabels(hasTiltedLabels: Boolean): Axis {
        this.hasTiltedLabels = hasTiltedLabels
        return this
    }

    companion object {
        const val DEFAULT_TEXT_SIZE_SP = 12
        const val DEFAULT_MAX_AXIS_LABEL_CHARS = 3

        /**
         * Generates Axis with values from start to stop inclusive.
         */
        fun generateAxisFromRange(start: Float, stop: Float, step: Float): Axis {
            val values: MutableList<AxisValue> = ArrayList()
            var value = start
            while (value <= stop) {
                val axisValue = AxisValue(value)
                values.add(axisValue)
                value += step
            }
            return Axis(values)
        }

        /**
         * Generates Axis with values from given list.
         */
        fun generateAxisFromCollection(axisValues: List<Float>): Axis {
            val values: MutableList<AxisValue> = ArrayList()
            for (value in axisValues) {
                val axisValue = AxisValue(value)
                values.add(axisValue)
            }
            return Axis(values)
        }

        /**
         * Generates Axis with values and labels from given lists, both lists must have the same size.
         */
        fun generateAxisFromCollection(
            axisValues: List<Float>,
            axisValuesLabels: List<String?>
        ): Axis {
            require(axisValues.size == axisValuesLabels.size) { "Values and labels lists must have the same size!" }
            val values: MutableList<AxisValue> = ArrayList()
            for ((index, value) in axisValues.withIndex()) {
                val axisValue = AxisValue(value).setLabel(axisValuesLabels[index]!!)
                values.add(axisValue)
            }
            return Axis(values)
        }
    }
}