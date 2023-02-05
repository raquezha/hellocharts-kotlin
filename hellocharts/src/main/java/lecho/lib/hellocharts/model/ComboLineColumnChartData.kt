@file:Suppress("unused")

package lecho.lib.hellocharts.model

import android.graphics.Typeface

/**
 * Data model for combo line-column chart. It uses ColumnChartData and LineChartData internally.
 */
class ComboLineColumnChartData : AbstractChartData {
    var columnChartData: ColumnChartData
    var lineChartData: LineChartData

    constructor() {
        columnChartData = ColumnChartData()
        lineChartData = LineChartData()
    }

    constructor(columnChartData: ColumnChartData, lineChartData: LineChartData) {
        this.columnChartData = columnChartData
        this.lineChartData = lineChartData
    }

    constructor(data: ComboLineColumnChartData) : super(data) {
        columnChartData = ColumnChartData(data.columnChartData)
        lineChartData = LineChartData(data.lineChartData)
    }

    override fun update(scale: Float) {
        columnChartData.update(scale)
        lineChartData.update(scale)
    }

    override fun finish() {
        columnChartData.finish()
        lineChartData.finish()
    }

    override fun setAxisXBottom(axis: Axis?) {
        this.axisXBottom = axis
    }

    override fun getAxisXBottom(): Axis? {
        return this.axisXBottom
    }

    override fun setAxisYLeft(axis: Axis?) {
        this.axisYLeft = axis
    }

    override fun getAxisYLeft(): Axis? {
        return this.axisYLeft
    }

    override fun setAxisXTop(axis: Axis?) {
        this.axisXTop = axis
    }

    override fun getAxisXTop(): Axis? {
        return this.axisXTop
    }

    override fun setAxisYRight(axis: Axis?) {
        this.axisYRight = axis
    }

    override fun getAxisYRight(): Axis? {
        return this.axisYRight
    }

    override fun setValueLabelsTextColor(color: Int) {
        this.valueLabelTextColor = color
    }

    override fun getValueLabelTextColor(): Int {
        return this.valueLabelTextColor
    }

    override fun setValueLabelTextSize(size: Int) {
        this.valueLabelTextSize = size
    }

    override fun getValueLabelTextSize(): Int {
        return this.valueLabelTextSize
    }

    override fun getValueLabelTypeface(): Typeface? {
        return this.valueLabelTypeface
    }

    override fun setValueLabelTypeface(typeface: Typeface?) {
        this.valueLabelTypeface = typeface
    }

    override fun setValueLabelBackgroundEnabled(isEnabled: Boolean) {
        this.isValueLabelBackgroundEnabled = isEnabled
    }

    override fun isValueLabelBackgroundEnabled(): Boolean {
        return this.isValueLabelBackgroundEnabled
    }

    override fun setValueLabelBackgroundAuto(isValueLabelBackgroundAuto: Boolean) {
        this.isValueLabelBackgroundAuto = isValueLabelBackgroundAuto
    }

    override fun isValueLabelBackgroundAuto(): Boolean {
        return this.isValueLabelBackgroundAuto
    }

    override fun setValueLabelBackgroundColor(valueLabelBackgroundColor: Int) {
        this.valueLabelBackgroundColor = valueLabelBackgroundColor
    }

    override fun getValueLabelBackgroundColor(): Int {
        return this.valueLabelTextColor
    }

    companion object {
        @JvmStatic
        fun generateDummyData(): ComboLineColumnChartData {
            val data = ComboLineColumnChartData()
            data.columnChartData = ColumnChartData.generateDummyData()
            data.lineChartData = LineChartData.generateDummyData()
            return data
        }
    }
}