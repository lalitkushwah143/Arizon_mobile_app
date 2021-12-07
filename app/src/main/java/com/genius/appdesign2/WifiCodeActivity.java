package com.genius.appdesign2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.genius.appdesign2.data.DataTemp;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.zxing.WriterException;

import java.util.ArrayList;
import java.util.List;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class WifiCodeActivity extends AppCompatActivity {

    private Bitmap bitmap;
    private QRGEncoder qrgEncoder;
    private Spinner spinner;
    private EditText etPass, etSSID;
    private Button button;
    private ImageView imageView;
    private WifiManager wifiManager;

    private static final int PERMISSIONS_REQUEST_CODE = 128;
    private ArrayList<String> strings = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_code);

        getSupportActionBar().hide();
        TextView textView = findViewById(R.id.activity_wificode_tvTitle);
        textView.setText("WIFI Code");
        ImageView imageView1 = findViewById(R.id.activity_wificode_imageview1);
        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        etSSID = findViewById(R.id.activity_wificode_tvSSID);
        etPass = findViewById(R.id.activity_wificode_etPass);
        button = findViewById(R.id.activity_wificode_bShow);
        imageView = findViewById(R.id.acitivity_wificode_imageview);
        spinner = findViewById(R.id.activity_wificode_spinner);

        if (strings.isEmpty()){
            strings.add(0, "WPA/WPA2");
            strings.add(1, "WEP");
            strings.add(2, "None");
            spinner.setAdapter(new ArrayAdapter<String>(WifiCodeActivity.this,R.layout.support_simple_spinner_dropdown_item,strings));
        }

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String qrcodeString = "";

                    if (!TextUtils.isEmpty(etSSID.getText())){

                    switch (spinner.getSelectedItemPosition()) {
                        case 0:

                            if (!TextUtils.isEmpty(etPass.getText())) {
                                qrcodeString = "WIFI:S:" + etSSID.getText().toString() + ";T:WPA;P:" + etPass.getText().toString() + ";;";
                                Generate(qrcodeString);

                            } else {
                                Toast.makeText(WifiCodeActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case 1:
                            if (!TextUtils.isEmpty(etPass.getText())) {
                                qrcodeString = "WIFI:S:" + etSSID.getText().toString() + ";T:WEP;P:" + etPass.getText().toString() + ";;";
                                Generate(qrcodeString);

                            } else {
                                Toast.makeText(WifiCodeActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case 2:
                            qrcodeString = "WIFI:S:" + etSSID.getText().toString() + ";T:nopass;P:" + etPass.getText().toString() + ";;";
                            Generate(qrcodeString);
                            break;
                    }
                    }else {
                        Toast.makeText(WifiCodeActivity.this, "Enter the SSID", Toast.LENGTH_SHORT).show();
                    }



                }

            });
    }

    private void Generate(String qrcodeString){
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
            Toast.makeText(this, "Qr Code Generated", Toast.LENGTH_SHORT).show();
        } catch (WriterException e) {
            Log.v("QR Code Activity", e.toString());
        }
    }

}