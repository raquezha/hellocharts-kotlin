package lecho.lib.hellocharts.listener

import lecho.lib.hellocharts.model.PointValue
import lecho.lib.hellocharts.model.SubcolumnValue

class DummyCompoLineColumnChartOnValueSelectListener : ComboLineColumnChartOnValueSelectListener {
    override fun onColumnValueSelected(
        columnIndex: Int,
        subColumnIndex: Int,
        value: SubcolumnValue
    ) {}

    override fun onPointValueSelected(lineIndex: Int, pointIndex: Int, value: PointValue) {}
    override fun onValueDeselected() {}
}