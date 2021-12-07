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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.genius.appdesign2.data.DataMachines;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MachinesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private MachineAdapter adapter;
    private ArrayList<DataMachines> arrayList = new ArrayList<>();
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_machines);

        getSupportActionBar().hide();
        TextView textView = findViewById(R.id.activity_machines_tvTitle);
        textView.setText("Select Machine");
        ImageView imageView = findViewById(R.id.activity_machines_imageview);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        fab = findViewById(R.id.activity_machines_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MachinesActivity.this, AddMachineActivity.class));
            }
        });
        fab.setVisibility(View.GONE);

        recyclerView = findViewById(R.id.activity_machines_rcView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        adapter = new MachineAdapter(this, arrayList);
        recyclerView.setAdapter(adapter);


        db = FirebaseFirestore.getInstance();


    }

    public class MachineAdapter extends RecyclerView.Adapter<MachineAdapter.ViewHolder>{

        private Context context;
        private ArrayList<DataMachines> list=new ArrayList<>();

        public MachineAdapter(Context context, ArrayList<DataMachines> list) {
            this.context=context;
            this.list=list;
        }

        @NonNull
        @Override
        public MachineAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_machine, parent, false);
            return new ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull MachineAdapter.ViewHolder holder, final int position) {
            holder.tvTitle.setText(list.get(position).getTitle().toString());
            holder.tvCreated.setText(list.get(position).getCreatedBy());
            holder.tvLocation.setText(list.get(position).getLocation());
            if (list.get(position).getKey().equals(SplashActivity.machine_id)){
                holder.cardView.setVisibility(View.VISIBLE);
            }else {
                holder.cardView.setVisibility(View.GONE);
            }

            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   /* Intent intent = new Intent(context, ContentsActivity.class);
                    intent.putExtra("mid", list.get(position).getKey());
                    intent.putExtra("machine_title", list.get(position).getTitle());
                    startActivity(intent);

                    */
                    SharedPreferences sharedPreferences = getSharedPreferences("test_prefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor  = sharedPreferences.edit();
                    editor.putString("machine_id", list.get(position).getKey());
                    editor.commit();
                    editor.apply();
                    SplashActivity.machine_id = list.get(position).getKey();
                    Toast.makeText(context, "Default Machine : " + list.get(position).getTitle(), Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();
                }
            });
            /*
            holder.layout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    SharedPreferences sharedPreferences = getSharedPreferences("test_prefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor  = sharedPreferences.edit();
                        editor.putString("machine_id", list.get(position).getKey());
                        editor.commit();
                        editor.apply();
                        SplashActivity.machine_id = list.get(position).getKey();
                    Toast.makeText(context, "Default Machine : " + list.get(position).getTitle(), Toast.LENGTH_SHORT).show();
                    return false;
                }
            });

             */
        }
        @Override
        public int getItemCount() {
            return list.size();
        }
        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvTitle, tvCreated, tvLocation;
            ConstraintLayout layout;
            CardView cardView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.list_machine_tvTitle);
                tvCreated = itemView.findViewById(R.id.list_machine_tvCreated);
                tvLocation = itemView.findViewById(R.id.list_machine_tvLocation);
                layout = itemView.findViewById(R.id.list_machine_layout);
                cardView = itemView.findViewById(R.id.list_machine_cardview);

            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();


        db.collection("machineData")
                .addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        assert value != null;
                        arrayList.clear();
                        for (QueryDocumentSnapshot snapshot : value) {
                            if (snapshot.getData().get("title") != null && snapshot.getData().get("desc") != null &&
                                    snapshot.getData().get("location") != null && snapshot.getData().get("createdBy") != null) {
                                arrayList.add(new DataMachines(snapshot.getId(),
                                        snapshot.getData().get("title").toString(),
                                        snapshot.getData().get("desc").toString(),
                                        snapshot.getData().get("location").toString(),
                                        snapshot.getData().get("createdBy").toString()));
                                if (snapshot.getData().get("timestamp") != null){
                                    Timestamp timestamp  = snapshot.getTimestamp("timestamp");
                                }
                                adapter.notifyDataSetChanged();

                            }else {
                                Log.e("Machines", "Missing Parameters");
                            }
                        }
                    }
                });

/*
        db.collection("machineData")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                arrayList.add(new DataMachine(document.getId(),
                                        document.getData().get("title").toString(),
                                        document.getData().get("createdBy").toString(),
                                        document.getData().get("location").toString()));
                                adapter.notifyDataSetChanged();

                                Log.e("NewActivity", document.getId() + " => " + document.getData());
                            }
                        }else {
                            Log.e("NewActivity", "Erroe");
                        }
                    }
                });

 */
    }
}