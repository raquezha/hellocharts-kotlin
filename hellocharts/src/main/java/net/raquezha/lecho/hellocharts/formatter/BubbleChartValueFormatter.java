package net.raquezha.lecho.hellocharts.formatter;

import net.raquezha.lecho.hellocharts.model.BubbleValue;

public interface BubbleChartValueFormatter {

    int formatChartValue(char[] formattedValue, BubbleValue value);
}
