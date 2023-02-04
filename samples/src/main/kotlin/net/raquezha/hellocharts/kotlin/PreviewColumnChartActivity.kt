package net.raquezha.hellocharts.kotlin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.commit
import lecho.lib.hellocharts.gesture.ZoomType
import lecho.lib.hellocharts.listener.ViewportChangeListener
import lecho.lib.hellocharts.model.Axis
import lecho.lib.hellocharts.model.Column
import lecho.lib.hellocharts.model.ColumnChartData
import lecho.lib.hellocharts.model.SubcolumnValue
import lecho.lib.hellocharts.model.Viewport
import lecho.lib.hellocharts.util.ChartUtils
import net.raquezha.hellocharts.kotlin.databinding.FragmentPreviewColumnChartBinding

class PreviewColumnChartActivity : HelloChartsActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview_column_chart)
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                add(R.id.container, PlaceholderFragment())
            }
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    class PlaceholderFragment : HelloChartsFragmentMenu() {

        private val binding: FragmentPreviewColumnChartBinding by lazy {
            FragmentPreviewColumnChartBinding.inflate(layoutInflater)
        }

        /**
         * Deep copy of data.
         */
        private lateinit var data: ColumnChartData
        private lateinit var previewData: ColumnChartData

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            // Generate data for previewed chart and copy of that data for preview chart.
            generateDefaultData()
            binding.chart.columnChartData = data
            // Disable zoom/scroll for previewed chart, visible chart ranges depends on preview chart viewport so
            // zoom/scroll is unnecessary.
            binding.chart.setZoomEnabled(false)
            binding.chart.setScrollEnabled(false)
            binding.previewChart.columnChartData = previewData
            binding.previewChart.setViewportChangeListener(ViewportListener())
            previewX(false)
            return binding.root
        }

        override fun getMenu() = R.menu.preview_column_chart

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            when (menuItem.itemId) {
                R.id.action_reset -> {
                    generateDefaultData()
                    binding.chart.columnChartData = data
                    binding.previewChart.columnChartData = previewData
                    previewX(true)
                    return true
                }
                R.id.action_preview_both -> {
                    previewXY()
                    binding.previewChart.setZoomType(ZoomType.HORIZONTAL_AND_VERTICAL)
                    return true
                }
                R.id.action_preview_horizontal -> {
                    previewX(true)
                    return true
                }
                R.id.action_preview_vertical -> {
                    previewY()
                    return true
                }
                R.id.action_change_color -> {
                    var color = ChartUtils.pickColor()
                    while (color == binding.previewChart.previewColor) {
                        color = ChartUtils.pickColor()
                    }
                    binding.previewChart.previewColor = color
                    return true
                }
            }
            return false
        }

        private fun generateDefaultData() {
            val numSubColumns = 1
            val numColumns = 50
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
                columns.add(Column(values))
            }
            data = ColumnChartData(columns)
            data.setAxisXBottom(Axis())
            data.setAxisYLeft(Axis().setHasLines(true))

            // prepare preview data, is better to use separate deep copy for preview chart.
            // set color to grey to make preview area more visible.
            previewData = ColumnChartData(data)
            for (column in previewData.columns) {
                for (value in column.values) {
                    value.color = ChartUtils.DEFAULT_DARKEN_COLOR
                }
            }
        }

        private fun previewY() {
            val tempViewport = Viewport(binding.chart.getMaximumViewport())
            val dy = tempViewport.height() / 4
            tempViewport.inset(0f, dy)
            binding.previewChart.setCurrentViewportWithAnimation(tempViewport)
            binding.previewChart.setZoomType(ZoomType.VERTICAL)
        }

        private fun previewX(animate: Boolean) {
            val tempViewport = Viewport(binding.chart.getMaximumViewport())
            val dx = tempViewport.width() / 4
            tempViewport.inset(dx, 0f)
            if (animate) {
                binding.previewChart.setCurrentViewportWithAnimation(tempViewport)
            } else {
                binding.previewChart.setCurrentViewport(tempViewport)
            }
            binding.previewChart.setZoomType(ZoomType.HORIZONTAL)
        }

        private fun previewXY() {
            // Better to not modify viewport of any chart directly so create a copy.
            val tempViewport = Viewport(binding.chart.getMaximumViewport())
            // Make temp viewport smaller.
            val dx = tempViewport.width() / 4
            val dy = tempViewport.height() / 4
            tempViewport.inset(dx, dy)
            binding.previewChart.setCurrentViewportWithAnimation(tempViewport)
        }

        /**
         * Viewport listener for preview chart(lower one).
         * in [onViewportChanged] method change viewport of upper chart.
         */
        private inner class ViewportListener : ViewportChangeListener {
            override fun onViewportChanged(newViewport: Viewport) {
                // don't use animation, it is unnecessary when using preview chart because usually viewport changes
                // happens to often.
                binding.chart.setCurrentViewport(newViewport)
            }
        }
    }
}