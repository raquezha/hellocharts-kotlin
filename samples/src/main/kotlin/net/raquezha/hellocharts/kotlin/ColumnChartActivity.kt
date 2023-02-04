package net.raquezha.hellocharts.kotlin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.MenuProvider
import androidx.fragment.app.commit
import lecho.lib.hellocharts.gesture.ZoomType
import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener
import lecho.lib.hellocharts.model.Axis
import lecho.lib.hellocharts.model.Column
import lecho.lib.hellocharts.model.ColumnChartData
import lecho.lib.hellocharts.model.SubcolumnValue
import lecho.lib.hellocharts.util.ChartUtils
import net.raquezha.hellocharts.kotlin.databinding.FragmentColumnChartBinding
import kotlin.math.roundToInt

class ColumnChartActivity : HelloChartsActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_column_chart)
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                add(R.id.container, PlaceholderFragment())
            }
        }
    }

    /**
     * A fragment containing a column chart.
     */
    class PlaceholderFragment : HelloChartsFragmentMenu(), MenuProvider {
        private lateinit var data: ColumnChartData
        private var hasAxis = true
        private var hasAxesNames = true
        private var hasLabels = false
        private var hasLabelForSelected = false
        private var dataType = DEFAULT_DATA
        private var isRoundedCorner = true

        private val binding: FragmentColumnChartBinding by lazy {
            FragmentColumnChartBinding.inflate(layoutInflater)
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

        override fun getMenu() = R.menu.column_chart

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            when (menuItem.itemId) {
                R.id.action_reset -> {
                    reset()
                    generateData()
                    return true
                }
                R.id.action_subcolumns -> {
                    dataType = SUB_COLUMNS_DATA
                    generateData()
                    return true
                }
                R.id.action_stacked -> {
                    dataType = STACKED_DATA
                    generateData()
                    return true
                }
                R.id.action_negative_subcolumns -> {
                    dataType = NEGATIVE_SUB_COLUMNS_DATA
                    generateData()
                    return true
                }
                R.id.action_negative_stacked -> {
                    dataType = NEGATIVE_STACKED_DATA
                    generateData()
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
                        "Selection mode set to "
                            + binding.chart.isValueSelectionEnabled()
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

        private fun reset() {
            hasAxis = true
            hasAxesNames = true
            hasLabels = false
            hasLabelForSelected = false
            dataType = DEFAULT_DATA
            binding.chart.setValueSelectionEnabled(hasLabelForSelected)
        }

        private fun generateDefaultData() {
            val numSubColumns = 1
            val numColumns = 8
            // Column can have many subColumns, here by default I use 1 subColumn in each of 8 columns.
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
                val column = Column(values)
                column.setHasLabels(hasLabels)
                column.setHasLabelsOnlyForSelected(hasLabelForSelected)
                columns.add(column)
            }
            data = ColumnChartData(columns)
            data.isRoundedCorner = isRoundedCorner
            if (hasAxis) {
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
            binding.chart.columnChartData = data
        }

        /**
         * Generates columns with subColumns, columns have larger separation than subColumns.
         */
        private fun generateSubcolumnsData() {
            val numSubColumns = 4
            val numColumns = 4
            // Column can have many subColumns, here I use 4 subColumn in each of 8 columns.
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
                val column = Column(values)
                column.setHasLabels(hasLabels)
                column.setHasLabelsOnlyForSelected(hasLabelForSelected)
                columns.add(column)
            }
            data = ColumnChartData(columns)
            data.isRoundedCorner = isRoundedCorner
            if (hasAxis) {
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
            binding.chart.columnChartData = data
        }

        /**
         * Generates columns with stacked subColumns.
         */
        private fun generateStackedData() {
            val numSubColumns = 4
            val numColumns = 8
            // Column can have many stacked subColumns, here I use 4 stacke subColumn in each of 4 columns.
            val columns: MutableList<Column> = ArrayList()
            var values: MutableList<SubcolumnValue>
            for (i in 0 until numColumns) {
                values = ArrayList()
                for (j in 0 until numSubColumns) {
                    values.add(
                        SubcolumnValue(
                            Math.random().toFloat() * 20f + 5,
                            ChartUtils.pickColor()
                        )
                    )
                }
                val column = Column(values)
                column.setHasLabels(hasLabels)
                column.setHasLabelsOnlyForSelected(hasLabelForSelected)
                columns.add(column)
            }
            data = ColumnChartData(columns)
            data.isRoundedCorner = isRoundedCorner

            // Set stacked flag.
            data.isStacked = true
            if (hasAxis) {
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
            binding.chart.columnChartData = data
        }

        private fun generateNegativeSubColumnsData() {
            val numSubColumns = 4
            val numColumns = 4
            val columns: MutableList<Column> = ArrayList()
            var values: MutableList<SubcolumnValue>
            for (i in 0 until numColumns) {
                values = ArrayList()
                for (j in 0 until numSubColumns) {
                    val sign = sign
                    values.add(
                        SubcolumnValue(
                            Math.random().toFloat() * 50f * sign + 5 * sign,
                            ChartUtils.pickColor()
                        )
                    )
                }
                val column = Column(values)
                column.setHasLabels(hasLabels)
                column.setHasLabelsOnlyForSelected(hasLabelForSelected)
                columns.add(column)
            }
            data = ColumnChartData(columns)
            data.isRoundedCorner = isRoundedCorner
            if (hasAxis) {
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
            binding.chart.columnChartData = data
        }

        private fun generateNegativeStackedData() {
            val numSubColumns = 4
            val numColumns = 8
            // Column can have many stacked subColumns, here I use 4 stacke subColumn in each of 4 columns.
            val columns: MutableList<Column> = ArrayList()
            var values: MutableList<SubcolumnValue>
            for (i in 0 until numColumns) {
                values = ArrayList()
                for (j in 0 until numSubColumns) {
                    val sign = sign
                    values.add(
                        SubcolumnValue(
                            Math.random().toFloat() * 20f * sign + 5 * sign,
                            ChartUtils.pickColor()
                        )
                    )
                }
                val column = Column(values)
                column.setHasLabels(hasLabels)
                column.setHasLabelsOnlyForSelected(hasLabelForSelected)
                columns.add(column)
            }
            data = ColumnChartData(columns)
            data.isRoundedCorner = isRoundedCorner

            // Set stacked flag.
            data.isStacked = true
            if (hasAxis) {
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
            binding.chart.columnChartData = data
        }

        private val sign: Int
            get() {
                val sign = intArrayOf(-1, 1)
                return sign[Math.random().toFloat().roundToInt()]
            }

        private fun generateData() {
            when (dataType) {
                DEFAULT_DATA -> generateDefaultData()
                SUB_COLUMNS_DATA -> generateSubcolumnsData()
                STACKED_DATA -> generateStackedData()
                NEGATIVE_SUB_COLUMNS_DATA -> generateNegativeSubColumnsData()
                NEGATIVE_STACKED_DATA -> generateNegativeStackedData()
                else -> generateDefaultData()
            }
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
         */
        private fun prepareDataAnimation() {
            for (column in data.columns) {
                for (value in column.values) {
                    value.setTarget(Math.random().toFloat() * 100)
                }
            }
        }

        private inner class ValueTouchListener : ColumnChartOnValueSelectListener {
            override fun onValueSelected(
                columnIndex: Int,
                subColumnIndex: Int,
                value: SubcolumnValue
            ) {
                Toast.makeText(activity, "Selected: $value", Toast.LENGTH_SHORT).show()
            }

            override fun onValueDeselected() {
                // Nothing to do here
            }
        }

        companion object {
            private const val DEFAULT_DATA = 0
            private const val SUB_COLUMNS_DATA = 1
            private const val STACKED_DATA = 2
            private const val NEGATIVE_SUB_COLUMNS_DATA = 3
            private const val NEGATIVE_STACKED_DATA = 4
        }

    }
}