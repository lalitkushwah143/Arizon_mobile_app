package com.genius.appdesign2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.genius.appdesign2.data.DataBatch;
import com.genius.appdesign2.data.DataModule;
import com.genius.appdesign2.data.DataReport;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
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
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class ReportActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DatabaseReference reference;
    private ArrayList<DataBatch> arrayList = new ArrayList<>();
    private ReportAdapter adapter;
    private String currentPhotoPath;
    private StorageReference storageReference, ref1;
    private File storageDir;
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private TextView tvBlank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        getSupportActionBar().hide();
        TextView textView = findViewById(R.id.activity_report_tvTitle);
        textView.setText("Batch Report");
        ImageView imageView = findViewById(R.id.activity_report_imageview);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        tvBlank = findViewById(R.id.activity_report_tvBlank);
        recyclerView = findViewById(R.id.activity_report_rcView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        adapter = new ReportAdapter(this, arrayList);
        recyclerView.setAdapter(adapter);

        storageReference = FirebaseStorage.getInstance().getReference().child("batch");

        reference = FirebaseDatabase.getInstance().getReference().child("records");
        reference.keepSynced(true);


        firestore.collection("batchReport")
                .whereEqualTo("machine_id", SplashActivity.machine_id)
                .addSnapshotListener( MetadataChanges.INCLUDE , new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        assert value != null;
                        arrayList.clear();
                        for (QueryDocumentSnapshot snapshot : value) {
                            if (snapshot.getData().get("user_id") != null && snapshot.getData().get("machine_id") != null &&
                                    snapshot.getData().get("manual_id") != null && snapshot.getData().get("url") != null &&
                                    snapshot.getData().get("time") != null) {

                                arrayList.add(new DataBatch(snapshot.getId(),
                                        snapshot.getData().get("user_id").toString(),
                                        snapshot.getData().get("machine_id").toString(),
                                        snapshot.getData().get("manual_id").toString(),
                                        snapshot.getData().get("url").toString(),
                                        snapshot.getTimestamp("time")));
                                adapter.notifyDataSetChanged();
                            }else {
                                Log.e("Report", "Missing Parameter");
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


    }

    public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ViewHolder>{

        private Context context;
        private ArrayList<DataBatch> list=new ArrayList<>();

        public ReportAdapter(Context context, ArrayList<DataBatch> list) {
            this.context=context;
            this.list=list;
        }

        @NonNull
        @Override
        public ReportAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_report, parent, false);
            return new ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull ReportAdapter.ViewHolder holder, final int position) {

            holder.tvUser.setText("By: " + list.get(position).getUser_id().toString());

            firestore.collection("manualData")
                    .document(list.get(position).getManual_id())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                            holder.tvManual.setText("Manual: " +Objects.requireNonNull(documentSnapshot.get("title")).toString());
                            Log.e("ReportActivity", "Success Manual Title");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    holder.tvManual.setText("Undefined");
                    Log.e("REportActivity", e.toString());
                }
            });

            String date, time, year, string;
            int hh, mm;

            holder.tvTime.setText(list.get(position).getTime().toDate().toString().substring(0, 20));

            String imageID = arrayList.get(position).getKey().toString();
            Boolean flag = fileExists(imageID);
            if (flag){
                holder.bDownload.setVisibility(View.GONE);
                String url = storageDir.getPath() + "/" + imageID+ ".jpg";
                File this_file = new File(url);
            //    Picasso.get().load(Uri.fromFile(this_file)).into(holder.imageView);
                Glide.with(context).load(Uri.fromFile(this_file)).into(holder.imageView);
            }else {
                holder.bDownload.setVisibility(View.VISIBLE);
             //   Picasso.get().load(arrayList.get(position).getUrl()).into(holder.imageView);
                Glide.with(context).load(arrayList.get(position).getUrl()).into(holder.imageView);
            }



            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ImageActivity.class);
                    Boolean aBoolean = fileExists(imageID);

                    if (aBoolean){
                        String url = storageDir.getPath() + "/" + imageID+ ".jpg";
                        intent.putExtra("url", url);
                        intent.putExtra("flag", true);
                    }else {
                        intent.putExtra("url", arrayList.get(position).getUrl());
                        intent.putExtra("flag", false);
                    }

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        ActivityOptions activityOptions=ActivityOptions.makeSceneTransitionAnimation(ReportActivity.this, holder.imageView, holder.imageView.getTransitionName());
                        startActivity(intent, activityOptions.toBundle());
                    }else{
                        startActivity(intent);
                    }
                }
            });

            holder.bDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    holder.bDownload.setVisibility(View.GONE);
                    holder.progressBar.setVisibility(View.VISIBLE);

                    File photoFile = null;
                    try {
                        photoFile = createImageFile(imageID);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e("Report: ", "Error Creating File");
                    }

                    if (photoFile != null){
                        Uri photoUri = FileProvider.getUriForFile(ReportActivity.this,
                                "com.genius.appdesign2.fileprovider",
                                photoFile);
                        ref1 = storageReference.child(arrayList.get(position).getKey());
                        Log.e("Report", ref1.getName());
                        ref1.getFile(photoFile)
                                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                        Log.e("Report: ", "Image downloaded");
                                        holder.progressBar.setVisibility(View.GONE);
                                        Boolean aBoolean = fileExists(imageID);
                                        if (aBoolean){
                                            String url = storageDir.getPath() + "/" + imageID+ ".jpg";
                                            File this_file = new File(url);
                                            Glide.with(context).load(this_file).into(holder.imageView);
                                        }

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                holder.progressBar.setVisibility(View.GONE);
                                holder.bDownload.setVisibility(View.VISIBLE);
                                Log.e("Report: ", "Failed");
                            }
                        });
                    }
                }
            });
        }
        @Override
        public int getItemCount() {
            return list.size();
        }
        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView, bDownload;
            TextView tvUser, tvManual, tvTime;
            ProgressBar progressBar;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.list_report_imageview);
                tvUser = itemView.findViewById(R.id.list_report_tvUser);
                bDownload = itemView.findViewById(R.id.list_report_bDownload);
                tvManual = itemView.findViewById(R.id.list_report_tvManual);
                tvTime = itemView.findViewById(R.id.list_report_tvTime);
                progressBar = itemView.findViewById(R.id.list_report_progressbar);
            }
        }
    }

    private File createImageFile(String imageID) throws IOException {
        // Create an image file name
        Log.e("Report: ", imageID);

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
   /*     File image = File.createTempFile(
                imageID,
                ".jpg",
                storageDir
        );

    */
        File file = new File(storageDir.getPath() + "/" + imageID +".jpg");
        Boolean aBoolean;
        aBoolean = file.createNewFile();
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = file.getAbsolutePath();
        Log.e("Report: ", currentPhotoPath.toString() +aBoolean.toString());
        return file;
    }

    public boolean fileExists(String filename) {
        String url = storageDir.getPath() + "/" + filename+ ".jpg";
        Log.e("Report: ", url);
        File file = new File(url);
        return file.exists();
    }

}