package net.raquezha.lecho.hellocharts.listener;


import net.raquezha.lecho.hellocharts.model.SubcolumnValue;

public interface ColumnChartOnValueSelectListener extends OnValueDeselectListener {

    void onValueSelected(int columnIndex, int subcolumnIndex, SubcolumnValue value);

}
