package net.raquezha.lecho.hellocharts.formatter;


import net.raquezha.lecho.hellocharts.model.PointValue;

public interface LineChartValueFormatter {

    int formatChartValue(char[] formattedValue, PointValue value);
}
