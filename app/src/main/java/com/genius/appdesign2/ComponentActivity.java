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
import android.widget.ImageView;
import android.widget.TextView;

import com.genius.appdesign2.data.DataComponent;
import com.genius.appdesign2.data.DataStep;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ComponentActivity extends AppCompatActivity {

    private FirebaseFirestore firestore;

    private FloatingActionButton fab;
    private RecyclerView recyclerView;
    private TextView textView;

    private String module_id, module_title;

    private ComponentAdapter adapter;
    private ArrayList<DataComponent> arrayList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_component);

        getSupportActionBar().hide();
        textView = findViewById(R.id.activity_component_tvTitle);
        ImageView imageView = findViewById(R.id.activity_component_imageview);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        fab = findViewById(R.id.activity_component_fab);

        firestore = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.activity_component_rcView);

        module_id = getIntent().getStringExtra("module_id");
        module_title = getIntent().getStringExtra("module_title");

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        adapter = new ComponentAdapter(this, arrayList);
        recyclerView.setAdapter(adapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ComponentActivity.this, AddComponentActivity.class);
                intent.putExtra("module_id", module_id);
                intent.putExtra("module_title", module_title);
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();

        if (module_title!= null && module_id != null){

            textView.setText(module_title + " - Parts");

            firestore.collection("componentData")
                    .whereEqualTo("module_id", module_id)
                    .addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            assert value != null;
                            arrayList.clear();
                            for (QueryDocumentSnapshot snapshot : value) {

                                if (snapshot.getData().get("title") != null && snapshot.getData().get("value") != null &&
                                        snapshot.getData().get("module_id") != null) {

                                    arrayList.add(new DataComponent(snapshot.getId(),
                                            snapshot.getData().get("title").toString(),
                                            snapshot.getData().get("value").toString(),
                                            snapshot.getData().get("module_id").toString()));
                                    adapter.notifyDataSetChanged();
                                }else {
                                    Log.e("Component", "Missing Parameters");
                                }
                            }

                        }
                    });
/*
            db.collection("stepData")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    if (document.getData().get("cid").equals(manual_id)) {
                                        arrayList.add(new DataSteps(document.getId(),
                                                document.getData().get("title").toString(),
                                                document.getData().get("cid").toString(),
                                                document.getData().get("desc").toString(),
                                                document.getData().get("createdAt").toString(),
                                                document.getData().get("link").toString(),
                                                document.getData().get("uniqueKey").toString()));
                                        adapter.notifyDataSetChanged();
                                        Log.e("steps: ", document.getData().get("link").toString());
                                    }

                                }
                            }else {
                                Log.e("Steps Activity", "Erroe");
                            }
                        }
                    });

 */
        }

    }

    public class ComponentAdapter extends RecyclerView.Adapter<ComponentAdapter.ViewHolder>{

        private Context context;
        private ArrayList<DataComponent> list=new ArrayList<>();

        public ComponentAdapter(Context context, ArrayList<DataComponent> list) {
            this.context=context;
            this.list=list;
        }

        @NonNull
        @Override
        public ComponentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_component, parent, false);
            return new ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull ComponentAdapter.ViewHolder holder, final int position) {
            holder.tvTitle.setText(list.get(position).getTitle().toString());
            holder.tvDesc.setText(list.get(position).getValue());

        }
        @Override
        public int getItemCount() {
            return list.size();
        }
        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvTitle, tvDesc;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.list_component_tvTitle);
                tvDesc = itemView.findViewById(R.id.list_component_tvDesc);
            }
        }
    }

}