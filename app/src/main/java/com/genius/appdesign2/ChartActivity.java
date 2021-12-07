package com.genius.appdesign2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.genius.appdesign2.data.DataJob;
import com.genius.appdesign2.data.DataRealTime;
import com.genius.appdesign2.data.DataRecipe;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class ChartActivity extends AppCompatActivity {

    private LineChart chart;
    private LineData data = new LineData();
    private ArrayList<DataRecipe> arrayList= LoadRecipeActivity.recipeArrayList;
    private float current_time=0, current_temp=0, current_pressure= 800;
    private float x, y;
    private ArrayList<Entry> entries = new ArrayList<>();
    private ArrayList<Entry> entries1 = new ArrayList<>();
    private ArrayList<Entry> entries3 = new ArrayList<>();
    ArrayList<ILineDataSet> dataSets = new ArrayList<>();
    private LineDataSet dataSet, dataSet1, realSet;
    private Thread thread;
    private float real_time=0, real_temp=0;
    private int total_time = 1675;
    private Boolean FLAG = false;
    private Boolean FLAG2 = false;
    private TextView tvPressure, tvTemp;
    private FirebaseFirestore firestore;
    private ArrayList<Float> temp_points = new ArrayList<>();
    private Date now = new Date();
    private String recipe_name, recipe_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        chart = findViewById(R.id.activity_chart_chart);
        tvPressure = findViewById(R.id.activity_chart_tvPressure);
        tvTemp = findViewById(R.id.activity_chart_tvTemp);

        getSupportActionBar().hide();
        TextView textView = findViewById(R.id.activity_chart_tvTitle);
        textView.setText("LYO Chart");
        ImageView imageView = findViewById(R.id.activity_chart_imageview);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recipe_id = getIntent().getStringExtra("recipe_id");
        recipe_name = getIntent().getStringExtra("recipe_name");
        firestore = FirebaseFirestore.getInstance();

        loadData();
        dataSet = new LineDataSet(entries, "Temperature");
        dataSet1 = new LineDataSet(entries1, "Pressure");

        dataSet.setColor(getResources().getColor(R.color.chart_temp));
        dataSet.setCircleColor(getResources().getColor(R.color.chart_temp));
        dataSet1.setColor(getResources().getColor(R.color.chart_pressure));
        dataSet1.setCircleColor(getResources().getColor(R.color.chart_pressure));

        chart.getLegend().setTextColor(getResources().getColor(R.color.white));
        chart.getDescription().setTextColor(getResources().getColor(R.color.white));
    //    realSet.setValueTextColor(getResources().getColor(R.color.white));
     //   dataSet1.setValueTextColor(getResources().getColor(R.color.white));

        realSet = new LineDataSet(entries3, "RealTime Data");
        realSet.setColor(getResources().getColor(R.color.chart_realtime));
        realSet.setCircleColor(getResources().getColor(R.color.chart_realtime));

        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSet1.setAxisDependency(YAxis.AxisDependency.RIGHT);
        realSet.setAxisDependency(YAxis.AxisDependency.LEFT);




        dataSets.add(dataSet);
        dataSets.add(dataSet1);
        dataSets.add(realSet);

        data =new LineData(dataSets);
        chart.setData(data);
        chart.invalidate();

        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setPinchZoom(true);
        chart.setHighlightPerDragEnabled(true);

        feedRealtime();
    }

    private void loadData() {

        for (int i=0; i< arrayList.size(); i++){
            DataRecipe lyo = arrayList.get(i);
            x = (float) (lyo.getTemp1() - current_temp)/lyo.getTime1();
            y = (float) (lyo.getPressure() - current_pressure)/lyo.getTime1();
            Log.e("This:  ", x+" "+current_time+" "+ current_temp+" ");

            for (int j=0; j<lyo.getTime1(); j++){
                current_time++;
                current_temp = current_temp + x;
                current_pressure = current_pressure + y;
                entries.add(new Entry(current_time, current_temp));
                entries1.add(new Entry(current_time, current_pressure));
            }
            Log.e("This:  ", current_time+" "+ current_temp+" ");

            for (int k=0; k<lyo.getTime2(); k++){
                current_time++;
                entries.add(new Entry(current_time, current_temp));
                entries1.add(new Entry(current_time, current_pressure));
            }
            Log.e("This:  ", current_time+" "+ current_temp+" ");

        }
    }

    private void feedRealtime() {
        entries3.add(new Entry(0, 0));
        temp_points.clear();
        temp_points.add(0, 0f);

        if (thread != null)
            thread.interrupt();

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                addEntry();
            }
        };
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i=0; i< arrayList.size(); i++) {
                    DataRecipe lyo = arrayList.get(i);
                    float temp_pre = 0;
                    if (i==0){
                        temp_pre =0f;
                    }else {
                        DataRecipe lyo1 = arrayList.get(i-1);
                        temp_pre = (float) lyo1.getTemp1();
                    }
                    x = (float) (lyo.getTemp1() - temp_pre) / lyo.getTime1();
                    FLAG2 = real_temp < lyo.getTemp1();
                    if (FLAG2){
                        Log.e("Randomx", "Section 1");
                        for (int j = 0; j < (lyo.getTime2()+ lyo.getTime1()); j++) {
                            real_time++;
                            if (real_temp < lyo.getTemp1()){
                                Random random = new Random();
                                if (x==0){
                                    Log.e("Chart", "Nothing");
                                }else if (x < 0) {
                                    Log.e("Random ", "Yes");
                                    float z= -x;
                                    int bound = (int) (z*(10000));
                                    Log.e("Random ", bound+ "");

                                    float abc = ((float)random.nextInt(bound))/10000f;
                                    Log.e("Sample" , " "+abc +" "+real_temp );
                                    real_temp = real_temp - abc;
                                } else {
                                    int bound = (int) (x*(10000));
                                    Log.e("Random ", bound+ "");

                                    float abc = ((float)random.nextInt(bound))/10000f;
                                    Log.e("Sample" , " "+abc +" "+real_temp );
                                    real_temp = real_temp + abc;
                                }
                            }else {
                                real_temp = lyo.getTemp1();
                            }
                            runOnUiThread(runnable);
                            try {
                                Thread.sleep(25);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            //addEntry();
                            // entries3.add(new Entry(real_time, real_temp));

                        }
                    }else {
                        Log.e("Randomx", "Section 2");
                        for (int j = 0; j < (lyo.getTime2()+ lyo.getTime1()); j++) {
                            real_time++;
                            if (real_temp > lyo.getTemp1()){
                                Random random = new Random();
                                if (x==0){
                                    Log.e("Chart", "Nothing");
                                }else if (x < 0) {
                                    Log.e("Random ", "Yes");
                                    float z= -x;
                                    int bound = (int) (z*(10000));
                                    Log.e("Random ", bound+ "");

                                    float abc = ((float)random.nextInt(bound))/10000f;
                                    Log.e("Sample" , " "+abc +" "+real_temp );
                                    real_temp = real_temp - abc;
                                } else {
                                    int bound = (int) (x*(10000));
                                    Log.e("Random ", bound+ "");

                                    float abc = ((float)random.nextInt(bound))/10000f;
                                    Log.e("Sample" , " "+abc +" "+real_temp );
                                    real_temp = real_temp + abc;
                                }
                            }else {
                                real_temp = lyo.getTemp1();
                            }

                            runOnUiThread(runnable);
                            try {
                                Thread.sleep(25);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            // addEntry();
                            //  entries3.add(new Entry(real_time, real_temp));
                        }
                    }

                }


            }
        });
        thread.start();

    }

    private void addEntry() {
        entries3.add(new Entry(real_time, real_temp));
        temp_points.add(real_temp);
        realSet.notifyDataSetChanged();
        data.notifyDataChanged();
        chart.notifyDataSetChanged();
        chart.moveViewToX(entries3.size());
        tvPressure.setText("Time: " + (int)real_time);
        tvTemp.setText("Temp: " +real_temp);

        DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        if (entries.size() == entries3.size() && recipe_name != null && recipe_id != null){
            if (temp_points!= null){
                firestore.collection("realtimeData")
                        .add(new DataRealTime( recipe_name, recipe_id, temp_points, now.toString()))
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.e("ChartActivity", "Updated realtime");
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("ChartActivity", "Updated failed");
                        finish();
                    }
                });
            }
        }
    }
}