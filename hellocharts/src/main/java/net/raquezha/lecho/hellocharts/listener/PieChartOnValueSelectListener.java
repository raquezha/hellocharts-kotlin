package net.raquezha.lecho.hellocharts.listener;


import net.raquezha.lecho.hellocharts.model.SliceValue;

public interface PieChartOnValueSelectListener extends OnValueDeselectListener {

    void onValueSelected(int arcIndex, SliceValue value);

}
