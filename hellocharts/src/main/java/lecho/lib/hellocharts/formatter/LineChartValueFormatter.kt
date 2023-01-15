package lecho.lib.hellocharts.formatter

import lecho.lib.hellocharts.model.PointValue

interface LineChartValueFormatter {
    fun formatChartValue(formattedValue: CharArray, value: PointValue): Int
}