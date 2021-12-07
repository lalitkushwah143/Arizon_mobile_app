package com.genius.appdesign2;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.opentok.android.Session;
import com.opentok.android.Subscriber;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    public static String machine_id;
    private static final long version = 7;
    private long ver ;
    private DatabaseReference reference;
    private String url, version_id;


    private ValueEventListener valueEventListener;

    ProgressDialog mProgressDialog;
    int PERMISSION_ALL = 1;

    private static final int PERMISSIONS_REQUEST_CODE = 128;

    private static final String[] PERMISSIONS = new String[]{"android.permission.RECORD_AUDIO", "android.permission.CHANGE_NETWORK_STATE", "android.permission.INTERNET",

            "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.ACCESS_NETWORK_STATE", "android.permission.READ_PHONE_STATE",

            "android.permission.RECORD_AUDIO", "android.permission.CAMERA", "android.permission.MODIFY_AUDIO_SETTINGS", "android.permission.ACCESS_NETWORK_STATE",

            "android.permission.BLUETOOTH", "android.permission.ACCESS_WIFI_STATE", "android.permission.READ_EXTERNAL_STORAGE",

            "android.permission.FOREGROUND_SERVICE", "android.permission.ACCESS_NOTIFICATION_POLICY", "android.permission.ACCESS_COARSE_LOCATION",

            "android.permission.ACCESS_WIFI_STATE", "android.permission.CHANGE_WIFI_STATE", "android.permission.ACCESS_BACKGROUND_LOCATION",

            "android.permission.REQUEST_INSTALL_PACKAGES"};
    private static final int REQUEST_PERMISSION_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getSupportActionBar().hide();

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser()!= null) {

            Intent startIntent = new Intent(getApplicationContext(), MyService.class);
            startIntent.setAction(getPackageName());
            startService(startIntent);

            Intent IssueIntent = new Intent(getApplicationContext(), IssueService.class);
            IssueIntent.setAction(getPackageName());
            startService(IssueIntent);
        }

        reference = FirebaseDatabase.getInstance().getReference().child("app_versions").child("android");

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("version").getValue() != null) {
                    ver =  (long)(snapshot.child("version").getValue());
                    url = snapshot.child("url").getValue().toString();
                    version_id = snapshot.child("version_id").getValue().toString();
                    if (ver > version){
                        OpenDialog();
                    }else {
                        goAhead();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        reference.addListenerForSingleValueEvent(valueEventListener);

    }



    private void goAhead(){

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                reference.removeEventListener(valueEventListener);
                firebaseAuth = FirebaseAuth.getInstance();
                if (firebaseAuth.getCurrentUser() != null){
                    SharedPreferences sharedPreferences = getSharedPreferences("test_prefs", Context.MODE_PRIVATE);
                    if (sharedPreferences == null){
                        SharedPreferences.Editor editor  = sharedPreferences.edit();
                        editor.putString("machine_id", "");
                        editor.commit();
                        editor.apply();
                        machine_id = "";
                    }else {
                        machine_id = sharedPreferences.getString("machine_id", "");
                    }
                    startActivity(new Intent(SplashActivity.this, HomeActivity.class));

                    finish();
                }else{

                    SharedPreferences sharedPreferences = getSharedPreferences("test_prefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor  = sharedPreferences.edit();
                    editor.putString("machine_id", "");
                    editor.commit();
                    editor.apply();
                    machine_id = "";
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                }

                finish();
            }
        }, 3000);

    }

    @WorkerThread
    private void OpenDialog(){
        reference.removeEventListener(valueEventListener);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater=SplashActivity.this.getLayoutInflater();
        final View view=inflater.inflate(R.layout.update_dialog, null);
        alertDialogBuilder.setView(view);
        alertDialogBuilder.setCancelable(false);
        AlertDialog alertDialog = alertDialogBuilder.create();


        TextView textView = view.findViewById(R.id.update_dialog_tvTitle);
        textView.setText("Version : " + version_id);
        TextView tvSize = view.findViewById(R.id.update_dialog_tvSize);

        Button bDownload = view.findViewById(R.id.update_dialog_bDownload);
        Button bCancel = view.findViewById(R.id.update_dialog_bCancel);

        bDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!hasPermissions(SplashActivity.this, PERMISSIONS)) {
                    ActivityCompat.requestPermissions(SplashActivity.this, PERMISSIONS, PERMISSION_ALL);
                }else {
                    if (url != null) {
                        Toast.makeText(SplashActivity.this, "Downloading", Toast.LENGTH_SHORT).show();
                        mProgressDialog = new ProgressDialog(SplashActivity.this);
                        mProgressDialog.setMessage("Downloading Update...");
                        mProgressDialog.setIndeterminate(false);
                        //   mProgressDialog.setMax(100);
                        //   mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                        mProgressDialog.show();
                        alertDialog.dismiss();
                        update();

                    }
                }
            }
        });

        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                goAhead();
            }
        });

        alertDialog.setTitle("Update Available");
        alertDialog.show();


     //   alertDialogBuilder.setTitle("Update Available");
      //  alertDialogBuilder.show();


    }

    private void update(){
        String destination = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getPath() + "/";

        Log.e("Desti", destination);
        String fileName = "AppDesign2.apk";
        destination += fileName;
        File file = new File(destination);
        final Uri uri = Uri.parse("file://" + destination);

        //Delete update file if exists
        if (file.exists()){
            file.delete();
        }
            //file.delete() - test this, I think sometimes it doesnt work

        //get url of app on server

        //set downloadmanager
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription("Downloading");
        request.setTitle("AppDesign2");

        //set destination
        request.setDestinationUri(uri);

        // get download service and enqueue file
        final DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        final long downloadId = manager.enqueue(request);

       //set BroadcastReceiver to install app when .apk is downloaded
        String finalDestination = destination;
        BroadcastReceiver onComplete = new BroadcastReceiver() {
            public void onReceive(Context ctxt, Intent intent) {
                Log.e("Completed ", "Success");
                mProgressDialog.dismiss();
                unregisterReceiver(this);
                finish();
                final Uri photoUri = FileProvider.getUriForFile(SplashActivity.this,
                        "com.genius.appdesign2.fileprovider", file);
                //  File this_file = new File(destination);
                Log.e("Desti", finalDestination);
                Log.e("URI", photoUri.toString());
                Intent intent1 = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                intent1.setData(photoUri);
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent1.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                //  intent1.setAction(Intent.ACTION_INSTALL_PACKAGE);
                startActivity(intent1);
                Log.e("download", "starting intent");
                finish();
                /*
                Intent intent1 = new Intent(Intent.ACTION_VIEW);
                intent1.setDataAndType(uri, "application/vnd.android.package-archive");
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent1.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                try {
                    startActivity(intent1);
                }catch (ActivityNotFoundException e){
                    e.printStackTrace();
                    Log.e("DOWnload", "Error in opening file");
                }


                 */

               // Intent install = new Intent(Intent.ACTION_VIEW);
              //  install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
               // install.setDataAndType(uri, "application/vnd.android.package-archive");
             //   install.setDataAndType(uri,
               //         manager.getMimeTypeForDownloadedFile(downloadId));
             //   startActivity(install);
            }
        };

      //  mProgressDialog.setProgress((int)(onComplete.getResultData().getBytes().length / url.getBytes().length)/100);

        //register receiver for when .apk download is compete
        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }



}