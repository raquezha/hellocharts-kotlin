package net.raquezha.hellocharts.kotlin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import lecho.lib.hellocharts.gesture.ZoomType.HORIZONTAL
import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener
import lecho.lib.hellocharts.model.Axis
import lecho.lib.hellocharts.model.AxisValue
import lecho.lib.hellocharts.model.Column
import lecho.lib.hellocharts.model.ColumnChartData
import lecho.lib.hellocharts.model.Line
import lecho.lib.hellocharts.model.LineChartData
import lecho.lib.hellocharts.model.PointValue
import lecho.lib.hellocharts.model.SubcolumnValue
import lecho.lib.hellocharts.model.TouchCoordinates
import lecho.lib.hellocharts.model.Viewport
import lecho.lib.hellocharts.util.ChartUtils
import net.raquezha.hellocharts.kotlin.databinding.FragmentLineColumnDependencyBinding


class LineColumnDependencyActivity : HelloChartsActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_line_column_dependency)
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                add(R.id.container, PlaceholderFragment())
            }
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    class PlaceholderFragment : Fragment() {
        private lateinit var lineData: LineChartData
        private val binding: FragmentLineColumnDependencyBinding by lazy {
            FragmentLineColumnDependencyBinding.inflate(layoutInflater)
        }
        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            // Generate and set data for line chart
            generateInitialLineData()
            generateColumnData()
            return binding.root
        }

        private fun generateColumnData() {
            val numSubColumns = 1
            val numColumns = months.size
            val axisValues: MutableList<AxisValue> = ArrayList()
            val columns: MutableList<Column> = ArrayList()
            var values: MutableList<SubcolumnValue>
            for (i in 0 until numColumns) {
                values = ArrayList()
                for (j in 0 until numSubColumns) {
                    values.add(
                        SubcolumnValue(
                            Math.random().toFloat() * 50f + 5,
                            ChartUtils.pickColor()
                        )
                    )
                }
                axisValues.add(AxisValue(i.toFloat()).setLabel(months[i]))
                columns.add(Column(values).setHasLabelsOnlyForSelected(true))
            }
            val columnData = ColumnChartData(columns)
            columnData.setAxisXBottom(Axis(axisValues).setHasLines(true))
            columnData.setAxisYLeft(Axis().setHasLines(true).setMaxLabelChars(2))
            binding.chartBottom.columnChartData = columnData

            // Set value touch listener that will trigger changes for chartTop.
            binding.chartBottom.onValueTouchListener = ValueTouchListener()

            // Set selection mode to keep selected month column highlighted.
            binding.chartBottom.setValueSelectionEnabled(true)
            binding.chartBottom.setZoomType(HORIZONTAL)
        }

        /**
         * Generates initial data for line chart.
         * At the beginning all Y values are equals 0.
         * That will change when user
         * will select value on column chart.
         */
        private fun generateInitialLineData() {
            val numValues = 7
            val axisValues: MutableList<AxisValue> = ArrayList()
            val values: MutableList<PointValue> = ArrayList()
            for (i in 0 until numValues) {
                values.add(PointValue(i.toFloat(), 0f))
                axisValues.add(AxisValue(i.toFloat()).setLabel(days[i]))
            }
            val line = Line(values)
            line.setColor(ChartUtils.COLOR_GREEN).isCubic = true
            val lines: MutableList<Line> = ArrayList()
            lines.add(line)
            lineData = LineChartData(lines)
            lineData.setAxisXBottom(Axis(axisValues).setHasLines(true))
            lineData.setAxisYLeft(Axis().setHasLines(true).setMaxLabelChars(3))
            binding.chartTop.lineChartData = lineData

            // For build-up animation you have to disable viewport recalculation.
            binding.chartTop.setViewportCalculationEnabled(false)

            // And set initial max viewport and current viewport- remember to set viewports after data.
            val v = Viewport(0f, 110f, 6f, 0f)
            binding.chartTop.setMaximumViewport(v)
            binding.chartTop.setCurrentViewport(v)
            binding.chartTop.setZoomType(HORIZONTAL)
        }

        private fun generateLineData(color: Int, range: Float) {
            // Cancel last animation if not finished.
            binding.chartTop.cancelDataAnimation()

            // Modify data targets
            val line = lineData.lines[0] // For this example there is always only one line.
            line.color = color
            for (value in line.values) {
                // Change target only for Y value.
                value.setTarget(value.x, Math.random().toFloat() * range)
            }

            // Start new data animation with 300ms duration;
            binding.chartTop.startDataAnimation(300)
        }

        private inner class ValueTouchListener : ColumnChartOnValueSelectListener {
            override fun onValueSelected(
                columnIndex: Int,
                subColumnIndex: Int,
                value: SubcolumnValue,
                touchCoordinates: TouchCoordinates?
            ) {
                generateLineData(value.color, 100f)
            }

            override fun onValueDeselected() {
                generateLineData(ChartUtils.COLOR_GREEN, 0f)
            }
        }

        companion object {
            val months = arrayOf(
                "Jan",
                "Feb",
                "Mar",
                "Apr",
                "May",
                "Jun",
                "Jul",
                "Aug",
                "Sep",
                "Oct",
                "Nov",
                "Dec"
            )
            val days = arrayOf(
                "Mon",
                "Tue",
                "Wen",
                "Thu",
                "Fri",
                "Sat",
                "Sun"
            )
        }
    }
}