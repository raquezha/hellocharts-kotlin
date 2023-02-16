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
    var axisXBottom: Axis? = null

    @JvmField
    var axisYLeft: Axis? = null

    @JvmField
    var axisXTop: Axis? = null

    @JvmField
    var axisYRight: Axis? = null

    @JvmField
    var valueLabelTextColor = Color.WHITE

    @JvmField
    var valueLabelTextSize = DEFAULT_TEXT_SIZE_SP

    @JvmField
    var valueLabelTypeface: Typeface? = null

    /**
     * If true each value label will have background rectangle
     */
    @JvmField
    var isValueLabelBackgroundEnabled = true

    /**
     * If true and [.isValueLabelBackgroundEnabled] is true each label will have background rectangle and that
     * rectangle will be filled with color specified for given value.
     */
    @JvmField
    var isValueLabelBackgroundAuto = true

    /**
     * If [.isValueLabelBackgroundEnabled] is true and [.isValueLabelBackgroundAuto] is false each label
     * will have background rectangle and that rectangle will be filled with this color. Helpful if you want all labels
     * to have the same background color.
     */
    @JvmField
    var valueLabelBackgroundColor = darkenColor(ChartUtils.DEFAULT_DARKEN_COLOR)

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

    companion object {
        const val DEFAULT_TEXT_SIZE_SP = 12
    }
}