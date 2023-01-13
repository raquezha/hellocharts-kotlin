package net.raquezha.lecho.hellocharts.provider;

import net.raquezha.lecho.hellocharts.model.LineChartData;

public interface LineChartDataProvider {

    LineChartData getLineChartData();

    void setLineChartData(LineChartData data);

}
