package com.genius.appdesign2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.genius.appdesign2.data.DataTitle;

import java.util.ArrayList;
import java.util.Objects;

public class DQViewActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<DataTitle> arrayList = new ArrayList<>();
    private TitleAdapter adapter;
    private String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dqview);

        Objects.requireNonNull(getSupportActionBar()).hide();
        TextView textView = findViewById(R.id.activity_dqview_tvTitle);
        textView.setText("DQ Report");
        ImageView imageView = findViewById(R.id.activity_dqview_imageview);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recyclerView = findViewById(R.id.activity_dqview_rcView);
        adapter = new TitleAdapter(this, arrayList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        key = getIntent().getStringExtra("key");
        if (key == null){
            finish();
            Toast.makeText(this, "No data received", Toast.LENGTH_SHORT).show();
        }

        if (arrayList.isEmpty()){
            arrayList.add(new DataTitle(0, "Approval"));
            arrayList.add(new DataTitle(1, "Purpose"));
            arrayList.add(new DataTitle(2, "General Information"));
            arrayList.add(new DataTitle(3, "Specifications Summary"));
            arrayList.add(new DataTitle(4, "Equipment Configuration"));
            arrayList.add(new DataTitle(5, "Design Specifications"));
            arrayList.add(new DataTitle(6, "List of Hard-Wired Safety Interlocks"));
            arrayList.add(new DataTitle(7, "Attachments"));
            arrayList.add(new DataTitle(8, "List of Abbreviations"));
         //   arrayList.add(new DataTitle(9, "Revision Index"));
        }
        adapter.notifyDataSetChanged();

    }

    public class TitleAdapter extends RecyclerView.Adapter<TitleAdapter.ViewHolder>{

        private Context context;
        private ArrayList<DataTitle> list=new ArrayList<>();

        public TitleAdapter(Context context, ArrayList<DataTitle> list) {
            this.context=context;
            this.list=list;
        }

        @NonNull
        @Override
        public TitleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_title, parent, false);
            return new ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull TitleAdapter.ViewHolder holder, final int position) {

            String s = (list.get(position).getIndex()+1) + ". " + list.get(position).getTitle();
            holder.tvTitle.setText(s);
            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.e("sample 2", key + list.get(position).getIndex() + list.get(position).getTitle());
                    Intent intent = new Intent(DQViewActivity.this, DescActivity.class);
                    intent.putExtra("key", key);
                    intent.putExtra("index",list.get(position).getIndex());
                    intent.putExtra("title", list.get(position).getTitle());
                    startActivity(intent);
                }
            });
        }
        @Override
        public int getItemCount() {
            return list.size();
        }
        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle;
            ConstraintLayout layout ;
            ImageView imageView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.list_title_tvTitle);
                layout = itemView.findViewById(R.id.list_title_layout);
                imageView = itemView.findViewById(R.id.list_title_imageview);

            }
        }
    }

}