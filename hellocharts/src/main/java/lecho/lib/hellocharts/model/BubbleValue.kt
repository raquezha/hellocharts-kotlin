@file:Suppress("unused")

package lecho.lib.hellocharts.model

import lecho.lib.hellocharts.util.ChartUtils
import lecho.lib.hellocharts.util.ChartUtils.darkenColor
import java.util.Arrays

/**
 * Single value drawn as bubble on BubbleChart.
 */
class BubbleValue {
    /**
     * Current X value.
     */
    var x = 0f
        private set

    /**
     * Current Y value.
     */
    var y = 0f
        private set

    /**
     * Current Z value , third bubble value interpreted as bubble area.
     */
    var z = 0f
        private set

    /**
     * Origin X value, used during value animation.
     */
    private var originX = 0f

    /**
     * Origin Y value, used during value animation.
     */
    private var originY = 0f

    /**
     * Origin Z value, used during value animation.
     */
    private var originZ = 0f

    /**
     * Difference between originX value and target X value.
     */
    private var diffX = 0f

    /**
     * Difference between originX value and target X value.
     */
    private var diffY = 0f

    /**
     * Difference between originX value and target X value.
     */
    private var diffZ = 0f
    var color = ChartUtils.DEFAULT_COLOR
    var darkenColor = ChartUtils.DEFAULT_DARKEN_COLOR

    var shape: ValueShape? = ValueShape.CIRCLE
    var labelAsChars: CharArray? = null


    constructor() {
        set(0f, 0f, 0f)
    }

    constructor(x: Float, y: Float, z: Float) {
        set(x, y, z)
    }

    constructor(x: Float, y: Float, z: Float, color: Int) {
        set(x, y, z)
        setColor(color)
    }

    constructor(bubbleValue: BubbleValue) {
        set(bubbleValue.x, bubbleValue.y, bubbleValue.z)
        setColor(bubbleValue.color)
        labelAsChars = bubbleValue.labelAsChars
        shape = bubbleValue.shape
    }

    fun update(scale: Float) {
        x = originX + diffX * scale
        y = originY + diffY * scale
        z = originZ + diffZ * scale
    }

    fun finish() {
        set(originX + diffX, originY + diffY, originZ + diffZ)
    }

    operator fun set(x: Float, y: Float, z: Float): BubbleValue {
        this.x = x
        this.y = y
        this.z = z
        originX = x
        originY = y
        originZ = z
        diffX = 0f
        diffY = 0f
        diffZ = 0f
        return this
    }

    /**
     * Set target value that should be reached when data animation
     * finish then call [lecho.lib.hellocharts.view.Chart.startDataAnimation]
     *
     * @return sub column value
     */
    fun setTarget(targetX: Float, targetY: Float, targetZ: Float): BubbleValue {
        set(x, y, z)
        diffX = targetX - originX
        diffY = targetY - originY
        diffZ = targetZ - originZ
        return this
    }

    fun setColor(color: Int): BubbleValue {
        this.color = color
        darkenColor = darkenColor(color)
        return this
    }

    fun setShape(shape: ValueShape?): BubbleValue {
        this.shape = shape
        return this
    }

    fun setLabel(label: String): BubbleValue {
        labelAsChars = label.toCharArray()
        return this
    }

    override fun toString(): String {
        return "BubbleValue [x=$x, y=$y, z=$z]"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as BubbleValue
        if (color != that.color) return false
        if (darkenColor != that.darkenColor) return false
        if (that.diffX.compareTo(diffX) != 0) return false
        if (that.diffY.compareTo(diffY) != 0) return false
        if (that.diffZ.compareTo(diffZ) != 0) return false
        if (that.originX.compareTo(originX) != 0) return false
        if (that.originY.compareTo(originY) != 0) return false
        if (that.originZ.compareTo(originZ) != 0) return false
        if (that.x.compareTo(x) != 0) return false
        if (that.y.compareTo(y) != 0) return false
        if (that.z.compareTo(z) != 0) return false
        return if (!Arrays.equals(labelAsChars, that.labelAsChars)) false else shape === that.shape
    }

    override fun hashCode(): Int {
        var result = if (x != +0.0f) java.lang.Float.floatToIntBits(x) else 0
        result = 31 * result + if (y != 0.0f) java.lang.Float.floatToIntBits(y) else 0
        result = 31 * result + if (z != 0.0f) java.lang.Float.floatToIntBits(z) else 0
        result = 31 * result + if (originX != 0.0f) java.lang.Float.floatToIntBits(originX) else 0
        result = 31 * result + if (originY != 0.0f) java.lang.Float.floatToIntBits(originY) else 0
        result = 31 * result + if (originZ != 0.0f) java.lang.Float.floatToIntBits(originZ) else 0
        result = 31 * result + if (diffX != 0.0f) java.lang.Float.floatToIntBits(diffX) else 0
        result = 31 * result + if (diffY != 0.0f) java.lang.Float.floatToIntBits(diffY) else 0
        result = 31 * result + if (diffZ != 0.0f) java.lang.Float.floatToIntBits(diffZ) else 0
        result = 31 * result + color
        result = 31 * result + darkenColor
        result = 31 * result + if (shape != null) shape.hashCode() else 0
        result = 31 * result + if (labelAsChars != null) Arrays.hashCode(labelAsChars) else 0
        return result
    }
}