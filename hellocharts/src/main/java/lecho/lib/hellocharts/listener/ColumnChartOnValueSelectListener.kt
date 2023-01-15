package lecho.lib.hellocharts.listener

import lecho.lib.hellocharts.model.SubcolumnValue

interface ColumnChartOnValueSelectListener : OnValueDeselectListener {
    fun onValueSelected(columnIndex: Int, subColumnIndex: Int, value: SubcolumnValue)
}