package lecho.lib.hellocharts.listener

import lecho.lib.hellocharts.model.SubcolumnValue

class DummyColumnChartOnValueSelectListener : ColumnChartOnValueSelectListener {
    override fun onValueSelected(columnIndex: Int, subColumnIndex: Int, value: SubcolumnValue) {}
    override fun onValueDeselected() {}
}