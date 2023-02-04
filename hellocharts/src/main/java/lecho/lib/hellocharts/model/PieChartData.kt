package lecho.lib.hellocharts.model

import android.graphics.Color
import android.graphics.Typeface
import lecho.lib.hellocharts.formatter.PieChartValueFormatter
import lecho.lib.hellocharts.formatter.SimplePieChartValueFormatter

/**
 * Data for PieChart, by default it doesn't have axes.
 */
@Suppress("unused")
class PieChartData : AbstractChartData {

    var centerText1FontSize = DEFAULT_CENTER_TEXT1_SIZE_SP

    var centerText2FontSize = DEFAULT_CENTER_TEXT2_SIZE_SP

    var centerCircleScale = DEFAULT_CENTER_CIRCLE_SCALE

    var slicesSpacing = DEFAULT_SLICE_SPACING_DP

    var formatter: PieChartValueFormatter = SimplePieChartValueFormatter()

    var hasLabels = false

    var hasLabelsOnlyForSelected = false

    var hasLabelsOutside = false

    var hasCenterCircle = false

    var centerCircleColor = Color.TRANSPARENT

    var centerText1Color = Color.BLACK

    var centerText1Typeface: Typeface? = null

    var centerText1: String? = null

    var centerText2Color = Color.BLACK

    var centerText2Typeface: Typeface? = null

    var centerText2: String? = null

    @JvmField
    var values: MutableList<SliceValue> = ArrayList()

    constructor() {
        setAxisXBottom(null)
        setAxisYLeft(null)
    }

    constructor(values: MutableList<SliceValue>?) {
        setValues(values)
        // Empty axes. Pie chart don't need axes.
        setAxisXBottom(null)
        setAxisYLeft(null)
    }

    constructor(data: PieChartData) : super(data) {
        formatter = data.formatter
        hasLabels = data.hasLabels
        hasLabelsOnlyForSelected = data.hasLabelsOnlyForSelected
        hasLabelsOutside = data.hasLabelsOutside
        hasCenterCircle = data.hasCenterCircle
        centerCircleColor = data.centerCircleColor
        centerCircleScale = data.centerCircleScale
        centerText1Color = data.centerText1Color
        centerText1FontSize = data.centerText1FontSize
        centerText1Typeface = data.centerText1Typeface
        centerText1 = data.centerText1
        centerText2Color = data.centerText2Color
        centerText2FontSize = data.centerText2FontSize
        centerText2Typeface = data.centerText2Typeface
        centerText2 = data.centerText2
        slicesSpacing = data.slicesSpacing
        for (sliceValue in data.values) {
            values.add(SliceValue(sliceValue.value))
        }
    }

    override fun update(scale: Float) {
        for (value in values) {
            value.update(scale)
        }
    }

    override fun finish() {
        for (value in values) {
            value.finish()
        }
    }

    /**
     * PieChart does not support axes so method call will be ignored
     */
    override fun setAxisXBottom(axis: Axis?) {
        super.setAxisXBottom(null)
    }

    /**
     * PieChart does not support axes so method call will be ignored
     */
    override fun setAxisYLeft(axis: Axis?) {
        super.setAxisYLeft(null)
    }

    fun getValues(): List<SliceValue> {
        return values
    }

    fun setValues(values: MutableList<SliceValue>?): PieChartData {
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

    fun setHasLabels(hasLabels: Boolean): PieChartData {
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
     * isValueSelectionEnabled set to true
     */
    fun setHasLabelsOnlyForSelected(hasLabelsOnlyForSelected: Boolean): PieChartData {
        this.hasLabelsOnlyForSelected = hasLabelsOnlyForSelected
        if (hasLabelsOnlyForSelected) {
            hasLabels = false
        }
        return this
    }

    fun hasLabelsOutside(): Boolean {
        return hasLabelsOutside
    }

    /**
     * Set if labels should be drawn inside circle(false) or outside(true).
     * By default false. If you set it to true you should also change chart fill ration
     * using [lecho.lib.hellocharts.view.PieChartView.setCircleFillRatio].
     * This flag is used only if you also set hasLabels or hasLabelsOnlyForSelected flags.
     */
    fun setHasLabelsOutside(hasLabelsOutside: Boolean): PieChartData {
        this.hasLabelsOutside = hasLabelsOutside
        return this
    }

    fun hasCenterCircle(): Boolean {
        return hasCenterCircle
    }

    fun setHasCenterCircle(hasCenterCircle: Boolean): PieChartData {
        this.hasCenterCircle = hasCenterCircle
        return this
    }

    fun setCenterCircleColor(centerCircleColor: Int): PieChartData {
        this.centerCircleColor = centerCircleColor
        return this
    }

    fun setCenterCircleScale(centerCircleScale: Float): PieChartData {
        this.centerCircleScale = centerCircleScale
        return this
    }

    fun setCenterText1Color(centerText1Color: Int): PieChartData {
        this.centerText1Color = centerText1Color
        return this
    }

    fun setCenterText1FontSize(centerText1FontSize: Int): PieChartData {
        this.centerText1FontSize = centerText1FontSize
        return this
    }

    fun setCenterText1Typeface(text1Typeface: Typeface?): PieChartData {
        centerText1Typeface = text1Typeface
        return this
    }

    fun setCenterText1(centerText1: String?): PieChartData {
        this.centerText1 = centerText1
        return this
    }

    /**
     * Note that centerText2 will be drawn only if centerText1 is not empty/null.
     */
    fun setCenterText2(centerText2: String?): PieChartData {
        this.centerText2 = centerText2
        return this
    }

    fun setCenterText2Color(centerText2Color: Int): PieChartData {
        this.centerText2Color = centerText2Color
        return this
    }

    fun setCenterText2FontSize(centerText2FontSize: Int): PieChartData {
        this.centerText2FontSize = centerText2FontSize
        return this
    }

    fun setCenterText2Typeface(text2Typeface: Typeface?): PieChartData {
        centerText2Typeface = text2Typeface
        return this
    }

    fun setSlicesSpacing(sliceSpacing: Int): PieChartData {
        slicesSpacing = sliceSpacing
        return this
    }

    fun setFormatter(formatter: PieChartValueFormatter?): PieChartData {
        if (null != formatter) {
            this.formatter = formatter
        }
        return this
    }

    companion object {
        const val DEFAULT_CENTER_TEXT1_SIZE_SP = 42
        const val DEFAULT_CENTER_TEXT2_SIZE_SP = 16
        const val DEFAULT_CENTER_CIRCLE_SCALE = 0.6f
        private const val DEFAULT_SLICE_SPACING_DP = 2
        @JvmStatic
        fun generateDummyData(): PieChartData {
            val numValues = 4
            val data = PieChartData()
            val values: MutableList<SliceValue> = ArrayList(numValues)
            values.add(SliceValue(40f))
            values.add(SliceValue(20f))
            values.add(SliceValue(30f))
            values.add(SliceValue(50f))
            data.setValues(values)
            return data
        }
    }
}