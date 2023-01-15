package lecho.lib.hellocharts.formatter

import lecho.lib.hellocharts.model.SliceValue

interface PieChartValueFormatter {
    fun formatChartValue(formattedValue: CharArray, value: SliceValue): Int
}