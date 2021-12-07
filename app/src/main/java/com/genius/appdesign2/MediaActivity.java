package com.genius.appdesign2;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.IOException;

public class MediaActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener, MediaController.MediaPlayerControl {

    private VideoView videoView;
    private ImageView imageView;
    private MediaPlayer mediaPlayer;
    private MediaController mediaController;

    private String url, type;
    private Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);

        getSupportActionBar().hide();

        videoView = findViewById(R.id.activity_media_videoview);
        imageView = findViewById(R.id.activity_media_bAudio);

        mediaPlayer = new MediaPlayer();
       // progressBar.setMax(mediaPlayer.getTrackInfo().length);
        mediaController = new MediaController(this);

        url = getIntent().getStringExtra("url");
        type = getIntent().getStringExtra("type");

        Log.e("MEdia", url);
        Log.e("Media", type);

        if (url == null || type == null){
            finish();
            Toast.makeText(this, "Missing Attributes", Toast.LENGTH_SHORT).show();
        }

        switch (type){
            case "audio":
                videoView.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);

                if (videoView.isPlaying()){
                    videoView.stopPlayback();
                }

                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                } else {
                    mediaPlayer.reset();
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    try {
                        mediaPlayer.setDataSource(url);
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                        mediaPlayer.setOnPreparedListener(this);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }



                break;

            case "video":
                imageView.setVisibility(View.GONE);
                videoView.setVisibility(View.VISIBLE);

                if (mediaPlayer.isPlaying()){
                    mediaPlayer.stop();
                }

                if (videoView.isPlaying()){
                videoView.stopPlayback();
                videoView.setVisibility(View.GONE);
            }else {
                videoView.setVideoPath(url);
                videoView.start();
                MediaController mediaController = new MediaController(MediaActivity.this);
                mediaController.setMediaPlayer(videoView);
                videoView.setMediaController(mediaController);
                videoView.requestFocus();
            }
                break;
        }

    }



    @Override
    protected void onPause() {
        super.onPause();

        if (mediaPlayer.isPlaying()){
            mediaPlayer.stop();
        }
        if (videoView.isPlaying()){
            videoView.stopPlayback();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mediaController.hide();
        mediaPlayer.stop();
        mediaPlayer.release();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //the MediaController will hide after 3 seconds - tap the screen to make it appear again
        if (mediaController != null){
            mediaController.show();

        }
        return false;
    }


    //--MediaPlayerControl methods----------------------------------------------------
    public void start() {
        mediaPlayer.start();
    }

    public void pause() {
        mediaPlayer.pause();
    }

    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    public void seekTo(int i) {
        mediaPlayer.seekTo(i);
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public int getBufferPercentage() {
        return 0;
    }

    public boolean canPause() {
        return true;
    }

    public boolean canSeekBackward() {
        return true;
    }

    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }
    //--------------------------------------------------------------------------------

    public void onPrepared(MediaPlayer mediaPlayer) {
        Log.d("MediaPlayer", "onPrepared");
        mediaController.setMediaPlayer(this);
        mediaController.setAnchorView(findViewById(R.id.activity_media_layout));

        handler.post(new Runnable() {
            public void run() {
                mediaController.setEnabled(true);
                mediaController.show();
            }
        });
    }

}