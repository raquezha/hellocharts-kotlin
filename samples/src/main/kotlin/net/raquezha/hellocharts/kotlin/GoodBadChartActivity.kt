package net.raquezha.hellocharts.kotlin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import lecho.lib.hellocharts.model.Line
import lecho.lib.hellocharts.model.LineChartData
import lecho.lib.hellocharts.model.PointValue
import lecho.lib.hellocharts.util.ChartUtils
import net.raquezha.hellocharts.kotlin.databinding.FragmentGoodBadBinding

class GoodBadChartActivity : HelloChartsActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_good_bad)
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
        private var data: LineChartData? = null
        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            val binding = FragmentGoodBadBinding.inflate(layoutInflater)
            generateDefaultData()
            binding.chart.lineChartData = data

            // Increase viewport height for better look
            binding.chart.getMaximumViewport().let {
                val dy = it?.height()?.times(0.2f) ?: 0f
                it?.inset(0f, -dy)
                binding.chart.setMaximumViewport(it)
                binding.chart.setCurrentViewport(it)
            }

            return binding.root
        }

        private fun generateDefaultData() {

            // Generate data, every line has 3 points to form filled triangle. Point radius is set to 1 to be almost
            // invisible but it has to be there because without points there is not labels. Area transparency is set to
            // 255(full opacity).

            // Important note. This example uses negative values, to properly fill area below 0 chart base value have to
            // be set to 0. That is default base value but if you want to be sure you can call data.setBaseValue(0)
            // method.
            var line: Line

            var values: MutableList<PointValue>
            val lines: MutableList<Line> = ArrayList()

            // First good triangle
            values = ArrayList()
            values.add(PointValue(0f, 0f).setLabel(""))
            values.add(PointValue(1f, 1f).setLabel("Very Good:)"))
            values.add(PointValue(2f, 0f).setLabel(""))
            line = Line(values)
            line.color = ChartUtils.COLOR_GREEN
            line.areaTransparency = 255
            line.isFilled = true
            line.pointRadius = 1
            line.setHasLabels(true)
            lines.add(line)

            // Second good triangle
            values = ArrayList()
            values.add(PointValue(3f, 0f).setLabel(""))
            values.add(PointValue(4f, 0.5f).setLabel("Good Enough"))
            values.add(PointValue(5f, 0f).setLabel(""))
            line = Line(values)
            line.color = ChartUtils.COLOR_GREEN
            line.areaTransparency = 255
            line.isFilled = true
            line.pointRadius = 1
            line.setHasLabels(true)
            lines.add(line)

            // Bad triangle
            values = ArrayList()
            values.add(PointValue(1f, 0f).setLabel(""))
            values.add(PointValue(2f, -1f).setLabel("Very Bad"))
            values.add(PointValue(3f, 0f).setLabel(""))
            line = Line(values)
            line.color = ChartUtils.COLOR_RED
            line.areaTransparency = 255
            line.isFilled = true
            line.pointRadius = 1
            line.setHasLabels(true)
            lines.add(line)
            data = LineChartData(lines)

            // *** Important, set base value to 0 to fill negative part of chart.
            // data.setBaseValue(0);
        }
    }
}