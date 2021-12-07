package com.genius.appdesign2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.genius.appdesign2.data.DataJob;
import com.genius.appdesign2.data.DataLoad;
import com.genius.appdesign2.data.DataRecipe;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

public class JobActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private DatabaseReference reference;
    private ArrayList<DataJob>  arrayList = new ArrayList<>();
    private JobAdapter adapter;
    private FirebaseFirestore firestore;
    private TextView tvBlank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job);

        getSupportActionBar().hide();
        TextView textView = findViewById(R.id.activity_job_tvTitle);
        textView.setText("Jobs");
        ImageView imageView = findViewById(R.id.activity_job_imageview);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        firestore = FirebaseFirestore.getInstance();

        reference = FirebaseDatabase.getInstance().getReference().child("jobs");

        recyclerView = findViewById(R.id.activity_job_rcView);
        fab = findViewById(R.id.activity_job_fab);
        tvBlank = findViewById(R.id.activity_job_tvBlank);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        adapter = new JobAdapter(this, arrayList);
        recyclerView.setAdapter(adapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(JobActivity.this, AddJobActivity.class));
            }
        });



        firestore.collection("jobData")
                .whereEqualTo("mid", SplashActivity.machine_id)
                .addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        assert value != null;
                        arrayList.clear();
                        for (QueryDocumentSnapshot snapshot : value) {
                            if (snapshot.getData().get("title") != null && snapshot.getData().get("rid") != null && snapshot.getData().get("email") != null
                                    && snapshot.getData().get("mid") != null && snapshot.getData().get("desc") != null
                                    && snapshot.getData().get("date") != null && snapshot.getData().get("status") != null) {
                                arrayList.add(new DataJob(snapshot.getId(),
                                        snapshot.getData().get("title").toString(),
                                        snapshot.getData().get("rid").toString(),
                                        snapshot.getData().get("mid").toString(),
                                        snapshot.getData().get("desc").toString(),
                                        snapshot.getData().get("email").toString(),
                                        snapshot.getTimestamp("date"),
                                        (Boolean) snapshot.getData().get("status")));
                                adapter.notifyDataSetChanged();
                                Log.e("Sample", snapshot.getData().get("title").toString());
                            }else {
                                Log.e("Jobs" , "Missing Parameters");
                            }
                        }
                        if (arrayList.size() == 0){
                            recyclerView.setVisibility(View.GONE);
                            tvBlank.setVisibility(View.VISIBLE);
                        }else {

                            arrayList.sort(new Comparator<DataJob>() {
                                @Override
                                public int compare(DataJob o1, DataJob o2) {
                                    return o2.getDate().compareTo(o1.getDate());
                                }
                            });

                            adapter.notifyDataSetChanged();
                            recyclerView.scheduleLayoutAnimation();

                            recyclerView.setVisibility(View.VISIBLE);
                            tvBlank.setVisibility(View.GONE);
                        }
                    }
                });


    }

    public class JobAdapter extends RecyclerView.Adapter<JobAdapter.ViewHolder>{

        private Context context;
        private ArrayList<DataJob> list=new ArrayList<>();

        public JobAdapter(Context context, ArrayList<DataJob> list) {
            this.context=context;
            this.list=list;
        }

        @NonNull
        @Override
        public JobAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_job, parent, false);
            return new ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull JobAdapter.ViewHolder holder, final int position) {
            holder.tvTitle.setText(list.get(position).getTitle());
            holder.tvDesc.setText(list.get(position).getDesc());
            holder.tvTime.setText(list.get(position).getDate().toDate().toString().substring(0, 20));
            holder.tvuser.setText(list.get(position).getEmail());
            if (list.get(position).getStatus()){
                holder.tvStatus.setText("Completed");
                holder.tvStatus.setTextColor(getResources().getColor(R.color.color_accept));
            }else {
                holder.tvStatus.setText("Not Completed");
                holder.tvStatus.setTextColor(getResources().getColor(R.color.color_reject));
            }
            firestore.collection("recipes")
                    .whereEqualTo("mid", SplashActivity.machine_id)
                    .addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            assert value != null;
                            for (QueryDocumentSnapshot snapshot : value) {
                                if (snapshot.getId().equals(list.get(position).getRid()) && snapshot.getData().get("title") != null){
                                    holder.tvRecipe.setText(snapshot.getData().get("title").toString());

                                }
                                Log.e("recipe:" , snapshot.getData().get("title").toString());
                            }
                        }
                    });

        }
        @Override
        public int getItemCount() {
            return list.size();
        }
        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle, tvRecipe, tvStatus, tvDesc, tvTime, tvuser;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.list_job_tvTitle);
                tvStatus = itemView.findViewById(R.id.list_job_tvStatus);
                tvRecipe = itemView.findViewById(R.id.list_job_tvRecipe);
                tvDesc = itemView.findViewById(R.id.list_job_tvDesc);
                tvTime = itemView.findViewById(R.id.list_job_tvTime);
                tvuser = itemView.findViewById(R.id.list_job_tvUser);
            }
        }
    }

}