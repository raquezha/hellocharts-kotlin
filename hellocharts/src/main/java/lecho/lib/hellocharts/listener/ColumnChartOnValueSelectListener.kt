package lecho.lib.hellocharts.listener

import lecho.lib.hellocharts.model.SubcolumnValue
import lecho.lib.hellocharts.model.TouchCoordinates

interface ColumnChartOnValueSelectListener : OnValueDeselectListener {

    fun onValueSelected(
        columnIndex: Int,
        subColumnIndex: Int,
        value: SubcolumnValue,
        touchCoordinates: TouchCoordinates?
    )
}