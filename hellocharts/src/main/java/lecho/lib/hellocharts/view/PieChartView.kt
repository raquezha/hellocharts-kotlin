package lecho.lib.hellocharts.view

import android.content.Context
import android.graphics.RectF
import android.util.AttributeSet
import androidx.core.view.ViewCompat
import lecho.lib.hellocharts.animation.PieChartRotationAnimator
import lecho.lib.hellocharts.animation.PieChartRotationAnimatorV14
import lecho.lib.hellocharts.computator.ChartComputator
import lecho.lib.hellocharts.gesture.ChartTouchHandler
import lecho.lib.hellocharts.gesture.PieChartTouchHandler
import lecho.lib.hellocharts.listener.DummyPieChartOnValueSelectListener
import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener
import lecho.lib.hellocharts.model.ChartData
import lecho.lib.hellocharts.model.PieChartData
import lecho.lib.hellocharts.model.PieChartData.Companion.generateDummyData
import lecho.lib.hellocharts.model.SelectedValue
import lecho.lib.hellocharts.model.SliceValue
import lecho.lib.hellocharts.provider.PieChartDataProvider
import lecho.lib.hellocharts.renderer.AxesRenderer
import lecho.lib.hellocharts.renderer.PieChartRenderer

/**
 * PieChart is a little different than others charts. It doesn't have axes.
 * It doesn't support viewport so changing viewport wont work. Instead it support "Circle Oval".
 * Pinch-to-Zoom and double tap zoom wont work either. Instead of scroll there is chart rotation
 * if isChartRotationEnabled is set to true. PieChart looks the best when it has the same width and
 * height, drawing chart on rectangle with proportions other than 1:1 will left some empty spaces.
 *
 * @author Leszek Wach
 */
@Suppress("unused")
open class PieChartView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : AbstractChartView(context, attrs, defStyle), PieChartDataProvider {

    @JvmField
    var data: PieChartData? = null

    @JvmField
    var onValueTouchListener: PieChartOnValueSelectListener =
        DummyPieChartOnValueSelectListener()

    @JvmField
    var pieChartRenderer: PieChartRenderer

    @JvmField
    var rotationAnimator: PieChartRotationAnimator

    final override var pieChartData: PieChartData
        get() = data!!
        set(data) {
            this.data = data
            super.onChartDataChange()
        }

    init {
        pieChartRenderer = PieChartRenderer(context, this, this)
        touchHandler = PieChartTouchHandler(context, this)
        setChartRenderer(pieChartRenderer)
        rotationAnimator = PieChartRotationAnimatorV14(this)
        pieChartData = generateDummyData()
    }


    override fun getChartData(): ChartData {
        return data!!
    }

    override fun callTouchListener() {
        val selectedValue = chartRenderer!!.getSelectedValue()
        if (selectedValue.isSet) {
            val sliceValue = data!!.getValues()[selectedValue.firstIndex]
            onValueTouchListener.onValueSelected(selectedValue.firstIndex, sliceValue)
        } else {
            onValueTouchListener.onValueDeselected()
        }
    }

    fun getOnValueTouchListener(): PieChartOnValueSelectListener {
        return onValueTouchListener
    }

    fun setOnValueTouchListener(touchListener: PieChartOnValueSelectListener?) {
        if (null != touchListener) {
            onValueTouchListener = touchListener
        }
    }

    var circleOval: RectF?
        /**
         * Returns rectangle that will constraint pie chart area.
         */
        get() = pieChartRenderer.circleOval
        /**
         * Use this to change pie chart area. Because by default CircleOval is calculated onSizeChanged() you must call this
         * method after size of PieChartView is calculated. In most cases it will probably be easier to use
         * [.setCircleFillRatio] to change chart area or just use view padding.
         */
        set(originCircleOval) {
            pieChartRenderer.circleOval = originCircleOval!!
            ViewCompat.postInvalidateOnAnimation(this)
        }
    val chartRotation: Int
        /**
         * Returns pie chart rotation, 0 rotation means that 0 degrees is at 3 o'clock.
         * Don't confuse with View.getRotation
         *
         * @return rotation
         */
        get() = pieChartRenderer.chartRotation

    /**
     * Set pie chart rotation. Don't confuse with View.getRotation
     *
     * @param rotation rotation
     */
    fun setChartRotation(rotation: Int, isAnimated: Boolean) {
        if (isAnimated) {
            rotationAnimator.cancelAnimation()
            rotationAnimator.startAnimation(
                pieChartRenderer.chartRotation.toFloat(),
                rotation.toFloat()
            )
        } else {
            pieChartRenderer.chartRotation = rotation
        }
        ViewCompat.postInvalidateOnAnimation(this)
    }

    var isChartRotationEnabled: Boolean
        get() = if (touchHandler is PieChartTouchHandler) {
            (touchHandler as PieChartTouchHandler).isRotationEnabled
        } else {
            false
        }
        /**
         * Set false if you don't wont the chart to be rotated by touch gesture. Rotating programmatically will still work.
         *
         * @param isRotationEnabled isRotationEnabled
         */
        set(isRotationEnabled) {
            if (touchHandler is PieChartTouchHandler) {
                (touchHandler as PieChartTouchHandler).isRotationEnabled = isRotationEnabled
            }
        }

    /**
     * Returns SliceValue that is under given angle, selectedValue (if not null) will be hold slice index.
     */
    fun getValueForAngle(angle: Int, selectedValue: SelectedValue?): SliceValue? {
        return pieChartRenderer.getValueForAngle(angle, selectedValue)
    }

    var circleFillRatio: Float
        /**
         * @see .setCircleFillRatio
         */
        get() = pieChartRenderer.getCircleFillRatio()
        /**
         * Set how much of view area should be taken by chart circle. Value should be between 0 and 1. Default is 1 so
         * circle will have radius equals min(View.width, View.height).
         */
        set(fillRatio) {
            pieChartRenderer.setCircleFillRatio(fillRatio)
            ViewCompat.postInvalidateOnAnimation(this)
        }

    override fun setChartData(chartData: ChartData) {
        this.chartData = chartData
    }

    override fun setAxesRenderer(axesRenderer: AxesRenderer) {
        this.axesRenderer = axesRenderer
    }

    override fun setChartComputator(chartComputator: ChartComputator) {
        this.chartComputator = chartComputator
    }

    override fun setTouchHandler(touchHandler: ChartTouchHandler) {
        this.touchHandler = touchHandler
    }

    override fun getInteractive(): Boolean {
        return isInteractive
    }

    override fun setContainerScrollEnabled(isEnabled: Boolean) {
        isContainerScrollEnabled = isEnabled
    }
}