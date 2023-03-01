package lecho.lib.hellocharts.listener

import lecho.lib.hellocharts.model.PointValue
import lecho.lib.hellocharts.model.TouchCoordinates

interface LineChartOnValueSelectListener : OnValueDeselectListener {

    fun onValueSelected(
        lineIndex: Int,
        pointIndex: Int,
        value: PointValue,
        touchCoordinates: TouchCoordinates?
    )
}