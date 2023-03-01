package lecho.lib.hellocharts.model

/**
 * Holds selected values indexes, i.e. for LineChartModel it will be firstIndex=lineIndex; secondIndex=valueIndex.
 */
@Suppress("unused")
class SelectedValue {
    /**
     * First index i.e for LineChart that will be line index.
     */
    /**
     * First index i.e for LineChart that will be line index.
     */
    @JvmField
    var firstIndex = 0
    /**
     * Second index i.e for LineChart that will be PointValue index.
     */
    /**
     * Second index i.e for LineChart that will be PointValue index.
     */
    @JvmField
    var secondIndex = 0

    /**
     * Used only for combo charts, in other cases should have value NONE.
     */
    @JvmField
    var type: SelectedValueType? = SelectedValueType.NONE

    @JvmField
    var selectedX : Float = 0f

    @JvmField

    var selectedY: Float = 0f

    constructor() {
        clear()
    }

    constructor(firstIndex: Int, secondIndex: Int, type: SelectedValueType?) {
        set(firstIndex, secondIndex, type)
    }

    operator fun set(firstIndex: Int, secondIndex: Int, type: SelectedValueType?) {
        this.firstIndex = firstIndex
        this.secondIndex = secondIndex
        if (null != type) {
            this.type = type
        } else {
            this.type = SelectedValueType.NONE
        }
    }

    fun set(selectedValue: SelectedValue) {
        firstIndex = selectedValue.firstIndex
        secondIndex = selectedValue.secondIndex
        type = selectedValue.type
    }

    fun clear() {
        set(Int.MIN_VALUE, Int.MIN_VALUE, SelectedValueType.NONE)
    }

    val isSet: Boolean
        /**
         * Return true if selected value have meaningful value.
         */
        get() = firstIndex >= 0 && secondIndex >= 0

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + firstIndex
        result = prime * result + secondIndex
        result = prime * result + if (type == null) 0 else type.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        if (javaClass != other.javaClass) return false
        val otherObject = other as SelectedValue
        if (firstIndex != otherObject.firstIndex) return false
        return if (secondIndex != otherObject.secondIndex) false else type == otherObject.type
    }

    override fun toString(): String {
        return "SelectedValue [firstIndex=$firstIndex, secondIndex=$secondIndex, type=$type]"
    }

    /**
     * Used in combo chart to determine if selected value is used for line or column selection.
     */
    enum class SelectedValueType {
        NONE, LINE, COLUMN
    }
}