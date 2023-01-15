@file:Suppress("unused")

package lecho.lib.hellocharts.formatter

import lecho.lib.hellocharts.model.PointValue

class SimpleLineChartValueFormatter() : LineChartValueFormatter {
    private val valueFormatterHelper = ValueFormatterHelper()

    init {
        valueFormatterHelper.determineDecimalSeparator()
    }

    constructor(decimalDigitsNumber: Int) : this() {
        valueFormatterHelper.decimalDigitsNumber = decimalDigitsNumber
    }

    override fun formatChartValue(formattedValue: CharArray, value: PointValue): Int {
        return valueFormatterHelper.formatFloatValueWithPrependedAndAppendedText(
            formattedValue, value.y, value
                .labelAsChars
        )
    }

    val decimalDigitsNumber: Int
        get() = valueFormatterHelper.decimalDigitsNumber

    fun setDecimalDigitsNumber(decimalDigitsNumber: Int): SimpleLineChartValueFormatter {
        valueFormatterHelper.decimalDigitsNumber = decimalDigitsNumber
        return this
    }

    val appendedText: CharArray
        get() = valueFormatterHelper.appendedText

    fun setAppendedText(appendedText: CharArray): SimpleLineChartValueFormatter {
        valueFormatterHelper.appendedText = appendedText
        return this
    }

    val prependedText: CharArray
        get() = valueFormatterHelper.prependedText

    fun setPrependedText(prependedText: CharArray): SimpleLineChartValueFormatter {
        valueFormatterHelper.prependedText = prependedText
        return this
    }

    val decimalSeparator: Char
        get() = valueFormatterHelper.decimalSeparator

    fun setDecimalSeparator(decimalSeparator: Char): SimpleLineChartValueFormatter {
        valueFormatterHelper.decimalSeparator = decimalSeparator
        return this
    }
}