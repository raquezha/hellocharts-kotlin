package lecho.lib.hellocharts.model

import android.graphics.Color
import android.graphics.Typeface
import lecho.lib.hellocharts.util.ChartUtils
import lecho.lib.hellocharts.util.ChartUtils.darkenColor

/**
 * Base class for most chart data models.
 */
abstract class AbstractChartData : ChartData {

    @JvmField
    protected var axisXBottom: Axis? = null

    @JvmField
    protected var axisYLeft: Axis? = null

    @JvmField
    protected var axisXTop: Axis? = null

    @JvmField
    protected var axisYRight: Axis? = null

    @JvmField
    protected var valueLabelTextColor = Color.WHITE

    @JvmField
    protected var valueLabelTextSize = DEFAULT_TEXT_SIZE_SP

    @JvmField
    protected var valueLabelTypeface: Typeface? = null

    /**
     * If true each value label will have background rectangle
     */
    @JvmField
    protected var isValueLabelBackgroundEnabled = true

    /**
     * If true and [.isValueLabelBackgroundEnabled] is true each label will have background rectangle and that
     * rectangle will be filled with color specified for given value.
     */
    @JvmField
    protected var isValueLabelBackgroundAuto = true

    /**
     * If [.isValueLabelBackgroundEnabled] is true and [.isValueLabelBackgroundAuto] is false each label
     * will have background rectangle and that rectangle will be filled with this color. Helpful if you want all labels
     * to have the same background color.
     */
    @JvmField
    protected var valueLabelBackgroundColor = darkenColor(ChartUtils.DEFAULT_DARKEN_COLOR)

    constructor()

    /**
     * Copy constructor for deep copy.
     *
     * @param data the abstract data
     */
    constructor(data: AbstractChartData) {
        if (null != data.axisXBottom) {
            axisXBottom = Axis(data.axisXBottom!!)
        }
        if (null != data.axisXTop) {
            axisXTop = Axis(data.axisXTop!!)
        }
        if (null != data.axisYLeft) {
            axisYLeft = Axis(data.axisYLeft!!)
        }
        if (null != data.axisYRight) {
            axisYRight = Axis(data.axisYRight!!)
        }
        valueLabelTextColor = data.valueLabelTextColor
        valueLabelTextSize = data.valueLabelTextSize
        valueLabelTypeface = data.valueLabelTypeface
        isValueLabelBackgroundEnabled = data.isValueLabelBackgroundEnabled
        isValueLabelBackgroundAuto = data.isValueLabelBackgroundAuto
        valueLabelBackgroundColor = data.valueLabelBackgroundColor
    }
//
//    override fun getAxisXBottom(): Axis? {
//        return axisXBottom
//    }
//
//    override fun setAxisXBottom(axis: Axis?) {
//        axisXBottom = axis
//    }
//
//    override fun getAxisYLeft(): Axis? {
//        return axisYLeft
//    }
//
//    override fun setAxisYLeft(axis: Axis?) {
//        axisYLeft = axis
//    }
//
//    override fun getAxisXTop(): Axis? {
//        return axisXTop
//    }
//
//    override fun setAxisXTop(axis: Axis?) {
//        axisXTop = axis
//    }
//
//    override fun getAxisYRight(): Axis? {
//        return axisYRight
//    }
//
//    override fun setAxisYRight(axis: Axis?) {
//        axisYRight = axis
//    }
//
//    override fun getValueLabelTextColor(): Int {
//        return valueLabelTextColor
//    }
//
//    override fun setValueLabelsTextColor(color: Int) {
//        this.valueLabelTextColor = color
//    }
//
//    override fun getValueLabelTextSize(): Int {
//        return valueLabelTextSize
//    }
//
//    override fun setValueLabelTextSize(size: Int) {
//        this.valueLabelTextSize = size
//    }
//
//    override fun getValueLabelTypeface(): Typeface? {
//        return valueLabelTypeface
//    }
//
//    override fun setValueLabelTypeface(typeface: Typeface?) {
//        valueLabelTypeface = typeface
//    }
//
//    override fun isValueLabelBackgroundEnabled(): Boolean {
//        return isValueLabelBackgroundEnabled
//    }
//
//    override fun setValueLabelBackgroundEnabled(isEnabled: Boolean) {
//        this.isValueLabelBackgroundEnabled = isEnabled
//    }
//
//    override fun isValueLabelBackgroundAuto(): Boolean {
//        return isValueLabelBackgroundAuto
//    }
//
//    override fun setValueLabelBackgroundAuto(isValueLabelBackgroundAuto: Boolean) {
//        this.isValueLabelBackgroundAuto = isValueLabelBackgroundAuto
//    }
//
//    override fun getValueLabelBackgroundColor(): Int {
//        return valueLabelBackgroundColor
//    }
//
//    override fun setValueLabelBackgroundColor(valueLabelBackgroundColor: Int) {
//        this.valueLabelBackgroundColor = valueLabelBackgroundColor
//    }

    companion object {
        const val DEFAULT_TEXT_SIZE_SP = 12
    }
}