package com.genius.appdesign2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.genius.appdesign2.data.DataAbbre;
import com.genius.appdesign2.data.DataApproval;
import com.genius.appdesign2.data.DataAttach;
import com.genius.appdesign2.data.DataCompNew;
import com.genius.appdesign2.data.DataCompNew2;
import com.genius.appdesign2.data.DataModuleNew;
import com.genius.appdesign2.data.DataPoint;
import com.genius.appdesign2.data.DataSafety;
import com.genius.appdesign2.data.DataSpecs;
import com.genius.appdesign2.data.DataTitle;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
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

public class DescActivity extends AppCompatActivity {

    private TextView tvDesc, textView;
    private RecyclerView recyclerView, rcView2;
    private ArrayList arrayList = new ArrayList();
    private ArrayList arrayList2 = new ArrayList();
    private SpecAdapter specAdapter;
    private ModuleAdapter moduleAdapter;
    private TitleAdapter titleAdapter;
    private HeadAdapter headAdapter;
    private SafetyAdapter safetyAdapter;
    private AttachAdapter attachAdapter;
    private AbbreAdapter abbreAdapter;

    private FirebaseFirestore firestore;
    private DocumentReference reference;

    private String title, key;
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desc);

        Objects.requireNonNull(getSupportActionBar()).hide();
        textView = findViewById(R.id.activity_desc_tvTitle);
        ImageView imageView = findViewById(R.id.activity_desc_imageview);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvDesc = findViewById(R.id.activity_desc_tvDesc);
        recyclerView = findViewById(R.id.activity_desc_rcView);
        rcView2 = findViewById(R.id.activity_desc_rcView2);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        index = getIntent().getIntExtra("index", -1);
        title = getIntent().getStringExtra("title");
        key = getIntent().getStringExtra("key");

        firestore = FirebaseFirestore.getInstance();

        if ( !TextUtils.isEmpty(key) && !TextUtils.isEmpty(title) && index != -1){
            textView.setText((index+1) + ". " + title);

            reference = firestore.collection("DQNewReport").document(key);


            switch (index){
                case 0:
                    headAdapter = new HeadAdapter(this, arrayList);
                    recyclerView.setAdapter(headAdapter);
                    recyclerView.setVisibility(View.VISIBLE);
                    headAdapter.notifyDataSetChanged();
                    setApproval(reference);
                    break;
                case 1:
                    setPurpose(reference);
                    recyclerView.setVisibility(View.GONE);
                    break;
                case 2:
                    setGeneral(reference);
                    recyclerView.setVisibility(View.GONE);
                    break;
                case 3:
                    specAdapter = new SpecAdapter(this, arrayList);
                    recyclerView.setAdapter(specAdapter);
                    specAdapter.notifyDataSetChanged();
                    recyclerView.setVisibility(View.VISIBLE);
                    setSpecs(reference);
                    break;
                case 4:
                    moduleAdapter = new ModuleAdapter(this, arrayList);
                    recyclerView.setAdapter(moduleAdapter);
                    moduleAdapter.notifyDataSetChanged();
                    recyclerView.setVisibility(View.VISIBLE);
                    rcView2.setVisibility(View.GONE);
                    setModule(reference);
                    break;
                case 5:
                    titleAdapter = new TitleAdapter(this, arrayList);
                    recyclerView.setAdapter(titleAdapter);
                    titleAdapter.notifyDataSetChanged();
                    recyclerView.setVisibility(View.VISIBLE);
                    rcView2.setVisibility(View.GONE);
                    setDesign(reference);
                    break;
                case 6:
                    safetyAdapter = new SafetyAdapter(this, arrayList);
                    recyclerView.setAdapter(safetyAdapter);
                    safetyAdapter.notifyDataSetChanged();
                    recyclerView.setVisibility(View.VISIBLE);
                    rcView2.setVisibility(View.GONE);
                    setSafety(reference);
                    break;
                case 7:
                    attachAdapter = new AttachAdapter(this, arrayList);
                    recyclerView.setAdapter(attachAdapter);
                    attachAdapter.notifyDataSetChanged();
                    recyclerView.setVisibility(View.VISIBLE);
                    rcView2.setVisibility(View.GONE);
                    setAttach(reference);
                    break;
                case 8:
                    abbreAdapter = new AbbreAdapter(this, arrayList);
                    recyclerView.setAdapter(abbreAdapter);
                    abbreAdapter.notifyDataSetChanged();
                    recyclerView.setVisibility(View.VISIBLE);
                    rcView2.setVisibility(View.GONE);
                    setAbbre(reference);
                    break;
                case 9:

                    break;
                default:
                    //Do Nothing
                    break;
            }
        }else {
            Toast.makeText(this, "No params received", Toast.LENGTH_SHORT).show();
            Log.e("DQDesc", "No params REceived");
            finish();
        }

    }

    private void setApproval(DocumentReference documentReference){
        documentReference.collection("content").document("approval")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists() && documentSnapshot.get("title") != null && documentSnapshot.get("desc") != null){
                            tvDesc.setText(documentSnapshot.get("desc").toString());
                        }else {
                            tvDesc.setText("N/A");
                        }
                    }
                });
        if (arrayList.size() == 0){
            arrayList.add(new DataTitle("vendor", 0, "Vendor"));
            arrayList.add(new DataTitle("customer", 1, "Customer"));
            headAdapter.notifyDataSetChanged();
        }
    }
    private void setPurpose(DocumentReference documentReference){
        documentReference.collection("content").document("purpose")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists() && documentSnapshot.get("title") != null && documentSnapshot.get("desc") != null){
                            tvDesc.setText(documentSnapshot.getData().get("desc").toString());
                        }else {
                            tvDesc.setText("N/A");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                tvDesc.setText("Error" + e.toString());
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
                            tvDesc.setText(documentSnapshot.get("desc").toString());
                        }else {
                            tvDesc.setText("N/A");
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
                            tvDesc.setText(documentSnapshot.get("desc").toString());
                            GenereteSpecsTable(documentReference);
                        }else {
                            tvDesc.setText("N/A");
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
                        arrayList.clear();
                        for (QueryDocumentSnapshot snapshot : value) {
                            if (snapshot.getData().get("title") != null && snapshot.getData().get("index") != null && snapshot.getData().get("input") != null){
                                arrayList.add(new DataSpecs(snapshot.getId(), snapshot.get("index", Integer.class), snapshot.getData().get("title").toString(), snapshot.getData().get("input").toString()));
                                specAdapter.notifyDataSetChanged();

                            }
                        }
                        Log.e("Content", "Added Specs");
                    }
                });
    }
    private void setModule(DocumentReference documentReference){
        documentReference.collection("content").document("config")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists() && documentSnapshot.get("name") != null && documentSnapshot.get("desc") != null){
                            tvDesc.setText(documentSnapshot.get("desc").toString());
                        }else {
                            tvDesc.setText("N/A");
                        }
                    }
                });


        documentReference.collection("content").document("config").collection("modules")
                .orderBy("index")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                        assert value != null;
                        arrayList.clear();
                        for (QueryDocumentSnapshot snapshot : value) {
                            if (snapshot.getData().get("title") != null && snapshot.getData().get("index") != null && snapshot.getData().get("desc") != null && snapshot.getData().get("type") != null){
                                arrayList.add(new DataModuleNew(snapshot.getId(), snapshot.getData().get("title").toString() , snapshot.getData().get("desc").toString(), snapshot.get("index", Integer.class), snapshot.get("type", Integer.class)));
                                new DataModuleNew("", "", "", 1, 1);
                                moduleAdapter.notifyDataSetChanged();
                            }
                        }
                        Log.e("Content", "Added Specs");
                    }
                });
    }

    private void setDesign(DocumentReference documentReference){
        documentReference.collection("content").document("designSpecs")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists() && documentSnapshot.get("title") != null && documentSnapshot.get("desc") != null){
                            tvDesc.setText(documentSnapshot.get("desc").toString());
                        }else {
                            tvDesc.setText("N/A");
                        }
                    }
                });


        documentReference.collection("content").document("designSpecs").collection("title")
                .orderBy("index")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                        assert value != null;
                        arrayList.clear();
                        for (QueryDocumentSnapshot snapshot : value) {
                            if (snapshot.getData().get("title") != null && snapshot.getData().get("index") != null ){
                                arrayList.add(new DataTitle(snapshot.getId(), snapshot.get("index", Integer.class), snapshot.getData().get("title").toString()));
                                new DataTitle("", 1,  "");
                                titleAdapter.notifyDataSetChanged();
                            }
                        }
                        Log.e("Content", "Added Design");
                    }
                });
    }

    private void setSafety(DocumentReference documentReference){
        documentReference.collection("content").document("safety")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists() && documentSnapshot.get("title") != null && documentSnapshot.get("desc") != null){
                            tvDesc.setText(documentSnapshot.get("desc").toString());
                        }else {
                            tvDesc.setText("N/A");
                        }
                    }
                });


        documentReference.collection("content").document("safety").collection("details")
                .orderBy("index")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                        assert value != null;
                        arrayList.clear();
                        for (QueryDocumentSnapshot snapshot : value) {
                            if (snapshot.getData().get("desc") != null && snapshot.getData().get("cause") != null &&
                                    snapshot.getData().get("issue_id") != null && snapshot.getData().get("action") != null &&
                                    snapshot.getData().get("index") != null && snapshot.getData().get("response") != null){
                                arrayList.add(new DataSafety(snapshot.getId(), snapshot.getData().get("desc").toString(),
                                        snapshot.getData().get("cause").toString(), snapshot.getData().get("action").toString(),
                                        snapshot.get("index", Integer.class), snapshot.get("response", Integer.class),
                                        snapshot.getData().get("issue_id").toString()));
                                new DataSafety("", "",  "", "", 1,  1, "");
                                safetyAdapter.notifyDataSetChanged();
                            }
                        }
                        Log.e("Content", "Added Safety");
                    }
                });
    }

    private void setAttach(DocumentReference documentReference){
        documentReference.collection("content").document("attachments")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists() && documentSnapshot.get("title") != null && documentSnapshot.get("desc") != null){
                            tvDesc.setText(documentSnapshot.get("desc").toString());
                        }else {
                            tvDesc.setText("N/A");
                        }
                    }
                });


        documentReference.collection("content").document("attachments").collection("details")
                .orderBy("index")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                        assert value != null;
                        arrayList.clear();
                        for (QueryDocumentSnapshot snapshot : value) {
                            if (snapshot.getData().get("desc") != null && snapshot.getData().get("dno") != null &&
                                    snapshot.getData().get("rev") != null && snapshot.getData().get("index") != null){
                                arrayList.add(new DataAttach(snapshot.getId(), snapshot.getData().get("desc").toString(),
                                        snapshot.getData().get("rev").toString(), snapshot.getData().get("dno").toString(),
                                        snapshot.get("index", Integer.class)));
                                new DataAttach("", "",  "", "",  1);
                                attachAdapter.notifyDataSetChanged();
                            }
                        }
                        Log.e("Content", "Attachments Added");
                    }
                });
    }

    private void setAbbre(DocumentReference documentReference){
        documentReference.collection("content").document("abbreviations")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists() && documentSnapshot.get("title") != null && documentSnapshot.get("desc") != null){
                            tvDesc.setText(documentSnapshot.get("desc").toString());
                        }else {
                            tvDesc.setText("N/A");
                        }
                    }
                });


        documentReference.collection("content").document("abbreviations").collection("details")
                .orderBy("index")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                        assert value != null;
                        arrayList.clear();
                        for (QueryDocumentSnapshot snapshot : value) {
                            if (snapshot.getData().get("full") != null && snapshot.getData().get("shorts") != null && snapshot.getData().get("index") != null){
                                arrayList.add(new DataAbbre(snapshot.getId(), snapshot.getData().get("shorts").toString(),
                                        snapshot.getData().get("full").toString(), snapshot.get("index", Integer.class)));
                                new DataAbbre("", "",  "",   1);
                                abbreAdapter.notifyDataSetChanged();
                            }
                        }
                        Log.e("Content", "Added Abbreviations");
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
            return new SpecAdapter.ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull SpecAdapter.ViewHolder holder, final int position) {

            holder.tvTitle.setText((list.get(position).getIndex() + 1) + " : " + list.get(position).getTitle().toString());

            if (TextUtils.isEmpty(list.get(position).getInput())){
                holder.tvInput.setText("N/A");
            }else {
                holder.tvInput.setText("Input: " + list.get(position).getInput().toString());
            }
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogSpecs(list.get(position));
                }
            });
        }
        @Override
        public int getItemCount() {
            return list.size();
        }
        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle, tvInput;
            CardView cardView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.list_specs_tvTitle);
                tvInput = itemView.findViewById(R.id.list_specs_tvInput);
                cardView = itemView.findViewById(R.id.list_specs_cardview);

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
            return new ModuleAdapter.ViewHolder(view);
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

            switch (list.get(position).getType()){
                case 0:
                    ArrayList<DataCompNew> compArrayList = new ArrayList<>();
                    ComponentAdapter componentAdapter = new ComponentAdapter(context, compArrayList);
                    holder.recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    //  holder.recyclerView.setHasFixedSize(true);
                    holder.recyclerView.setNestedScrollingEnabled(false);
                    holder.recyclerView.setAdapter(componentAdapter);
                    componentAdapter.notifyDataSetChanged();

                    firestore.collection("DQNewReport").document(key).collection("content")
                            .document("config").collection("components")
                            .whereEqualTo("module_id", list.get(position).getKey())
                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                                    assert value != null;
                                    compArrayList.clear();
                                    for (QueryDocumentSnapshot snapshot : value) {
                                        if (snapshot.getData().get("title") != null && snapshot.getData().get("value") != null &&
                                                snapshot.getData().get("response") != null && snapshot.getData().get("module_id") != null &&
                                                snapshot.getData().get("index") != null && snapshot.getData().get("issue_id") != null){
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
                    break;

                case 1:
                    ArrayList<DataCompNew2> compArrayList2 = new ArrayList<>();
                    Component2Adapter component2Adapter = new Component2Adapter(context, compArrayList2);
                    holder.recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    //  holder.recyclerView.setHasFixedSize(true);
                    holder.recyclerView.setNestedScrollingEnabled(false);
                    holder.recyclerView.setAdapter(component2Adapter);
                    component2Adapter.notifyDataSetChanged();

                    firestore.collection("DQNewReport").document(key).collection("content")
                            .document("config").collection("components")
                            .whereEqualTo("module_id", list.get(position).getKey())
                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                                    assert value != null;
                                    compArrayList2.clear();
                                    for (QueryDocumentSnapshot snapshot : value) {
                                        if (snapshot.getData().get("desc") != null && snapshot.getData().get("module_id") != null &&
                                                snapshot.getData().get("req") != null && snapshot.getData().get("connection") != null &&
                                                snapshot.getData().get("inst") != null && snapshot.getData().get("response") != null &&
                                                snapshot.getData().get("issue_id") != null && snapshot.getData().get("index") != null) {

                                            compArrayList2.add(new DataCompNew2(snapshot.getId(), snapshot.getData().get("desc").toString() ,
                                                    snapshot.getData().get("req").toString(), snapshot.getData().get("inst").toString(),
                                                    snapshot.getData().get("connection").toString(), snapshot.getData().get("module_id").toString(),
                                                    snapshot.get("index", Integer.class), snapshot.get("response", Integer.class),
                                                    snapshot.getData().get("issue_id").toString()));

                                            new DataCompNew2("", "", "", "", "", "", 1, 1, "");

                                        }else {
                                            Log.e("Machines", "Missing Parameters");
                                        }
                                    }

                                    compArrayList2.sort(new Comparator<DataCompNew2>() {
                                        @Override
                                        public int compare(DataCompNew2 com1, DataCompNew2 com2) {
                                            return Integer.compare(com1.getIndex(), com2.getIndex());
                                        }
                                    });
                                    component2Adapter.notifyDataSetChanged();
                                }
                            });
                    break;

                default:
                    // Do  nothing
                    break;
            }



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
            return new ComponentAdapter.ViewHolder(view);
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
                        Intent intent = new Intent(DescActivity.this, IssueActivity.class);
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

    public class Component2Adapter extends RecyclerView.Adapter<Component2Adapter.ViewHolder>{

        private Context context;
        private ArrayList<DataCompNew2> list=new ArrayList<>();

        public Component2Adapter(Context context, ArrayList<DataCompNew2> list) {
            this.context=context;
            this.list=list;
        }

        @NonNull
        @Override
        public Component2Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_quality_single2, parent, false);
            return new Component2Adapter.ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull Component2Adapter.ViewHolder holder, final int position) {

            holder.tvTitle.setText((list.get(position).getIndex() + 1) + " : " + list.get(position).getDesc());
            holder.tvConn.setText(list.get(position).getConnection());
            holder.tvInst.setText(list.get(position).getInst());
            holder.tvValue.setText(list.get(position).getReq());
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
                        Intent intent = new Intent(DescActivity.this, IssueActivity.class);
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
            TextView tvTitle, tvValue, tvStatus, tvComment, tv4, tvConn, tvInst;
            ConstraintLayout constraintLayout;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvStatus = itemView.findViewById(R.id.list_quality_single2_tvStatus);
                tvTitle = itemView.findViewById(R.id.list_quality_single2_tvTitle);
                tvConn = itemView.findViewById(R.id.list_quality_single2_tvConn);
                tvInst = itemView.findViewById(R.id.list_quality_single2_tvInst);
                tvValue = itemView.findViewById(R.id.list_quality_single2_tvValue);
                tvComment = itemView.findViewById(R.id.list_quality_single2_tvContent);
                constraintLayout = itemView.findViewById(R.id.list_quality_single2_layout);
                tv4 = itemView.findViewById(R.id.list_quality_single2_tv6);

            }
        }
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

            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_title2, parent, false);
            return new ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull TitleAdapter.ViewHolder holder, final int position) {

            holder.tvTitle.setText((list.get(position).getIndex() + 1) + " : " + list.get(position).getTitle().toString());
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

            ArrayList<DataPoint> pointArrayList = new ArrayList<>();
            PointAdapter pointAdapter = new PointAdapter(context, pointArrayList);
            holder.recyclerView.setLayoutManager(new LinearLayoutManager(context));
            //  holder.recyclerView.setHasFixedSize(true);
            holder.recyclerView.setNestedScrollingEnabled(false);
            holder.recyclerView.setAdapter(pointAdapter);
            pointAdapter.notifyDataSetChanged();

            firestore.collection("DQNewReport").document(key).collection("content")
                    .document("designSpecs").collection("points")
                    .whereEqualTo("tid", list.get(position).getKey())
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                            assert value != null;
                            pointArrayList.clear();
                            for (QueryDocumentSnapshot snapshot : value) {
                                if (snapshot.getData().get("desc") != null && snapshot.getData().get("tid") != null &&
                                        snapshot.getData().get("response") != null  &&
                                        snapshot.getData().get("index") != null && snapshot.getData().get("issue_id") != null){
                                    pointArrayList.add(new DataPoint(snapshot.getId(), snapshot.getData().get("desc").toString(), snapshot.getData().get("tid").toString() , snapshot.get("index", Integer.class),snapshot.get("response",Integer.class), snapshot.getData().get("issue_id").toString() ));
                                }
                            }

                            pointArrayList.sort(new Comparator<DataPoint>() {
                                @Override
                                public int compare(DataPoint com1, DataPoint com2) {
                                    return Integer.compare(com1.getIndex(), com2.getIndex());
                                }
                            });
                            pointAdapter.notifyDataSetChanged();
                        }
                    });

        }
        @Override
        public int getItemCount() {
            return list.size();
        }
        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle;
            ImageView imageView;
            RecyclerView recyclerView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.list_title2_tvTitle);
                imageView = itemView.findViewById(R.id.list_title2_bShow);
                recyclerView = itemView.findViewById(R.id.list_title2_rcView);

            }
        }
    }

    public class PointAdapter extends RecyclerView.Adapter<PointAdapter.ViewHolder>{

        private Context context;
        private ArrayList<DataPoint> list=new ArrayList<>();

        public PointAdapter(Context context, ArrayList<DataPoint> list) {
            this.context=context;
            this.list=list;
        }

        @NonNull
        @Override
        public PointAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_point, parent, false);
            return new PointAdapter.ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull PointAdapter.ViewHolder holder, final int position) {

            holder.tvTitle.setText((list.get(position).getIndex() + 1) + " : " + list.get(position).getDesc());
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
                        Intent intent = new Intent(DescActivity.this, IssueActivity.class);
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
            TextView tvTitle, tvStatus, tvComment, tv4;
            ConstraintLayout constraintLayout;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvStatus = itemView.findViewById(R.id.list_point_tvStatus);
                tvTitle = itemView.findViewById(R.id.list_point_tvTitle);
                tvComment = itemView.findViewById(R.id.list_point_tvContent);
                constraintLayout = itemView.findViewById(R.id.list_point_layout);
                tv4 = itemView.findViewById(R.id.list_point_tv3);

            }
        }
    }

    public class SafetyAdapter extends RecyclerView.Adapter<SafetyAdapter.ViewHolder>{

        private Context context;
        private ArrayList<DataSafety> list=new ArrayList<>();

        public SafetyAdapter(Context context, ArrayList<DataSafety> list) {
            this.context=context;
            this.list=list;
        }

        @NonNull
        @Override
        public SafetyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_safety, parent, false);
            return new SafetyAdapter.ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull SafetyAdapter.ViewHolder holder, final int position) {

            holder.tvDesc.setText((list.get(position).getIndex() + 1) + " : " + list.get(position).getDesc());
            holder.tvCause.setText(list.get(position).getCause());
            holder.tvAction.setText(list.get(position).getAction());
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
                        Intent intent = new Intent(DescActivity.this, IssueActivity.class);
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
            TextView tvDesc, tvCause, tvAction, tvStatus, tvComment, tv4;
            ConstraintLayout constraintLayout;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvStatus = itemView.findViewById(R.id.list_safety_tvStatus);
                tvDesc = itemView.findViewById(R.id.list_safety_tvDesc);
                tvCause = itemView.findViewById(R.id.list_safety_tvCause);
                tvAction = itemView.findViewById(R.id.list_safety_tvAction);
                tvComment = itemView.findViewById(R.id.list_safety_tvContent);
                constraintLayout = itemView.findViewById(R.id.list_safety_layout);
                tv4 = itemView.findViewById(R.id.list_safety_tv6);

            }
        }
    }

    public class AttachAdapter extends RecyclerView.Adapter<AttachAdapter.ViewHolder>{

        private Context context;
        private ArrayList<DataAttach> list=new ArrayList<>();

        public AttachAdapter(Context context, ArrayList<DataAttach> list) {
            this.context=context;
            this.list=list;
        }

        @NonNull
        @Override
        public AttachAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_attach, parent, false);
            return new AttachAdapter.ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull AttachAdapter.ViewHolder holder, final int position) {

            holder.tvDesc.setText((list.get(position).getIndex() + 1) + " : " + list.get(position).getDesc().toString());
            holder.tvRevision.setText(list.get(position).getRev());

            if (TextUtils.isEmpty(list.get(position).getDno())){
                holder.tvDraw.setText("N/A");
            }else {
                holder.tvDraw.setText(list.get(position).getDno().toString());
            }

        }
        @Override
        public int getItemCount() {
            return list.size();
        }
        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvDesc, tvDraw, tvRevision;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvDesc = itemView.findViewById(R.id.list_attach_tvDesc);
                tvDraw = itemView.findViewById(R.id.list_attach_tvDraw);
                tvRevision = itemView.findViewById(R.id.list_attach_tvRev);

            }
        }
    }

    public class AbbreAdapter extends RecyclerView.Adapter<AbbreAdapter.ViewHolder>{

        private Context context;
        private ArrayList<DataAbbre> list=new ArrayList<>();

        public AbbreAdapter(Context context, ArrayList<DataAbbre> list) {
            this.context=context;
            this.list=list;
        }

        @NonNull
        @Override
        public AbbreAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_abbre, parent, false);
            return new AbbreAdapter.ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull AbbreAdapter.ViewHolder holder, final int position) {

            holder.tvAbbre.setText((list.get(position).getIndex() + 1) + " : " + list.get(position).getShorts().toString());
            holder.tvMeaning.setText(list.get(position).getFull());
        }
        @Override
        public int getItemCount() {
            return list.size();
        }
        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvAbbre, tvMeaning;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvAbbre = itemView.findViewById(R.id.list_abbre_tvAbbre);
                tvMeaning = itemView.findViewById(R.id.list_abbre_tvMeaning);

            }
        }
    }

    public class HeadAdapter extends RecyclerView.Adapter<HeadAdapter.ViewHolder>{

        private Context context;
        private ArrayList<DataTitle> list=new ArrayList<>();

        public HeadAdapter(Context context, ArrayList<DataTitle> list) {
            this.context=context;
            this.list=list;
        }

        @NonNull
        @Override
        public HeadAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_title2, parent, false);
            return new ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull HeadAdapter.ViewHolder holder, final int position) {

            holder.tvTitle.setText((list.get(position).getIndex() + 1) + " : " + list.get(position).getTitle().toString());
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

            ArrayList<DataApproval> approvalArrayList = new ArrayList<>();
            ApprovalAdapter approvalAdapter = new ApprovalAdapter(context, approvalArrayList);
            holder.recyclerView.setLayoutManager(new LinearLayoutManager(context));
            //  holder.recyclerView.setHasFixedSize(true);
            holder.recyclerView.setNestedScrollingEnabled(false);
            holder.recyclerView.setAdapter(approvalAdapter);
            approvalAdapter.notifyDataSetChanged();

            switch (list.get(position).getIndex()){
                case 0:
                    firestore.collection("DQNewReport").document(key).collection("content")
                            .document("approval").collection("vendor")
                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                                    assert value != null;
                                    approvalArrayList.clear();
                                    for (QueryDocumentSnapshot snapshot : value) {
                                        if (snapshot.getData().get("timestamp") != null && snapshot.getData().get("url") != null &&
                                                snapshot.getData().get("name") != null){
                                            approvalArrayList.add(new DataApproval(snapshot.getId(), snapshot.getData().get("name").toString(),
                                                    snapshot.getData().get("url").toString(), snapshot.getTimestamp("timestamp")));
                                        }
                                    }
                                    approvalAdapter.notifyDataSetChanged();
                                }
                            });
                    break;
                case 1:
                    firestore.collection("DQNewReport").document(key).collection("content")
                            .document("approval").collection("customer")
                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                                    assert value != null;
                                    approvalArrayList.clear();
                                    for (QueryDocumentSnapshot snapshot : value) {
                                        if (snapshot.getData().get("timestamp") != null && snapshot.getData().get("url") != null &&
                                                snapshot.getData().get("name") != null){
                                            approvalArrayList.add(new DataApproval(snapshot.getId(), snapshot.getData().get("name").toString(),
                                                    snapshot.getData().get("url").toString(), snapshot.getTimestamp("timestamp")));
                                        }
                                    }
                                    approvalAdapter.notifyDataSetChanged();
                                }
                            });
                    break;

                default:
                    break;
            }

        }
        @Override
        public int getItemCount() {
            return list.size();
        }
        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle;
            ImageView imageView;
            RecyclerView recyclerView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.list_title2_tvTitle);
                imageView = itemView.findViewById(R.id.list_title2_bShow);
                recyclerView = itemView.findViewById(R.id.list_title2_rcView);

            }
        }
    }


    public class ApprovalAdapter extends RecyclerView.Adapter<ApprovalAdapter.ViewHolder>{

        private Context context;
        private ArrayList<DataApproval> list=new ArrayList<>();

        public ApprovalAdapter(Context context, ArrayList<DataApproval> list) {
            this.context=context;
            this.list=list;
        }

        @NonNull
        @Override
        public ApprovalAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_approval, parent, false);
            return new ApprovalAdapter.ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull ApprovalAdapter.ViewHolder holder, final int position) {

            holder.tvUser.setText("Name: " + list.get(position).getName());
            Timestamp timestamp = list.get(position).getTimestamp();
            holder.tvTime.setText("Date: " + timestamp.toDate().toString().substring(0, 20));
            Glide.with(context).load(list.get(position).getUrl()).into(holder.imageView);
        }
        @Override
        public int getItemCount() {
            return list.size();
        }
        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvUser, tvTime;
            ImageView imageView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvUser = itemView.findViewById(R.id.list_approval_tvUser);
                tvTime = itemView.findViewById(R.id.list_approval_tvTime);
                imageView = itemView.findViewById(R.id.list_approval_imageview);

            }
        }
    }

    @WorkerThread
    private void DialogSpecs(DataSpecs dataSpecs){
        ProgressDialog mProgressDialog;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater=DescActivity.this.getLayoutInflater();
        final View view=inflater.inflate(R.layout.update_specs_dialog, null);
        alertDialogBuilder.setView(view);
        //alertDialogBuilder.setCancelable(false);
        AlertDialog alertDialog = alertDialogBuilder.create();

        TextView textView = view.findViewById(R.id.update_specs_dialog_tvTitle);
        EditText tvInput = view.findViewById(R.id.update_specs_dialog_tvInput);
        textView.setText(dataSpecs.getTitle().toString());
        tvInput.setText(dataSpecs.getInput().toString());

        Button bDownload = view.findViewById(R.id.update_specs_dialog_bDownload);
        Button bCancel = view.findViewById(R.id.update_specs_dialog_bCancel);

        bDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                reference.collection("content").document("specifications").collection("specDetails")
                        .document(dataSpecs.getKey())
                        .update("input", tvInput.getText().toString().trim())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                alertDialog.dismiss();
                                Toast.makeText(DescActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull @NotNull Exception e) {
                                Toast.makeText(DescActivity.this, "Cannot Update", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        alertDialog.setTitle("Edit Input");
        alertDialog.show();


        //   alertDialogBuilder.setTitle("Update Available");
        //  alertDialogBuilder.show();


    }

}