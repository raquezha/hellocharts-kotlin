package net.raquezha.lecho.hellocharts.provider;


import net.raquezha.lecho.hellocharts.model.ColumnChartData;

public interface ColumnChartDataProvider {

    ColumnChartData getColumnChartData();

    void setColumnChartData(ColumnChartData data);

}
