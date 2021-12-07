package com.genius.appdesign2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.genius.appdesign2.data.DataUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ManageActivity extends AppCompatActivity {

    private EditText etEmail, etPass;
    private Spinner spinner;
    private Button button;
    private String email, pass;
    private ArrayList<String> arrayList= new ArrayList<>();
    private ArrayList<DataUser> dataUsers = new ArrayList<>();
    private DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);

        arrayList.add(0, "Admin");
        arrayList.add(1, "Trainee");
        arrayList.add(2, "Operator");
        arrayList.add(3, "Supervisor");
        arrayList.add(4, "Validator");
        arrayList.add(5, "Maintenance");

        spinner = findViewById(R.id.activity_manage_role);
        button = findViewById(R.id.activity_manage_bSubmit);
        etEmail = findViewById(R.id.activity_manage_tvEmail);
        etPass = findViewById(R.id.activity_manage_tvPass);

        spinner.setAdapter(new ArrayAdapter<String>(ManageActivity.this,R.layout.support_simple_spinner_dropdown_item,arrayList));
        reference = FirebaseDatabase.getInstance().getReference().child("users");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = etEmail.getText().toString().trim();
                pass = etPass.getText().toString().trim();
                String role = spinner.getSelectedItem().toString();
                FirebaseAuth.getInstance().createUserWithEmailAndPassword( email, pass)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                String key = task.getResult().getUser().getUid();
                                reference.child(key).setValue(new DataUser(key, email, role));
                                Toast.makeText(ManageActivity.this, "User Added", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

    }
}