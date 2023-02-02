package lecho.lib.hellocharts.listener

import lecho.lib.hellocharts.model.PointValue

interface LineChartOnValueSelectListener : OnValueDeselectListener {
    fun onValueSelected(lineIndex: Int, pointIndex: Int, value: PointValue)
}