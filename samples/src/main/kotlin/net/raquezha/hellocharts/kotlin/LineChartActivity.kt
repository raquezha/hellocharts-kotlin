package net.raquezha.hellocharts.kotlin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.commit
import lecho.lib.hellocharts.animation.ChartAnimationListener
import lecho.lib.hellocharts.gesture.ZoomType
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener
import lecho.lib.hellocharts.model.Axis
import lecho.lib.hellocharts.model.Line
import lecho.lib.hellocharts.model.LineChartData
import lecho.lib.hellocharts.model.PointValue
import lecho.lib.hellocharts.model.TouchCoordinates
import lecho.lib.hellocharts.model.ValueShape
import lecho.lib.hellocharts.model.Viewport
import lecho.lib.hellocharts.util.ChartUtils
import net.raquezha.hellocharts.kotlin.databinding.FragmentLineChartBinding

class LineChartActivity : HelloChartsActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_line_chart)
        supportFragmentManager.commit {
            add(R.id.container, PlaceholderFragment())
        }
    }

    /**
     * A fragment containing a line chart.
     */
    class PlaceholderFragment : HelloChartsFragmentMenu(), MenuProvider {
        private var data: LineChartData? = null
        private var numberOfLines = 1
        private val maxNumberOfLines = 4
        private val numberOfPoints = 12
        private var randomNumbersTab = Array(maxNumberOfLines) { FloatArray(numberOfPoints) }
        private var hasAxis = true
        private var hasAxesNames = true
        private var hasLines = true
        private var hasPoints = true
        private var shape = ValueShape.CIRCLE
        private var isFilled = false
        private var hasLabels = false
        private var isCubic = false
        private var hasLabelForSelected = false
        private var pointsHaveDifferentColor = false
        private var hasGradientToTransparent = false

        private val binding: FragmentLineChartBinding by lazy {
            FragmentLineChartBinding.inflate(layoutInflater)
        }
        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            binding.chart.onValueTouchListener = ValueTouchListener()

            // Generate some random values.
            generateValues()
            generateData()

            // Disable viewport recalculations, see toggleCubic() method for more info.
            binding.chart.setViewportCalculationEnabled(false)
            resetViewport()
            return binding.root
        }

        override fun getMenu() = R.menu.line_chart

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            when (menuItem.itemId) {
                R.id.action_reset -> {
                    reset()
                    generateData()
                    return true
                }
                R.id.action_add_line -> {
                    addLineToData()
                    return true
                }
                R.id.action_toggle_lines -> {
                    toggleLines()
                    return true
                }
                R.id.action_toggle_points -> {
                    togglePoints()
                    return true
                }
                R.id.action_toggle_gradient -> {
                    toggleGradient()
                    return true
                }
                R.id.action_toggle_cubic -> {
                    toggleCubic()
                    return true
                }
                R.id.action_toggle_area -> {
                    toggleFilled()
                    return true
                }
                R.id.action_point_color -> {
                    togglePointColor()
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
                R.id.action_shape_diamond -> {
                    setDiamonds()
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
                    showToast(
                        "Selection mode set to " + binding.chart.isValueSelectionEnabled()
                            + " select any point."
                    )
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
                else -> return false
            }
        }

        private fun generateValues() {
            for (i in 0 until maxNumberOfLines) {
                for (j in 0 until numberOfPoints) {
                    randomNumbersTab[i][j] = Math.random().toFloat() * 100f
                }
            }
        }

        private fun reset() {
            numberOfLines = 1
            hasAxis = true
            hasAxesNames = true
            hasLines = true
            hasPoints = true
            shape = ValueShape.CIRCLE
            isFilled = false
            hasLabels = false
            isCubic = false
            hasLabelForSelected = false
            pointsHaveDifferentColor = false
            binding.chart.setValueSelectionEnabled(hasLabelForSelected)
            resetViewport()
        }

        private fun resetViewport() {
            // Reset viewport height range to (0,100)
            val v = Viewport(binding.chart.getMaximumViewport())
            v.bottom = 0f
            v.top = 100f
            v.left = 0f
            v.right = (numberOfPoints - 1).toFloat()
            binding.chart.setMaximumViewport(v)
            binding.chart.setCurrentViewport(v)
        }

        private fun generateData() {
            val lines: MutableList<Line> = ArrayList()
            for (i in 0 until numberOfLines) {
                val values: MutableList<PointValue> = ArrayList()
                for (j in 0 until numberOfPoints) {
                    values.add(PointValue(j.toFloat(), randomNumbersTab[i][j]))
                }
                val line = Line(values)
                line.color = ChartUtils.COLORS[i]
                line.shape = shape
                line.isCubic = isCubic
                line.isFilled = isFilled
                line.setHasLabels(hasLabels)
                line.setHasLabelsOnlyForSelected(hasLabelForSelected)
                line.setHasLines(hasLines)
                line.setHasPoints(hasPoints)
                line.setHasGradientToTransparent(hasGradientToTransparent)
                if (pointsHaveDifferentColor) {
                    line.pointColor = ChartUtils.COLORS[(i + 1) % ChartUtils.COLORS.size]
                }
                lines.add(line)
            }
            data = LineChartData(lines)
            if (hasAxis) {
                val axisX = Axis()
                val axisY = Axis().setHasLines(true)
                if (hasAxesNames) {
                    axisX.name = "Axis X"
                    axisY.name = "Axis Y"
                }
                data!!.setAxisXBottom(axisX)
                data!!.setAxisYLeft(axisY)
            } else {
                data!!.setAxisXBottom(null)
                data!!.setAxisYLeft(null)
            }
            data!!.baseValue = Float.NEGATIVE_INFINITY
            binding.chart.lineChartData = data!!
        }

        /**
         * Adds lines to data, after that data should be set again with
         * [lecho.lib.hellocharts.view.LineChartView.lineChartData]. Last 4th line has non-monotonically x values.
         */
        private fun addLineToData() {
            if (data!!.lines.size >= maxNumberOfLines) {
                showToast("Samples app uses max 4 lines!")
                return
            } else {
                ++numberOfLines
            }
            generateData()
        }

        private fun toggleLines() {
            hasLines = !hasLines
            generateData()
        }

        private fun togglePoints() {
            hasPoints = !hasPoints
            generateData()
        }

        private fun toggleGradient() {
            hasGradientToTransparent = !hasGradientToTransparent
            generateData()
        }

        private fun toggleCubic() {
            isCubic = !isCubic
            generateData()
            if (isCubic) {
                // It is good idea to manually set a little higher max viewport for cubic lines because sometimes line
                // go above or below max/min. To do that use Viewport.inest() method and pass negative value as dy
                // parameter or just set top and bottom values manually.
                // In this example I know that Y values are within (0,100) range so I set viewport height range manually
                // to (-5, 105).
                // To make this works during animations you should use Chart.setViewportCalculationEnabled(false) before
                // modifying viewport.
                // Remember to set viewport after you call setLineChartData().
                val v = Viewport(binding.chart.getMaximumViewport())
                v.bottom = -5f
                v.top = 105f
                // You have to set max and current viewports separately.
                binding.chart.setMaximumViewport(v)
                // I changing current viewport with animation in this case.
                binding.chart.setCurrentViewportWithAnimation(v)
            } else {
                // If not cubic restore viewport to (0,100) range.
                val v = Viewport(binding.chart.getMaximumViewport())
                v.bottom = 0f
                v.top = 100f

                // You have to set max and current viewports separately.
                // In this case, if I want animation I have to set current viewport first and use animation listener.
                // Max viewport will be set in onAnimationFinished method.
                binding.chart.setViewportAnimationListener(object : ChartAnimationListener {
                    override fun onAnimationStarted() {
                        // nothing to do here
                    }

                    override fun onAnimationFinished() {
                        // Set max viewport and remove listener.
                        binding.chart.setMaximumViewport(v)
                        binding.chart.setViewportAnimationListener(null)
                    }
                })
                // Set current viewport with animation;
                binding.chart.setCurrentViewportWithAnimation(v)
            }
        }

        private fun toggleFilled() {
            isFilled = !isFilled
            generateData()
        }

        private fun togglePointColor() {
            pointsHaveDifferentColor = !pointsHaveDifferentColor
            generateData()
        }

        private fun setCircles() {
            shape = ValueShape.CIRCLE
            generateData()
        }

        private fun setSquares() {
            shape = ValueShape.SQUARE
            generateData()
        }

        private fun setDiamonds() {
            shape = ValueShape.DIAMOND
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
            hasAxis = !hasAxis
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
         * If you operate on data that was set before you don't have to call
         * [lecho.lib.hellocharts.view.LineChartView.lineChartData] again.
         */
        private fun prepareDataAnimation() {
            for (line in data!!.lines) {
                for (value in line.values) {
                    // Here I modify target only for Y values but it is OK to modify X targets as well.
                    value.setTarget(value.x, Math.random().toFloat() * 100)
                }
            }
        }

        private inner class ValueTouchListener : LineChartOnValueSelectListener {
            override fun onValueSelected(
                lineIndex: Int,
                pointIndex: Int,
                value: PointValue,
                touchCoordinates: TouchCoordinates?
            ) {
                showToast("Selected: $value")
            }

            override fun onValueDeselected() {

            }
        }
    }
}