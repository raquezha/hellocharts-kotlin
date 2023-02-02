package lecho.lib.hellocharts.listener

import lecho.lib.hellocharts.model.SliceValue

class DummyPieChartOnValueSelectListener : PieChartOnValueSelectListener {
    override fun onValueSelected(arcIndex: Int, value: SliceValue) {}
    override fun onValueDeselected() {}
}