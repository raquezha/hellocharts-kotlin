package lecho.lib.hellocharts.listener

import lecho.lib.hellocharts.model.SliceValue

interface PieChartOnValueSelectListener : OnValueDeselectListener {
    fun onValueSelected(arcIndex: Int, value: SliceValue)
}