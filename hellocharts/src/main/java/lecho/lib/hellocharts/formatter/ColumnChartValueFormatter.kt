package lecho.lib.hellocharts.formatter

import lecho.lib.hellocharts.model.SubcolumnValue

interface ColumnChartValueFormatter {
    fun formatChartValue(formattedValue: CharArray, value: SubcolumnValue): Int
}