@file:Suppress("unused",
    "MemberVisibilityCanBePrivate",
    "UNUSED_CHANGED_VALUE",
    "KotlinConstantConditions"
)

package lecho.lib.hellocharts.util

import java.lang.Float.floatToRawIntBits
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.roundToInt

object FloatUtils {
    private val POW10 = intArrayOf(1, 10, 100, 1000, 10000, 100000, 1000000)

    /**
     * Returns next bigger float value considering precision of the argument.
     */
    fun nextUpF(f: Float): Float {
        var value = f
        return if (java.lang.Float.isNaN(value) || value == Float.POSITIVE_INFINITY) {
            value
        } else {
            value += 0.0f
            java.lang.Float.intBitsToFloat(floatToRawIntBits(value) + if (value >= 0.0f) +1 else -1)
        }
    }

    /**
     * Returns next smaller float value considering precision of the argument.
     */
    fun nextDownF(f: Float): Float {
        return if (java.lang.Float.isNaN(f) || f == Float.NEGATIVE_INFINITY) {
            f
        } else {
            if (f == 0.0f) {
                -Float.MIN_VALUE
            } else {
                java.lang.Float.intBitsToFloat(floatToRawIntBits(f) + if (f > 0.0f) -1 else +1)
            }
        }
    }

    /**
     * Returns next bigger double value considering precision of the argument.
     */
    fun nextUp(d: Double): Double {
        var value = d
        return if (java.lang.Double.isNaN(value) || value == Double.POSITIVE_INFINITY) {
            value
        } else {
            value += 0.0
            java.lang.Double.longBitsToDouble(java.lang.Double.doubleToRawLongBits(value) + if (value >= 0.0) +1 else -1)
        }
    }

    /**
     * Returns next smaller float value considering precision of the argument.
     */
    fun nextDown(d: Double): Double {
        return if (java.lang.Double.isNaN(d) || d == Double.NEGATIVE_INFINITY) {
            d
        } else {
            if (d == 0.0) {
                (-Float.MIN_VALUE).toDouble()
            } else {
                java.lang.Double.longBitsToDouble(java.lang.Double.doubleToRawLongBits(d) + if (d > 0.0f) -1 else +1)
            }
        }
    }

    /**
     * Method checks if two float numbers are similar.
     */
    fun almostEqual(a: Float, b: Float, absoluteDiff: Float, relativeDiff: Float): Boolean {
        var value1 = a
        var value2 = b
        val diff = abs(value1 - value2)
        if (diff <= absoluteDiff) {
            return true
        }
        value1 = abs(value1)
        value2 = abs(value2)
        val largest = if (value1 > value2) value1 else value2
        return diff <= largest * relativeDiff
    }

    /**
     * Rounds the given number to the given number of significant digits. Based on an answer on [Stack Overflow](http://stackoverflow.com/questions/202302).
     */
    fun roundToOneSignificantFigure(num: Double): Float {
        val d = ceil(log10(if (num < 0) -num else num).toFloat().toDouble()).toFloat()
        val power = 1 - d.toInt()
        val magnitude = 10.0.pow(power.toDouble()).toFloat()
        val shifted = (num * magnitude).roundToInt()
        return shifted / magnitude
    }

    /**
     * Formats a float value to the given number of decimals. Returns the length of the string. The string begins at
     * [endIndex] - [return value] and ends at [endIndex]. It's up to you to check indexes correctness.
     * Parameter [endIndex] can be helpful when you want to append some text to formatted value.
     *
     * @return number of characters of formatted value
     */
    fun formatFloat(
        formattedValue: CharArray,
        floatValue: Float,
        endIndex: Int,
        digitsValue: Int,
        separator: Char
    ): Int {
        var value = floatValue
        var digits = digitsValue
        if (digits >= POW10.size) {
            formattedValue[endIndex - 1] = '.'
            return 1
        }
        var negative = false
        if (value == 0f) {
            formattedValue[endIndex - 1] = '0'
            return 1
        }
        if (value < 0) {
            negative = true
            value = -value
        }
        if (digits > POW10.size) {
            digits = POW10.size - 1
        }
        value *= POW10[digits].toFloat()
        var longValue = value.roundToInt().toLong()
        var index = endIndex - 1
        var charsNumber = 0
        while (longValue != 0L || charsNumber < digits + 1) {
            val digit = (longValue % 10).toInt()
            longValue /= 10
            formattedValue[index--] = (digit + '0'.code).toChar()
            charsNumber++
            if (charsNumber == digits) {
                formattedValue[index--] = separator
                charsNumber++
            }
        }
        if (formattedValue[index + 1] == separator) {
            formattedValue[index--] = '0'
            charsNumber++
        }
        if (negative) {
            formattedValue[index--] = '-'
            charsNumber++
        }
        return charsNumber
    }

    /**
     * Computes the set of axis labels to show given start and stop boundaries and an ideal number of stops between
     * these boundaries.
     *
     * @param start     The minimum extreme (e.g. the left edge) for the axis.
     * @param stop      The maximum extreme (e.g. the right edge) for the axis.
     * @param steps     The ideal number of stops to create. This should be based on available screen space; the more
     * space
     * there is, the more stops should be shown.
     * @param outValues The destination [AxisAutoValues] object to populate.
     */
    @JvmStatic
    fun computeAutoGeneratedAxisValues(
        start: Float,
        stop: Float,
        steps: Int,
        outValues: AxisAutoValues
    ) {
        val range = (stop - start).toDouble()
        if (steps == 0 || range <= 0) {
            outValues.values = floatArrayOf()
            outValues.valuesNumber = 0
            return
        }
        val rawInterval = range / steps
        var interval = roundToOneSignificantFigure(rawInterval).toDouble()
        val intervalMagnitude = 10.0.pow(log10(interval).toInt().toDouble())
        val intervalSigDigit = (interval / intervalMagnitude).toInt()
        if (intervalSigDigit > 5) {
            // Use one order of magnitude higher, to avoid intervals like 0.9 or 90
            interval = floor(10 * intervalMagnitude)
        }
        val first = ceil(start / interval) * interval
        val last = nextUp(floor(stop / interval) * interval)
        var valuesNum = 0
        var intervalValue: Double = first
        while (intervalValue <= last) {
            ++valuesNum
            intervalValue += interval
        }
        outValues.valuesNumber = valuesNum
        if (outValues.values.size < valuesNum) {
            // Ensure stops contains at least numStops elements.
            outValues.values = FloatArray(valuesNum)
        }
        intervalValue = first
        var valueIndex = 0
        while (valueIndex < valuesNum) {
            outValues.values[valueIndex] = intervalValue.toFloat()
            intervalValue += interval
            ++valueIndex
        }
        if (interval < 1) {
            outValues.decimals = ceil(-log10(interval)).toInt()
        } else {
            outValues.decimals = 0
        }
    }
}