package com.genius.appdesign2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.genius.appdesign2.data.DataButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity {


    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference, ref_session, ref_uid;
    private ArrayList<DataButton> arrayList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ButtonAdapter adapter;
    private long count;
    String current_session="";
    Boolean FLAG= false;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private String uid;
    private ImageView imageView;
    private ImageView sampleImage;

    private String email;
    private String catogery;
    private TextView tvMachine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Objects.requireNonNull(getSupportActionBar()).hide();

        imageView = findViewById(R.id.activity_home_ivProfile);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(HomeActivity.this, AccountActivity.class));
              //  startActivity(new Intent(HomeActivity.this, Login_cdu.class));
            }
        });

        tvMachine = findViewById(R.id.activity_home_tvMachine);

        recyclerView = findViewById(R.id.activity_home_rcView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setHasFixedSize(true);

        firebaseAuth = FirebaseAuth.getInstance();
        sampleImage = findViewById(R.id.sampleImage);


        tvMachine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, MachinesActivity.class));
            }
        });

        long thumb = 2000;
        RequestOptions options = new RequestOptions().frame(thumb);
        Glide.with(this).load("https://firebasestorage.googleapis.com/v0/b/lyodata.appspot.com/o/users%2Fadmin%40gmail.com%2Fvideoplayback.mp4?alt=media&token=febdcb01-57c2-4432-bbe3-ad7e516289c6").apply(options).into(sampleImage);


        if (firebaseAuth.getCurrentUser() != null){
            uid = firebaseAuth.getCurrentUser().getUid().toString();

            email = firebaseAuth.getCurrentUser().getEmail().toString();
        /*    ref_uid = FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("role");
            ref_uid.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Log.e("Firebase: ", snapshot.getValue().toString());
                    catogery = snapshot.getValue().toString();
                    prepareData(catogery);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

         */
            firestore = FirebaseFirestore.getInstance();




            firestore.collection("users")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){
                                for (QueryDocumentSnapshot document : task.getResult()) {

                                    if (document.getData().get("email") != null && document.getData().get("role") != null) {

                                        if (email.equals(document.getData().get("email").toString())) {
                                            prepareData(document.getData().get("role").toString());
                                            Log.e("HomeActivity", document.getData().get("role").toString());
                                            break;
                                        }
                                    }else {
                                        Log.e("Home: ", "Missing some parametere");
                                    }
                                }
                            }else {
                                Log.e("HomeActivity", "Error");
                            }
                        }
                    });
        }else {
            finish();
            startActivity(new Intent(HomeActivity.this, MainActivity.class));
        }


        adapter = new ButtonAdapter(this, arrayList);
        recyclerView.setAdapter(adapter);


        firebaseDatabase=FirebaseDatabase.getInstance();

        reference= firebaseDatabase.getReference();

        adapter.notifyDataSetChanged();

    }

    public class ButtonAdapter extends RecyclerView.Adapter<ButtonAdapter.ViewHolder>{

        private Context context;
        private ArrayList<DataButton> list=new ArrayList<>();

        public ButtonAdapter(Context context, ArrayList<DataButton> list) {
            this.context=context;
            this.list=list;
        }

        @NonNull
        @Override
        public ButtonAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_button, parent, false);
            return new ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull ButtonAdapter.ViewHolder holder, final int position) {
            holder.tvTitle.setText(list.get(position).getTitle());
            holder.imageView.setImageDrawable(getDrawable(list.get(position).getImage()));

            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (list.get(position).getTitle()){
                        case "Manual":
                            if (!SplashActivity.machine_id.equals("")) {
                                if(SplashActivity.machine_id.equals("87VaZ9R1BfWpNdbQyY4B")){
                                    startActivity(new Intent(HomeActivity.this, ManualActivity.class));

                                }else {
                                    startActivity(new Intent(HomeActivity.this, ManualsActivity.class));

                                }
                            }else {
                                Log.e("HomeActivity", "Please select machine first");
                                Toast.makeText(context, "Please select machine first", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case "Recipes":
                            if (!SplashActivity.machine_id.equals("")) {
                                if(SplashActivity.machine_id.equals("87VaZ9R1BfWpNdbQyY4B")){
                                    Toast.makeText(context, "No Contentful data available", Toast.LENGTH_SHORT).show();
                                }else {
                                    startActivity(new Intent(HomeActivity.this, LoadRecipeActivity.class));

                                }
                            }else {
                                Log.e("HomeActivity", "Please select machine first");
                                Toast.makeText(context, "Please select machine first", Toast.LENGTH_SHORT).show();

                            }
                            break;
                        case "Generate QR Code":
                            startActivity(new Intent(HomeActivity.this, QrcodeActivity.class));
                            break;

                        case "WIFI Code":
                            startActivity(new Intent(HomeActivity.this, WifiCodeActivity.class));
                            break;

                        case "Video Call":
                       //     if (FLAG){
                                startActivity(new Intent(HomeActivity.this, MultiVideoCallActivity.class));
                              //  startActivity(new Intent(HomeActivity.this, CallActivity.class));
                       /*     }else {
                                Toast.makeText(HomeActivity.this, "Session not available", Toast.LENGTH_SHORT).show();
                            }

                        */
                            break;
                        case "Call Log":

                            if (!SplashActivity.machine_id.equals("")) {
                                startActivity(new Intent(HomeActivity.this, CallLogActivity.class));

                            }else {
                                Log.e("HomeActivity", "Please select machine first");
                                Toast.makeText(context, "Please select machine first", Toast.LENGTH_SHORT).show();

                            }
                            break;
                        case "Jobs":
                            if (!SplashActivity.machine_id.equals("")) {
                                startActivity(new Intent(HomeActivity.this, JobActivity.class));

                            }else {
                                Log.e("HomeActivity", "Please select machine first");
                                Toast.makeText(context, "Please select machine first", Toast.LENGTH_SHORT).show();

                            }

                            break;
                        case "Batch Report":
                            if (!SplashActivity.machine_id.equals("")) {
                                startActivity(new Intent(HomeActivity.this, ReportActivity.class));

                            }else {
                                Log.e("HomeActivity", "Please select machine first");
                                Toast.makeText(context, "Please select machine first", Toast.LENGTH_SHORT).show();
                            }
                            break;

                        case "FAT/SAT Data":
                            if (!SplashActivity.machine_id.equals("")) {
                                startActivity(new Intent(HomeActivity.this, FatSatActivity.class));

                            }else {
                                Log.e("HomeActivity", "Please select machine first");
                                Toast.makeText(context, "Please select machine first", Toast.LENGTH_SHORT).show();
                            }
                            break;

                        case "Machines":
                            startActivity(new Intent(HomeActivity.this, MachinesActivity.class));
                            break;
                        case "Users":
                            startActivity(new Intent(HomeActivity.this, UsersActivity.class));
                            break;
            /*            case 8:
                            startActivity(new Intent(HomeActivity.this, MonitorActivity.class));
                            break;


             */
                        case "Quality Report":

                            if (!SplashActivity.machine_id.equals("")) {
                                startActivity(new Intent(HomeActivity.this, QualityReportActivity.class));

                            }else {
                                Log.e("HomeActivity", "Please select machine first");
                                Toast.makeText(context, "Please select machine first", Toast.LENGTH_SHORT).show();

                            }
                            break;

                        case "DQ New":
                            startActivity(new Intent(HomeActivity.this, DQNewActivity.class));
                            break;

/*
                        case 9:
                            startActivity(new Intent(HomeActivity.this, ManageActivity.class));
                            break;

 */
                    }
                }
            });
        }
        @Override
        public int getItemCount() {
            return list.size();
        }
        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle;
            ImageView imageView;
            ConstraintLayout layout;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.list_button_tv);
                imageView = itemView.findViewById(R.id.list_button_imageview);
                layout = itemView.findViewById(R.id.list_button_layout);
            }
        }
    }


    private void prepareData( String cat){
        arrayList.clear();
        Log.e("Firebase: ", "Preparing");
        switch (cat){

            case "Trainee":
                arrayList.add(0, new DataButton("Manual", R.drawable.ic_baseline_class_24));
                arrayList.add(1, new DataButton("Recipes", R.drawable.ic_baseline_tv_24));
                arrayList.add(2, new DataButton("Generate QR Code", R.drawable.ic_baseline_qr_code_24));
                arrayList.add(3, new DataButton("Video Call", R.drawable.ic_baseline_videocam_24));
                arrayList.add(4, new DataButton("Call Log", R.drawable.ic_baseline_call_24));
            //    arrayList.add(5, new DataButton("Machines", R.drawable.ic_baseline_class_24));
                arrayList.add(5, new DataButton("WIFI Code", R.drawable.ic_baseline_qr_code_24));
                adapter.notifyDataSetChanged();
                break;

            case "Operator":
                arrayList.add(0, new DataButton("Manual", R.drawable.ic_baseline_class_24));
                arrayList.add(1, new DataButton("Recipes", R.drawable.ic_baseline_tv_24));
                arrayList.add(2, new DataButton("Generate QR Code", R.drawable.ic_baseline_qr_code_24));
                arrayList.add(3, new DataButton("Jobs", R.drawable.ic_baseline_work_24));
                arrayList.add(4, new DataButton("Video Call", R.drawable.ic_baseline_videocam_24));
                arrayList.add(5, new DataButton("Call Log", R.drawable.ic_baseline_call_24));
            //    arrayList.add(6, new DataButton("Machines", R.drawable.ic_baseline_class_24));
                arrayList.add(6, new DataButton("WIFI Code", R.drawable.ic_baseline_qr_code_24));
                adapter.notifyDataSetChanged();
                break;

            case "Validator":
                arrayList.add(0, new DataButton("Manual", R.drawable.ic_baseline_class_24));
                arrayList.add(1, new DataButton("Generate QR Code", R.drawable.ic_baseline_qr_code_24));
                arrayList.add(2, new DataButton("Video Call", R.drawable.ic_baseline_videocam_24));
                arrayList.add(3, new DataButton("Call Log", R.drawable.ic_baseline_call_24));
                arrayList.add(4, new DataButton("Batch Report", R.drawable.ic_baseline_content_paste_24));
                arrayList.add(5, new DataButton("DQ New", R.drawable.ic_baseline_content_paste_24));
            //    arrayList.add(6, new DataButton("Machines", R.drawable.ic_baseline_class_24));
                arrayList.add(6, new DataButton("WIFI Code", R.drawable.ic_baseline_qr_code_24));
                adapter.notifyDataSetChanged();
                break;

            case "Supervisor":
                arrayList.add(0, new DataButton("Manual", R.drawable.ic_baseline_class_24));
                arrayList.add(1, new DataButton("Recipes", R.drawable.ic_baseline_tv_24));
                arrayList.add(2, new DataButton("Generate QR Code", R.drawable.ic_baseline_qr_code_24));
                arrayList.add(3, new DataButton("Jobs", R.drawable.ic_baseline_work_24));
                arrayList.add(4, new DataButton("Video Call", R.drawable.ic_baseline_videocam_24));
                arrayList.add(5, new DataButton("Call Log", R.drawable.ic_baseline_call_24));
                arrayList.add(6, new DataButton("Batch Report", R.drawable.ic_baseline_content_paste_24));
            //    arrayList.add(7, new DataButton("Machines", R.drawable.ic_baseline_class_24));
                arrayList.add(7, new DataButton("WIFI Code", R.drawable.ic_baseline_qr_code_24));
                adapter.notifyDataSetChanged();
                break;

            case "Maintenance":
                arrayList.add(0, new DataButton("Manual", R.drawable.ic_baseline_class_24));
                arrayList.add(1, new DataButton("Recipes", R.drawable.ic_baseline_tv_24));
                arrayList.add(2, new DataButton("Generate QR Code", R.drawable.ic_baseline_qr_code_24));
                arrayList.add(3, new DataButton("Video Call", R.drawable.ic_baseline_videocam_24));
                arrayList.add(4, new DataButton("Call Log", R.drawable.ic_baseline_call_24));
              //  arrayList.add(5, new DataButton("Machines", R.drawable.ic_baseline_class_24));
                arrayList.add(5, new DataButton("WIFI Code", R.drawable.ic_baseline_qr_code_24));
                adapter.notifyDataSetChanged();

                break;

            case "Admin":
                arrayList.add(0, new DataButton("Manual", R.drawable.ic_baseline_class_24));
                arrayList.add(1, new DataButton("Recipes", R.drawable.ic_baseline_tv_24));
                arrayList.add(2, new DataButton("Generate QR Code", R.drawable.ic_baseline_qr_code_24));
                arrayList.add(3, new DataButton("Jobs", R.drawable.ic_baseline_work_24));
                arrayList.add(4, new DataButton("Video Call", R.drawable.ic_baseline_videocam_24));
                arrayList.add(5, new DataButton("Call Log", R.drawable.ic_baseline_call_24));
                arrayList.add(6, new DataButton("Batch Report", R.drawable.ic_baseline_content_paste_24));
                arrayList.add(7, new DataButton("DQ New", R.drawable.ic_baseline_content_paste_24));
                arrayList.add(8, new DataButton("Users", R.drawable.ic_baseline_account_circle_24));
             //   arrayList.add(9, new DataButton("Machines", R.drawable.ic_baseline_class_24));
                arrayList.add(9, new DataButton("WIFI Code", R.drawable.ic_baseline_qr_code_24));
                arrayList.add(10, new DataButton("FAT/SAT Data", R.drawable.ic_baseline_content_paste_24));
                adapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            firebaseAuth.signOut();
            startActivity(new Intent(HomeActivity.this, MainActivity.class));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(HomeActivity.this, MainActivity.class));
        }

        if (SplashActivity.machine_id != null && !TextUtils.isEmpty(SplashActivity.machine_id)){
            DocumentReference documentReference = firestore.collection("machineData").document(SplashActivity.machine_id);
            documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot != null) {
                        tvMachine.setText(documentSnapshot.getData().get("title").toString());
                    }
                }
            });
        }else {
            tvMachine.setText("Select Machine");
        }
    }
}