package lecho.lib.hellocharts.listener

import lecho.lib.hellocharts.model.SubcolumnValue
import lecho.lib.hellocharts.model.TouchCoordinates

class DummyColumnChartOnValueSelectListener : ColumnChartOnValueSelectListener {
    override fun onValueSelected(
        columnIndex: Int,
        subColumnIndex: Int,
        value: SubcolumnValue,
        touchCoordinates: TouchCoordinates?
    ) {}

    override fun onValueDeselected() {}
}