package com.genius.appdesign2;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.io.File;

public class ImageActivity extends AppCompatActivity {

    private ImageView imageView;
    private TouchImageView myImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        imageView = findViewById(R.id.activity_image_imageview);
        myImageView = findViewById(R.id.activity_image_imageview1);
        getSupportActionBar().hide();

        String  url = getIntent().getStringExtra("url");
        boolean flag = getIntent().getBooleanExtra("flag", false);

        if (flag){
            File file= new File(url);
            //Picasso.get().load(Uri.fromFile(file)).into(imageView);
           // Glide.with(this).load(Uri.fromFile(file)).into(imageView);
            Glide.with(this).load(Uri.fromFile(file)).into(myImageView);
        }else {
           // Picasso.get().load(url).into(imageView);
            Picasso.get().load(url).into(myImageView);
        }
    }
}