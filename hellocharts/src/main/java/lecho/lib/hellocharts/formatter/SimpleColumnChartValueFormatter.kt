@file:Suppress("unused")

package lecho.lib.hellocharts.formatter

import lecho.lib.hellocharts.model.SubcolumnValue

class SimpleColumnChartValueFormatter() : ColumnChartValueFormatter {
    private val valueFormatterHelper = ValueFormatterHelper()

    init {
        valueFormatterHelper.determineDecimalSeparator()
    }

    constructor(decimalDigitsNumber: Int) : this() {
        valueFormatterHelper.decimalDigitsNumber = decimalDigitsNumber
    }

    override fun formatChartValue(formattedValue: CharArray, value: SubcolumnValue): Int {
        return valueFormatterHelper.formatFloatValueWithPrependedAndAppendedText(
            formattedValue, value.value,
            value.labelAsChars
        )
    }

    val decimalDigitsNumber: Int
        get() = valueFormatterHelper.decimalDigitsNumber

    fun setDecimalDigitsNumber(decimalDigitsNumber: Int): SimpleColumnChartValueFormatter {
        valueFormatterHelper.decimalDigitsNumber = decimalDigitsNumber
        return this
    }

    val appendedText: CharArray
        get() = valueFormatterHelper.appendedText

    fun setAppendedText(appendedText: CharArray): SimpleColumnChartValueFormatter {
        valueFormatterHelper.appendedText = appendedText
        return this
    }

    val prependedText: CharArray
        get() = valueFormatterHelper.prependedText

    fun setPrependedText(prependedText: CharArray): SimpleColumnChartValueFormatter {
        valueFormatterHelper.prependedText = prependedText
        return this
    }

    val decimalSeparator: Char
        get() = valueFormatterHelper.decimalSeparator

    fun setDecimalSeparator(decimalSeparator: Char): SimpleColumnChartValueFormatter {
        valueFormatterHelper.decimalSeparator = decimalSeparator
        return this
    }
}