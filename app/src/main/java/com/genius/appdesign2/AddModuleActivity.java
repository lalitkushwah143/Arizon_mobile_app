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

import com.genius.appdesign2.data.DataModule;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddModuleActivity extends AppCompatActivity {

    private EditText etTitle, etValue;
    private Button button;
    private FirebaseFirestore firestore;
    private String mid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_module);

        etTitle = findViewById(R.id.activity_add_module_etTitle);
        etValue = findViewById(R.id.activity_add_module_etValue);
        button = findViewById(R.id.activity_add_module_bSubmit);

        mid = getIntent().getStringExtra("mid");

        firestore = FirebaseFirestore.getInstance();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title, value;
                title = etTitle.getText().toString();
                value = etValue.getText().toString();

                if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(value)){

                    firestore.collection("moduleData")
                            .add(new DataModule(title, value, mid))
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(AddModuleActivity.this, "Added Succesfully", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("Add Machines:", "" + e);
                            Toast.makeText(AddModuleActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }
}