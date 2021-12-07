package com.genius.appdesign2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import com.genius.appdesign2.data.DataDQNew;
import com.google.firebase.Timestamp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class DQNewActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<DataDQNew> arrayList = new ArrayList<>();
    private DQNewAdapter adapter;
    private DatabaseReference reference, ref1;
    private FirebaseFirestore firestore;
    private TextView tvBlank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dqnew);

        Objects.requireNonNull(getSupportActionBar()).hide();
        TextView textView = findViewById(R.id.activity_dqnew_report_tvTitle);
        textView.setText("New DQ");
        ImageView imageView = findViewById(R.id.activity_dqnew_report_imageview);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        firestore = FirebaseFirestore.getInstance();
        tvBlank = findViewById(R.id.activity_dqnew_report_tvBlank);
        recyclerView = findViewById(R.id.activity_dqnew_report_rcView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        adapter = new DQNewAdapter(this, arrayList);
        recyclerView.setAdapter(adapter);

        firestore.collection("DQNewReport")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        assert value != null;
                        arrayList.clear();
                        for (QueryDocumentSnapshot snapshot : value) {
                            if (snapshot.getData().get("desc") != null && snapshot.getData().get("name") != null &&
                                    snapshot.getData().get("mid") != null && snapshot.getData().get("timestamp") != null) {
                                arrayList.add(new DataDQNew(snapshot.getId(), snapshot.getData().get("mid").toString(), snapshot.getData().get("name").toString(), snapshot.getData().get("desc").toString(), snapshot.getTimestamp("timestamp")));
                                adapter.notifyDataSetChanged();

                            }else {
                                Log.e("Machines", "Missing Parameters");
                            }
                        }

                        if (arrayList.size() == 0){
                            recyclerView.setVisibility(View.GONE);
                            tvBlank.setVisibility(View.VISIBLE);
                        }else {
                            recyclerView.setVisibility(View.VISIBLE);
                            tvBlank.setVisibility(View.GONE);
                        }
                    }
                });

        adapter.notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();

    }

    public class DQNewAdapter extends RecyclerView.Adapter<DQNewAdapter.ViewHolder>{

        private Context context;
        private ArrayList<DataDQNew> list=new ArrayList<>();

        public DQNewAdapter(Context context, ArrayList<DataDQNew> list) {
            this.context=context;
            this.list=list;
        }

        @NonNull
        @Override
        public DQNewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_dqnew, parent, false);
            return new ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull DQNewAdapter.ViewHolder holder, final int position) {

            holder.tvTitle.setText(list.get(position).getName());
            holder.tvDesc.setText(list.get(position).getDesc());
            String date = list.get(position).getTimestamp().toDate().toString().substring(0,20);
            holder.tvTime.setText(date + "");

            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("sample 1", list.get(position).getKey());
                    Intent intent = new Intent(DQNewActivity.this, DQViewActivity.class);
                    intent.putExtra("key", list.get(position).getKey());
                    startActivity(intent);
                }
            });

        }
        @Override
        public int getItemCount() {
            return list.size();
        }
        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle, tvTime, tvDesc;
            CardView cardView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.list_dqnew_tvName);
                tvTime = itemView.findViewById(R.id.list_dqnew_tvTime);
                tvDesc = itemView.findViewById(R.id.list_dqnew_tvDesc);
                cardView = itemView.findViewById(R.id.list_dqnew_cardview);
            }
        }
    }

}