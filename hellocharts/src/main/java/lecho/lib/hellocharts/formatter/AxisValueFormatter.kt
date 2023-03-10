package lecho.lib.hellocharts.formatter

import lecho.lib.hellocharts.model.AxisValue

interface AxisValueFormatter {
    /**
     * Formats AxisValue for manual(custom) axis. Result is stored in (output) formattedValue array. Method
     * returns number of chars of formatted value. The formatted value starts at index [formattedValue.length -
     * charsNumber] and ends at index [formattedValue.length-1].
     */
    fun formatValueForManualAxis(formattedValue: CharArray, axisValue: AxisValue): Int

    /**
     * Used only for auto-generated axes. If you are not going to use your implementation for aut-generated axes you can
     * skip implementation of this method and just return 0. SFormats values with given number of digits after
     * decimal separator. Result is stored in given array. Method returns number of chars for formatted value. The
     * formatted value starts at index [formattedValue.length - charsNumber] and ends at index [formattedValue
     * .length-1].
     */
    fun formatValueForAutoGeneratedAxis(
        formattedValue: CharArray,
        value: Float,
        autoDecimalDigits: Int
    ): Int
}