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

import com.genius.appdesign2.data.DataStep;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddStepActivity extends AppCompatActivity {

    private EditText etTitle, etDesc, etUrl, etType;
    private String title, desc, url, type, manualID, manualTitle;
    private FirebaseFirestore firestore;
    private Button button;
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_step);

        etTitle = findViewById(R.id.activity_add_step_etTitle);
        etDesc = findViewById(R.id.activity_add_step_etDesc);
        etType = findViewById(R.id.activity_add_step_etType);
        etUrl = findViewById(R.id.activity_add_step_etUrl);
        button = findViewById(R.id.activity_add_step_bSubmit);

        etUrl.setText("https://firebasestorage.googleapis.com/v0/b/lyodata.appspot.com/o/media%2Fsteps%2Fdef1.png?alt=media&token=02d80611-fa8c-4968-aac6-9b93efcdb284");
        etUrl.setClickable(false);
        firestore = FirebaseFirestore.getInstance();

        manualID = getIntent().getStringExtra("manual_id");
        manualTitle = getIntent().getStringExtra("manual_title");
        index = getIntent().getIntExtra("index", 0);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = etTitle.getText().toString();
                desc = etDesc.getText().toString();
                type = etType.getText().toString();
                url = etUrl.getText().toString();

                if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(desc) && !TextUtils.isEmpty(type) && !TextUtils.isEmpty(url) && !TextUtils.isEmpty(manualID) && !TextUtils.isEmpty(manualTitle) ){

                    firestore.collection("stepData")
                            .add(new DataStep(index, title, desc, "image", url, type, manualID))
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(AddStepActivity.this, "Added Succesfully", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("Add Machines:", "" + e);
                            Toast.makeText(AddStepActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });


    }
}