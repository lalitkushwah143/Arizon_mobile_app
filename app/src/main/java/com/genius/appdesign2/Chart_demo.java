package com.genius.appdesign2;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class Chart_demo extends AppCompatActivity {

    LineChart lineChart;
    LineData lineData;
    List<Entry> entryList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chart_demo);
        lineChart = findViewById(R.id.activity_main_linechart);
        entryList.add(new Entry(0,45));
        entryList.add(new Entry(45,65));




        LineDataSet lineDataSet = new LineDataSet(entryList,"country");
        lineData = new LineData(lineDataSet);
        lineDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        lineDataSet.setFillAlpha(110);

//        Log.e("error",(lineData).toString());
        lineChart.setData(lineData);
        lineChart.setVisibleXRangeMaximum(10);
         lineChart.invalidate();


    }


}