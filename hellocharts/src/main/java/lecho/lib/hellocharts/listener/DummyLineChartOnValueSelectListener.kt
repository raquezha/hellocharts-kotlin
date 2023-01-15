package lecho.lib.hellocharts.listener

import lecho.lib.hellocharts.model.PointValue

class DummyLineChartOnValueSelectListener : LineChartOnValueSelectListener {
    override fun onValueSelected(lineIndex: Int, pointIndex: Int, value: PointValue) {}
    override fun onValueDeselected() {}
}