@file:Suppress("unused")

package lecho.lib.hellocharts.model

import lecho.lib.hellocharts.formatter.ColumnChartValueFormatter
import lecho.lib.hellocharts.formatter.SimpleColumnChartValueFormatter
import java.util.Collections

/**
 * Single column for ColumnChart. One column can be divided into multiple sub-columns(ColumnValues) especially for
 * stacked ColumnChart.
 * Note: you can set X value for columns or sub-columns, columns are by default indexed from 0 to numOfColumns-1 and
 * column index is used as column X value, so first column has X value 0, second clumn has X value 1 etc.
 * If you want to display AxisValue for given column you should initialize AxisValue with X value of that column.
 */
class Column {
    private var hasLabels = false
    private var hasLabelsOnlyForSelected = false
    var formatter: ColumnChartValueFormatter = SimpleColumnChartValueFormatter()
        private set

    var values: MutableList<SubcolumnValue> = Collections.emptyList()

    constructor(values: MutableList<SubcolumnValue>?) {
        setValues(values)
    }

    constructor(column: Column) {
        hasLabels = column.hasLabels
        hasLabelsOnlyForSelected = column.hasLabelsOnlyForSelected
        formatter = column.formatter
        for (columnValue in column.values) {
            values.add(SubcolumnValue(columnValue))
        }
    }

    fun update(scale: Float) {
        for (value in values) {
            value.update(scale)
        }
    }

    fun finish() {
        for (value in values) {
            value.finish()
        }
    }

    fun setValues(values: MutableList<SubcolumnValue>?): Column {
        if (null == values) {
            this.values = ArrayList()
        } else {
            this.values = values
        }
        return this
    }

    fun hasLabels(): Boolean {
        return hasLabels
    }

    fun setHasLabels(hasLabels: Boolean): Column {
        this.hasLabels = hasLabels
        if (hasLabels) {
            hasLabelsOnlyForSelected = false
        }
        return this
    }

    /**
     * @see .setHasLabelsOnlyForSelected
     */
    fun hasLabelsOnlyForSelected(): Boolean {
        return hasLabelsOnlyForSelected
    }

    /**
     * Set true if you want to show value labels only for selected value, works best when chart has
     * isValueSelectionEnabled set to true {@link Chart#setValueSelectionEnabled(boolean)}.
     */
    fun setHasLabelsOnlyForSelected(hasLabelsOnlyForSelected: Boolean): Column {
        this.hasLabelsOnlyForSelected = hasLabelsOnlyForSelected
        if (hasLabelsOnlyForSelected) {
            hasLabels = false
        }
        return this
    }

    fun setFormatter(formatter: ColumnChartValueFormatter?): Column {
        if (null != formatter) {
            this.formatter = formatter
        }
        return this
    }
}