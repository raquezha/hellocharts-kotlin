package net.raquezha.lecho.hellocharts.formatter;

import net.raquezha.lecho.hellocharts.model.SubcolumnValue;

public interface ColumnChartValueFormatter {

    int formatChartValue(char[] formattedValue, SubcolumnValue value);

}
