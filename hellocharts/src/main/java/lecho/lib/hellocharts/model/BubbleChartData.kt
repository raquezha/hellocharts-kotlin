@file:Suppress("unused")

package lecho.lib.hellocharts.model

import android.graphics.Typeface
import lecho.lib.hellocharts.formatter.BubbleChartValueFormatter
import lecho.lib.hellocharts.formatter.SimpleBubbleChartValueFormatter

/**
 * Data for BubbleChart.
 */
class BubbleChartData : AbstractChartData {

    var formatter: BubbleChartValueFormatter = SimpleBubbleChartValueFormatter()

    var hasLabels = false

    var hasLabelsOnlyForSelected = false

    /**
     * Set minimal bubble radius in dp, helpful when you want small bubbles(bubbles with very small z values compared to
     * other bubbles) to be visible on chart, default 6dp
     */
    var minBubbleRadius = DEFAULT_MIN_BUBBLE_RADIUS_DP

    /**
     * Set bubble scale which is used to adjust bubble size. If you want smaller bubbles set scale `<0, 1>`,
     * if you want bigger bubbles set scale greater than 1, default is 1.0f.
     */
    var bubbleScale = DEFAULT_BUBBLE_SCALE

    @JvmField
    var values: MutableList<BubbleValue> = arrayListOf()

    constructor()
    constructor(values: MutableList<BubbleValue>?) {
        setBubbleValues(values)
    }

    /**
     * Copy constructor for deep copy.
     */
    constructor(data: BubbleChartData) : super(data) {
        formatter = data.formatter
        hasLabels = data.hasLabels
        hasLabelsOnlyForSelected = data.hasLabelsOnlyForSelected
        minBubbleRadius = data.minBubbleRadius
        bubbleScale = data.bubbleScale
        for (bubbleValue in data.values) {
            values.add(BubbleValue(bubbleValue))
        }
    }

    override fun update(scale: Float) {
        for (value in values) {
            value.update(scale)
        }
    }

    override fun finish() {
        for (value in values) {
            value.finish()
        }
    }

    override fun setAxisXBottom(axis: Axis?) {
        this.axisXBottom = axis
    }

    override fun getAxisXBottom(): Axis? {
        return this.axisXBottom
    }

    override fun setAxisYLeft(axis: Axis?) {
        this.axisYLeft = axis
    }

    override fun getAxisYLeft(): Axis? {
        return this.axisYLeft
    }

    override fun setAxisXTop(axis: Axis?) {
        this.axisXTop = axis
    }

    override fun getAxisXTop(): Axis? {
        return this.axisXTop
    }

    override fun setAxisYRight(axis: Axis?) {
        this.axisYRight = axis
    }

    override fun getAxisYRight(): Axis? {
        return this.axisYRight
    }

    override fun setValueLabelsTextColor(color: Int) {
        this.valueLabelTextColor = color
    }

    override fun getValueLabelTextColor(): Int {
        return this.valueLabelTextColor
    }

    override fun setValueLabelTextSize(size: Int) {
        this.valueLabelTextSize = size
    }

    override fun getValueLabelTextSize(): Int {
        return this.valueLabelTextSize
    }

    override fun getValueLabelTypeface(): Typeface? {
        return this.valueLabelTypeface
    }

    override fun setValueLabelTypeface(typeface: Typeface?) {
        this.valueLabelTypeface = typeface
    }

    override fun setValueLabelBackgroundEnabled(isEnabled: Boolean) {
        this.isValueLabelBackgroundEnabled = isEnabled
    }

    override fun isValueLabelBackgroundEnabled(): Boolean {
        return this.isValueLabelBackgroundEnabled
    }

    override fun setValueLabelBackgroundAuto(isValueLabelBackgroundAuto: Boolean) {
        this.isValueLabelBackgroundAuto = isValueLabelBackgroundAuto
    }

    override fun isValueLabelBackgroundAuto(): Boolean {
        return this.isValueLabelBackgroundAuto
    }

    override fun setValueLabelBackgroundColor(valueLabelBackgroundColor: Int) {
        this.valueLabelBackgroundColor = valueLabelBackgroundColor
    }

    override fun getValueLabelBackgroundColor(): Int {
        return this.valueLabelTextColor
    }

    private fun setBubbleValues(values: MutableList<BubbleValue>?): BubbleChartData {
        if (null == values) {
            this.values = ArrayList()
        } else {
            this.values = values
        }
        return this
    }

    fun hasLabels(): Boolean {
        return hasLabels
    }

    fun setHasLabels(hasLabels: Boolean): BubbleChartData {
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

    fun setHasLabelsOnlyForSelected(hasLabelsOnlyForSelected: Boolean): BubbleChartData {
        this.hasLabelsOnlyForSelected = hasLabelsOnlyForSelected
        if (hasLabelsOnlyForSelected) {
            hasLabels = false
        }
        return this
    }

    fun setFormatter(formatter: BubbleChartValueFormatter?): BubbleChartData {
        if (null != formatter) {
            this.formatter = formatter
        }
        return this
    }

    companion object {
        const val DEFAULT_MIN_BUBBLE_RADIUS_DP = 6
        const val DEFAULT_BUBBLE_SCALE = 1f
        @JvmStatic
        fun generateDummyData(): BubbleChartData {
            val numValues = 4
            val data = BubbleChartData()
            val values: MutableList<BubbleValue> = ArrayList(numValues)
            values.add(BubbleValue(0f, 20f, 15000f))
            values.add(BubbleValue(3f, 22f, 20000f))
            values.add(BubbleValue(5f, 25f, 5000f))
            values.add(BubbleValue(7f, 30f, 30000f))
            values.add(BubbleValue(11f, 22f, 10f))
            data.values = values
            return data
        }
    }
}