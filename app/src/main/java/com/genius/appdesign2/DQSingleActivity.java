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
import android.widget.Toast;

import com.genius.appdesign2.data.DataCompNew;
import com.genius.appdesign2.data.DataDQNew;
import com.genius.appdesign2.data.DataModuleNew;
import com.genius.appdesign2.data.DataSpecs;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

public class DQSingleActivity extends AppCompatActivity {

    private RecyclerView rcSpecs, rcModule;
    private ArrayList<DataDQNew> arrayList = new ArrayList<>();
    private ArrayList<DataSpecs> specsArrayList = new ArrayList<>();
    private ArrayList<DataModuleNew> moduleArrayList = new ArrayList<>();
    private DQNewActivity.DQNewAdapter adapter;
    private DocumentReference reference, ref1;
    private FirebaseFirestore firestore;
    private TextView tvBlank;
    private TextView tvPurpose, tvGeneral, tvSpecs, tvModule;
    private String key;
    private SpecAdapter specAdapter;
    private ModuleAdapter moduleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dqsingle);

        Objects.requireNonNull(getSupportActionBar()).hide();
        TextView textView = findViewById(R.id.activity_dqsingle_tvTitle);
        textView.setText("DQ Report");
        ImageView imageView = findViewById(R.id.activity_dqsingle_imageview);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvPurpose = findViewById(R.id.activity_dqsingle_tvPurpose);
        tvGeneral = findViewById(R.id.activity_dqsingle_tvGeneral);
        tvSpecs = findViewById(R.id.activity_dqsingle_tvSpecs);
        tvModule = findViewById(R.id.activity_dqsingle_tvModule);
        rcModule = findViewById(R.id.activity_dqsingle_rcViewModule);
        rcSpecs = findViewById(R.id.activity_dqsingle_rcViewSpecs);

        firestore = FirebaseFirestore.getInstance();

        key = getIntent().getStringExtra("key");
        if (key == null){
            finish();
            Toast.makeText(this, "No data received", Toast.LENGTH_SHORT).show();
        }else {
            Log.e("key", key);
            reference = firestore.collection("DQNewReport").document(key);


            setPurpose(reference);
            setGeneral(reference);
            setSpecs(reference);
            setModule(reference);

            specAdapter = new SpecAdapter(this, specsArrayList);
            rcSpecs.setLayoutManager(new LinearLayoutManager(this));
           // rcSpecs.setHasFixedSize(true);
            rcSpecs.setNestedScrollingEnabled(false);
            rcSpecs.setAdapter(specAdapter);
            specAdapter.notifyDataSetChanged();

            moduleAdapter = new ModuleAdapter(this, moduleArrayList);
            rcModule.setLayoutManager(new LinearLayoutManager(this));
        //    rcModule.setHasFixedSize(true);
            rcModule.setNestedScrollingEnabled(false);
            rcModule.setAdapter(moduleAdapter);
            moduleAdapter.notifyDataSetChanged();
        }
    }

    private void setPurpose(DocumentReference documentReference){
        documentReference.collection("content").document("purpose")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists() && documentSnapshot.get("title") != null && documentSnapshot.get("desc") != null){
                            tvPurpose.setText(documentSnapshot.getData().get("desc").toString());
                        }else {
                            tvPurpose.setText("N/A");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                tvPurpose.setText("Error" + e.toString());
            }
        });
    }
    private void setGeneral(DocumentReference documentReference){
        documentReference.collection("content").document("general")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists() && documentSnapshot.get("title") != null && documentSnapshot.get("desc") != null){
                            tvGeneral.setText(documentSnapshot.get("desc").toString());
                        }else {
                            tvGeneral.setText("N/A");
                        }
                    }
                });
    }
    private void setSpecs(DocumentReference documentReference){
        documentReference.collection("content").document("specifications")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists() && documentSnapshot.get("name") != null && documentSnapshot.get("desc") != null){
                            tvSpecs.setText(documentSnapshot.get("desc").toString());
                            GenereteSpecsTable(documentReference);
                        }else {
                            tvSpecs.setText("N/A");
                        }
                    }
                });


    }
    private void GenereteSpecsTable(DocumentReference documentReference){
        documentReference.collection("content").document("specifications").collection("specDetails")
                .orderBy("index")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                        assert value != null;
                        specsArrayList.clear();
                        for (QueryDocumentSnapshot snapshot : value) {
                            if (snapshot.getData().get("title") != null && snapshot.getData().get("index") != null && snapshot.getData().get("input") != null){
                                specsArrayList.add(new DataSpecs(snapshot.getId(), snapshot.get("index", Integer.class), snapshot.getData().get("title").toString(), snapshot.getData().get("input").toString()));
                                specAdapter.notifyDataSetChanged();
                            }
                        }
                        Log.e("Content", "Added Specs");
                    }
                });
    }

    private void setModule(DocumentReference documentReference){
        documentReference.collection("content").document("configuration")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists() && documentSnapshot.get("name") != null && documentSnapshot.get("desc") != null){
                            tvModule.setText(documentSnapshot.get("desc").toString());
                        }else {
                            tvModule.setText("N/A");
                        }
                    }
                });


        documentReference.collection("content").document("configuration").collection("modules")
                .orderBy("index")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                        assert value != null;
                        specsArrayList.clear();
                        for (QueryDocumentSnapshot snapshot : value) {
                            if (snapshot.getData().get("title") != null && snapshot.getData().get("index") != null && snapshot.getData().get("desc") != null){
                                moduleArrayList.add(new DataModuleNew(snapshot.getId(), snapshot.getData().get("title").toString() , snapshot.getData().get("desc").toString(), snapshot.get("index", Integer.class)));
                                moduleAdapter.notifyDataSetChanged();
                            }
                        }
                        Log.e("Content", "Added Specs");
                    }
                });
    }

    public class SpecAdapter extends RecyclerView.Adapter<SpecAdapter.ViewHolder>{

        private Context context;
        private ArrayList<DataSpecs> list=new ArrayList<>();

        public SpecAdapter(Context context, ArrayList<DataSpecs> list) {
            this.context=context;
            this.list=list;
        }

        @NonNull
        @Override
        public SpecAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_specs, parent, false);
            return new ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull SpecAdapter.ViewHolder holder, final int position) {

            holder.tvTitle.setText((list.get(position).getIndex() + 1) + " : " + list.get(position).getTitle().toString());
            holder.tvInput.setText("Input: " + list.get(position).getInput().toString());


        }
        @Override
        public int getItemCount() {
            return list.size();
        }
        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle, tvInput;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.list_specs_tvTitle);
                tvInput = itemView.findViewById(R.id.list_specs_tvInput);

            }
        }
    }

    public class ModuleAdapter extends RecyclerView.Adapter<ModuleAdapter.ViewHolder>{

        private Context context;
        private ArrayList<DataModuleNew> list=new ArrayList<>();

        public ModuleAdapter(Context context, ArrayList<DataModuleNew> list) {
            this.context=context;
            this.list=list;
        }

        @NonNull
        @Override
        public ModuleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_module_new, parent, false);
            return new ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull ModuleAdapter.ViewHolder holder, final int position) {

            final int[] index = {-1};

            holder.tvTitle.setText((list.get(position).getIndex() + 1) + " : " + list.get(position).getTitle().toString());
            holder.tvDesc.setText("Description: " + list.get(position).getDesc().toString());
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.recyclerView.getVisibility() == View.VISIBLE){
                        holder.recyclerView.setVisibility(View.GONE);
                        holder.imageView.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
                    }else {
                        holder.recyclerView.setVisibility(View.VISIBLE);
                        holder.imageView.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24);
                    }
                }
            });

            ArrayList<DataCompNew> compArrayList = new ArrayList<>();
            ComponentAdapter componentAdapter = new ComponentAdapter(context, compArrayList);
            holder.recyclerView.setLayoutManager(new LinearLayoutManager(context));
          //  holder.recyclerView.setHasFixedSize(true);
            holder.recyclerView.setNestedScrollingEnabled(false);
            holder.recyclerView.setAdapter(componentAdapter);
            componentAdapter.notifyDataSetChanged();

            firestore.collection("DQNewReport").document(key).collection("content")
                    .document("configuration").collection("components")
                    .whereEqualTo("module_id", list.get(position).getKey())
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                            assert value != null;
                            compArrayList.clear();
                            for (QueryDocumentSnapshot snapshot : value) {
                                if (snapshot.getData().get("title") != null && snapshot.getData().get("value") != null && snapshot.getData().get("response") != null
                                        && snapshot.getData().get("module_id") != null && snapshot.getData().get("index") != null
                                        && snapshot.getData().get("issue_id") != null){
                                    compArrayList.add(new DataCompNew(snapshot.getId(), snapshot.get("index", Integer.class), snapshot.getData().get("title").toString(),
                                            snapshot.getData().get("value").toString(), snapshot.getData().get("module_id").toString(),snapshot.get("response", Integer.class),
                                            snapshot.getData().get("issue_id").toString()));
                                }
                            }

                            compArrayList.sort(new Comparator<DataCompNew>() {
                                @Override
                                public int compare(DataCompNew com1, DataCompNew com2) {
                                    return Integer.compare(com1.getIndex(), com2.getIndex());
                                }
                            });
                            componentAdapter.notifyDataSetChanged();
                        }
                    });

        }
        @Override
        public int getItemCount() {
            return list.size();
        }
        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle, tvDesc;
            ImageView imageView;
            RecyclerView recyclerView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.list_module_new_tvTitle);
                tvDesc = itemView.findViewById(R.id.list_module_new_tvDesc);
                imageView = itemView.findViewById(R.id.list_module_new_bShow);
                recyclerView = itemView.findViewById(R.id.list_module_new_rcView);

            }
        }
    }

    public class ComponentAdapter extends RecyclerView.Adapter<ComponentAdapter.ViewHolder>{

        private Context context;
        private ArrayList<DataCompNew> list=new ArrayList<>();

        public ComponentAdapter(Context context, ArrayList<DataCompNew> list) {
            this.context=context;
            this.list=list;
        }

        @NonNull
        @Override
        public ComponentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_quality_single, parent, false);
            return new ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull ComponentAdapter.ViewHolder holder, final int position) {

            holder.tvTitle.setText((list.get(position).getIndex() + 1) + " : " + list.get(position).getTitle());
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
                        Intent intent = new Intent(DQSingleActivity.this, IssueActivity.class);
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