package com.genius.appdesign2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.genius.appdesign2.data.DataIssue;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class IssueActivity extends AppCompatActivity {

    private TextView tvTitle, tvModule;
    private Button bSubmit;
    private EditText etContent;
    private DatabaseReference reference;

    private FirebaseFirestore firestore;
    private DocumentReference documentReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue);

        getSupportActionBar().hide();

        tvModule = findViewById(R.id.activity_issue_tvModule);
        tvTitle = findViewById(R.id.activity_issue_tvTitle);
        etContent = findViewById(R.id.activity_issue_etContent);
        bSubmit = findViewById(R.id.activity_issue_bSubmit);

        String key = getIntent().getStringExtra("key");

        firestore = FirebaseFirestore.getInstance();
        documentReference = firestore.collection("issueData").document(key);

        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {

                if (snapshot != null && snapshot.exists() && snapshot.get("module") != null && snapshot.get("title") != null && snapshot.get("content") != null) {
                    tvModule.setText(snapshot.get("module").toString());
                    tvTitle.setText(snapshot.get("title").toString());
                    etContent.setText(snapshot.get("content").toString());

                } else {
                    Log.d("IssueActivity", "Current data: null");
                }
            }
        });
/*
        reference = FirebaseDatabase.getInstance().getReference().child("validator").child(key);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DataIssue dataIssue = snapshot.getValue(DataIssue.class);

              //  assert dataIssue != null;
                tvModule.setText(dataIssue.getModule().toString());
                tvTitle.setText(dataIssue.getTitle().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


 */
        bSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content= etContent.getText().toString();
                if (TextUtils.isEmpty(content)){
                    Toast.makeText(IssueActivity.this, "Please enter the Description", Toast.LENGTH_SHORT).show();
                }else {

                    documentReference.update("flag", false);
                    documentReference.update("content", content);
                    documentReference.update("flag", false, "content", content)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(IssueActivity.this, "Issue Submitted", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(IssueActivity.this, "Firestore Error", Toast.LENGTH_SHORT).show();
                        }
                    });


               //     reference.child("flag").setValue(false);
                //    reference.child("content").setValue(content);
                    Toast.makeText(IssueActivity.this, "Issue Submitted", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }
}