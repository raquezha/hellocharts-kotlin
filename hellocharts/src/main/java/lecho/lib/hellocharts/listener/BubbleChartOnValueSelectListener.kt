package lecho.lib.hellocharts.listener

import lecho.lib.hellocharts.model.BubbleValue

interface BubbleChartOnValueSelectListener : OnValueDeselectListener {
    fun onValueSelected(bubbleIndex: Int, value: BubbleValue)
}