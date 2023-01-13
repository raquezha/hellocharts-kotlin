package net.raquezha.lecho.hellocharts.listener;


import net.raquezha.lecho.hellocharts.model.BubbleValue;

public interface BubbleChartOnValueSelectListener extends OnValueDeselectListener {

    void onValueSelected(int bubbleIndex, BubbleValue value);

}
