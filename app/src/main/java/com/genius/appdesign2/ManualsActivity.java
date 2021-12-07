package com.genius.appdesign2;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.genius.appdesign2.data.DataManual;
import com.genius.appdesign2.data.DataManuals;
import com.genius.appdesign2.data.DataStep;
import com.genius.appdesign2.data.DataSteps;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

public class ManualsActivity extends AppCompatActivity {

    private Button bForward, bBack;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private TabLayout tabLayout;
    private TextView tvPage;
    private FloatingActionButton floatingActionButton;
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;

    private ArrayList<DataManuals> manualsArrayList = new ArrayList<>();
    private ArrayList<DataStep> arrayList = new ArrayList<>();
    private ArrayList<String> manualNames = new ArrayList<>();

    private Spinner spinnerMachine, spinnermanual;
    private Button bSubmit;
    private ImageView bSwitch;
    private ConstraintLayout layout;
    private TextView tvBlank;
    MediaPlayer mPlayer = new MediaPlayer();
    private File storageDir;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manuals);

        getSupportActionBar().hide();
        TextView textView = findViewById(R.id.activity_manuals_tvTitle);
        textView.setText("Manual");
        ImageView imageView = findViewById(R.id.activity_manuals_imageview);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        bBack=findViewById(R.id.activity_manuals_bBack);
        bForward=findViewById(R.id.activity_manuals_bForward);
        tabLayout = findViewById(R.id.activity_manuals_indicator);
        tvPage = findViewById(R.id.activity_manuals_tvPage);
        tvBlank = findViewById(R.id.activity_manuals_tvBlank);

        bSwitch = findViewById(R.id.activity_manuals_switch);
        spinnermanual = findViewById(R.id.activity_manuals_spinnerManuals);
        bSubmit = findViewById(R.id.activity_manuals_bSubmit);
        layout = findViewById(R.id.activity_manuals_layout);
        floatingActionButton = findViewById(R.id.activity_manuals_fabShare);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendImage();
            }
        });

        bSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layout.getVisibility() == View.VISIBLE){
                    layout.setVisibility(View.GONE);
                }else {
                    layout.setVisibility(View.VISIBLE);
                }
            }
        });
        bSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSteps();
            }
        });


        viewPager=findViewById(R.id.activity_manuals_viewpager);
        arrayList=new ArrayList<>();

        bBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPager.getCurrentItem()!=0){
                    int current=viewPager.getCurrentItem();
                    viewPager.setCurrentItem(current-1);
                }
            }
        });
        bForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPager.getCurrentItem()!=arrayList.size()-1){
                    viewPager.setCurrentItem(viewPager.getCurrentItem()+1);
                }
            }
        });

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tvPage.setText((tabLayout.getSelectedTabPosition()+1) + "/" + tabLayout.getTabCount());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        Toast.makeText(this, "Loading Content", Toast.LENGTH_SHORT).show();

        if (firebaseAuth.getCurrentUser() != null){
            firestore.collection("manualData")
                    .whereEqualTo("mid", SplashActivity.machine_id)
                    .addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            assert value != null;
                            manualsArrayList.clear();
                            manualNames.clear();
                            for (QueryDocumentSnapshot snapshot : value) {

                                if (snapshot.getData().get("title") != null && snapshot.getData().get("desc") != null &&
                                        snapshot.getData().get("mid") != null) {

                                    manualsArrayList.add(new DataManuals(snapshot.getId(),
                                            snapshot.getData().get("title").toString(),
                                            snapshot.getData().get("desc").toString(),
                                            snapshot.getData().get("mid").toString()));
                                    manualNames.add(snapshot.getData().get("title").toString());
                                    Log.e("Sample", snapshot.getData().get("title").toString());
                                }else {
                                    Log.e("Manuals: ", "Missing some parameters");
                                }
                            }
                            spinnermanual.setAdapter(new ArrayAdapter<String>(ManualsActivity.this, R.layout.support_simple_spinner_dropdown_item, manualNames));
                            spinnermanual.setSelection(0);
                            getSteps();
                        }
                    });
        }
    }

    private void getSteps() {
        Log.e("Sample", manualsArrayList.size() + "/");
        if (manualsArrayList.size() != 0){
            String manual_id = manualsArrayList.get(spinnermanual.getSelectedItemPosition()).getKey();

            firestore.collection("stepData")
                    .whereEqualTo("manual_id", manual_id)
                    .addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            assert value != null;
                            arrayList.clear();
                            for (QueryDocumentSnapshot snapshot : value) {
                              //  Log.e("MachineActivity: ", snapshot.getData().toString());

                                if (snapshot.getData().get("title") != null && snapshot.getData().get("desc") != null &&
                                        snapshot.getData().get("format") != null && snapshot.getData().get("url") != null &&
                                        snapshot.getData().get("type") != null && snapshot.getData().get("manual_id") != null) {

                                    arrayList.add(new DataStep(snapshot.getId(),
                                            Integer.parseInt(snapshot.getData().get("index").toString()),
                                            snapshot.getData().get("title").toString(),
                                            snapshot.getData().get("desc").toString(),
                                            snapshot.getData().get("format").toString(),
                                            snapshot.getData().get("url").toString(),
                                            snapshot.getData().get("type").toString(),
                                            snapshot.getData().get("manual_id").toString()));
                                }else {
                                    Log.e("Steps: ", "Missing some parametere");

                                }
                            }

                            arrayList.sort(new Comparator<DataStep>() {
                                @Override
                                public int compare(DataStep dataStep, DataStep dataStep1) {
                                    return Integer.compare(dataStep.getIndex(), dataStep1.getIndex());
                                }
                            });

                            if (arrayList.size() == 0){
                                viewPager.setVisibility(View.GONE);
                                tabLayout.setVisibility(View.GONE);
                                tvPage.setVisibility(View.GONE);
                                tvBlank.setVisibility(View.VISIBLE);
                            }else {
                                viewPager.setVisibility(View.VISIBLE);
                                tvPage.setVisibility(View.VISIBLE);
                                tabLayout.setVisibility(View.VISIBLE);
                                tvBlank.setVisibility(View.GONE);
                            }

                            viewPagerAdapter = new ViewPagerAdapter(getApplicationContext(), arrayList);
                            viewPagerAdapter.notifyDataSetChanged();
                            viewPager.setAdapter(viewPagerAdapter);
                            tabLayout.setupWithViewPager(viewPager);
                        }
                    });


        }
    }

    public class ViewPagerAdapter extends PagerAdapter {

        private Context context;
        private LayoutInflater layoutInflater;
        private ArrayList<DataStep> list;

        public ViewPagerAdapter(Context context, ArrayList<DataStep> list) {
            this.context = context;
            this.list= arrayList;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {

            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.list_manual, null);

            ImageView imageView = (ImageView) view.findViewById(R.id.list_manual_imageview);
            VideoView videoView = view.findViewById(R.id.list_manual_videoView);
            TextView bAudio = view.findViewById(R.id.list_manual_bAudio);
            TextView tvTitle=view.findViewById(R.id.list_manual_tvName);
            TextView tvDesc = view.findViewById(R.id.list_manual_tvDesc);



            tvTitle.setText(list.get(position).getTitle());
            tvDesc.setText(list.get(position).getDesc());


            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (list.get(position).getFormat().equals("video")){
                        Intent intent = new Intent(ManualsActivity.this, MediaActivity.class);
                        intent.putExtra("url", list.get(position).getUrl());
                        intent.putExtra("type", list.get(position).getFormat());
                        startActivity(intent);
                    }
                }
            });

            switch (list.get(position).getFormat()){

                case "image":
                    imageView.setVisibility(View.VISIBLE);
                    videoView.setVisibility(View.GONE);
                    bAudio.setVisibility(View.GONE);
                    Picasso.get().load(list.get(position).getUrl()).into(imageView);
                    break;

                case "video":
                    imageView.setVisibility(View.VISIBLE);
                    videoView.setVisibility(View.GONE);
                    bAudio.setVisibility(View.GONE);

                  //  imageView.setImageDrawable(getResources().getDrawable(R.drawable.baseline_movie_24));

                    long thumb = 2000;
                    RequestOptions options = new RequestOptions().frame(thumb);
                    Glide.with(context).load(list.get(position).getUrl()).apply(options).into(imageView);

                    if (mPlayer.isPlaying()){
                        mPlayer.stop();
                    }
                 //   videoView.setVideoPath(list.get(position).getUrl());
                  //  videoView.start();
                   // MediaController mediaController = new MediaController(ManualsActivity.this);
                  //  mediaController.setMediaPlayer(videoView);
                   // videoView.setMediaController(mediaController);
                   // videoView.requestFocus();

                    break;

                case "audio":
                    imageView.setVisibility(View.GONE);
                    videoView.setVisibility(View.GONE);
                    bAudio.setVisibility(View.VISIBLE);

                    break;

            }

            bAudio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(ManualsActivity.this, MediaActivity.class);
                    intent.putExtra("url", list.get(position).getUrl());
                    intent.putExtra("type", list.get(position).getFormat());
                    startActivity(intent);

                    /*

                    if (videoView.isPlaying()){
                        videoView.stopPlayback();
                    }

                    if (mPlayer.isPlaying()){
                        mPlayer.stop();
                    }else {
                        mPlayer.stop();
                        mPlayer.reset();
                        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        try {
                            mPlayer.setDataSource(list.get(position).getUrl());
                            mPlayer.prepare();
                            mPlayer.start();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                     */
                  /*  MediaPlayer mediaPlayer = new MediaPlayer();
                    try {
                        mediaPlayer.setDataSource(list.get(position).getUrl());
                        mediaPlayer.prepareAsync();
                        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                mediaPlayer.start();
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                   */
                }
            });

            ViewPager vp = (ViewPager) container;
            vp.addView(view, 0);

            return view;
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {

            ViewPager vp = (ViewPager) container;
            View view = (View) object;
            vp.removeView(view);
        }
    }

    private File createImageFile(String imageID) throws IOException {
        // Create an image file name
        Log.e("Report: ", imageID);

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
   /*     File image = File.createTempFile(
                imageID,
                ".jpg",
                storageDir
        );

    */

        File file = new File(storageDir.getPath() + "/" + imageID +".jpg");
        Boolean aBoolean;
        aBoolean = file.createNewFile();
        // Save a file: path for use with ACTION_VIEW intents
        return file;
    }




/*
    private void takeScreenshot() {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            File file = createImageFile(now.toString());
            String mPath = file.getAbsolutePath();
            Log.e("Image", "Created at" + mPath);
            // create bitmap screen capture
            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

         //   File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(file);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();
            sendImage();
            Log.e("image", "done");
        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }
    }


 */


    private void sendImage(){
        floatingActionButton.setVisibility(View.GONE);
        View v1 = getWindow().getDecorView().getRootView();
        v1.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
        v1.setDrawingCacheEnabled(false);
        floatingActionButton.setVisibility(View.VISIBLE);
        if (bitmap != null){
         //   if (checkApp()){
                Uri uri = getImageUri(ManualsActivity.this, bitmap);
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_STREAM, uri);
              //  intent.setPackage("com.whatsapp");
                startActivity(intent);
       /*     }else {
                Log.e("Manuals", "Whatsapp not installed");
                Toast.makeText(this, "Whatsapp not installed", Toast.LENGTH_SHORT).show();
            }

        */
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



    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mPlayer.isPlaying()){
            mPlayer.stop();
        }
    }

        public Uri getImageUri(Context inContext, Bitmap inImage) {
            Date now = new Date();
            android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

            String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, now.toString(), null);
            return Uri.parse(path);
        }
}