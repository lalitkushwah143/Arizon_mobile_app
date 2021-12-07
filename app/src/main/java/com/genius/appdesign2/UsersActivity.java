package com.genius.appdesign2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.genius.appdesign2.data.DataUsers;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UsersActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private UsersAdapter adapter;
    private ArrayList<DataUsers> arrayList = new ArrayList<>();
    private TextView tvBlank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        getSupportActionBar().hide();
        TextView textView = findViewById(R.id.activity_users_tvTitle);
        textView.setText("Users");
        ImageView imageView = findViewById(R.id.activity_users_imageview);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recyclerView = findViewById(R.id.activity_users_rcView);
        tvBlank = findViewById(R.id.activity_users_tvBlank);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        adapter = new UsersAdapter(this, arrayList);
        recyclerView.setAdapter(adapter);
    /*    recyclerView.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {

                    @Override
                    public boolean onPreDraw() {
                        recyclerView.getViewTreeObserver().removeOnPreDrawListener(this);

                        for (int i = 0; i < recyclerView.getChildCount(); i++) {
                            View v = recyclerView.getChildAt(i);
                            v.setAlpha(0.0f);
                            v.animate().alpha(1.0f)
                                    .setDuration(300)
                                    .setStartDelay(i * 50)
                                    .start();
                        }

                        return true;
                    }
                });


     */

        db = FirebaseFirestore.getInstance();

        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                if (document.getData().get("email") != null &&
                                        document.getData().get("firstName") != null &&
                                        document.getData().get("lastName") != null &&
                                        document.getData().get("password") != null &&
                                        document.getData().get("phone") != null &&
                                        document.getData().get("role") != null &&
                                        document.getData().get("url") != null) {

                                    arrayList.add(new DataUsers(document.getId(),
                                            document.getData().get("email").toString(),
                                            document.getData().get("firstName").toString(),
                                            document.getData().get("lastName").toString(),
                                            document.getData().get("password").toString(),
                                            document.getData().get("phone").toString(),
                                            document.getData().get("role").toString(),
                                            document.getData().get("url").toString()));
                                    adapter.notifyDataSetChanged();
                                    recyclerView.scheduleLayoutAnimation();
                                //    Log.e("Useractivity", document.getId() + " => " + document.getData());
                                }else{
                                    Log.e("Users", "Missing Parameter");
                                }
                            }
                            if (arrayList.size() == 0){
                                recyclerView.setVisibility(View.GONE);
                                tvBlank.setVisibility(View.VISIBLE);
                            }else {
                                recyclerView.setVisibility(View.VISIBLE);
                                tvBlank.setVisibility(View.GONE);
                            }
                        }else {
                            Log.e("Useractivity", "Erroe");
                        }
                    }
                });
    }

    public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder>{

        private Context context;
        private ArrayList<DataUsers> list=new ArrayList<>();

        public UsersAdapter(Context context, ArrayList<DataUsers> list) {
            this.context=context;
            this.list=list;
        }

        @NonNull
        @Override
        public UsersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_users, parent, false);
            return new ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull UsersAdapter.ViewHolder holder, final int position) {

            if(list.get(position).getUrl() != null && !TextUtils.isEmpty(list.get(position).getUrl())) {
                Picasso.get().load(list.get(position).getUrl().toString()).into(holder.imageView);
            }
            if(list.get(position).getRole().equals("Admin")){
                holder.ivAdmin.setVisibility(View.VISIBLE);
            }else {
                holder.ivAdmin.setVisibility(View.GONE);
            }
            holder.tvFname.setText(list.get(position).getfName().toString());
            holder.tvLname.setText(list.get(position).getlName());
            holder.tvEmail.setText(list.get(position).getEmail());
            holder.tvPass.setText(list.get(position).getPass());
            holder.tvRole.setText(list.get(position).getRole());
            holder.tvPhone.setText(list.get(position).getPhone());

        }
        @Override
        public int getItemCount() {
            return list.size();
        }
        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvFname, tvLname, tvEmail, tvPass, tvPhone, tvRole;
            ImageView imageView, ivAdmin;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvFname = itemView.findViewById(R.id.list_users_tvFname);
                tvLname = itemView.findViewById(R.id.list_users_Lname);
                tvEmail = itemView.findViewById(R.id.list_users_tvEmail);
                tvPass = itemView.findViewById(R.id.list_users_tvPass);
                tvPhone = itemView.findViewById(R.id.list_users_tvPhone);
                tvRole = itemView.findViewById(R.id.list_users_tvRole);
                imageView = itemView.findViewById(R.id.list_users_imageview);
                ivAdmin = itemView.findViewById(R.id.list_users_ivAdmin);

            }
        }
    }
}