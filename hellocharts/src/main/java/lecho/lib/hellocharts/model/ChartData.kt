package lecho.lib.hellocharts.model

import android.graphics.Typeface

/**
 * Base interface for all chart data models.
 */
@Suppress("unused")
interface ChartData {
    /**
     * Updates data by scale during animation.
     *
     * @param scale value from 0 to 1.0
     */
    fun update(scale: Float)

    /**
     * Inform data that animation finished(data should be update with scale 1.0f).
     */
    fun finish()

    /**
     * Set horizontal axis at the bottom of the chart. Pass null to remove that axis.
     *
     * @param axis the X axis
     */
    fun setAxisXBottom(axis: Axis?)

    fun getAxisXBottom(): Axis?

    /**
     * Set vertical axis on the left of the chart. Pass null to remove that axis.
     *
     * @param axis the Y axis
     */
    fun setAxisYLeft(axis: Axis?)
    fun getAxisYLeft(): Axis?


    /**
     * Set horizontal axis at the top of the chart. Pass null to remove that axis.
     *
     * @param axis the X axis
     */
    fun setAxisXTop(axis: Axis?)

    fun getAxisXTop(): Axis?

    /**
     * Set vertical axis on the right of the chart. Pass null to remove that axis.
     *
     * @param axis the Y Axis
     */
    fun setAxisYRight(axis: Axis?)

    fun getAxisYRight(): Axis?

    /**
     * Set value label text color, by default Color.WHITE.
     */
    fun setValueLabelsTextColor(color: Int)

    fun getValueLabelTextColor(): Int

    /**
     * Returns text size for value label in SP units.
     */
    fun setValueLabelTextSize(size: Int)

    /**
     * Set text size for value label in SP units.
     */
    fun getValueLabelTextSize(): Int

    /**
     * Returns Typeface for value labels.
     *
     * @return Typeface or null if Typeface is not set.
     */
    fun getValueLabelTypeface(): Typeface?

    /**
     * Set Typeface for all values labels.
     *
     * @param typeface params
     */
    fun setValueLabelTypeface(typeface: Typeface?)

    /**
     * Set whether labels should have rectangle background. Default is true.
     */
    fun setValueLabelBackgroundEnabled(isEnabled: Boolean)

    fun isValueLabelBackgroundEnabled(): Boolean


    /**
     * Set false if you want to set custom color for all value labels. Default is true.
     */
    fun setValueLabelBackgroundAuto(isValueLabelBackgroundAuto: Boolean)

    fun isValueLabelBackgroundAuto(): Boolean

    /**
     * Set value labels background. This value is used only if isValueLabelBackgroundAuto returns false. Default is
     * green.
     */
    fun setValueLabelBackgroundColor(valueLabelBackgroundColor: Int)

    fun getValueLabelBackgroundColor(): Int
}