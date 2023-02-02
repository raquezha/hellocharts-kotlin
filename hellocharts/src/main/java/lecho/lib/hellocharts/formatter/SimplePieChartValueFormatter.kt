@file:Suppress("unused")

package lecho.lib.hellocharts.formatter

import lecho.lib.hellocharts.model.SliceValue

class SimplePieChartValueFormatter() : PieChartValueFormatter {
    private val valueFormatterHelper = ValueFormatterHelper()

    init {
        valueFormatterHelper.determineDecimalSeparator()
    }

    constructor(decimalDigitsNumber: Int) : this() {
        valueFormatterHelper.decimalDigitsNumber = decimalDigitsNumber
    }

    override fun formatChartValue(formattedValue: CharArray, value: SliceValue): Int {
        return valueFormatterHelper.formatFloatValueWithPrependedAndAppendedText(
            formattedValue, value.value,
            value.labelAsChars
        )
    }

    val decimalDigitsNumber: Int
        get() = valueFormatterHelper.decimalDigitsNumber

    fun setDecimalDigitsNumber(decimalDigitsNumber: Int): SimplePieChartValueFormatter {
        valueFormatterHelper.decimalDigitsNumber = decimalDigitsNumber
        return this
    }

    val appendedText: CharArray
        get() = valueFormatterHelper.appendedText

    fun setAppendedText(appendedText: CharArray): SimplePieChartValueFormatter {
        valueFormatterHelper.appendedText = appendedText
        return this
    }

    val prependedText: CharArray
        get() = valueFormatterHelper.prependedText

    fun setPrependedText(prependedText: CharArray): SimplePieChartValueFormatter {
        valueFormatterHelper.prependedText = prependedText
        return this
    }

    val decimalSeparator: Char
        get() = valueFormatterHelper.decimalSeparator

    fun setDecimalSeparator(decimalSeparator: Char): SimplePieChartValueFormatter {
        valueFormatterHelper.decimalSeparator = decimalSeparator
        return this
    }
}