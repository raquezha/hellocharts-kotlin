package net.raquezha.hellocharts.kotlin

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import lecho.lib.hellocharts.formatter.SimpleAxisValueFormatter
import lecho.lib.hellocharts.model.Axis
import lecho.lib.hellocharts.model.Line
import lecho.lib.hellocharts.model.LineChartData
import lecho.lib.hellocharts.model.PointValue
import lecho.lib.hellocharts.util.ChartUtils
import net.raquezha.hellocharts.kotlin.databinding.FragmentTempoChartBinding

class SpeedChartActivity : HelloChartsActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tempo_chart)
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                add(R.id.container, PlaceholderFragment())
            }
        }
    }

    class PlaceholderFragment : Fragment() {

        private val binding: FragmentTempoChartBinding by lazy {
            FragmentTempoChartBinding.inflate(layoutInflater)
        }
        private var data: LineChartData? = null
        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            generateSpeedData()
            return binding.root
        }

        private fun generateSpeedData() {
            // I got speed in range (0-55) and height in meters in range(200 - 300).
            // I want this chart to display both information. Differences between speed and height
            // values are large and chart doesn't look good so I need to modify height values
            // to be in range of speed values.
            val speedRange = 55f
            val minHeight = 200f
            val maxHeight = 300f
            val scale = speedRange / maxHeight
            val sub = minHeight * scale / 2
            val numValues = 52
            var line: Line
            var values: MutableList<PointValue?>
            val lines: MutableList<Line> = ArrayList()

            // Height line, add it as first line to be drawn in the background.
            values = ArrayList()
            for (i in 0 until numValues) {
                // Some random height values, add +200 to make line a little more natural
                val rawHeight = (Math.random() * 100 + 200).toFloat()
                val normalizedHeight = rawHeight * scale - sub
                values.add(PointValue(i.toFloat(), normalizedHeight))
            }
            line = Line(values)
            line.color = Color.GRAY
            line.setHasPoints(false)
            line.isFilled = true
            line.strokeWidth = 1
            lines.add(line)

            // Speed line
            values = ArrayList()
            for (i in 0 until numValues) {
                // Some random speed values, add +20 to make line a little more natural.
                values.add(PointValue(i.toFloat(), Math.random().toFloat() * 30 + 20))
            }
            line = Line(values)
            line.color = ChartUtils.COLOR_GREEN
            line.setHasPoints(false)
            line.strokeWidth = 3
            lines.add(line)

            // Data and axes
            data = LineChartData(lines)

            // Distance axis(bottom X) with formatter that will ad [km] to values, remember to modify max label charts
            // value.
            val distanceAxis = Axis()
            distanceAxis.name = "Distance"
            distanceAxis.textColor = ChartUtils.COLOR_ORANGE
            distanceAxis.maxLabelChars = 4
            distanceAxis.formatter = SimpleAxisValueFormatter().setAppendedText("km".toCharArray())
            distanceAxis.setHasLines(true)
            distanceAxis.isInside = true
            data!!.axisXBottom = distanceAxis

            // Speed axis
            data!!.axisYLeft = Axis().setName("Speed [km/h]").setHasLines(true)
                .setMaxLabelChars(3)
                .setTextColor(ChartUtils.COLOR_RED).setInside(true)

            // Height axis, this axis need custom formatter that will translate values back to real height values.
            data!!.axisYRight = Axis().setName("Height [m]").setMaxLabelChars(3)
                .setTextColor(ChartUtils.COLOR_BLUE)
                .setFormatter(
                    HeightValueFormatter(
                        scale,
                        sub,
                        0
                    )
                ).setInside(true)

            // Set data
            binding.chart.lineChartData = data

            // Important: adjust viewport, you could skip this step but in this case it will looks better with custom
            // viewport. Set
            // viewport with Y range 0-55;
            val viewport = binding.chart.maximumViewport
            viewport[viewport.left, speedRange, viewport.right] = 0f
            binding.chart.maximumViewport = viewport
            binding.chart.currentViewport = viewport
        }

        /**
         * Recalculated height values to display on axis.
         */
        private class HeightValueFormatter(
            private val scale: Float,
            private val sub: Float,
            private val decimalDigits: Int
        ) : SimpleAxisValueFormatter() {
            override fun formatValueForAutoGeneratedAxis(
                formattedValue: CharArray,
                value: Float,
                autoDecimalDigits: Int
            ): Int {
                val scaledValue = (value + sub) / scale
                return super.formatValueForAutoGeneratedAxis(
                    formattedValue,
                    scaledValue,
                    decimalDigits
                )
            }
        }
    }
}