package lecho.lib.hellocharts.model

import java.util.Arrays

/**
 * Single point coordinates, used for LineChartData.
 */
class PointValue {
    var x = 0f
        private set
    var y = 0f
        private set
    private var originX = 0f
    private var originY = 0f
    private var diffX = 0f
    private var diffY = 0f

    @get:Deprecated("")
    var labelAsChars: CharArray? = null

    constructor() {
        set(0f, 0f)
    }

    constructor(x: Float, y: Float) {
        set(x, y)
    }

    constructor(pointValue: PointValue) {
        set(pointValue.x, pointValue.y)
        labelAsChars = pointValue.labelAsChars
    }

    fun update(scale: Float) {
        x = originX + diffX * scale
        y = originY + diffY * scale
    }

    fun finish() {
        set(originX + diffX, originY + diffY)
    }

    operator fun set(x: Float, y: Float): PointValue {
        this.x = x
        this.y = y
        originX = x
        originY = y
        diffX = 0f
        diffY = 0f
        return this
    }

    /**
     * Set target value that should be reached when data animation
     * finish then call [lecho.lib.hellocharts.view.Chart.startDataAnimation]
     */
    fun setTarget(targetX: Float, targetY: Float): PointValue {
        set(x, y)
        diffX = targetX - originX
        diffY = targetY - originY
        return this
    }

    fun setLabel(label: String): PointValue {
        labelAsChars = label.toCharArray()
        return this
    }

    @Deprecated("")
    fun setLabel(label: CharArray?): PointValue {
        labelAsChars = label
        return this
    }

    override fun toString(): String {
        return "PointValue [x=$x, y=$y]"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as PointValue
        if (that.diffX.compareTo(diffX) != 0) return false
        if (that.diffY.compareTo(diffY) != 0) return false
        if (that.originX.compareTo(originX) != 0) return false
        if (that.originY.compareTo(originY) != 0) return false
        if (that.x.compareTo(x) != 0) return false
        return if (that.y.compareTo(y) != 0) false else Arrays.equals(
            labelAsChars,
            that.labelAsChars
        )
    }

    override fun hashCode(): Int {
        var result = if (x != 0.0f) java.lang.Float.floatToIntBits(x) else 0
        result = 31 * result + if (y != 0.0f) java.lang.Float.floatToIntBits(y) else 0
        result = 31 * result + if (originX != 0.0f) java.lang.Float.floatToIntBits(originX) else 0
        result = 31 * result + if (originY != 0.0f) java.lang.Float.floatToIntBits(originY) else 0
        result = 31 * result + if (diffX != 0.0f) java.lang.Float.floatToIntBits(diffX) else 0
        result = 31 * result + if (diffY != 0.0f) java.lang.Float.floatToIntBits(diffY) else 0
        result = 31 * result + if (labelAsChars != null) Arrays.hashCode(labelAsChars) else 0
        return result
    }
}