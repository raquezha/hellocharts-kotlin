package lecho.lib.hellocharts.model

import lecho.lib.hellocharts.util.ChartUtils
import lecho.lib.hellocharts.util.ChartUtils.darkenColor
import java.util.Arrays

/**
 * Single sub-column value for ColumnChart.
 */
class SubcolumnValue {
    var value = 0f
    private var originValue = 0f
    private var diff = 0f
    var color = ChartUtils.DEFAULT_COLOR
    var darkenColor = ChartUtils.DEFAULT_DARKEN_COLOR
    var labelAsChars: CharArray? = null

    constructor() {
        setValue(0f)
    }

    constructor(value: Float) {
        // point and targetPoint have to be different objects
        setValue(value)
    }

    constructor(value: Float, color: Int) {
        // point and targetPoint have to be different objects
        setValue(value)
        setColor(color)
    }

    constructor(columnValue: SubcolumnValue) {
        setValue(columnValue.value)
        setColor(columnValue.color)
        labelAsChars = columnValue.labelAsChars
    }

    fun update(scale: Float) {
        value = originValue + diff * scale
    }

    fun finish() {
        setValue(originValue + diff)
    }

    fun setValue(value: Float): SubcolumnValue {
        this.value = value
        originValue = value
        diff = 0f
        return this
    }

    /**
     * Set target value that should be reached when data animation
     * finish then call [lecho.lib.hellocharts.view.Chart.startDataAnimation]
     *
     * @param target target
     * @return sub column value
     */
    fun setTarget(target: Float): SubcolumnValue {
        setValue(value)
        diff = target - originValue
        return this
    }

    fun setColor(color: Int): SubcolumnValue {
        this.color = color
        darkenColor = darkenColor(color)
        return this
    }

    @Suppress("unused")
    fun setLabel(label: String): SubcolumnValue {
        labelAsChars = label.toCharArray()
        return this
    }

    override fun toString(): String {
        return "ColumnValue [value=$value]"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as SubcolumnValue
        if (color != that.color) return false
        if (darkenColor != that.darkenColor) return false
        if (that.diff.compareTo(diff) != 0) return false
        if (that.originValue.compareTo(originValue) != 0) return false
        return if (that.value.compareTo(value) != 0) false else Arrays.equals(
            labelAsChars, that.labelAsChars
        )
    }

    override fun hashCode(): Int {
        var result = if (value != 0.0f) java.lang.Float.floatToIntBits(value) else 0
        result =
            31 * result + if (originValue != 0.0f) java.lang.Float.floatToIntBits(originValue) else 0
        result = 31 * result + if (diff != 0.0f) java.lang.Float.floatToIntBits(diff) else 0
        result = 31 * result + color
        result = 31 * result + darkenColor
        result = 31 * result + if (labelAsChars != null) Arrays.hashCode(labelAsChars) else 0
        return result
    }
}