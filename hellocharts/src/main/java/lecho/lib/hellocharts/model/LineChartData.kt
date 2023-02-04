package lecho.lib.hellocharts.model

/**
 * Data model for LineChartView.
 */
@Suppress("unused")
class LineChartData : AbstractChartData {
    private var lines: MutableList<Line> = ArrayList()

    /**
     * @see .setBaseValue
     */
    var baseValue = DEFAULT_BASE_VALUE
        private set

    constructor()
    constructor(lines: MutableList<Line>?) {
        setLines(lines)
    }

    /**
     * Copy constructor to perform deep copy of chart data.
     */
    constructor(data: LineChartData) : super(data) {
        baseValue = data.baseValue
        for (line in data.lines) {
            lines.add(Line(line))
        }
    }

    override fun update(scale: Float) {
        for (line in lines) {
            line.update(scale)
        }
    }

    override fun finish() {
        for (line in lines) {
            line.finish()
        }
    }

    fun getLines(): List<Line> {
        return lines
    }

    fun setLines(lines: MutableList<Line>?): LineChartData {
        if (null == lines) {
            this.lines = ArrayList()
        } else {
            this.lines = lines
        }
        return this
    }

    /**
     * Set value below which values will be drawn as negative, important attribute for drawing filled area charts, by
     * default 0.
     */
    fun setBaseValue(baseValue: Float): LineChartData {
        this.baseValue = baseValue
        return this
    }

    companion object {
        const val DEFAULT_BASE_VALUE = 0.0f
        @JvmStatic
        fun generateDummyData(): LineChartData {
            val numValues = 4
            val data = LineChartData()
            val values: MutableList<PointValue> = ArrayList(numValues)
            values.add(PointValue(0f, 2f))
            values.add(PointValue(1f, 4f))
            values.add(PointValue(2f, 3f))
            values.add(PointValue(3f, 4f))
            val line = Line(values)
            val lines: MutableList<Line> = ArrayList(1)
            lines.add(line)
            data.setLines(lines)
            return data
        }
    }
}