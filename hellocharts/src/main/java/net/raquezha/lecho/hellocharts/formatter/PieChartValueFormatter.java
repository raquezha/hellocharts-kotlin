package net.raquezha.lecho.hellocharts.formatter;

import net.raquezha.lecho.hellocharts.model.SliceValue;

public interface PieChartValueFormatter {

    int formatChartValue(char[] formattedValue, SliceValue value);
}
