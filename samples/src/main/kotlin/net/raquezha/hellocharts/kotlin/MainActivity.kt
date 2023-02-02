package net.raquezha.hellocharts.kotlin

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import lecho.lib.hellocharts.view.AbstractChartView
import lecho.lib.hellocharts.view.BubbleChartView
import lecho.lib.hellocharts.view.ColumnChartView
import lecho.lib.hellocharts.view.LineChartView
import lecho.lib.hellocharts.view.PieChartView
import lecho.lib.hellocharts.view.PreviewColumnChartView
import lecho.lib.hellocharts.view.PreviewLineChartView
import net.raquezha.hellocharts.kotlin.MainActivity.ChartType.BUBBLE_CHART
import net.raquezha.hellocharts.kotlin.MainActivity.ChartType.COLUMN_CHART
import net.raquezha.hellocharts.kotlin.MainActivity.ChartType.LINE_CHART
import net.raquezha.hellocharts.kotlin.MainActivity.ChartType.OTHER
import net.raquezha.hellocharts.kotlin.MainActivity.ChartType.PIE_CHART
import net.raquezha.hellocharts.kotlin.MainActivity.ChartType.PREVIEW_COLUMN_CHART
import net.raquezha.hellocharts.kotlin.MainActivity.ChartType.PREVIEW_LINE_CHART
import net.raquezha.hellocharts.kotlin.databinding.FragmentMainBinding
import net.raquezha.hellocharts.kotlin.databinding.ListItemSampleBinding


class MainActivity : HelloChartsActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                add(R.id.container, PlaceholderFragment())
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        if (id == R.id.action_about) {
            Intent(this, AboutActivity::class.java).run(::startActivity)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    enum class ChartType {
        LINE_CHART,
        COLUMN_CHART,
        PIE_CHART,
        BUBBLE_CHART,
        PREVIEW_LINE_CHART,
        PREVIEW_COLUMN_CHART,
        OTHER
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    class PlaceholderFragment : Fragment(), OnItemClickListener {
        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            val binding = FragmentMainBinding.inflate(layoutInflater)
            binding.list.adapter = ChartSamplesAdapter(
                requireContext(),
                0,
                generateSamplesDescriptions()
            )
            binding.list.onItemClickListener = this
            return binding.root
        }

        override fun onItemClick(
            adapter: AdapterView<*>?,
            view: View,
            position: Int,
            id: Long
        ) {
            val intent: Intent = when (position) {
                0 -> Intent(activity, LineChartActivity::class.java) // Line Chart
                1 -> Intent(activity, ColumnChartActivity::class.java) // Column Chart
                2 -> Intent(activity, PieChartActivity::class.java) // Pie Chart
                3 -> Intent(activity, BubbleChartActivity::class.java) // Bubble Chart
                4 -> Intent(activity, PreviewLineChartActivity::class.java) // Preview Line Chart
                5 -> Intent(activity, PreviewColumnChartActivity::class.java) // Preview Column Chart
                6 -> Intent(activity, ComboLineColumnChartActivity::class.java) // Combo Chart
                7 -> Intent(activity, LineColumnDependencyActivity::class.java) // Line Column Dependency
                8 -> Intent(activity, TempoChartActivity::class.java) // Tempo line chart;
                9 -> Intent(activity, SpeedChartActivity::class.java) // Speed line chart
                10 -> Intent(activity, GoodBadChartActivity::class.java) // Good Bad filled line chart
                else -> { Intent(activity, MainActivity::class.java) }
            }
            openActivity(intent)
        }

        private fun openActivity(intent: Intent) {
            intent.run(::startActivity)
        }

        private fun generateSamplesDescriptions(): List<ChartSampleDescription> {
            val list: MutableList<ChartSampleDescription> = ArrayList()
            list.add(
                ChartSampleDescription(
                    text1 = getString(R.string.line_chart),
                    text2 = "",
                    chartType = LINE_CHART
                )
            )
            list.add(
                ChartSampleDescription(
                    text1 = getString(R.string.column_chart),
                    text2 = "",
                    chartType = COLUMN_CHART
                )
            )
            list.add(
                ChartSampleDescription(
                    text1 = getString(R.string.pie_chart),
                    text2 = "",
                    chartType = PIE_CHART
                )
            )
            list.add(
                ChartSampleDescription(
                    text1 = getString(R.string.bubble_chart),
                    text2 = "",
                    chartType = BUBBLE_CHART
                )
            )
            list.add(
                ChartSampleDescription(
                    text1 = getString(R.string.preview_line_chart),
                    text2 = getString(R.string.control_line_chart),
                    chartType = PREVIEW_LINE_CHART
                )
            )
            list.add(
                ChartSampleDescription(
                    text1 = getString(R.string.preview_column_chart),
                    text2 = getString(R.string.control_column_chart_viewport),
                    chartType = PREVIEW_COLUMN_CHART
                )
            )
            list.add(
                ChartSampleDescription(
                    text1 = getString(R.string.combo_line_column_chart),
                    text2 = getString(R.string.combo_chart_with_lines),
                    chartType = OTHER
                )
            )
            list.add(
                ChartSampleDescription(
                    text1 = getString(R.string.line_column_chart_dependency),
                    text2 = getString(R.string.linechart_responds_with_animation),
                    chartType = OTHER
                )
            )
            list.add(
                ChartSampleDescription(
                    text1 = getString(R.string.tempo_chart),
                    text2 = getString(R.string.presents_tempo_and_height),
                    chartType = OTHER
                )
            )
            list.add(
                ChartSampleDescription(
                    text1 = getString(R.string.speed_chart),
                    text2 = getString(R.string.presents_speed_and_height_values),
                    chartType = OTHER
                )
            )
            list.add(
                ChartSampleDescription(
                    text1 = getString(R.string.good_bad_chart),
                    text2 = getString(R.string.example_of_filled_area_line_chart),
                    chartType = OTHER
                )
            )
            return list
        }
    }

    class ChartSamplesAdapter(
        context: Context,
        resource: Int,
        objects: List<ChartSampleDescription>
    ) : ArrayAdapter<ChartSampleDescription?>(context, resource, objects) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val binding: ListItemSampleBinding = if (convertView == null) {
                val inflater = context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
                ListItemSampleBinding.inflate(inflater, parent, false)
            } else {
                ListItemSampleBinding.bind(convertView)
            }

            val holder = ViewHolder(
                text1 = binding.text1,
                text2 = binding.text2,
                chartLayout = binding.chartLayout
            )

            val item = getItem(position)!!
            holder.chartLayout.visibility = View.VISIBLE
            holder.chartLayout.removeAllViews()

            val chart: AbstractChartView? = when (item.chartType) {
                LINE_CHART -> LineChartView(context)
                COLUMN_CHART -> ColumnChartView(context)
                PIE_CHART -> PieChartView(context)
                BUBBLE_CHART -> BubbleChartView(context)
                PREVIEW_LINE_CHART -> PreviewLineChartView(context)
                PREVIEW_COLUMN_CHART -> PreviewColumnChartView(context)
                else -> null
            }

            if(chart != null) {
                holder.chartLayout.visibility = View.VISIBLE
                holder.chartLayout.addView(chart)
            } else {
                holder.chartLayout.visibility = View.GONE
            }

            if (null != chart) {
                // Disable touch handling for chart on the ListView.
                chart.isInteractive = false
            }

            holder.text1.text = item.text1
            holder.text2.text = item.text2
            holder.text1.setTextColor(Color.DKGRAY)
            return binding.root
        }

        data class ViewHolder(
            var text1: TextView,
            var text2: TextView,
            var chartLayout: FrameLayout,
        )
    }

    data class ChartSampleDescription(
        var text1: String,
        var text2: String,
        var chartType: ChartType
    )
}