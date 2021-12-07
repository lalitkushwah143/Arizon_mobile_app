package com.genius.appdesign2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;

public class UploadActivity extends AppCompatActivity {

    private CardView cardView;
    private ImageView imageView, bSend;

    private Uri uri;
    Date now = new Date();
    private String session_key;

    private StorageReference storageReference, storageRef1;
    private DatabaseReference reference, ref1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        cardView = findViewById(R.id.activity_call_cardview);
        bSend = findViewById(R.id.activity_call_bsend);
        imageView = findViewById(R.id.activity_call_imageview);

        session_key = getIntent().getStringExtra("session_key");
        uri = getIntent().getParcelableExtra("uri");

        reference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        if (session_key!= null && uri!=null){
            ref1 = reference.child("uploads").child(session_key);
            storageRef1 = storageReference.child("images").child(session_key);
            imageView.setImageURI(uri);
        }


        bSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(UploadActivity.this, "Sending", Toast.LENGTH_SHORT).show();
                android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);
                if (uri!= null && now!= null){

                    UploadTask uploadTask = storageRef1.child(now.toString()).putFile(uri);

                    Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()){
                                throw task.getException();
                            }
                            Toast.makeText(UploadActivity.this, "Image Sending", Toast.LENGTH_SHORT).show();
                            return storageRef1.child(now.toString()).getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()){
                                Uri uri1 = task.getResult();
                                ref1.push().setValue(uri1.toString());
                                ref1.keepSynced(true);
                                finish();
                                Toast.makeText(UploadActivity.this, "Sent Successfully", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(UploadActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
/*
                    storageRef1.child(now.toString()).putFile(uri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Uri url = taskSnapshot.getUploadSessionUri();
                                    ref1.push().setValue(url);
                                    imageView.setVisibility(View.GONE);
                                    bSend.setVisibility(View.GONE);
                                    Toast.makeText(CallActivity.this, "Image sent Successfully", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(CallActivity.this, "Failed to send Image", Toast.LENGTH_SHORT).show();
                                }
                            });

 */
                }
            }
        });
    }
}