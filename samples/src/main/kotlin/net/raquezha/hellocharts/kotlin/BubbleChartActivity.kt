package net.raquezha.hellocharts.kotlin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.commit
import lecho.lib.hellocharts.gesture.ZoomType
import lecho.lib.hellocharts.listener.BubbleChartOnValueSelectListener
import lecho.lib.hellocharts.model.Axis
import lecho.lib.hellocharts.model.BubbleChartData
import lecho.lib.hellocharts.model.BubbleValue
import lecho.lib.hellocharts.model.ValueShape
import lecho.lib.hellocharts.util.ChartUtils
import net.raquezha.hellocharts.kotlin.databinding.FragmentBubbleChartBinding

import java.lang.Math.random
import kotlin.math.roundToInt

class BubbleChartActivity : HelloChartsActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bubble_chart)
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                add(R.id.container, PlaceholderFragment())
            }
        }
    }

    /**
     * A fragment containing a bubble chart.
     */
    class PlaceholderFragment : HelloChartsFragmentMenu(), MenuProvider {
        private lateinit var data: BubbleChartData
        private var hasAxes = true
        private var hasAxesNames = true
        private var shape = ValueShape.CIRCLE
        private var hasLabels = false
        private var hasLabelForSelected = false

        private val binding: FragmentBubbleChartBinding by lazy {
            FragmentBubbleChartBinding.inflate(layoutInflater)
        }

        override fun getMenu() = R.menu.bubble_chart

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            binding.chart.onValueTouchListener = ValueTouchListener()
            generateData()
            return binding.root
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            when (menuItem.itemId) {
                R.id.action_reset -> {
                    reset()
                    generateData()
                    return true
                }
                R.id.action_shape_circles -> {
                    setCircles()
                    return true
                }
                R.id.action_shape_square -> {
                    setSquares()
                    return true
                }
                R.id.action_toggle_labels -> {
                    toggleLabels()
                    return true
                }
                R.id.action_toggle_axes -> {
                    toggleAxes()
                    return true
                }
                R.id.action_toggle_axes_names -> {
                    toggleAxesNames()
                    return true
                }
                R.id.action_animate -> {
                    prepareDataAnimation()
                    binding.chart.startDataAnimation()
                    return true
                }
                R.id.action_toggle_selection_mode -> {
                    toggleLabelForSelected()
                    showToast("Selection mode set to " + binding.chart.isValueSelectionEnabled()
                        + " select any point.")
                    return true
                }
                R.id.action_toggle_touch_zoom -> {
                    binding.chart.setZoomEnabled(!binding.chart.isZoomEnabled())
                    showToast("IsZoomEnabled " + binding.chart.isZoomEnabled())
                    return true
                }
                R.id.action_zoom_both -> {
                    binding.chart.setZoomType(ZoomType.HORIZONTAL_AND_VERTICAL)
                    return true
                }
                R.id.action_zoom_horizontal -> {
                    binding.chart.setZoomType(ZoomType.HORIZONTAL)
                    return true
                }
                R.id.action_zoom_vertical -> {
                    binding.chart.setZoomType(ZoomType.VERTICAL)
                    return true
                }
            }
            return false
        }

        private fun reset() {
            hasAxes = true
            hasAxesNames = true
            shape = ValueShape.CIRCLE
            hasLabels = false
            hasLabelForSelected = false
            binding.chart.setValueSelectionEnabled(hasLabelForSelected)
        }

        private fun generateData() {
            val values: MutableList<BubbleValue> = ArrayList()
            for (i in 0 until BUBBLES_NUM) {
                val value = BubbleValue(
                    i.toFloat(),
                    random().toFloat() * 100,
                    random().toFloat() * 1000
                )
                value.color = ChartUtils.pickColor()
                value.shape = shape
                values.add(value)
            }
            data = BubbleChartData(values)
            data.setHasLabels(hasLabels)
            data.setHasLabelsOnlyForSelected(hasLabelForSelected)
            if (hasAxes) {
                val axisX = Axis()
                val axisY = Axis().setHasLines(true)
                if (hasAxesNames) {
                    axisX.name = "Axis X"
                    axisY.name = "Axis Y"
                }
                data.setAxisXBottom(axisX)
                data.setAxisYLeft(axisY)
            } else {
                data.setAxisXBottom(null)
                data.setAxisYLeft(null)
            }
            binding.chart.bubbleChartData = data
        }

        private fun setCircles() {
            shape = ValueShape.CIRCLE
            generateData()
        }

        private fun setSquares() {
            shape = ValueShape.SQUARE
            generateData()
        }

        private fun toggleLabels() {
            hasLabels = !hasLabels
            if (hasLabels) {
                hasLabelForSelected = false
                binding.chart.setValueSelectionEnabled(hasLabelForSelected)
            }
            generateData()
        }

        private fun toggleLabelForSelected() {
            hasLabelForSelected = !hasLabelForSelected
            binding.chart.setValueSelectionEnabled(hasLabelForSelected)
            if (hasLabelForSelected) {
                hasLabels = false
            }
            generateData()
        }

        private fun toggleAxes() {
            hasAxes = !hasAxes
            generateData()
        }

        private fun toggleAxesNames() {
            hasAxesNames = !hasAxesNames
            generateData()
        }

        /**
         * To animate values you have to change targets values and then call
         * [lecho.lib.hellocharts.view.Chart.startDataAnimation]
         * method(don't confuse with View.animate()).
         */
        private fun prepareDataAnimation() {
            for (value in data.values) {
                value.setTarget(
                    /* targetX = */ value.x + random().toFloat() * 4 * sign,
                    /* targetY = */ random().toFloat() * 100,
                    /* targetZ = */ random().toFloat() * 1000
                )
            }
        }

        private val sign: Int
            get() {
                val sign = intArrayOf(-1, 1)
                return sign[random().toFloat().roundToInt()]
            }

        private inner class ValueTouchListener : BubbleChartOnValueSelectListener {
            override fun onValueSelected(bubbleIndex: Int, value: BubbleValue) {
                showToast("Selected: $value")
            }

            override fun onValueDeselected() {
                // Nothing To DO
            }
        }

        companion object {
            private const val BUBBLES_NUM = 8
        }
    }
}