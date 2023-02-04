package net.raquezha.hellocharts.kotlin

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.commit
import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener
import lecho.lib.hellocharts.model.PieChartData
import lecho.lib.hellocharts.model.SliceValue
import lecho.lib.hellocharts.util.ChartUtils
import net.raquezha.hellocharts.kotlin.databinding.FragmentPieChartBinding

class PieChartActivity : HelloChartsActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pie_chart)
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                add(R.id.container, PlaceholderFragment())
            }
        }
    }

    /**
     * A fragment containing a pie chart.
     */
    class PlaceholderFragment : HelloChartsFragmentMenu(), MenuProvider {
        private lateinit var data: PieChartData
        private var hasLabels = false
        private var hasLabelsOutside = false
        private var hasCenterCircle = false
        private var hasCenterText1 = false
        private var hasCenterText2 = false
        private var isExploded = false
        private var hasLabelForSelected = false

        private val binding: FragmentPieChartBinding by lazy {
            FragmentPieChartBinding.inflate(layoutInflater)
        }

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            binding.chart.onValueTouchListener = ValueTouchListener()
            generateData()
            return binding.root
        }

        override fun getMenu() = R.menu.pie_chart

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            when (menuItem.itemId) {
                R.id.action_reset -> {
                    reset()
                    generateData()
                    return true
                }
                R.id.action_explode -> {
                    explodeChart()
                    return true
                }
                R.id.action_center_circle -> {
                    hasCenterCircle = !hasCenterCircle
                    if (!hasCenterCircle) {
                        hasCenterText1 = false
                        hasCenterText2 = false
                    }
                    generateData()
                    return true
                }
                R.id.action_center_text1 -> {
                    hasCenterText1 = !hasCenterText1
                    if (hasCenterText1) {
                        hasCenterCircle = true
                    }
                    hasCenterText2 = false
                    generateData()
                    return true
                }
                R.id.action_center_text2 -> {
                    hasCenterText2 = !hasCenterText2
                    if (hasCenterText2) {
                        hasCenterText1 = true // text 2 need text 1 to by also drawn.
                        hasCenterCircle = true
                    }
                    generateData()
                    return true
                }
                R.id.action_toggle_labels -> {
                    toggleLabels()
                    return true
                }
                R.id.action_toggle_labels_outside -> {
                    toggleLabelsOutside()
                    return true
                }
                R.id.action_animate -> {
                    prepareDataAnimation()
                    binding.chart.startDataAnimation()
                    return true
                }
                R.id.action_toggle_selection_mode -> {
                    toggleLabelForSelected()
                    val text = "Selection mode set " +
                        "to " + binding.chart.isValueSelectionEnabled() +
                        " select any point."
                    showToast(text)
                    return true
                }
                else -> return false
            }
        }

        private fun reset() {
            binding.chart.circleFillRatio = 1.0f
            hasLabels = false
            hasLabelsOutside = false
            hasCenterCircle = false
            hasCenterText1 = false
            hasCenterText2 = false
            isExploded = false
            hasLabelForSelected = false
        }

        private fun generateData() {
            val numValues = 6
            val values: MutableList<SliceValue> = ArrayList()
            for (i in 0 until numValues) {
                val sliceValue =
                    SliceValue(Math.random().toFloat() * 30 + 15, ChartUtils.pickColor())
                values.add(sliceValue)
            }
            data = PieChartData(values)
            data.setHasLabels(hasLabels)
            data.setHasLabelsOnlyForSelected(hasLabelForSelected)
            data.setHasLabelsOutside(hasLabelsOutside)
            data.setHasCenterCircle(hasCenterCircle)
            if (isExploded) {
                data.setSlicesSpacing(24)
            }
            if (hasCenterText1) {
                data.centerText1 = "Hello!"

                // Get roboto-italic font.
                val tf = Typeface.createFromAsset(requireActivity().assets, "Roboto-Italic.ttf")
                data.centerText1Typeface = tf

                // Get font size from dimens.xml and convert it to sp(library uses sp values).
                data.centerText1FontSize = ChartUtils.px2sp(
                    resources.displayMetrics.scaledDensity,
                    resources.getDimension(R.dimen.pie_chart_text1_size).toInt()
                )
            }
            if (hasCenterText2) {
                data.centerText2 = "Charts (Roboto Italic)"
                val tf = Typeface.createFromAsset(requireActivity().assets, "Roboto-Italic.ttf")
                data.centerText2Typeface = tf
                data.centerText2FontSize = ChartUtils.px2sp(
                    resources.displayMetrics.scaledDensity,
                    resources.getDimension(R.dimen.pie_chart_text2_size).toInt()
                )
            }
            binding.chart.pieChartData = data
        }

        private fun explodeChart() {
            isExploded = !isExploded
            generateData()
        }

        private fun toggleLabelsOutside() {
            // has labels have to be true:P
            hasLabelsOutside = !hasLabelsOutside
            if (hasLabelsOutside) {
                hasLabels = true
                hasLabelForSelected = false
                binding.chart.setValueSelectionEnabled(hasLabelForSelected)
            }
            if (hasLabelsOutside) {
                binding.chart.circleFillRatio = 0.7f
            } else {
                binding.chart.circleFillRatio = 1.0f
            }
            generateData()
        }

        private fun toggleLabels() {
            hasLabels = !hasLabels
            if (hasLabels) {
                hasLabelForSelected = false
                binding.chart.setValueSelectionEnabled(hasLabelForSelected)
                if (hasLabelsOutside) {
                    binding.chart.circleFillRatio = 0.7f
                } else {
                    binding.chart.circleFillRatio = 1.0f
                }
            }
            generateData()
        }

        private fun toggleLabelForSelected() {
            hasLabelForSelected = !hasLabelForSelected
            binding.chart.setValueSelectionEnabled(hasLabelForSelected)
            if (hasLabelForSelected) {
                hasLabels = false
                hasLabelsOutside = false
                if (this.hasLabelsOutside) {
                    binding.chart.circleFillRatio = 0.7f
                } else {
                    binding.chart.circleFillRatio = 1.0f
                }
            }
            generateData()
        }

        /**
         * To animate values you have to change targets values and then call
         * [lecho.lib.hellocharts.view.Chart.startDataAnimation]
         * method(don't confuse with View.animate()).
         */
        private fun prepareDataAnimation() {
            for (value in data.values) {
                value.setTarget(Math.random().toFloat() * 30 + 15)
            }
        }

        private inner class ValueTouchListener : PieChartOnValueSelectListener {
            override fun onValueSelected(arcIndex: Int, value: SliceValue) {
                showToast("Selected: $value")
            }

            override fun onValueDeselected() {
                // Nothing to do here
            }
        }
    }
}