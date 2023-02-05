package lecho.lib.hellocharts.renderer

import android.content.Context
import lecho.lib.hellocharts.provider.ColumnChartDataProvider
import lecho.lib.hellocharts.provider.LineChartDataProvider
import lecho.lib.hellocharts.view.Chart

class ComboLineColumnChartRenderer(
    context: Context?, chart: Chart?, columnChartRenderer: ColumnChartRenderer?,
    lineChartRenderer: LineChartRenderer?
) : ComboChartRenderer(context, chart) {
    constructor(
        context: Context?, chart: Chart?, columnChartDataProvider: ColumnChartDataProvider?,
        lineChartDataProvider: LineChartDataProvider?
    ) : this(
        context, chart, ColumnChartRenderer(context, chart, columnChartDataProvider),
        LineChartRenderer(context, chart, lineChartDataProvider)
    )

    constructor(
        context: Context?, chart: Chart?, columnChartRenderer: ColumnChartRenderer?,
        lineChartDataProvider: LineChartDataProvider?
    ) : this(
        context,
        chart,
        columnChartRenderer,
        LineChartRenderer(context, chart, lineChartDataProvider)
    )

    constructor(
        context: Context?, chart: Chart?, columnChartDataProvider: ColumnChartDataProvider?,
        lineChartRenderer: LineChartRenderer?
    ) : this(
        context,
        chart,
        ColumnChartRenderer(context, chart, columnChartDataProvider),
        lineChartRenderer
    )

    init {
        renderers.add(columnChartRenderer)
        renderers.add(lineChartRenderer)
    }
}