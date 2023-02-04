@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package lecho.lib.hellocharts.model

/**
 * Data model for column chart. Note: you can set X value for columns or sub-columns,
 * columns are by default indexed from 0 to numOfColumns-1 and column index is used
 * as column X value, so first column has X value 0, second column has X value 1 etc. If you want to
 * display AxisValue for given column you should initialize AxisValue with X value of that column.
 */
class ColumnChartData : AbstractChartData {
    var fillRatio = DEFAULT_FILL_RATIO
        private set

    /**
     * @see setBaseValue
     */
    var baseValue = DEFAULT_BASE_VALUE

    var columns: MutableList<Column> = ArrayList()

    var isStacked = false

    var isRoundedCorner: Boolean = false

    constructor(columns: MutableList<Column>? = null) {
        setColumns(columns)
    }

    /**
     * Copy constructor for deep copy.
     */
    constructor(data: ColumnChartData) : super(data) {
        isStacked = data.isStacked
        fillRatio = data.fillRatio
        isRoundedCorner = data.isRoundedCorner
        for (column in data.columns) {
            columns.add(Column(column))
        }
    }

    override fun update(scale: Float) {
        for (column in columns) {
            column.update(scale)
        }
    }

    override fun finish() {
        for (column in columns) {
            column.finish()
        }
    }

    fun setColumns(columns: MutableList<Column>?): ColumnChartData {
        if (null == columns) {
            this.columns = ArrayList()
        } else {
            this.columns = columns
        }
        return this
    }

    /**
     * Set fill ration for columns, value from 0 to 1, 1 means that there will be almost no free space between columns,
     * 0 means that columns will have minimum width(2px).
     *
     * @param fillRatio ration
     * @return column chart data
     */
    fun setFillRatio(fillRatio: Float): ColumnChartData {
        var ratio = fillRatio
        if (ratio < 0) {
            ratio = 0f
        }
        if (ratio > 1) {
            ratio = 1f
        }
        this.fillRatio = ratio
        return this
    }

    /**
     * Set value below which values will be drawn as negative, by default 0.
     */
    fun setBaseValue(baseValue: Float): ColumnChartData {
        this.baseValue = baseValue
        return this
    }

    companion object {
        const val DEFAULT_FILL_RATIO = 0.75f
        const val DEFAULT_BASE_VALUE = 0.0f

        @JvmStatic
        fun generateDummyData(): ColumnChartData {
            val numColumns = 4
            val data = ColumnChartData()
            val columns: MutableList<Column> = ArrayList(numColumns)
            var values: MutableList<SubcolumnValue>
            var column: Column
            for (i in 1..numColumns) {
                values = ArrayList(numColumns)
                values.add(SubcolumnValue(i.toFloat()))
                column = Column(values)
                columns.add(column)
            }
            data.setColumns(columns)
            return data
        }
    }
}