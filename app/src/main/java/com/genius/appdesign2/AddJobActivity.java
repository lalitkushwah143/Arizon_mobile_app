package com.genius.appdesign2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.genius.appdesign2.data.DataJob;
import com.genius.appdesign2.data.DataLoad;
import com.genius.appdesign2.data.DataRecipe;
import com.genius.appdesign2.data.DataUser;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AddJobActivity extends AppCompatActivity {

    private Button button;
    private DatabaseReference reference, ref_job;
    private EditText etTitle, etDesc;
    private Spinner spinner, spinner2;
    private ArrayList<String> arrayList= new ArrayList<>();
    private ArrayList<DataLoad> recipeArrayList = new ArrayList<>();
    private ArrayList<String> userArrayList = new ArrayList<>();

    private FirebaseFirestore firestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_job);

        getSupportActionBar().hide();
        TextView textView = findViewById(R.id.activity_add_job_tvTitle);
        textView.setText("Create Job");
        ImageView imageView = findViewById(R.id.activity_add_job_imageview);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        firestore = FirebaseFirestore.getInstance();

        reference = FirebaseDatabase.getInstance().getReference().child("recipes");
        ref_job = FirebaseDatabase.getInstance().getReference().child("jobs");

        button = findViewById(R.id.activity_add_job_bAdd);
        etTitle = findViewById(R.id.activity_add_job_etTitle);
        etDesc = findViewById(R.id.activity_add_job_etDesc);
        spinner = findViewById(R.id.activity_add_job_spinner);
        spinner2 = findViewById(R.id.activity_add_job_spinnerUser);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add job to firebase
                if (TextUtils.isEmpty(etTitle.getText().toString()) || TextUtils.isEmpty(etDesc.getText().toString()) || arrayList == null || userArrayList == null){
                    Toast.makeText(AddJobActivity.this, "Enter Job Details", Toast.LENGTH_SHORT).show();
                }else {
                    String title = etTitle.getText().toString();
                    String desc = etDesc.getText().toString();
                    String recipe_key = recipeArrayList.get(spinner.getSelectedItemPosition()).getKey();
                    String user_email = userArrayList.get(spinner2.getSelectedItemPosition());

                    firestore.collection("jobData")
                            .add(new DataJob(title, recipe_key, SplashActivity.machine_id, desc, user_email, Timestamp.now(), false))
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(AddJobActivity.this, "Job Created Succesfully", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddJobActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });
        /*
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                arrayList.add(snapshot.child("title").getValue().toString());
                if (arrayList!= null){
                    spinner.setAdapter(new ArrayAdapter<String>(AddJobActivity.this,R.layout.support_simple_spinner_dropdown_item,arrayList));
                }else {
                    Toast.makeText(AddJobActivity.this, "No recipes found", Toast.LENGTH_SHORT).show();
                }

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
        firestore.collection("recipes")
                .whereEqualTo("mid", SplashActivity.machine_id)
                .addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        assert value != null;
                        recipeArrayList.clear();
                        arrayList.clear();
                        for (QueryDocumentSnapshot snapshot : value) {

                            if (snapshot.getData().get("title") != null && snapshot.getData().get("mid") != null) {

                                ArrayList<DataRecipe> dataRecipes = new ArrayList<>();
                                recipeArrayList.add(new DataLoad(snapshot.getId(),
                                        snapshot.getData().get("title").toString(),
                                        snapshot.getData().get("mid").toString(),
                                        null));

                                arrayList.add(snapshot.getData().get("title").toString());
                                Log.e("Sample", snapshot.getData().get("title").toString());
                            }else {
                                Log.e("Add Job", "Missing Parameter");
                            }
                        }
                        if (arrayList!= null){
                            spinner.setAdapter(new ArrayAdapter<String>(AddJobActivity.this,R.layout.support_simple_spinner_dropdown_item,arrayList));
                        }else {
                            Toast.makeText(AddJobActivity.this, "No recipes found", Toast.LENGTH_SHORT).show();
                        }                    }
                });

        firestore.collection("users")
                .addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        assert value != null;
                        userArrayList.clear();
                        for (QueryDocumentSnapshot snapshot : value) {

                            if (snapshot.getData().get("email") != null) {
                                userArrayList.add(snapshot.getData().get("email").toString());
                            }else {
                                Log.e("Add Job", "Missing Parameter");
                            }
                        }
                        if (userArrayList!= null){
                            spinner2.setAdapter(new ArrayAdapter<String>(AddJobActivity.this,R.layout.support_simple_spinner_dropdown_item,userArrayList));
                        }else {
                            Toast.makeText(AddJobActivity.this, "No Users found", Toast.LENGTH_SHORT).show();
                        }                    }
                });


    }


}