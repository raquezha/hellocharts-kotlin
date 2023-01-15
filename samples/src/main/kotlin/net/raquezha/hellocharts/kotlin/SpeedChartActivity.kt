package net.raquezha.lecho.samples;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import net.raquezha.lecho.hellocharts.formatter.SimpleAxisValueFormatter;
import net.raquezha.lecho.hellocharts.model.Axis;
import net.raquezha.lecho.hellocharts.model.Line;
import net.raquezha.lecho.hellocharts.model.LineChartData;
import net.raquezha.lecho.hellocharts.model.PointValue;
import net.raquezha.lecho.hellocharts.model.Viewport;
import net.raquezha.lecho.hellocharts.util.ChartUtils;
import net.raquezha.lecho.hellocharts.view.LineChartView;

import java.util.ArrayList;
import java.util.List;

public class SpeedChartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tempo_chart);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
        }
    }

    public static class PlaceholderFragment extends Fragment {

        private LineChartView chart;
        private LineChartData data;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_tempo_chart, container, false);

            chart = rootView.findViewById(R.id.chart);

            generateSpeedData();

            return rootView;
        }

        private void generateSpeedData() {
            // I got speed in range (0-55) and height in meters in range(200 - 300). I want this chart to display both
            // information. Differences between speed and height values are large and chart doesn't look good so I need
            // to modify height values to be in range of speed values.

            float speedRange = 55;
            float minHeight = 200;
            float maxHeight = 300;

            float scale = speedRange / maxHeight;
            float sub = (minHeight * scale) / 2;

            int numValues = 52;

            Line line;
            List<PointValue> values;
            List<Line> lines = new ArrayList<>();

            // Height line, add it as first line to be drawn in the background.
            values = new ArrayList<>();
            for (int i = 0; i < numValues; ++i) {
                // Some random height values, add +200 to make line a little more natural
                float rawHeight = (float) (Math.random() * 100 + 200);
                float normalizedHeight = rawHeight * scale - sub;
                values.add(new PointValue(i, normalizedHeight));
            }

            line = new Line(values);
            line.setColor(Color.GRAY);
            line.setHasPoints(false);
            line.setFilled(true);
            line.setStrokeWidth(1);
            lines.add(line);

            // Speed line
            values = new ArrayList<>();
            for (int i = 0; i < numValues; ++i) {
                // Some random speed values, add +20 to make line a little more natural.
                values.add(new PointValue(i, (float) Math.random() * 30 + 20));
            }

            line = new Line(values);
            line.setColor(ChartUtils.COLOR_GREEN);
            line.setHasPoints(false);
            line.setStrokeWidth(3);
            lines.add(line);

            // Data and axes
            data = new LineChartData(lines);

            // Distance axis(bottom X) with formatter that will ad [km] to values, remember to modify max label charts
            // value.
            Axis distanceAxis = new Axis();
            distanceAxis.setName("Distance");
            distanceAxis.setTextColor(ChartUtils.COLOR_ORANGE);
            distanceAxis.setMaxLabelChars(4);
            distanceAxis.setFormatter(new SimpleAxisValueFormatter().setAppendedText("km".toCharArray()));
            distanceAxis.setHasLines(true);
            distanceAxis.setInside(true);
            data.setAxisXBottom(distanceAxis);

            // Speed axis
            data.setAxisYLeft(new Axis().setName("Speed [km/h]").setHasLines(true).setMaxLabelChars(3)
                    .setTextColor(ChartUtils.COLOR_RED).setInside(true));

            // Height axis, this axis need custom formatter that will translate values back to real height values.
            data.setAxisYRight(new Axis().setName("Height [m]").setMaxLabelChars(3).setTextColor(ChartUtils.COLOR_BLUE)
                    .setFormatter(new HeightValueFormatter(scale, sub, 0)).setInside(true));

            // Set data
            chart.setLineChartData(data);

            // Important: adjust viewport, you could skip this step but in this case it will looks better with custom
            // viewport. Set
            // viewport with Y range 0-55;
            Viewport v = chart.getMaximumViewport();
            v.set(v.left, speedRange, v.right, 0);
            chart.setMaximumViewport(v);
            chart.setCurrentViewport(v);

        }

        /**
         * Recalculated height values to display on axis.
         */
        private static class HeightValueFormatter extends SimpleAxisValueFormatter {

            private final float scale;
            private final float sub;
            private final int decimalDigits;

            public HeightValueFormatter(float scale, float sub, int decimalDigits) {
                this.scale = scale;
                this.sub = sub;
                this.decimalDigits = decimalDigits;
            }

            @Override
            public int formatValueForAutoGeneratedAxis(char[] formattedValue, float value, int autoDecimalDigits) {
                float scaledValue = (value + sub) / scale;
                return super.formatValueForAutoGeneratedAxis(formattedValue, scaledValue, this.decimalDigits);
            }
        }

    }
}
