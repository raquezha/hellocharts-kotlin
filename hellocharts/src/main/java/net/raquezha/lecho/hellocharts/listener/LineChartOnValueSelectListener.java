package net.raquezha.lecho.hellocharts.listener;


import net.raquezha.lecho.hellocharts.model.PointValue;

public interface LineChartOnValueSelectListener extends OnValueDeselectListener {

    void onValueSelected(int lineIndex, int pointIndex, PointValue value);

}
