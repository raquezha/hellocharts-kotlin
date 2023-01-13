package net.raquezha.lecho.hellocharts.listener;


import net.raquezha.lecho.hellocharts.model.PointValue;
import net.raquezha.lecho.hellocharts.model.SubcolumnValue;

public interface ComboLineColumnChartOnValueSelectListener extends OnValueDeselectListener {

    void onColumnValueSelected(int columnIndex, int subColumnIndex, SubcolumnValue value);

    void onPointValueSelected(int lineIndex, int pointIndex, PointValue value);

}
