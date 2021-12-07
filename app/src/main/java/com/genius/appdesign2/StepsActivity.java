package com.genius.appdesign2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.genius.appdesign2.data.DataManuals;
import com.genius.appdesign2.data.DataStep;
import com.genius.appdesign2.data.DataSteps;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class StepsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private StepsAdapter adapter;
    private ArrayList<DataStep> arrayList = new ArrayList<>();
    private StorageReference storageReference;
    private String manualTitle, manual_id;
    private String currentPhotoPath;
    private File storageDir;
    private TextView textView;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps);

        getSupportActionBar().hide();
        textView = findViewById(R.id.activity_steps_tvTitle);
        ImageView imageView = findViewById(R.id.activity_steps_imageview);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        fab = findViewById(R.id.activity_steps_fab);

        manualTitle = getIntent().getStringExtra("manual_title");
        manual_id = getIntent().getStringExtra("manual_id");

        storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);


        recyclerView = findViewById(R.id.activity_steps_rcView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        adapter = new StepsAdapter(this, arrayList);
        recyclerView.setAdapter(adapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StepsActivity.this, AddStepActivity.class);
                intent.putExtra("manual_id", manual_id);
                intent.putExtra("manual_title", manualTitle);
                intent.putExtra("index", arrayList.size());
                startActivity(intent);
            }
        });


        db = FirebaseFirestore.getInstance();



    }

    public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.ViewHolder>{

        private Context context;
        private ArrayList<DataStep> list=new ArrayList<>();

        public StepsAdapter(Context context, ArrayList<DataStep> list) {
            this.context=context;
            this.list=list;
        }

        @NonNull
        @Override
        public StepsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_steps, parent, false);
            return new ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull StepsAdapter.ViewHolder holder, final int position) {
            holder.tvTitle.setText(list.get(position).getTitle().toString());
            holder.tvDesc.setText(list.get(position).getDesc());
            holder.tvCreated.setText(list.get(position).getType());
            Picasso.get().load(list.get(position).getUrl()).into(holder.imageView);

        }
        @Override
        public int getItemCount() {
            return list.size();
        }
        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvTitle, tvDesc, tvCreated;
            ImageView imageView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.list_steps_tvTitle);
                tvDesc = itemView.findViewById(R.id.list_steps_tvDesc);
                tvCreated = itemView.findViewById(R.id.list_steps_tvCreated);
                imageView = itemView.findViewById(R.id.list_steps_imageview);
            }
        }
    }

/*
    public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.ViewHolder>{

        private Context context;
        private ArrayList<DataStep> list=new ArrayList<>();

        public StepsAdapter(Context context, ArrayList<DataStep> list) {
            this.context=context;
            this.list=list;
        }

        @NonNull
        @Override
        public StepsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_steps, parent, false);
            return new ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull StepsAdapter.ViewHolder holder, final int position) {
            holder.tvTitle.setText(list.get(position).getTitle().toString());
            holder.tvDesc.setText(list.get(position).getDesc());
            holder.tvCreated.setText(list.get(position).getCreatedAt());

            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            StorageReference ref1 = firebaseStorage.getReference().child("media").child("steps").child(list.get(position).getUniqueKey()).child(list.get(position).getImg());
            //StorageReference ref1 = firebaseStorage.getReferenceFromUrl("gs://lyoimsweb.appspot.com/media/steps/"+ list.get(position).getUniqueKey() + "/" + list.get(position).getImg());
            //   Log.e("Stepss: ", storageReference.toString());

            //     StorageReference ref1 = storageReference.child("media").child("steps").child("sample.png");
            Log.e("Stepss: ", ref1.toString());

            File photoFile = null;
            try {
                photoFile = createImageFile(list.get(position).getImg());
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("Report: ", "Error Creating File");
            }

            if (photoFile != null){
                Uri photoUri = FileProvider.getUriForFile(StepsActivity.this,
                        "com.genius.appdesign2.fileprovider",
                        photoFile);
                Log.e("Report", ref1.getName());
                ref1.getFile(photoFile)
                        .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                Log.e("Report: ", "Image downloaded");
                                Boolean aBoolean = fileExists(list.get(position).getImg());
                                if (aBoolean){
                                    Log.e("Steps: " , "wokring tru");
                                    String url = storageDir.getPath() + "/" + list.get(position).getImg();
                                    File this_file = new File(url);
                                    Glide.with(context).load(this_file).into(holder.imageView);
                                }

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.e("Report: ", "Failed");
                    }
                });
            }


        }
        @Override
        public int getItemCount() {
            return list.size();
        }
        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvTitle, tvDesc, tvCreated;
            ImageView imageView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.list_steps_tvTitle);
                tvDesc = itemView.findViewById(R.id.list_steps_tvDesc);
                tvCreated = itemView.findViewById(R.id.list_steps_tvCreated);
                imageView = itemView.findViewById(R.id.list_steps_imageview);
            }
        }
    }


 */
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
        File file = new File(storageDir.getPath() + "/" + imageID);
        Boolean aBoolean;
        aBoolean = file.createNewFile();
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = file.getAbsolutePath();
        Log.e("Steps: ", currentPhotoPath.toString() +aBoolean.toString());
        return file;
    }

    public boolean fileExists(String filename) {
        String url = storageDir.getPath() + "/" + filename;
        Log.e("Steps: ", url);
        File file = new File(url);
        return file.exists();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (manualTitle!= null && manual_id != null){

            textView.setText(manualTitle + " - Steps");

            db.collection("stepData")
                    .whereEqualTo("manual_id", manual_id)
                    .addSnapshotListener( MetadataChanges.INCLUDE , new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            assert value != null;
                            arrayList.clear();
                            for (QueryDocumentSnapshot snapshot : value) {
                                Log.e("StepsActivity: ", snapshot.getData().toString());

                                if (snapshot.getData().get("index") != null &&
                                        snapshot.getData().get("title") != null &&
                                        snapshot.getData().get("desc") != null &&
                                        snapshot.getData().get("format") != null &&
                                        snapshot.getData().get("url") != null &&
                                        snapshot.getData().get("type") != null &&
                                        snapshot.getData().get("manual_id") != null) {

                                    arrayList.add(new DataStep(snapshot.getId(),
                                            Integer.parseInt(snapshot.getData().get("index").toString()),
                                            snapshot.getData().get("title").toString(),
                                            snapshot.getData().get("desc").toString(),
                                            snapshot.getData().get("format").toString(),
                                            snapshot.getData().get("url").toString(),
                                            snapshot.getData().get("type").toString(),
                                            snapshot.getData().get("manual_id").toString()));
                                    adapter.notifyDataSetChanged();
                                }else {
                                    Log.e("Steps", "Missing parameters");
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
}