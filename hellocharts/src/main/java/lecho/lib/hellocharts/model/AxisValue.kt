package lecho.lib.hellocharts.model

import java.util.Arrays

/**
 * Single axis value, use it to manually set axis labels position.You can use label attribute
 * to display text instead of number but value formatter implementation have to handle it.
 */
class AxisValue {
    var value = 0f
        private set

    @get:Deprecated("")
    var labelAsChars: CharArray? = null

    constructor(value: Float) {
        setValue(value)
    }

    constructor(axisValue: AxisValue) {
        value = axisValue.value
        labelAsChars = axisValue.labelAsChars
    }

    fun setValue(value: Float): AxisValue {
        this.value = value
        return this
    }

    /**
     * Set custom label for this axis value.
     *
     * @param label label
     */
    fun setLabel(label: String): AxisValue {
        labelAsChars = label.toCharArray()
        return this
    }

    /**
     * Set custom label for this axis value.
     *
     * @param label label
     */
    @Deprecated("")
    fun setLabel(label: CharArray?): AxisValue {
        labelAsChars = label
        return this
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val axisValue = other as AxisValue
        return if (axisValue.value.compareTo(value) != 0) false else Arrays.equals(
            labelAsChars, axisValue.labelAsChars
        )
    }

    override fun hashCode(): Int {
        var result = if (value != 0.0f) java.lang.Float.floatToIntBits(value) else 0
        result = 31 * result + if (labelAsChars != null) Arrays.hashCode(labelAsChars) else 0
        return result
    }
}