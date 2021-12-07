package com.genius.appdesign2;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MonitorActivity extends AppCompatActivity {

    private TextView tvTime;
    private BarChart chart, barChart2;
    private ArrayList<BarEntry> entries=new ArrayList<>();
    private ArrayList<BarEntry> entries2=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);

        getSupportActionBar().setTitle("Monitor");

        tvTime=findViewById(R.id.activity_monitor_tvTime);
        chart=findViewById(R.id.activity_monitor_barchart);
        barChart2=findViewById(R.id.activity_monitor_barchart2);
        Button button = findViewById(R.id.button2);

       // DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("skilltest");

        DatabaseReference  databaseReference = FirebaseDatabase.getInstance().getReference().child("DQ");
        Thread thread = new Thread(){
            @Override
            public void run() {
                try {
                    synchronized (this) {
                        wait(5000);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TextView mTextView = new TextView(MonitorActivity.this);
                                mTextView.setText("YourText");
                                mTextView.setTextColor(Color.WHITE);
                                mTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32);
                                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                                params.setMargins(150, 150, 50, 50);
                                params.gravity = Gravity.CENTER;
                                addContentView(mTextView, params);
                            }
                        });

                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            };
        };
        thread.start();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Content for mcq skill test
        /*        ArrayList<String> list1 = new ArrayList<>();
                list1.add(0, "characterization of microorganisms");
                list1.add(1, "destroying microorganisms");
                list1.add(2, "preservation of microorganisms");
                list1.add(3, "regulating microorganisms");
                String key1 = databaseReference.push().getKey();
                DataMCQ dataMCQ1 = new DataMCQ(key1, "Lyophilization is a method of ________.", list1, 2);
                databaseReference.child(key1).setValue(dataMCQ1);

                ArrayList<String> list2 = new ArrayList<>();
                list2.add(0, "ethylene oxide");
                list2.add(1, "methane");
                list2.add(2, "chloroform");
                list2.add(3, "ozone");
                String key2 = databaseReference.push().getKey();
                DataMCQ dataMCQ2 = new DataMCQ(key2, "The iodine-organic carrier complex iodophore is __________.", list2, 0);
                databaseReference.child(key2).setValue(dataMCQ2);

                ArrayList<String> list3 = new ArrayList<>();
                list3.add(0, "Lyophilisation");
                list3.add(1, "Cold pasteurization");
                list3.add(2, "Irradiation");
                list3.add(3, "Liposuction");
                String key3 =  databaseReference.push().getKey();
                DataMCQ dataMCQ3 = new DataMCQ(key3  , "What is alternative name for freeze drying?", list3, 0);
                databaseReference.child(key3).setValue(dataMCQ3);
                ArrayList<String> list4 = new ArrayList<>();
                list4.add(0, "10 to 40 hours");
                list4.add(1, "20 to 40 hours");
                list4.add(2, "30 to 50 hours");
                list4.add(3, "5 to 20 hours");
                String key4 = databaseReference.push().getKey();
                DataMCQ dataMCQ4 = new DataMCQ(key4 , "How long does freeze drying take?", list4, 2);
                databaseReference.child(key4).setValue(dataMCQ4);
                ArrayList<String> list5 = new ArrayList<>();
                list5.add(0, "Jacques-Arsened’Arsonval");
                list5.add(1, "Charles Chamberland");
                list5.add(2, "Willis Carrier");
                list5.add(3, "Clarence Birdseye");
                String key5 = databaseReference.push().getKey();
                DataMCQ dataMCQ5 = new DataMCQ(key5 , "Who discovered freeze drying?", list5, 0);
                databaseReference.child(key5).setValue(dataMCQ5);

         */

                // Content for design Qualification
/*
                ArrayList<DataSingle> arrayList = new ArrayList<>();

                arrayList.add(new DataSingle("No of Compressors and Design ", "Two x compressors, Cascade Design  Make Copeland Scroll 1 x 3.5 HP Hi Stage and 1 x 2 HP Lo Stage "));
                arrayList.add(new DataSingle("Refrigerant", "R 404A CFC Free and R 508B/R23 "));
                arrayList.add(new DataSingle("Heat Exchangers ", "High Efficiency BPHE_ SS316_ Make Alfa-Laval "));


                ArrayList<DataSingle> arrayList1 = new ArrayList<>();

                arrayList1.add(new DataSingle("Shelf Dimensions", "W 12” x D 24” x ½” T  "));
                arrayList1.add(new DataSingle("No of Shelves ", "3+1 radiant"));
                arrayList1.add(new DataSingle("Total Shelf Area ", "6 SFT or 0.559m2 "));

                arrayList1.add(new DataSingle("Shelf Clearance ", "89mm (up to 100ml Vials with Shelf Latching) "));
                arrayList1.add(new DataSingle("MOC of the Shelves ", "SS316L "));
                arrayList1.add(new DataSingle("Finish ", "Pharma Finish _Better than 220 Grit "));

                arrayList1.add(new DataSingle("Shelf Temperature Range ", "-60 to + 650C "));
                arrayList1.add(new DataSingle("Cooling time to -400C ", "60 Minutes "));
                arrayList1.add(new DataSingle("Shelf Uniformity ", "±1.5 0C across and ±1.5 0C between the shelves "));

                arrayList1.add(new DataSingle("Heater Capacity ", "1 KW "));
                arrayList1.add(new DataSingle("Heating Rate ", "Greater than 10C "));
                arrayList1.add(new DataSingle(" Shelf Fluid ", "Momentive Silicon Oil M5, Volume around 15Ltrs "));

                arrayList1.add(new DataSingle("Fluid Pump ", "Canned Rotor, Magnetic Drive Grundfos "));
                arrayList1.add(new DataSingle("Stoppering", "Bottom Up Hydraulic Stoppering "));

                ArrayList<DataSingle> arrayList2 = new ArrayList<>();
                arrayList2.add(new DataSingle("Maximum Low Temperature", "-800C "));
                arrayList2.add(new DataSingle("Type of Condenser ", "Coil "));
                arrayList2.add(new DataSingle("MOC of the Shelves ", "SS316L"));

                arrayList2.add(new DataSingle("Finish", "Pharma Finish _Better than 220 Grit "));
                arrayList2.add(new DataSingle("Condenser Capacity", "Total Capacity 20 Kgs  Capacity 12 Kgs/24 Hours "));
                arrayList2.add(new DataSingle("Condenser Area ", "5 SFT or 0.47 m2 "));

                arrayList2.add(new DataSingle("Cooling time to -400C ", "20 Minutes "));
                arrayList2.add(new DataSingle("Defrost System ", "Hot Gas Defrost "));
                arrayList2.add(new DataSingle("Control", "Automatic "));

                ArrayList<DataSingle> arrayList3 = new ArrayList<>();
                arrayList3.add(new DataSingle("Vacuum Pumps and Design ", "Pfeiffer /Edwards Vacuum Model SD 2015/E2M18 "));
                arrayList3.add(new DataSingle("Ultimate Vacuum ", "10 mTorr with Two Stage Pump "));
                arrayList3.add(new DataSingle("Pump down time to 100mTorr", "30 Minutes "));

                arrayList3.add(new DataSingle("Chambers Leak Rate ", "Less than 30mT_L/S "));
                arrayList3.add(new DataSingle("Sensor Type ", "Granville Phillips / Thyracont Pirani, and Capacitance Manometer with Optional PVCM Software "));
                arrayList3.add(new DataSingle("Control Range ", "10-1000 millitorr with combination solenoid and needle valve "));

                arrayList3.add(new DataSingle("Power Failure Protection ", "Diaphragm valve between the condenser and the pump to prevent oil backstreaming "));
                arrayList3.add(new DataSingle("Backfilling", "Inert gas using solenoid valve "));
                arrayList3.add(new DataSingle("Isolation Valve ", "Butterfly Design DN 100 "));

                ArrayList<DataSingle> arrayList4 = new ArrayList<>();

                arrayList4.add(new DataSingle("Product Chamber ", "Rectangular Chamber MOC SS316L Finish Pharma _ Better than 220 Grit "));
                arrayList4.add(new DataSingle("Door", "Full Width Opening to a Max angle of 1100 Optionally SS316L Door "));
                arrayList4.add(new DataSingle("Sealing", "Split Ring Gasket / O Ring "));

                arrayList4.add(new DataSingle("Validation Port", "Provided Size 1.5” with standard TC Liner "));
                arrayList4.add(new DataSingle("Condenser Chamber ", "Round Chamber  MOC SS316L Finish Pharma _ Better than 220 Grit "));
                arrayList4.add(new DataSingle("Door", "Full Width Opening to a Max angle of 1100 Optionally SS316L Door "));

                arrayList4.add(new DataSingle("Sealing", "Split Ring Gasket/ O Ring "));


                String key = databaseReference.push().getKey();
                databaseReference.child(key).setValue(new DataDQ(key, "REFRIGERATION SYSTEM ", arrayList));

                String key1 = databaseReference.push().getKey();
                databaseReference.child(key1).setValue(new DataDQ(key1, " SHELF SYSTEM ", arrayList1));


                String key2 = databaseReference.push().getKey();
                databaseReference.child(key2).setValue(new DataDQ(key2, "CONDENSER ", arrayList2));

                String key3 = databaseReference.push().getKey();
                databaseReference.child(key3).setValue(new DataDQ(key3, "VACUUM ", arrayList3));


                String key4 = databaseReference.push().getKey();
                databaseReference.child(key4).setValue(new DataDQ(key4, " STEAM STERILIZABLE CHAMBERS ", arrayList4));



 */
            }
        });


        final Handler handler=new Handler(getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tvTime.setText(new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(new Date()));
                handler.postDelayed(this, 1000);
            }
        }, 10);

        entries.add(new BarEntry(0, new float[]{2, 3}));
        entries.add(new BarEntry(1, new float[]{4, 3}));
        entries.add(new BarEntry(2, new float[]{4, 5}));
        entries.add(new BarEntry(3, new float[]{4, 3}));
        entries.add(new BarEntry(4, new float[]{1, 3}));
        entries.add(new BarEntry(5, new float[]{5, 3}));
        entries.add(new BarEntry(6, new float[]{3, 3}));
        entries.add(new BarEntry(7, new float[]{2, 3}));

        int[] colors=new int[]{Color.LTGRAY, Color.DKGRAY};
        int[] colors2=new int[]{getResources().getColor(R.color.light_pink), getResources().getColor(R.color.dark_pink)};

        entries2.add(new BarEntry(0, new float[]{4, 5}));
        entries2.add(new BarEntry(1, new float[]{5, 3}));
        entries2.add(new BarEntry(2, new float[]{3, 3}));
        entries2.add(new BarEntry(3, new float[]{4, 3}));
        entries2.add(new BarEntry(4, new float[]{1, 3}));
        entries2.add(new BarEntry(5, new float[]{2, 3}));
        entries2.add(new BarEntry(6, new float[]{2, 3}));
        entries2.add(new BarEntry(7, new float[]{4, 3}));

        BarDataSet barDataSet=new BarDataSet(entries, "");
        barDataSet.setColors(colors);

        BarDataSet barDataSet2=new BarDataSet(entries2, "");
        barDataSet2.setColors(colors2);

        BarData barData=new BarData(barDataSet);
        BarData barData2=new BarData(barDataSet2);
        barData.setBarWidth(0.5f);
        barData2.setBarWidth(0.5f);

        barDataSet.setDrawValues(false);
        barDataSet2.setDrawValues(false);

        chart.setData(barData);
        chart.animateY(2000);
        chart.getXAxis().setDrawLabels(false);
        chart.getXAxis().setEnabled(false);
        chart.getAxisRight().setEnabled(false);
        chart.getAxisLeft().setEnabled(false);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);

        barChart2.setData(barData2);
        barChart2.animateY(2000);
        barChart2.getXAxis().setEnabled(false);
        barChart2.getXAxis().setDrawLabels(false);
        barChart2.getAxisRight().setEnabled(false);
        barChart2.getAxisLeft().setEnabled(false);
        barChart2.getDescription().setEnabled(false);
        barChart2.getLegend().setEnabled(false);


    }

    private void createMCQ(){



    }
}