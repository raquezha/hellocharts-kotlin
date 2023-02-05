package lecho.lib.hellocharts.model

import android.graphics.Typeface

/**
 * Data model for LineChartView.
 */
@Suppress("unused")
class LineChartData : AbstractChartData {

    @JvmField
    var lines: MutableList<Line> = ArrayList()

    /**
     * @see .setBaseValue
     */
    var baseValue = DEFAULT_BASE_VALUE

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