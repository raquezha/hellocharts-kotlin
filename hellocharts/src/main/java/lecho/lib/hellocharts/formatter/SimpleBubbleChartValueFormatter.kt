@file:Suppress("unused")

package lecho.lib.hellocharts.formatter

import lecho.lib.hellocharts.model.BubbleValue

class SimpleBubbleChartValueFormatter() : BubbleChartValueFormatter {
    private val valueFormatterHelper = ValueFormatterHelper()

    init {
        valueFormatterHelper.determineDecimalSeparator()
    }

    constructor(decimalDigitsNumber: Int) : this() {
        valueFormatterHelper.decimalDigitsNumber = decimalDigitsNumber
    }

    override fun formatChartValue(formattedValue: CharArray, value: BubbleValue): Int {
        return valueFormatterHelper.formatFloatValueWithPrependedAndAppendedText(
            formattedValue, value.z, value
                .labelAsChars
        )
    }

    val decimalDigitsNumber: Int
        get() = valueFormatterHelper.decimalDigitsNumber

    fun setDecimalDigitsNumber(decimalDigitsNumber: Int): SimpleBubbleChartValueFormatter {
        valueFormatterHelper.decimalDigitsNumber = decimalDigitsNumber
        return this
    }

    val appendedText: CharArray
        get() = valueFormatterHelper.appendedText

    fun setAppendedText(appendedText: CharArray): SimpleBubbleChartValueFormatter {
        valueFormatterHelper.appendedText = appendedText
        return this
    }

    val prependedText: CharArray
        get() = valueFormatterHelper.prependedText

    fun setPrependedText(prependedText: CharArray): SimpleBubbleChartValueFormatter {
        valueFormatterHelper.prependedText = prependedText
        return this
    }

    val decimalSeparator: Char
        get() = valueFormatterHelper.decimalSeparator

    fun setDecimalSeparator(decimalSeparator: Char): SimpleBubbleChartValueFormatter {
        valueFormatterHelper.decimalSeparator = decimalSeparator
        return this
    }
}