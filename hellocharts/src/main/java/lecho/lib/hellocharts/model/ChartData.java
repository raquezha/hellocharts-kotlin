package lecho.lib.hellocharts.model;

import android.graphics.Typeface;

/**
 * Base interface for all chart data models.
 */
@SuppressWarnings("unused")
public interface ChartData {

    /**
     * Updates data by scale during animation.
     *
     * @param scale value from 0 to 1.0
     */
    void update(float scale);

    /**
     * Inform data that animation finished(data should be update with scale 1.0f).
     */
    void finish();

    /**
     * @see #setAxisXBottom(Axis)
     */
    Axis getAxisXBottom();

    /**
     * Set horizontal axis at the bottom of the chart. Pass null to remove that axis.
     *
     * @param axisX the X axis
     */
    void setAxisXBottom(Axis axisX);

    /**
     * @see #setAxisYLeft(Axis)
     */
    Axis getAxisYLeft();

    /**
     * Set vertical axis on the left of the chart. Pass null to remove that axis.
     *
     * @param axisY the Y axis
     */
    void setAxisYLeft(Axis axisY);

    /**
     * @see #setAxisXTop(Axis)
     */
    Axis getAxisXTop();

    /**
     * Set horizontal axis at the top of the chart. Pass null to remove that axis.
     *
     * @param axisX the X axis
     */
    void setAxisXTop(Axis axisX);

    /**
     * @see #setAxisYRight(Axis)
     */
    Axis getAxisYRight();

    /**
     * Set vertical axis on the right of the chart. Pass null to remove that axis.
     *
     * @param axisY the Y Axis
     */
    void setAxisYRight(Axis axisY);

    /**
     * Returns color used to draw value label text.
     */
    int getValueLabelTextColor();

    /**
     * Set value label text color, by default Color.WHITE.
     */
    void setValueLabelsTextColor(int labelsTextColor);

    /**
     * Returns text size for value label in SP units.
     */
    int getValueLabelTextSize();

    /**
     * Set text size for value label in SP units.
     */
    void setValueLabelTextSize(int labelsTextSize);

    /**
     * Returns Typeface for value labels.
     *
     * @return Typeface or null if Typeface is not set.
     */
    Typeface getValueLabelTypeface();

    /**
     * Set Typeface for all values labels.
     *
     * @param typeface params
     */
    void setValueLabelTypeface(Typeface typeface);

    /**
     * @see #setValueLabelBackgroundEnabled(boolean)
     */
    boolean isValueLabelBackgroundEnabled();

    /**
     * Set whether labels should have rectangle background. Default is true.
     */
    void setValueLabelBackgroundEnabled(boolean isValueLabelBackgroundEnabled);

    /**
     * @see #setValueLabelBackgroundAuto(boolean)
     */
    boolean isValueLabelBackgroundAuto();

    /**
     * Set false if you want to set custom color for all value labels. Default is true.
     */
    void setValueLabelBackgroundAuto(boolean isValueLabelBackgroundAuto);

    /**
     * @see #setValueLabelBackgroundColor(int)
     */
    int getValueLabelBackgroundColor();

    /**
     * Set value labels background. This value is used only if isValueLabelBackgroundAuto returns false. Default is
     * green.
     */
    void setValueLabelBackgroundColor(int valueLabelBackgroundColor);
}
