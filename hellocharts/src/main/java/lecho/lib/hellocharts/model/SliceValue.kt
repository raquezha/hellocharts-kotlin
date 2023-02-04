package lecho.lib.hellocharts.model

import lecho.lib.hellocharts.util.ChartUtils
import lecho.lib.hellocharts.util.ChartUtils.darkenColor
import java.util.Arrays

/**
 * Model representing single slice on PieChart.
 */
@Suppress("unused")
class SliceValue {
    /**
     * Current value of this slice.
     */
    var value = 0f
        private set

    /**
     * Origin value of this slice, used during value animation.
     */
    private var originValue = 0f

    /**
     * Difference between originValue and targetValue.
     */
    private var diff = 0f

    /**
     * Color of this slice.
     */
    var color = ChartUtils.DEFAULT_COLOR
        private set

    /**
     * Darken color used to draw label background and give touch feedback.
     */
    var darkenColor = ChartUtils.DEFAULT_DARKEN_COLOR
        private set

    /**
     * Custom label for this slice, if not set number formatting will be used.
     */

    var labelAsChars: CharArray? = null

    constructor() {
        setValue(0f)
    }

    constructor(value: Float) {
        setValue(value)
    }

    constructor(value: Float, color: Int) {
        setValue(value)
        setColor(color)
    }

    fun update(scale: Float) {
        value = originValue + diff * scale
    }

    fun finish() {
        setValue(originValue + diff)
    }

    fun setValue(value: Float): SliceValue {
        this.value = value
        originValue = value
        diff = 0f
        return this
    }

    /**
     * Set target value that should be reached when data animation
     * finish then call [lecho.lib.hellocharts.view.Chart.startDataAnimation]
     *
     * @param target the floating target
     * @return [SliceValue]
     */
    fun setTarget(target: Float): SliceValue {
        setValue(value)
        diff = target - originValue
        return this
    }

    fun setColor(color: Int): SliceValue {
        this.color = color
        darkenColor = darkenColor(color)
        return this
    }

    fun setLabel(label: String): SliceValue {
        labelAsChars = label.toCharArray()
        return this
    }

    override fun toString(): String {
        return "SliceValue [value=$value]"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as SliceValue
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

    companion object {
        private const val DEFAULT_SLICE_SPACING_DP = 2
    }
}