package com.genius.appdesign2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.genius.appdesign2.data.DataManuals;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddManualActivity extends AppCompatActivity {

    private EditText etTitle, etDesc;
    private Button button;
    private FirebaseFirestore firestore;
    private String mid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_manual);

        etDesc = findViewById(R.id.activity_add_manual_etDesc);
        etTitle = findViewById(R.id.activity_add_manual_etTitle);
        button = findViewById(R.id.activity_add_manual_bSubmit);

        mid = getIntent().getStringExtra("mid");

        firestore = FirebaseFirestore.getInstance();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title, desc;
                title = etTitle.getText().toString();
                desc = etDesc.getText().toString();

                if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(desc) && mid!= null){
                    firestore.collection("manualData")
                            .add(new DataManuals(title, desc, mid))
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(AddManualActivity.this, "Added Succesfully", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("Add Machines:", "" + e);
                            Toast.makeText(AddManualActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}