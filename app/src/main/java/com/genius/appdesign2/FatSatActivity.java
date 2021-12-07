package com.genius.appdesign2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.genius.appdesign2.data.DataLoad_fatsat;
import com.genius.appdesign2.data.DataRecipe;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class FatSatActivity extends AppCompatActivity {

    private DatabaseReference reference;
    private RecyclerView recyclerView;
    private ArrayList<DataLoad_fatsat> arrayList = new ArrayList<>();
    private FatSatActivity.LoadAdapter adapter;
    public static ArrayList<DataRecipe> recipeArrayList = new ArrayList<>();
    private ImageButton fab;
    private TextView tvBlank;



    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fat_sat);

        getSupportActionBar().hide();

        TextView tvTitle = findViewById(R.id.activity_fatsat_tvTitle);
        tvTitle.setText("FAT/SAT");
        ImageView imageView = findViewById(R.id.activity_fatsat_imageview);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        firestore = FirebaseFirestore.getInstance();

        reference = FirebaseDatabase.getInstance().getReference().child("recipes");
        recyclerView = findViewById(R.id.activity_fatsat_rcView);
        // fab = findViewById(R.id.activity_fatsat_fab);
        tvBlank = findViewById(R.id.activity_fatsat_tvBlank);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        adapter = new FatSatActivity.LoadAdapter(this,arrayList);
        recyclerView.setAdapter(adapter);

        // arrayList.add(new DataLoad("Name : Vacume Pump","dfdf","Vacume","pump"));
        firestore.collection("recipes")
                .whereEqualTo("mid", SplashActivity.machine_id)
                .addSnapshotListener( MetadataChanges.INCLUDE, new EventListener<QuerySnapshot>() {
                    @Override

                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        assert value != null;
                        arrayList.clear();

                        for (QueryDocumentSnapshot snapshot : value) {
                           // ArrayList<DataRecipe> dataRecipes = new ArrayList<>();
                            //dataRecipes = getRecipeData(snapshot.getId());
                           // arrayList.add(new DataLoad_fatsat("John", "15", "23", "Day"));


                            if (snapshot.getData().get("title") != null && snapshot.getData().get("mid") != null) {
                                arrayList.add(new DataLoad_fatsat(
                                        snapshot.getData().get("title").toString(),
                                        "30",
                                        "60",
                                        "5"));

                                Log.e("Sample", snapshot.getData().get("title").toString());
                            }
                        }
                        if (arrayList.size() == 0){
                            recyclerView.setVisibility(View.GONE);
                            tvBlank.setVisibility(View.VISIBLE);
                        }else {
                            recyclerView.setVisibility(View.VISIBLE);
                            tvBlank.setVisibility(View.GONE);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    public class LoadAdapter extends RecyclerView.Adapter<FatSatActivity.LoadAdapter.ViewHolder>{

        private Context context;
        private ArrayList<DataLoad_fatsat> list=new ArrayList<>();

        public LoadAdapter(Context context, ArrayList<DataLoad_fatsat> list) {
            this.context=context;
            this.list=list;
        }

        @NonNull
        @Override
        public FatSatActivity.LoadAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_load_fatsat, parent, false);
            return new FatSatActivity.LoadAdapter.ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
            holder.name.setText(list.get(position).getTitle());
            holder.start.setText(list.get(position).getStart());
            holder.stop.setText(list.get(position).getMid());
            holder.time.setText(list.get(position).getTime());
           // holder.gettxtvalue.setText("Hiii");

            List<Entry> entryList = new ArrayList<>();
            LineDataSet lineDataSet;

            Float a = Float.valueOf(list.get(position).getStart());
            Float b = Float.valueOf(list.get(position).getMid());
            Float c = Float.valueOf(list.get(position).getTime());
//            Log.i("start co or", (Float.parseFloat(list.get(position).getStart())).toString());
            entryList.add(new Entry(0,10));
            entryList.add(new Entry(5,20));
            entryList.add(new Entry(10,15));
            entryList.add(new Entry(15,35));
            entryList.add(new Entry(35,10));

//            loadData(position,entryList);
           /* float t = (b - a)/c;
            int j = 0;
            for(float i=a;i<=b; i = i+t){
                entryList.add(new Entry(j,i));
            ++j;
            }*/
            lineDataSet = new LineDataSet(entryList,"country");
            LineData lineData = new LineData(lineDataSet);

            lineDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
            lineDataSet.setFillAlpha(110);
            Log.e("errors",(lineDataSet).toString());
            lineDataSet.setValueTextColor(Color.WHITE);
            lineDataSet.setValueTextSize(12f);

            lineDataSet.setColor(getResources().getColor(R.color.chart_pressure));
            lineDataSet.setCircleColor(getResources().getColor(R.color.chart_pressure));
           holder.lineChart.getLegend().setTextColor(getResources().getColor(R.color.white));
            holder.lineChart.getDescription().setTextColor(getResources().getColor(R.color.white));
            holder.lineChart.setData(lineData);
            holder.lineChart.setVisibleXRangeMaximum(100);


           holder.lineChart.invalidate();
            XAxis xAxis = holder.lineChart.getXAxis();
            xAxis.setTextColor(getResources().getColor(R.color.white));
            xAxis.setLabelCount(5);
            YAxis leftAxis = holder.lineChart.getAxisLeft();
            leftAxis.setTextColor(getResources().getColor(R.color.chart_temp));
            leftAxis.setDrawGridLines(true);
            leftAxis.setGranularityEnabled(true);

            YAxis rightAxis = holder.lineChart.getAxisRight();
            rightAxis.setTextColor(getResources().getColor(R.color.chart_pressure));
            rightAxis.setDrawGridLines(false);
            rightAxis.setDrawZeroLine(false);
            rightAxis.setGranularityEnabled(false);

            holder.lineChart.setTouchEnabled(true);
            holder.lineChart.setDragEnabled(true);
            holder.lineChart.setScaleEnabled(true);
            holder.lineChart.setPinchZoom(true);
            holder.lineChart.setHighlightPerDragEnabled(true);



            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.layout.getVisibility()== View.VISIBLE){
                        //holder.recyclerView.setVisibility(View.GONE);
                        //holder.recyclerView.setVisibility(View.GONE);
                        //holder.recyclerView.setVisibility(View.GONE);
                        holder.layout.setVisibility(View.GONE);
                        holder.imageView.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
                        // holder.button.setVisibility(View.GONE);
                    }else {
                       // holder.recyclerView.setLayoutManager(new LinearLayoutManager(context));
                        //holder.recyclerView.setHasFixedSize(true);
                        // FatSatActivity.RecipeAdapter recipeAdapter = new FatSatActivity.RecipeAdapter(context, list.get(position).getArrayList());
                        // holder.recyclerView.setAdapter(recipeAdapter);
                        // recipeAdapter.notifyDataSetChanged();
                        holder.layout.setVisibility(View.VISIBLE);
                       // holder.recyclerView.setVisibility(View.VISIBLE);
                        holder.imageView.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24); //change arrow on show
                        //holder.button.setVisibility(View.VISIBLE);
                    }
                }
            });

        }


        @Override
        public int getItemCount() {
            return list.size();
        }
        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView name,gettxtvalue;
            TextView start;
            TextView stop;
            TextView time;
            ImageView imageView;
            LinearLayout layout;
            LineChart lineChart;

            //Button button;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
               lineChart = itemView.findViewById(R.id.list_load_fatsat_chart);

               // gettxtvalue = itemView.findViewById(R.id.list_load_recipe2_txt);
                name = itemView.findViewById(R.id.list_lode_fatsat_tvUser);
                start = itemView.findViewById(R.id.list_lode_fatsat_tvManual);
                stop = itemView.findViewById(R.id.list_lode_fatsat_tvStep);
                time = itemView.findViewById(R.id.list_lode_fatsat_tvTime);
                imageView = itemView.findViewById(R.id.list_load_fatsat_bShow);
                layout = itemView.findViewById(R.id.activity_load_fatsat_layout);
            }
        }
    }


}
