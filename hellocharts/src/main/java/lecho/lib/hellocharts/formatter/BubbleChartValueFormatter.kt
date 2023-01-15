package lecho.lib.hellocharts.formatter

import lecho.lib.hellocharts.model.BubbleValue

interface BubbleChartValueFormatter {
    fun formatChartValue(formattedValue: CharArray, value: BubbleValue): Int
}