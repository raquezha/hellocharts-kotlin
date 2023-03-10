@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package lecho.lib.hellocharts.formatter

import android.util.Log
import lecho.lib.hellocharts.util.FloatUtils
import java.text.DecimalFormat
import java.text.NumberFormat

class ValueFormatterHelper {
    var decimalDigitsNumber = Int.MIN_VALUE
    var appendedText = CharArray(0)
    var prependedText = CharArray(0)
    var decimalSeparator = '.'


    fun determineDecimalSeparator() {
        val numberFormat = NumberFormat.getInstance()
        if (numberFormat is DecimalFormat) {
            decimalSeparator = numberFormat.decimalFormatSymbols.decimalSeparator
        }
    }

    fun setDecimalDigitsNumber(decimalDigitsNumber: Int): ValueFormatterHelper {
        this.decimalDigitsNumber = decimalDigitsNumber
        return this
    }

    fun setAppendedText(appendedText: CharArray?): ValueFormatterHelper {
        if (null != appendedText) {
            this.appendedText = appendedText
        }
        return this
    }

    fun setPrependedText(prependedText: CharArray?): ValueFormatterHelper {
        if (null != prependedText) {
            this.prependedText = prependedText
        }
        return this
    }

    fun setDecimalSeparator(decimalSeparator: Char): ValueFormatterHelper {
        val nullChar = '\u0000'
        if (nullChar != decimalSeparator) {
            this.decimalSeparator = decimalSeparator
        }
        return this
    }
    /**
     * Formats float value. Result is stored in (output) formattedValue array. Method
     * returns number of chars of formatted value. The formatted value starts at index [formattedValue.length -
     * charsNumber] and ends at index [formattedValue.length-1].
     * Note: If label is not null it will be used as formattedValue instead of float value.
     * Note: Parameter defaultDigitsNumber is used only if you didn't change decimalDigintsNumber value using
     * method [lecho.lib.hellocharts.formatter.ValueFormatterHelper.setDecimalDigitsNumber].
     */
    /**
     * @see lecho.lib.hellocharts.formatter.ValueFormatterHelper.formatFloatValueWithPrependedAndAppendedText
     */
    @JvmOverloads
    fun formatFloatValueWithPrependedAndAppendedText(
        formattedValue: CharArray,
        value: Float,
        defaultDigitsNumber: Int,
        label: CharArray? = null
    ): Int {
        if (null != label) {
            // If custom label is not null use only name characters as formatted value.
            // Copy label into formatted value array.
            var labelLength = label.size
            if (labelLength > formattedValue.size) {
                Log.w(
                    TAG,
                    "Label length is larger than buffer size(64chars)," +
                        " some chars will be skipped!"
                )
                labelLength = formattedValue.size
            }
            System.arraycopy(
                label,
                0,
                formattedValue,
                formattedValue.size - labelLength,
                labelLength
            )
            return labelLength
        }
        val appliedDigitsNumber = getAppliedDecimalDigitsNumber(defaultDigitsNumber)
        val charsNumber = formatFloatValue(formattedValue, value, appliedDigitsNumber)
        appendText(formattedValue)
        prependText(formattedValue, charsNumber)
        return charsNumber + prependedText.size + appendedText.size
    }

    /**
     * @see lecho.lib.hellocharts.formatter.ValueFormatterHelper.formatFloatValueWithPrependedAndAppendedText
     */
    fun formatFloatValueWithPrependedAndAppendedText(
        formattedValue: CharArray,
        value: Float,
        label: CharArray?
    ): Int {
        return formatFloatValueWithPrependedAndAppendedText(
            formattedValue,
            value,
            DEFAULT_DIGITS_NUMBER,
            label
        )
    }

    fun formatFloatValue(formattedValue: CharArray, value: Float, decimalDigitsNumber: Int): Int {
        return FloatUtils.formatFloat(
            formattedValue, value, formattedValue.size - appendedText.size,
            decimalDigitsNumber,
            decimalSeparator
        )
    }

    fun appendText(formattedValue: CharArray) {
        if (appendedText.isNotEmpty()) {
            System.arraycopy(
                appendedText, 0, formattedValue, formattedValue.size - appendedText.size,
                appendedText.size
            )
        }
    }

    fun prependText(formattedValue: CharArray, charsNumber: Int) {
        if (prependedText.isNotEmpty()) {
            System.arraycopy(
                prependedText,
                0,
                formattedValue,
                formattedValue.size - charsNumber - appendedText.size
                    - prependedText.size,
                prependedText.size
            )
        }
    }

    fun getAppliedDecimalDigitsNumber(defaultDigitsNumber: Int): Int {
        val appliedDecimalDigitsNumber: Int = if (decimalDigitsNumber < 0) {
            //When decimalDigitsNumber < 0 that means that user didn't set that value
            // and defaultDigits Number should be used.
            defaultDigitsNumber
        } else {
            decimalDigitsNumber
        }
        return appliedDecimalDigitsNumber
    }

    companion object {
        const val DEFAULT_DIGITS_NUMBER = 0
        private const val TAG = "ValueFormatterHelper"
    }
}