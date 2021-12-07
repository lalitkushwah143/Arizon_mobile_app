package com.genius.appdesign2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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

import com.genius.appdesign2.data.DataComponent;
import com.genius.appdesign2.data.DataComponent2;
import com.genius.appdesign2.data.DataDQ2;
import com.genius.appdesign2.data.DataDQReport;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class QualityReportActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<DataDQReport> arrayList = new ArrayList<>();
    private QualityAdapter adapter;
    private DatabaseReference reference, ref1;
    private FirebaseFirestore firestore; 
    private TextView tvBlank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quality_report);

        Objects.requireNonNull(getSupportActionBar()).hide();
        TextView textView = findViewById(R.id.activity_quality_report_tvTitle);
        textView.setText("Quality Report");
        ImageView imageView = findViewById(R.id.activity_quality_report_imageview);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        firestore = FirebaseFirestore.getInstance();
        tvBlank = findViewById(R.id.activity_quality_report_tvBlank);
        recyclerView = findViewById(R.id.activity_quality_report_rcView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        adapter = new QualityAdapter(this, arrayList);
        recyclerView.setAdapter(adapter);

        reference = FirebaseDatabase.getInstance().getReference().child("validator");

        firestore.collection("DQReport")
                .whereEqualTo("mid", SplashActivity.machine_id)
                .addSnapshotListener( MetadataChanges.INCLUDE , new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        assert value != null;
                        arrayList.clear();
                        for (QueryDocumentSnapshot snapshot : value) {

                            if (snapshot.getData().get("title") != null && snapshot.getData().get("desc") != null
                                    && snapshot.getData().get("mid") != null) {
                                ArrayList<DataComponent2> component2s = new ArrayList<>();
                                component2s = getComponenets(snapshot.getId());
                                if (snapshot.getData().get("date") != null){
                                    arrayList.add(new DataDQReport(snapshot.getId(),
                                            snapshot.getData().get("title").toString(),
                                            snapshot.getData().get("desc").toString(),
                                            snapshot.getData().get("mid").toString(),
                                            snapshot.getData().get("date").toString(),
                                            component2s));
                                    adapter.notifyDataSetChanged();
                                }else {
                                    arrayList.add(new DataDQReport(snapshot.getId(),
                                            snapshot.getData().get("title").toString(),
                                            snapshot.getData().get("desc").toString(),
                                            snapshot.getData().get("mid").toString(),
                                            "No Date",
                                            component2s));
                                    adapter.notifyDataSetChanged();
                                }

                            }else {
                                Log.e("Quality", "Missing Parameters");
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
/*
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                arrayList.add(snapshot.getValue(DataDQ2.class));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


 */
        adapter.notifyDataSetChanged();

    }

    private ArrayList<DataComponent2> getComponenets(String dq_id){
        ArrayList<DataComponent2> component2s = new ArrayList<>();

        firestore.collection("DQReportData")
                .whereEqualTo("module_id", dq_id)
                .addSnapshotListener( MetadataChanges.INCLUDE , new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        assert value != null;
                        component2s.clear();
                        for (QueryDocumentSnapshot snapshot : value) {

                            if (snapshot.getData().get("title") != null && snapshot.getData().get("value") != null &&
                                    snapshot.getData().get("module_id") != null &&
                                    snapshot.getData().get("response") != null && snapshot.getData().get("issue_id") != null) {
                                component2s.add(new DataComponent2(snapshot.getId(),
                                        snapshot.getData().get("title").toString(),
                                        snapshot.getData().get("value").toString(),
                                        snapshot.getData().get("module_id").toString(),
                                        Integer.parseInt(snapshot.getData().get("response").toString()),
                                        snapshot.getData().get("issue_id").toString()));
                            }else {
                                Log.e("Quality", "Missing Parameters");
                            }
                        }

                    }
                });

        return component2s;
    }

    public class QualityAdapter extends RecyclerView.Adapter<QualityAdapter.ViewHolder>{

        private Context context;
        private ArrayList<DataDQReport> list=new ArrayList<>();

        public QualityAdapter(Context context, ArrayList<DataDQReport> list) {
            this.context=context;
            this.list=list;
        }

        @NonNull
        @Override
        public QualityAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_quality_report, parent, false);
            return new ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull QualityAdapter.ViewHolder holder, final int position) {

            holder.tvTitle.setText("Module: " + list.get(position).getTitle());
            if (list.get(position).getDate().equals("No Date")){
                holder.tvTime.setText("No Date");
            }else {
                holder.tvTime.setText(list.get(position).getDate().substring(0, 10));
            }
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            holder.rcView.setLayoutManager(layoutManager);
            holder.rcView.setHasFixedSize(true);
            SingleAdapter singleAdapter = new SingleAdapter(context, list.get(position).getArrayList());
            holder.rcView.setAdapter(singleAdapter);
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.rcView.getVisibility() == View.VISIBLE){
                        holder.rcView.setVisibility(View.GONE);
                    }else {
                        holder.rcView.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
        @Override
        public int getItemCount() {
            return list.size();
        }
        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle, tvTime;
            RecyclerView rcView;
            ImageView imageView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.list_quality_report_tvTitle);
                tvTime = itemView.findViewById(R.id.list_quality_report_tvTime);
                rcView = itemView.findViewById(R.id.list_quality_report_rcView);
                imageView = itemView.findViewById(R.id.list_quality_report_bShow);
            }
        }
    }

    public class SingleAdapter extends RecyclerView.Adapter<SingleAdapter.ViewHolder>{

        private Context context;
        private ArrayList<DataComponent2> list=new ArrayList<>();

        public SingleAdapter(Context context, ArrayList<DataComponent2> list) {
            this.context=context;
            this.list=list;
        }

        @NonNull
        @Override
        public SingleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_quality_single, parent, false);
            return new ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull SingleAdapter.ViewHolder holder, final int position) {
            holder.tvTitle.setText(list.get(position).getTitle());
            holder.tvValue.setText(list.get(position).getValue());
            switch (list.get(position).getResponse()){
                case 0:
                    holder.tvStatus.setText("Not Updated");
                    holder.tvStatus.setTextColor(getResources().getColor(R.color.color_normal));
                    holder.tvComment.setVisibility(View.GONE);
                    holder.tv4.setVisibility(View.GONE);
                    break;
                case 1:
                    holder.tvStatus.setText("Accepted");
                    holder.tvStatus.setTextColor(getResources().getColor(R.color.color_accept));
                    holder.tvComment.setVisibility(View.GONE);
                    holder.tv4.setVisibility(View.GONE);
                    break;

                case 2:
                    holder.tvStatus.setText("Rejected");
                    holder.tvStatus.setTextColor(getResources().getColor(R.color.color_reject));
                    holder.tvComment.setVisibility(View.GONE);
                    holder.tv4.setVisibility(View.GONE);
                    break;

                case 3:
                    holder.tvStatus.setText("Issued");
                    holder.tvStatus.setTextColor(getResources().getColor(R.color.color_issue));

                    DocumentReference documentReference = firestore.collection("issueData").document(list.get(position).getIssue_id());

                    documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot snapshot,
                                            @Nullable FirebaseFirestoreException e) {

                            if (snapshot != null && snapshot.exists() && snapshot.get("content") != null) {
                                holder.tvComment.setText(snapshot.get("content").toString());
                                holder.tvComment.setVisibility(View.VISIBLE);
                                holder.tv4.setVisibility(View.VISIBLE);

                            } else {
                                Log.d("Issue", "Current data: null");
                            }
                        }
                    });
                    break;
            }

            holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (list.get(position).getResponse() == 3) {
                        Intent intent = new Intent(QualityReportActivity.this, IssueActivity.class);
                        intent.putExtra("key", list.get(position).getIssue_id());
                        startActivity(intent);
                    }
                }
            });

        }
        @Override
        public int getItemCount() {
            return list.size();
        }
        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvTitle, tvValue, tvStatus, tvComment, tv4;
            ConstraintLayout constraintLayout;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvStatus = itemView.findViewById(R.id.list_quality_single_tvStatus);
                tvTitle = itemView.findViewById(R.id.list_quality_single_tvTitle);
                tvValue = itemView.findViewById(R.id.list_quality_single_tvValue);
                tvComment = itemView.findViewById(R.id.list_quality_single_tvContent);
                constraintLayout = itemView.findViewById(R.id.list_quality_single_layout);
                tv4 = itemView.findViewById(R.id.list_quality_single_tv4);

            }
        }
    }


}