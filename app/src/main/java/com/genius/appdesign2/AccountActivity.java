package com.genius.appdesign2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

public class AccountActivity extends AppCompatActivity {

    private TextView tvUsename, tvEmail, tvWebsite, tvMob, tvFname, tvabout, tvEdit;
   // private ImageView tvUrl;
    private Button button;
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    String emailid,imgurl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        getSupportActionBar().hide();

        Objects.requireNonNull(getSupportActionBar()).hide();

        tvEmail = findViewById(R.id.activity_account_tvEmail);
        tvUsename = findViewById(R.id.activity_account_tvUsername);
        tvFname = findViewById(R.id.activity_account_tvFname);
        tvMob = findViewById(R.id.activity_account_tvMobile);
        tvWebsite = findViewById(R.id.activity_account_tvWebsite);
        tvabout = findViewById(R.id.activity_account_tvAbout);
        tvEdit = findViewById(R.id.activity_account_edit);
        //tvUrl = (ImageView) findViewById(R.id.activity_account_profile_photo);

        ImageView imageView = findViewById(R.id.activity_account_imageview);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        button = findViewById(R.id.activity_account_bLogOut);
        final ImageView iv = (ImageView) findViewById(R.id.activity_account_profile_photo);

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AccountActivity.this, FullprofileimageActivity.class);
                intent.putExtra("img_url",imgurl);
                startActivity(intent);
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                finish();
            }
        });
        tvWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://lyodata.web.app/"));
                startActivity(intent);
            }
        });
        tvabout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aboutDialog();
            }
        });

        if (firebaseAuth.getCurrentUser() != null) {

            firestore.collection("users")
                    .whereEqualTo("email", firebaseAuth.getCurrentUser().getEmail())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    if (document.getData().get("email") != null && document.getData().get("username") != null) {
                                        //tvEmail.setText("Email ID : " + document.getData().get("email").toString());
                                        // tvUsename.setText("Username : " + document.getData().get("username").toString());
                                        emailid = document.getData().get("email").toString();
                                        tvEmail.setText(document.getData().get("email").toString());
                                        tvUsename.setText(document.getData().get("username").toString());
                                        String Fullname = document.getData().get("firstName").toString() + " " +document.getData().get("lastName").toString();
                                        tvFname.setText(Fullname);
                                        tvMob.setText(document.getData().get("phone").toString());
                                        String url2 = document.getData().get("url").toString();
                                        imgurl=document.getData().get("url").toString();
                                        //new DownLoadImageTask(iv).execute(url2);
                                        Picasso.get().load(url2).into(iv);
                                       // Glide.with(AccountActivity.this).load(url2).into(iv);
                                    } else {
                                        Log.e("Account", "Missing Parameters");
                                    }
                                }
                            } else {
                                Log.e("HomeActivity", "Error");
                            }
                        }
                    });
        }

        tvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountActivity.this, Activity_profile.class);
                intent.putExtra("email_id",emailid);
                startActivity(intent);
            }
        });
    }
    /*
        public class DownLoadImageTask extends AsyncTask<String, Void, Bitmap> {
            ImageView imageView;

            public DownLoadImageTask(ImageView imageView) {
                this.imageView = imageView;
            }

            protected Bitmap doInBackground(String... urls) {
                String urlOfImage = urls[0];
                Bitmap logo = null;
                try {
                    InputStream is = new URL(urlOfImage).openStream();
                    logo = BitmapFactory.decodeStream(is);
                } catch (Exception e) { // Catch the download exception
                    e.printStackTrace();
                }
                return logo;
            }
            protected void onPostExecute(Bitmap result) {
                imageView.setImageBitmap(result);
            }
        }
*/
    public void aboutDialog() {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
        View mView = layoutInflaterAndroid.inflate(R.layout.custom_dialog_about, null);

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(mView);
        alert.setCancelable(false);
        alert.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.show();
    }

}