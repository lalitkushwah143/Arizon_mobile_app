package com.genius.appdesign2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.genius.appdesign2.data.DataMachines;
import com.genius.appdesign2.data.DataTemp;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.WriterException;
import com.opentok.android.BaseVideoRenderer;
import com.opentok.android.Publisher;
import com.opentok.android.Session;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class QrcodeActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private Button bShow;
    private ImageView imageView;
    private Bitmap bitmap;
    private QRGEncoder qrgEncoder;
    private ArrayList<DataMachines> arrayList = new ArrayList<>();
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference reference;
    private String email, pass, uid;
    private FloatingActionButton fab;

    private Spinner spinnerMachine;
    private ArrayList<String> strings = new ArrayList<>();

    private static final int PERMISSIONS_REQUEST_CODE = 125;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);

        getSupportActionBar().hide();
        TextView textView = findViewById(R.id.activity_qrcode_tvTitle);
        textView.setText("QR Code Generator");
        ImageView imageView1 = findViewById(R.id.activity_qrcode_imageview1);
        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imageView = findViewById(R.id.acitivity_qrcode_imageview);
        bShow = findViewById(R.id.activity_qrcode_bShow);
        spinnerMachine = findViewById(R.id.activity_qrcode_spinnerMachine);
        fab = findViewById(R.id.activity_qrcode_fabshare);

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();

        email = firebaseAuth.getCurrentUser().getEmail();
        uid = firebaseAuth.getCurrentUser().getUid();

        bShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                reference.child("tempUserData").child(uid).setValue(new DataTemp(email, pass)).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e("CKHJFDSF", "Success");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("fdksjfk", "Failure "  + e);
                    }
                });

                int position = spinnerMachine.getSelectedItemPosition();
                Log.e("QR", position + " sss");
                String machine_id = arrayList.get(position).getKey();

                String qrcodeString = uid + "/" + machine_id;
                Log.e("QrCode", qrcodeString);

                WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
                Display display = manager.getDefaultDisplay();
                Point point = new Point();
                display.getSize(point);
                int width = point.x;
                int height = point.y;
                int smallerDimension = width < height ? width : height;
                smallerDimension = smallerDimension * 3 / 4;

                qrgEncoder = new QRGEncoder(qrcodeString, null, QRGContents.Type.TEXT, smallerDimension);
                try {
                    bitmap = qrgEncoder.encodeAsBitmap();
                    imageView.setImageBitmap(bitmap);
                    Toast.makeText(QrcodeActivity.this, "Created QR Code", Toast.LENGTH_SHORT).show();
                } catch (WriterException e) {
                    Log.v("QR Code Activity", e.toString());
                }
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                requestPermissions();

            }
        });
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "QrCode", null);
        return Uri.parse(path);
    }

    @Override
    protected void onResume() {
        super.onResume();

        firestore.collection("machineData")
                .addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        assert value != null;
                        arrayList.clear();
                        strings.clear();
                        for (QueryDocumentSnapshot snapshot : value) {
                         //   Log.e("QrActivity: ", snapshot.getData().toString());

                            if (snapshot.getData().get("title") != null && snapshot.getData().get("desc") != null &&
                                    snapshot.getData().get("location") != null && snapshot.getData().get("createdBy") != null) {

                                arrayList.add(new DataMachines(snapshot.getId(),
                                        snapshot.getData().get("title").toString(),
                                        snapshot.getData().get("desc").toString(),
                                        snapshot.getData().get("location").toString(),
                                        snapshot.getData().get("createdBy").toString()));
                                strings.add(snapshot.getData().get("title").toString());

                            }else {
                                Log.e("QR Code: ", "Missing Parameter");
                            }
                        }

                        for (int i = 0; i <arrayList.size(); i++){
                            Log.e("Qrcode", arrayList.get(i).getKey().toString());
                        }
                        spinnerMachine.setAdapter(new ArrayAdapter<String>(QrcodeActivity.this,R.layout.support_simple_spinner_dropdown_item,strings));
                    }
                });

        firestore.collection("users")
                .whereEqualTo("email", email)
                .addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        assert value != null;
                        for (QueryDocumentSnapshot snapshot : value){
                            if (snapshot.getData().get("email") != null && snapshot.getData().get("email").toString().equals(email)){
                                pass = snapshot.getData().get("password").toString();
                            }else {
                                Log.e("QR Code: ", "Missing Parameter");
                            }

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
        Toast.makeText(this, "Cannot share Qr Code without these permissions", Toast.LENGTH_SHORT).show();
    }

    @AfterPermissionGranted(PERMISSIONS_REQUEST_CODE)
    private void requestPermissions() {
        String[] perms = { Manifest.permission.INTERNET, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE };

        if (EasyPermissions.hasPermissions(this, perms)) {
            if (bitmap != null){
                if (checkApp()){
                    Uri uri = getImageUri(QrcodeActivity.this, bitmap);
                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                    sharingIntent.setType("image/*");
                    sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
                    startActivity(sharingIntent);
                    //   startActivity(Intent.createChooser(sharingIntent, "Share Image Using"));
                }else {
                    Log.e("QR Code", "Whatsapp not installed");
                    Toast.makeText(this, "Whatsapp not installed", Toast.LENGTH_SHORT).show();
                }
                
            }
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_video_app), PERMISSIONS_REQUEST_CODE, perms);
        }
    }
    
    private boolean checkApp(){
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }
}