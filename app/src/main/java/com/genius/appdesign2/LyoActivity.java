package com.genius.appdesign2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.genius.appdesign2.data.DataLoad;
import com.genius.appdesign2.data.DataRecipe;
import com.github.mikephil.charting.charts.LineChart;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class LyoActivity extends AppCompatActivity {

    private LineChart chart;
    private RecyclerView recyclerView;
    private Button button, bSample, bChart, bClear, bUpload;
    private DatabaseReference reference, ref1;
    private EditText etTitle;

    public static ArrayList<DataRecipe> arrayList = new ArrayList<>();
    public static LyoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lyo);

        getSupportActionBar().hide();
        TextView textView = findViewById(R.id.activity_lyo_tvTitle1);
        textView.setText("LYO Data");
        ImageView imageView = findViewById(R.id.activity_lyo_imageview);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        reference = FirebaseDatabase.getInstance().getReference().child("recipes");


        chart = findViewById(R.id.activity_lyo_chart);
        recyclerView = findViewById(R.id.activity_lyo_rcView);
        button = findViewById(R.id.activity_lyo_bAdd);
        bSample = findViewById(R.id.activity_lyo_bSample);
        bChart = findViewById(R.id.activity_lyo_bChart);
        bClear = findViewById(R.id.activity_lyo_bClear);
        etTitle = findViewById(R.id.activity_lyo_tvTitle);
        bUpload = findViewById(R.id.activity_lyo_bUpload);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        adapter = new LyoAdapter(this, arrayList);
        recyclerView.setAdapter(adapter);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (arrayList.size() < 7){
                    startActivity(new Intent(LyoActivity.this, AddActivity.class));
                }else {
                    Toast.makeText(LyoActivity.this, "Steps are completed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        bSample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  prepare();
                //startActivity(new Intent(LyoActivity.this, LoadRecipeActivity.class));
                Toast.makeText(LyoActivity.this, "Button Disabled", Toast.LENGTH_SHORT).show();
            }
        });
        bChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (arrayList.size()==0){
                    Toast.makeText(LyoActivity.this, "No data", Toast.LENGTH_SHORT).show();
                }else {
                    startActivity(new Intent(LyoActivity.this, ChartActivity.class));
                }
            }
        });
        bClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrayList.clear();
                adapter.notifyDataSetChanged();
            }
        });

        bUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(etTitle.getText().toString())){
                    Toast.makeText(LyoActivity.this, "Enter the Recipe Title", Toast.LENGTH_SHORT).show();
                }else if (arrayList.size()!= 7){
                    Toast.makeText(LyoActivity.this, "Missing Parameters", Toast.LENGTH_SHORT).show();
                }else {
                    String key = reference.push().getKey();
                    reference.child(key).setValue(new DataLoad(key, etTitle.getText().toString(), arrayList));
                    arrayList.clear();
                    Toast.makeText(LyoActivity.this, "Recipe Uploaded", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }
    public class LyoAdapter extends RecyclerView.Adapter<LyoAdapter.ViewHolder>{

        private Context context;
        private ArrayList<DataRecipe> list=new ArrayList<>();

        public LyoAdapter(Context context, ArrayList<DataRecipe> list) {
            this.context=context;
            this.list=list;
        }

        @NonNull
        @Override
        public LyoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_lyo, parent, false);
            return new ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull LyoAdapter.ViewHolder holder, final int position) {
            //    holder.tvKey.setText(arrayList.get(position).getKey());
            holder.tvId.setText(list.get(position).getStep());
            holder.tvTemp.setText(list.get(position).getTemp1() + "");
            holder.tvTime.setText(list.get(position).getTime1() + "");
            holder.tvTime2.setText(list.get(position).getTime2() + "");
            holder.tvPressure.setText(list.get(position).getPressure() + "");

        }
        @Override
        public int getItemCount() {
            return list.size();
        }
        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvId, tvTemp, tvTime, tvTime2, tvPressure;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvId = itemView.findViewById(R.id.list_lyo_tvId);
                tvTemp = itemView.findViewById(R.id.list_lyo_tvTemp);
                tvTime = itemView.findViewById(R.id.list_lyo_tvTime);
                tvTime2 = itemView.findViewById(R.id.list_lyo_tvTime2);
                tvPressure = itemView.findViewById(R.id.list_lyo_tvPressure);
            }
        }
    }
/*
    private void prepare(){
        arrayList.clear();
        arrayList.add(new DataRecipe("FZ 1", -40, 30, 180, 800));
        arrayList.add(new DataRecipe("PR 1", -30,  30, 120, 300));
        arrayList.add(new DataRecipe("PR 2",-20,  60, 120, 300));
        arrayList.add(new DataRecipe("PR 3" , -10, 60, 180, 300));
        arrayList.add(new DataRecipe("PR 4", 0, 60, 360, 200));
        arrayList.add(new DataRecipe("PR 5", 25,180, 240, 200));
        arrayList.add(new DataRecipe("PR 6", 25, 55, 0, 200));

        adapter.notifyDataSetChanged();
    }


 */
}