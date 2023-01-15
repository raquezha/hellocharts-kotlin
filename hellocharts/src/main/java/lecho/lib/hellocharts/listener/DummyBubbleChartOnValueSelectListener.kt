package lecho.lib.hellocharts.listener

import lecho.lib.hellocharts.model.BubbleValue

class DummyBubbleChartOnValueSelectListener : BubbleChartOnValueSelectListener {
    override fun onValueSelected(bubbleIndex: Int, value: BubbleValue) {}
    override fun onValueDeselected() {}
}