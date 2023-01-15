package lecho.lib.hellocharts.listener

import lecho.lib.hellocharts.model.PointValue
import lecho.lib.hellocharts.model.SubcolumnValue

interface ComboLineColumnChartOnValueSelectListener : OnValueDeselectListener {
    fun onColumnValueSelected(columnIndex: Int, subColumnIndex: Int, value: SubcolumnValue)
    fun onPointValueSelected(lineIndex: Int, pointIndex: Int, value: PointValue)
}