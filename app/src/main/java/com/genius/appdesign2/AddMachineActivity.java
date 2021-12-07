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

import com.genius.appdesign2.data.DataMachines;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddMachineActivity extends AppCompatActivity {

    private Button button;
    private EditText etTitle, etDesc, etLocation;
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_machine);

        etDesc = findViewById(R.id.activity_add_mmachine_etDesc);
        etTitle = findViewById(R.id.activity_add_mmachine_etTitle);
        etLocation = findViewById(R.id.activity_add_mmachine_etLocation);
        button = findViewById(R.id.activity_add_mmachine_bSubmit);

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title, desc, location;
                title = etTitle.getText().toString();
                desc = etDesc.getText().toString();
                location = etLocation.getText().toString();

                if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(desc) && !TextUtils.isEmpty(location)){

                    firestore.collection("machineData")
                            .add(new DataMachines(title, desc, location, firebaseAuth.getCurrentUser().getEmail()))
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    documentReference.update("timestamp", Timestamp.now());
                                    Toast.makeText(AddMachineActivity.this, "Added Succesfully", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("Add Machines:", "" + e);
                            Toast.makeText(AddMachineActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}