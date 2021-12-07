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

import com.genius.appdesign2.data.DataComponent;
import com.genius.appdesign2.data.DataStep;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddComponentActivity extends AppCompatActivity {

    private EditText etTitle, etValue;
    private Button button;
    private String title, value, module_id, module_title;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_component);

        etTitle = findViewById(R.id.activity_add_component_etTitle);
        etValue = findViewById(R.id.activity_add_component_etDesc);
        button = findViewById(R.id.activity_add_component_bSubmit);

        firestore = FirebaseFirestore.getInstance();

        module_id = getIntent().getStringExtra("module_id");
        module_title = getIntent().getStringExtra("module_title");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = etTitle.getText().toString();
                value = etValue.getText().toString();


                if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(value) && !TextUtils.isEmpty(module_id) && !TextUtils.isEmpty(module_title) ){

                    firestore.collection("componentData")
                            .add(new DataComponent(title, value, module_id))
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(AddComponentActivity.this, "Added Succesfully", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("Add Machines:", "" + e);
                            Toast.makeText(AddComponentActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });


    }
}