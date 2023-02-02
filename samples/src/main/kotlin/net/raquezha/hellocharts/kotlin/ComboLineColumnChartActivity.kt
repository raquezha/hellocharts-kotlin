package net.raquezha.hellocharts.kotlin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.commit
import lecho.lib.hellocharts.listener.ComboLineColumnChartOnValueSelectListener
import lecho.lib.hellocharts.model.Axis
import lecho.lib.hellocharts.model.Column
import lecho.lib.hellocharts.model.ColumnChartData
import lecho.lib.hellocharts.model.ComboLineColumnChartData
import lecho.lib.hellocharts.model.Line
import lecho.lib.hellocharts.model.LineChartData
import lecho.lib.hellocharts.model.PointValue
import lecho.lib.hellocharts.model.SubcolumnValue
import lecho.lib.hellocharts.util.ChartUtils
import net.raquezha.hellocharts.kotlin.databinding.FragmentComboLineColumnChartBinding

class ComboLineColumnChartActivity : HelloChartsActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_combo_line_column_chart)
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                add(R.id.container, PlaceholderFragment())
            }
        }
    }

    /**
     * A fragment containing a combo line/column chart view.
     */
    class PlaceholderFragment : HelloChartsFragmentMenu(), MenuProvider {
        private lateinit var data: ComboLineColumnChartData
        private var numberOfLines = 1
        private val maxNumberOfLines = 4
        private val numberOfPoints = 12
        private var randomNumbersTab = Array(maxNumberOfLines) { FloatArray(numberOfPoints) }
        private var hasAxis = true
        private var hasAxesNames = true
        private var hasPoints = true
        private var hasLines = true
        private var isCubic = false
        private var hasLabels = false

        private val binding: FragmentComboLineColumnChartBinding by lazy {
            FragmentComboLineColumnChartBinding.inflate(layoutInflater)
        }

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            binding.chart.onValueTouchListener = ValueTouchListener()
            generateValues()
            generateData()
            return binding.root
        }

        override fun getMenu() = R.menu.combo_line_column_chart

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
                R.id.action_toggle_cubic -> {
                    toggleCubic()
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
            }
            return false
        }

        private fun generateValues() {
            for (i in 0 until maxNumberOfLines) {
                for (j in 0 until numberOfPoints) {
                    randomNumbersTab[i][j] = Math.random().toFloat() * 50f + 5
                }
            }
        }

        private fun reset() {
            numberOfLines = 1
            hasAxis = true
            hasAxesNames = true
            hasLines = true
            hasPoints = true
            hasLabels = false
            isCubic = false
        }

        private fun generateData() {
            // Chart looks the best when line data and column data have similar maximum viewports.
            data = ComboLineColumnChartData(generateColumnData(), generateLineData())
            if (hasAxis) {
                val axisX = Axis()
                val axisY = Axis().setHasLines(true)
                if (hasAxesNames) {
                    axisX.name = "Axis X"
                    axisY.name = "Axis Y"
                }
                data.axisXBottom = axisX
                data.axisYLeft = axisY
            } else {
                data.axisXBottom = null
                data.axisYLeft = null
            }
            binding.chart.comboLineColumnChartData = data
        }

        private fun generateLineData(): LineChartData {
            val lines: MutableList<Line> = ArrayList()
            for (i in 0 until numberOfLines) {
                val values: MutableList<PointValue> = ArrayList()
                for (j in 0 until numberOfPoints) {
                    values.add(PointValue(j.toFloat(), randomNumbersTab[i][j]))
                }
                val line = Line(values)
                line.color = ChartUtils.COLORS[i]
                line.isCubic = isCubic
                line.setHasLabels(hasLabels)
                line.setHasLines(hasLines)
                line.setHasPoints(hasPoints)
                lines.add(line)
            }
            return LineChartData(lines)
        }

        private fun generateColumnData(): ColumnChartData {
            val numSubColumns = 1
            val numColumns = 12
            // Column can have many subColumns, here by default I use 1 subColumn in each of 8 columns.
            val columns: MutableList<Column> = ArrayList()
            var values: MutableList<SubcolumnValue>
            for (i in 0 until numColumns) {
                values = ArrayList()
                for (j in 0 until numSubColumns) {
                    values.add(
                        SubcolumnValue(
                            Math.random().toFloat() * 50 + 5,
                            ChartUtils.COLOR_GREEN
                        )
                    )
                }
                columns.add(Column(values))
            }
            return ColumnChartData(columns)
        }

        private fun addLineToData() {
            if (data.lineChartData.lines.size >= maxNumberOfLines) {
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

        private fun toggleCubic() {
            isCubic = !isCubic
            generateData()
        }

        private fun toggleLabels() {
            hasLabels = !hasLabels
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
         *
         * To animate values you have to change targets values and then call
         * [lecho.lib.hellocharts.view.Chart.startDataAnimation]
         * method(don't confuse with View.animate()).
         */
        private fun prepareDataAnimation() {
            // Line animations
            for (line in data.lineChartData.lines) {
                for (value in line.values) {
                    // Here I modify target only for Y values but it is OK to modify X targets as well.
                    value.setTarget(value.x, Math.random().toFloat() * 50 + 5)
                }
            }

            // Columns animations
            for (column in data.columnChartData.columns) {
                for (value in column.values) {
                    value.setTarget(Math.random().toFloat() * 50 + 5)
                }
            }
        }

        private inner class ValueTouchListener : ComboLineColumnChartOnValueSelectListener {
            override fun onValueDeselected() {
                // Nothing to do here
            }

            override fun onColumnValueSelected(
                columnIndex: Int,
                subColumnIndex: Int,
                value: SubcolumnValue
            ) {
                showToast("Selected column: $value")
            }

            override fun onPointValueSelected(lineIndex: Int, pointIndex: Int, value: PointValue) {
                showToast("Selected line point: $value")
            }
        }
    }
}