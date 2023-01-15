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
import lecho.lib.hellocharts.model.AxisValue
import lecho.lib.hellocharts.model.Line
import lecho.lib.hellocharts.model.LineChartData
import lecho.lib.hellocharts.model.PointValue
import lecho.lib.hellocharts.util.ChartUtils
import net.raquezha.hellocharts.kotlin.databinding.FragmentTempoChartBinding
import kotlin.math.floor

class TempoChartActivity : HelloChartsActivity() {
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
        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            generateTempoData()
            return binding.root
        }

        private fun generateTempoData() {
            // I got speed in range (0-50) and height in meters in range(200 - 300).
            // I want this chart to display both information. Differences between speed
            // and height values are large and chart doesn't look good so I need to
            // modify height values to be in range of speed values.

            // The same for displaying Tempo/Height chart.
            val minHeight = 200f
            val maxHeight = 300f
            val tempoRange = 15f // from 0min/km to 15min/km
            val scale = tempoRange / maxHeight
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

            // Tempo line is a little tricky because worse tempo means bigger value
            // for example 11min per km is worse than 2min per km but
            // the second should be higher on the chart. So you need to know max tempo
            // and tempoRange and set chart values to minTempo - realTempo.
            values = ArrayList()
            for (i in 0 until numValues) {
                // Some random raw tempo values.
                val realTempo = Math.random().toFloat() * 6 + 2
                val revertedTempo = tempoRange - realTempo
                values.add(PointValue(i.toFloat(), revertedTempo))
            }
            line = Line(values)
            line.color = ChartUtils.COLOR_RED
            line.setHasPoints(false)
            line.strokeWidth = 3
            lines.add(line)

            // Data and axes
            val data = LineChartData(lines)

            // Distance axis(bottom X) with formatter that will ad [km] to values, remember to modify max label charts
            // value.
            val distanceAxis = Axis()
            distanceAxis.name = "Distance"
            distanceAxis.textColor = ChartUtils.COLOR_ORANGE
            distanceAxis.maxLabelChars = 4
            distanceAxis.formatter = SimpleAxisValueFormatter().setAppendedText("km".toCharArray())
            distanceAxis.setHasLines(true)
            distanceAxis.setHasTiltedLabels(true)
            data.axisXBottom = distanceAxis

            // Tempo uses minutes so I can't use auto-generated axis because auto-generation works only for decimal
            // system. So generate custom axis values for example every 15 seconds and set custom labels in format
            // minutes:seconds(00:00), you could do it in formatter but here will be faster.
            val axisValues: MutableList<AxisValue> = ArrayList()
            var i = 0f
            while (i < tempoRange) {

                // I'am translating float to minutes because I don't have data in minutes, if You store some time data
                // you may skip translation.
                axisValues.add(AxisValue(i).setLabel(formatMinutes(tempoRange - i)))
                i += 0.25f
            }
            val tempoAxis =
                Axis(axisValues).setName("Tempo [min/km]").setHasLines(true).setMaxLabelChars(4)
                    .setTextColor(ChartUtils.COLOR_RED)
            data.axisYLeft = tempoAxis

            // *** Same as in Speed/Height chart.
            // Height axis, this axis need custom formatter that will translate values back to real height values.
            data.axisYRight = Axis().setName("Height [m]").setMaxLabelChars(3)
                .setFormatter(
                    HeightValueFormatter(
                        scale,
                        sub,
                        0
                    )
                )

            // Set data
            binding.chart.lineChartData = data

            // Important: adjust viewport,
            // you could skip this step but in this case it will looks better with custom viewport.
            // Set viewport with Y range 0-12;
            val viewport = binding.chart.maximumViewport
            viewport[viewport.left, tempoRange, viewport.right] = 0f
            binding.chart.maximumViewport = viewport
            binding.chart.currentViewport = viewport
        }

        private fun formatMinutes(value: Float): String {
            val sb = StringBuilder()

            // translate value to seconds, for example
            val valueInSeconds = (value * 60).toInt()
            val minutes = floor((valueInSeconds / 60).toDouble()).toInt()
            val seconds = valueInSeconds % 60
            sb.append(minutes).append(':')
            if (seconds < 10) {
                sb.append('0')
            }
            sb.append(seconds)
            return sb.toString()
        }

        /**
         * Recalculated height values to display on axis.
         * For this example I use auto-generated height axis so I
         * override only formatAutoValue method.
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