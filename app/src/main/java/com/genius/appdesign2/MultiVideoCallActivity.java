package com.genius.appdesign2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.Manifest;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.opentok.android.BaseVideoRenderer;
import com.opentok.android.OpentokError;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Subscriber;
import com.opentok.android.SubscriberKit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MultiVideoCallActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks, View.OnClickListener {


    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int PERMISSIONS_REQUEST_CODE = 124;

    private Session session;
    private Publisher publisher;

    private ArrayList<Subscriber> subscribers = new ArrayList<>();
    private HashMap<Stream, Subscriber> subscriberStreams = new HashMap<>();

    private ConstraintLayout container;
    private ArrayList<Integer> videoList = new ArrayList<>();
    private TextView tvDisabled;

    private PublisherKit.PublisherListener publisherListener = new PublisherKit.PublisherListener() {
        @Override
        public void onStreamCreated(PublisherKit publisherKit, Stream stream) {
            Log.d(TAG, "onStreamCreated: Own stream " + stream.getStreamId() + " created");
        }

        @Override
        public void onStreamDestroyed(PublisherKit publisherKit, Stream stream) {
            Log.d(TAG, "onStreamDestroyed: Own stream " + stream.getStreamId() + " destroyed");
        }

        @Override
        public void onError(PublisherKit publisherKit, OpentokError opentokError) {
            finishWithMessage("PublisherKit error: " + opentokError.getMessage());
        }
    };

    private Session.SessionListener sessionListener = new Session.SessionListener() {
        @Override
        public void onConnected(Session session) {
            Log.d(TAG, "onConnected: Connected to session " + session.getSessionId());

            session.publish(publisher);
        }

        @Override
        public void onDisconnected(Session session) {
            Log.d(TAG, "onDisconnected: disconnected from session " + session.getSessionId());

            MultiVideoCallActivity.this.session = null;
        }

        @Override
        public void onError(Session session, OpentokError opentokError) {
            finishWithMessage("Session error: " + opentokError.getMessage());
        }

        @Override
        public void onStreamReceived(Session session, Stream stream) {
            Log.d(TAG, "onStreamReceived: New stream " + stream.getStreamId() + " in session " + session.getSessionId());

            final Subscriber subscriber = new Subscriber.Builder(MultiVideoCallActivity.this, stream).build();
            session.subscribe(subscriber);
            subscribers.add(subscriber);
            subscriberStreams.put(stream, subscriber);

            subscriber.setVideoListener(new SubscriberKit.VideoListener() {
                @Override
                public void onVideoDataReceived(SubscriberKit subscriberKit) {

                }

                @Override
                public void onVideoDisabled(SubscriberKit subscriberKit, String s) {
                    calculateLayout();
                }

                @Override
                public void onVideoEnabled(SubscriberKit subscriberKit, String s) {
                    calculateLayout();
                }

                @Override
                public void onVideoDisableWarning(SubscriberKit subscriberKit) {

                }

                @Override
                public void onVideoDisableWarningLifted(SubscriberKit subscriberKit) {

                }
            });


            int subId = getResIdForSubscriberIndex(subscribers.size() - 1);
            subscriber.getView().setId(subId);
            container.addView(subscriber.getView());
            subscriber.getView().setOnClickListener(MultiVideoCallActivity.this::onClick);
            container.setOnClickListener(MultiVideoCallActivity.this::onClick);
            calculateLayout();

        }



        @Override
        public void onStreamDropped(Session session, Stream stream) {
            Log.d(TAG, "onStreamDropped: Stream " + stream.getStreamId() + " dropped from session " + session.getSessionId());

            Subscriber subscriber = subscriberStreams.get(stream);

            if (subscriber == null) {
                return;
            }

            subscribers.remove(subscriber);
            subscriberStreams.remove(stream);

            container.removeView(subscriber.getView());

            // Recalculate view Ids
            for (int i = 0; i < subscribers.size(); i++) {
                subscribers.get(i).getView().setId(getResIdForSubscriberIndex(i));
            }
            calculateLayout();
        }
    };

    private FirebaseFirestore firestore;

    private FloatingActionButton fabVideo, fabAudio;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_video_call);

        getSupportActionBar().hide();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        fabVideo = findViewById(R.id.activity_multi_fabVideo);
        fabAudio = findViewById(R.id.activity_multi_fabAudio);
        tvDisabled = findViewById(R.id.activity_multi_tvCountDisabled);

        fabVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (publisher != null){
                    if (publisher.getPublishVideo()){
                        publisher.setPublishVideo(false);
                        fabVideo.setImageResource(R.drawable.ic_baseline_videocam_24);
                    }else {
                        publisher.setPublishVideo(true);
                        fabVideo.setImageResource(R.drawable.ic_baseline_videocam_off_24);
                    }

                }
            }
        });

        fabAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (publisher != null){
                    if (publisher.getPublishAudio()){
                        publisher.setPublishAudio(false);
                        fabAudio.setImageResource(R.drawable.ic_baseline_mic_24);
                    }else {
                        publisher.setPublishAudio(true);
                        fabAudio.setImageResource(R.drawable.ic_baseline_mic_off_24);
                    }
                }
            }
        });

        container = findViewById(R.id.activity_multi_container);

        firestore = FirebaseFirestore.getInstance();

        DocumentReference documentReference = firestore.collection("OpenTokConfig").document("BkEGCdgSefXrFkEmzcCG");
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    if (document.exists()){
                        if (document.getData().get("api_key") != null && document.getData().get("token") != null && document.getData().get("session_id") != null) {
                            OpenTokConfig.API_KEY = document.getData().get("api_key").toString();
                            OpenTokConfig.TOKEN = document.getData().get("token").toString();
                            OpenTokConfig.SESSION_ID = document.getData().get("session_id").toString();
                            if(!OpenTokConfig.isValid()) {
                                finishWithMessage("Invalid OpenTokConfig. " + OpenTokConfig.getDescription());
                            }else {
                                Log.e("OpenTokConfig: ", "API: " + OpenTokConfig.API_KEY + "\n SEssion: " + OpenTokConfig.SESSION_ID + "\n Token: " + OpenTokConfig.TOKEN);
                                requestPermissions();
                            }
                        }else {
                            finishWithMessage("Initiation Call: Missing Parameters");
                        }
                    }else {
                        finishWithMessage("Intiation Call: No document found");
                    }
                }else {
                    finishWithMessage("Initiation Call : Failed to get Credentials");
                }
            }
        });

/*
        firestore.collection("OpenTokConfig")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        assert value != null;
                        OpenTokConfig.API_KEY = "";
                        OpenTokConfig.TOKEN = "";
                        OpenTokConfig.SESSION_ID = "";

                        for (QueryDocumentSnapshot snapshot : value) {
                            OpenTokConfig.API_KEY = snapshot.getData().get("api_key").toString();
                            OpenTokConfig.TOKEN = snapshot.getData().get("token").toString();
                            OpenTokConfig.SESSION_ID = snapshot.getData().get("session_id").toString();
                        }
                        if(!OpenTokConfig.isValid()) {
                            finishWithMessage("Invalid OpenTokConfig. " + OpenTokConfig.getDescription());
                            return;
                        }

                        Log.e("OpenTokConfig: ", "API: " + OpenTokConfig.API_KEY + "\n SEssion: " + OpenTokConfig.SESSION_ID + "\n Token: " + OpenTokConfig.TOKEN);
                        requestPermissions();


                    }
                });


 */

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (session == null) {
            return;
        }

        session.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (session == null) {
            return;
        }

        session.onPause();

        if (isFinishing()) {
            disconnectSession();
        }
    }

    @Override
    protected void onDestroy() {
        disconnectSession();

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Log.d(TAG, "onPermissionsGranted:" + requestCode + ":" + perms.size());
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        finishWithMessage("onPermissionsDenied: " + requestCode + ":" + perms.size());
    }

    private int getResIdForSubscriberIndex(int index) {
        TypedArray arr = getResources().obtainTypedArray(R.array.subscriber_view_ids);
        int subId = arr.getResourceId(index, 0);
        arr.recycle();
        return subId;
    }

    private void startPublisherPreview() {
        publisher = new Publisher.Builder(this).build();
       // publisher.setCameraId(0);
        publisher.setPublisherListener(publisherListener);
        publisher.setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE, BaseVideoRenderer.STYLE_VIDEO_FILL);
        publisher.startPreview();
    }

    @AfterPermissionGranted(PERMISSIONS_REQUEST_CODE)
    private void requestPermissions() {
        String[] perms = { Manifest.permission.INTERNET, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO };

        if (EasyPermissions.hasPermissions(this, perms)) {
            session = new Session.Builder(this, OpenTokConfig.API_KEY, OpenTokConfig.SESSION_ID).sessionOptions(new Session.SessionOptions() {
                @Override
                public boolean useTextureViews() {
                    return true;
                }
            }).build();
            session.setSessionListener(sessionListener);
            session.connect(OpenTokConfig.TOKEN);

            startPublisherPreview();
            publisher.getView().setId(R.id.publisher_view_id);
            container.addView(publisher.getView());
            calculateLayout();
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_video_app), PERMISSIONS_REQUEST_CODE, perms);
        }
    }

    private void calculateLayout() {
        ConstraintSetHelper set = new ConstraintSetHelper(R.id.activity_multi_container);

     //   int size = subscribers.size();
        videoList = new ArrayList<>();

        for (int i =0; i<subscribers.size(); i++){
            if (subscribers.get(i).getStream().hasVideo()){
                videoList.add(getResIdForSubscriberIndex(i));
               // subscribers.get(i).getView().setVisibility(View.VISIBLE);
            }else {
              //  subscribers.get(i).getView().setVisibility(View.GONE);
            }
        }
        int size = videoList.size();
        tvDisabled.setText(videoList.size() + " : " + subscribers.size());


        if (size == 0) {
            // Publisher full screen
            set.layoutViewFullScreen(R.id.publisher_view_id);
        } else if (size == 1) {
            // Publisher
            // Subscriber
            set.layoutViewAboveView(R.id.publisher_view_id, videoList.get(0));
            set.layoutViewWithTopBound(R.id.publisher_view_id, R.id.activity_multi_container);
            set.layoutViewWithBottomBound(videoList.get(0), R.id.activity_multi_container);
            set.layoutViewAllContainerWide(R.id.publisher_view_id, R.id.activity_multi_container);
            set.layoutViewAllContainerWide(videoList.get(0), R.id.activity_multi_container);
            set.layoutViewHeightPercent(R.id.publisher_view_id, .5f);
            set.layoutViewHeightPercent(videoList.get(0), .5f);
        } else if (size > 1 && size % 2 == 0){
            //  Publisher
            // Sub1 | Sub2
            // Sub3 | Sub4
            //    .....
            int rows = (size / 2) + 1;
            float heightPercent = 1f / rows;

            set.layoutViewWithTopBound(R.id.publisher_view_id, R.id.activity_multi_container);
            set.layoutViewAllContainerWide(R.id.publisher_view_id, R.id.activity_multi_container);
            set.layoutViewHeightPercent(R.id.publisher_view_id, heightPercent);

            for (int i = 0; i < size; i += 2) {
                if (i == 0) {
                    set.layoutViewAboveView(R.id.publisher_view_id, videoList.get(i));
                    set.layoutViewAboveView(R.id.publisher_view_id, videoList.get(i + 1));
                } else {
                    set.layoutViewAboveView(videoList.get(i - 2), videoList.get(i));
                    set.layoutViewAboveView(videoList.get(i - 1), videoList.get(i + 1));
                }

                set.layoutTwoViewsOccupyingAllRow(videoList.get(i), videoList.get(i + 1));
                set.layoutViewHeightPercent(videoList.get(i), heightPercent);
                set.layoutViewHeightPercent(videoList.get(i + 1), heightPercent);
            }

            set.layoutViewWithBottomBound(videoList.get(size - 2), R.id.activity_multi_container);
            set.layoutViewWithBottomBound(videoList.get(size - 1), R.id.activity_multi_container);
        } else if (size > 1) {
            // Pub  | Sub1
            // Sub2 | Sub3
            // Sub3 | Sub4
            //    .....
            int rows = ((size + 1) / 2);
            float heightPercent = 1f / rows;

            set.layoutViewWithTopBound(R.id.publisher_view_id, R.id.activity_multi_container);
            set.layoutViewHeightPercent(R.id.publisher_view_id, heightPercent);
            set.layoutViewWithTopBound(videoList.get(0), R.id.activity_multi_container);
            set.layoutViewHeightPercent(videoList.get(0), heightPercent);
            set.layoutTwoViewsOccupyingAllRow(R.id.publisher_view_id, videoList.get(0));

            for (int i = 1; i < size; i += 2) {
                if (i == 1) {
                    set.layoutViewAboveView(R.id.publisher_view_id, videoList.get(i));
                    set.layoutViewHeightPercent(R.id.publisher_view_id, heightPercent);
                    set.layoutViewAboveView(videoList.get(0), videoList.get(i + 1));
                    set.layoutViewHeightPercent(videoList.get(0), heightPercent);
                } else {
                    set.layoutViewAboveView(videoList.get(i - 2), videoList.get(i));
                    set.layoutViewHeightPercent(videoList.get(i - 2), heightPercent);
                    set.layoutViewAboveView(videoList.get(i - 1), videoList.get(i + 1));
                    set.layoutViewHeightPercent(videoList.get(i - 1), heightPercent);
                }
                set.layoutTwoViewsOccupyingAllRow(videoList.get(i), videoList.get(i + 1));
            }

            set.layoutViewWithBottomBound(videoList.get(size - 2), R.id.activity_multi_container);
            set.layoutViewWithBottomBound(videoList.get(size - 1), R.id.activity_multi_container);
        }

        set.applyToLayout(container, true);
    }

    private void disconnectSession() {
        if (session == null) {
            return;
        }

        if (subscribers.size() > 0) {
            for (Subscriber subscriber : subscribers) {
                if (subscriber != null) {
                    session.unsubscribe(subscriber);
                }
            }
        }

        if (publisher != null) {
            publisher.getCapturer().stopCapture();
            session.unpublish(publisher);
            container.removeView(publisher.getView());
            publisher = null;
        }
        session.disconnect();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void finishWithMessage(String message) {
        if (message.equals("Session error: Cannot publish: the client is not connected to the OpenTok session.")){
            disconnectSession();
        }else {
            Log.e(TAG, message);
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            disconnectSession();
        }

        this.finish();
    }

    @Override
    public void onClick(View v) {

            switch (v.getId()){
                case  R.id.subscriber_1:
                    OpenDialog(0);

                    Log.e("IDS:", "Sub 1");
                    break;

                case R.id.subscriber_2:
                    OpenDialog(1);

                    Log.e("IDS:", "Sub 2");

                    break;

                case R.id.subscriber_3:
                    OpenDialog(2);

                    Log.e("IDS:", "Sub 3");

                    break;

                case R.id.subscriber_4:
                    OpenDialog(3);

                    Log.e("IDS:", "Sub 4");

                    break;

                case R.id.subscriber_5:
                    OpenDialog(4);

                    Log.e("IDS:", "Sub 4");

                    break;

                case R.id.subscriber_6:
                    OpenDialog(5);

                    Log.e("IDS:", "Sub 4");

                    break;
                case R.id.subscriber_7:
                    OpenDialog(6);

                    Log.e("IDS:", "Sub 4");

                    break;
                case R.id.subscriber_8:
                    OpenDialog(7);

                    Log.e("IDS:", "Sub 4");

                    break;
                case R.id.subscriber_9:
                    OpenDialog(8);

                    Log.e("IDS:", "Sub 4");

                    break;
                case R.id.subscriber_10:
                    OpenDialog(9);
                    Log.e("IDS:", "Sub 4");
                    break;

                default:
                    Log.e("IDS", "Main container");
                    break;
            }
        }

        private void OpenDialog(int index){

            Subscriber subscriber = subscribers.get(index);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MultiVideoCallActivity.this);
            LayoutInflater inflater=MultiVideoCallActivity.this.getLayoutInflater();
            final View view=inflater.inflate(R.layout.video_call_dialog, null);
            alertDialogBuilder.setView(view);

            CheckBox cVideo = view.findViewById(R.id.cVideo);
            CheckBox cAudio = view.findViewById(R.id.cAudio);
            cVideo.setChecked(subscriber.getSubscribeToVideo());
            cAudio.setChecked(subscriber.getSubscribeToAudio());

            alertDialogBuilder.setTitle("Call Settings");
            alertDialogBuilder.show();

            cVideo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    subscriber.setSubscribeToVideo(isChecked);
                }
            });
            cAudio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    subscriber.setSubscribeToAudio(isChecked);
                }
            });

        }
}
