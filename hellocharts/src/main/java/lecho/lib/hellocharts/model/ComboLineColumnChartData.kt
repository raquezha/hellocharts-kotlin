@file:Suppress("unused")

package lecho.lib.hellocharts.model

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