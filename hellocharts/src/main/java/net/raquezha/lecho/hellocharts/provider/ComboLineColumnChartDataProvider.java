package net.raquezha.lecho.hellocharts.provider;

import net.raquezha.lecho.hellocharts.model.ComboLineColumnChartData;

public interface ComboLineColumnChartDataProvider {

    @SuppressWarnings("unused")
    ComboLineColumnChartData getComboLineColumnChartData();

    void setComboLineColumnChartData(ComboLineColumnChartData data);

}
