package com.genius.appdesign2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.genius.appdesign2.data.DataCallLog;
import com.genius.appdesign2.data.DataLog;
import com.genius.appdesign2.data.DataManuals;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Comparator;

public class CallLogActivity extends AppCompatActivity {

    private DatabaseReference reference;
    private FirebaseDatabase database;
    private ArrayList<DataCallLog> arrayList =new ArrayList<>();
    private LogAdapter adapter;
    private RecyclerView recyclerView;
    private TextView tvBlank;

    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_log);

        getSupportActionBar().hide();

        TextView tvTitle = findViewById(R.id.activity_log_tvTitle);
        tvTitle.setText("Call Log");
        ImageView imageView = findViewById(R.id.activity_log_imageview);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvBlank = findViewById(R.id.activity_call_log_tvBlank);
        recyclerView = findViewById(R.id.activity_log_rcView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        adapter =new LogAdapter(this, arrayList);
        recyclerView.setAdapter(adapter);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference().child("calls");

        firestore = FirebaseFirestore.getInstance();


        firestore.collection("CallLogData")
                .whereEqualTo("machine_id", SplashActivity.machine_id)
                .addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        assert value != null;
                        arrayList.clear();
                        for (QueryDocumentSnapshot snapshot : value) {

                            if (snapshot.getData().get("user_id") != null && snapshot.getData().get("machine_id") != null &&
                                    snapshot.getData().get("manual") != null && snapshot.getData().get("step") != null &&
                                    snapshot.getData().get("time") != null) {

                                arrayList.add(new DataCallLog(snapshot.getId(),
                                        snapshot.getData().get("user_id").toString(),
                                        snapshot.getData().get("machine_id").toString(),
                                        snapshot.getData().get("manual").toString(),
                                        snapshot.getData().get("step").toString(),
                                        snapshot.getTimestamp("time")));
                          //      Log.e("Sample", snapshot.getData().toString());
                                adapter.notifyDataSetChanged();

                            }else {
                                Log.e("Call Log: ", "Missing Parameter");

                            }
                        }
                        if (arrayList.size() == 0){
                            recyclerView.setVisibility(View.GONE);
                            tvBlank.setVisibility(View.VISIBLE);
                        }else {
                            arrayList.sort(new Comparator<DataCallLog>() {
                                @Override
                                public int compare(DataCallLog o1, DataCallLog o2) {
                                    return o2.getTime().compareTo(o1.getTime());
                                }
                            });
                            recyclerView.setVisibility(View.VISIBLE);
                            tvBlank.setVisibility(View.GONE);
                        }

                    }
                });
/*
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String key, manual, step, time;
                key = snapshot.child("key").getValue().toString();
                manual= snapshot.child("manual").getValue().toString();
                step = snapshot.child("step").getValue().toString();
                time = snapshot.child("time").getValue().toString();
                arrayList.add(new DataLog(key, manual, step, time));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                arrayList.remove(new DataLog(snapshot.child("key").getValue().toString(),
                        snapshot.child("manual").getValue().toString(),
                        snapshot.child("step").getValue().toString(),
                        snapshot.child("time").getValue().toString()));
                adapter.notifyDataSetChanged();
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

    public class LogAdapter extends RecyclerView.Adapter<LogAdapter.ViewHolder>{

        private Context context;
        private ArrayList<DataCallLog> list=new ArrayList<>();

        public LogAdapter(Context context, ArrayList<DataCallLog> list) {
            this.context=context;
            this.list=list;
        }

        @NonNull
        @Override
        public LogAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_log, parent, false);
            return new ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull LogAdapter.ViewHolder holder, final int position) {
        //    holder.tvKey.setText(arrayList.get(position).getKey());

            holder.tvUser.setText(list.get(position).getUser_id().toString());
            holder.tvTime.setText(arrayList.get(position).getTime().toDate().toString().substring(0, 20));
            if (arrayList.get(position).getManual().equals("General")){
                holder.tvManual.setText(getString(R.string.general));
                holder.tvStep.setVisibility(View.GONE);
                holder.tv3.setVisibility(View.GONE);
            }else{
                holder.tvStep.setVisibility(View.VISIBLE);
                holder.tv3.setVisibility(View.VISIBLE);
                holder.tvStep.setText(arrayList.get(position).getStep());
                firestore.collection("manualData")
                        .document(arrayList.get(position).getManual())
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.get("title") != null) {
                                    holder.tvManual.setText(documentSnapshot.get("title").toString());
                                }else {
                                    Log.e("CAll Log: ", "Missing Parameter");
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                holder.tvManual.setText("Undefined");
                            }
                        });

            }

        }
        @Override
        public int getItemCount() {
            return list.size();
        }
        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvUser, tvTime, tvStep, tvManual, tv3;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvUser = itemView.findViewById(R.id.list_log_tvUser);
                tvTime = itemView.findViewById(R.id.list_log_tvTime);
                tvStep= itemView.findViewById(R.id.list_log_tvStep);
                tvManual = itemView.findViewById(R.id.list_log_tvManual);
                tv3 = itemView.findViewById(R.id.list_log_tv3);
            }
        }
    }

}