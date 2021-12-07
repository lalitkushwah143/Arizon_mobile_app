package com.genius.appdesign2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.genius.appdesign2.data.DataRecipe;

public class AddActivity extends AppCompatActivity {

    private EditText etTitle, etTemp, etTime, etTime2, etPressure;
    private Button button;
    private String steps[]={"FR 1","PR 1","PR 2", "PR 3","PR 4","PR 5","PR 6"};
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        getSupportActionBar().hide();
        TextView textView = findViewById(R.id.activity_add_tvTitle1);
        textView.setText("Add Data");
        ImageView imageView = findViewById(R.id.activity_add_imageview);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        etPressure= findViewById(R.id.activity_add_etPressure);
        etTemp= findViewById(R.id.activity_add_etTemp);
        etTime= findViewById(R.id.activity_add_etTime);
        etTime2= findViewById(R.id.activity_add_etTime2);
        button= findViewById(R.id.activity_add_bAdd);
        etTitle = findViewById(R.id.activity_add_etTitle);
        spinner = findViewById(R.id.activity_add_spinner);

        spinner.setAdapter(new ArrayAdapter<String>(AddActivity.this,R.layout.support_simple_spinner_dropdown_item,steps));
        spinner.setSelection(LyoActivity.arrayList.size());
        spinner.setClickable(false);
        spinner.setEnabled(false);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(etTemp.getText().toString()) || TextUtils.isEmpty(etPressure.getText().toString()) ||
                        TextUtils.isEmpty(etTime2.getText().toString()) || TextUtils.isEmpty(etTime.getText().toString())){
                    Toast.makeText(AddActivity.this, "Insert all data", Toast.LENGTH_SHORT).show();

                }else {
                    int time, temp, pressure, time2;
                    String title;
                   // title  = etTitle.getText().toString();
                    title = spinner.getSelectedItem().toString();
                    time = Integer.parseInt(etTime.getText().toString());
                    temp = Integer.parseInt(etTemp.getText().toString());
                    time2 = Integer.parseInt(etTime2.getText().toString());
                    pressure= Integer.parseInt(etPressure.getText().toString());

                    addData(title, temp, time, time2, pressure);
                    Toast.makeText(AddActivity.this, "Data added", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

    private void addData(String title, int temp, int time, int time2, int pressure) {
        LyoActivity.arrayList.add(new DataRecipe(0, title, "", temp, time, time2, pressure));
        LyoActivity.adapter.notifyDataSetChanged();
    }
}