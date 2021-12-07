package com.genius.appdesign2;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Date;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{

    private EditText etEmail, etPassword;
    private Button bSubmit, bNoti;
    private String emailId="example@email.com";
    private String pwd="123456";
    Date now = new Date();
    private FirebaseAuth firebaseAuth;
    private static final int PERMISSIONS_REQUEST_CODE = 138;
    private String email, pass;
    private Boolean flag_per;

    int PERMISSION_ALL = 1;

    private static final String[] PERMISSIONS = new String[]{"android.permission.RECORD_AUDIO", "android.permission.CHANGE_NETWORK_STATE", "android.permission.INTERNET",

            "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.ACCESS_NETWORK_STATE", "android.permission.READ_PHONE_STATE",

            "android.permission.RECORD_AUDIO", "android.permission.CAMERA", "android.permission.MODIFY_AUDIO_SETTINGS", "android.permission.ACCESS_NETWORK_STATE",

            "android.permission.BLUETOOTH", "android.permission.ACCESS_WIFI_STATE", "android.permission.READ_EXTERNAL_STORAGE",

            "android.permission.FOREGROUND_SERVICE", "android.permission.ACCESS_NOTIFICATION_POLICY", "android.permission.ACCESS_COARSE_LOCATION",

            "android.permission.ACCESS_WIFI_STATE", "android.permission.CHANGE_WIFI_STATE", "android.permission.ACCESS_BACKGROUND_LOCATION",

            "android.permission.REQUEST_INSTALL_PACKAGES"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();


        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null){
            startActivity(new Intent(MainActivity.this, HomeActivity.class));
            finish();
        }

        etEmail=findViewById(R.id.activity_main_etEmail);
        etPassword=findViewById(R.id.activity_main_etPassword);
        bSubmit=findViewById(R.id.activity_main_bSubmit);
        bNoti = findViewById(R.id.activity_main_bNoti);
/*
        bNoti.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {

 */
                /*
                Notification notification = new Notification.Builder(MainActivity.this)
                        .setSmallIcon(R.drawable.ic_bell)
                        .setContentTitle("AppDesign2")
                        .setContentText("Incoming Call")
                        .setAutoCancel(true)
                        .build();

                NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

                notificationManager.notify(0, notification);


                 */

        /*
                String title = "AppDesign2";
                String message = "Incoming Call";
                android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

                NotificationManager mNotificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel(now.toString(),
                            "AppDesign2",
                            NotificationManager.IMPORTANCE_DEFAULT);
                    channel.setDescription("Channel Description");
                    mNotificationManager.createNotificationChannel(channel);
                }
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), now.toString())
                        .setSmallIcon(R.mipmap.ic_launcher) // notification icon
                        .setContentTitle(title) // title for notification
                        .setContentText(message)// message for notification
                        .setAutoCancel(true); // clear notification after click
                Intent intent = new Intent(getApplicationContext(), CallActivity.class);
                PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                mBuilder.setContentIntent(pi);
                mNotificationManager.notify(0, mBuilder.build());
            }
        });

*/
        bSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email=etEmail.getText().toString().trim();
                pass=etPassword.getText().toString().trim();
                if (TextUtils.isEmpty(email)){
                    Toast.makeText(MainActivity.this, "Please enter Email ID ", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(pass)){
                    Toast.makeText(MainActivity.this, "Please enter Password", Toast.LENGTH_SHORT).show();
                }else{
                    requestPermissions();
                }
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Log.d("QrCodeActivity", "onPermissionsGranted:" + requestCode + ":" + perms.size());
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Toast.makeText(this, "This app needs all the permissions to proceed", Toast.LENGTH_SHORT).show();
    }

    @AfterPermissionGranted(PERMISSIONS_REQUEST_CODE)
    private void requestPermissions() {
        String[] perms = { Manifest.permission.INTERNET, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO };

        if (EasyPermissions.hasPermissions(this, perms)) {
            Toast.makeText(MainActivity.this, "Signing In", Toast.LENGTH_SHORT).show();
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, pass)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Toast.makeText(MainActivity.this, "Successfully signed in", Toast.LENGTH_SHORT).show();
                            finish();
                            startActivity(new Intent(MainActivity.this, HomeActivity.class));
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            EasyPermissions.requestPermissions(this, "This app needs access to the following permissions", PERMISSIONS_REQUEST_CODE, perms);
        }
    }

}