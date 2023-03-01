package lecho.lib.hellocharts.listener

import lecho.lib.hellocharts.model.PointValue
import lecho.lib.hellocharts.model.TouchCoordinates

class DummyLineChartOnValueSelectListener : LineChartOnValueSelectListener {
    override fun onValueSelected(
        lineIndex: Int,
        pointIndex: Int,
        value: PointValue,
        touchCoordinates: TouchCoordinates?
    ) {}
    override fun onValueDeselected() {}
}