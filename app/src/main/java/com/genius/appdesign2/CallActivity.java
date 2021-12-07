package com.genius.appdesign2;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import com.opentok.android.BaseVideoRenderer;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Subscriber;
import com.opentok.android.OpentokError;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class CallActivity extends AppCompatActivity implements  EasyPermissions.PermissionCallbacks
        , Session.SessionListener,
        PublisherKit.PublisherListener{

   static class SubscriberContainer {
        public RelativeLayout container;
        public ToggleButton toggleAudio;
        public Subscriber subscriber;

        public SubscriberContainer(RelativeLayout container,
                                   ToggleButton toggleAudio,
                                   Subscriber subscriber) {
            this.container = container;
            this.toggleAudio = toggleAudio;
            this.subscriber = subscriber;
        }
    }
    private static final String TAG = "Call Activity " + MainActivity.class.getSimpleName();

    private final int MAX_NUM_SUBSCRIBERS = 2;

    private static final int RC_SETTINGS_SCREEN_PERM = 123;
    private static final int RC_VIDEO_APP_PERM = 124;

    private Session mSession;
    private Publisher mPublisher;

    private List<SubscriberContainer> mSubscribers;

    private RelativeLayout mPublisherViewContainer;

    private boolean sessionConnected = false;

    private ImageView bScreenshot;

    private static String API_KEY = "47217094";
    public static final String SESSION_ID = "1_MX40NzIxNzA5NH5-MTYxOTg1NDAzMjYxMn5ZU21TVzNmVlhpYjc1QmJVdjBOMlZtekt-UH4";
    public static final String TOKEN = "T1==cGFydG5lcl9pZD00NzIxNzA5NCZzaWc9NzUxYWVkM2YwZDBhM2JhOWU5MjdjZTJkOTRhMmU4Nzg0OGNjZGM3NzpzZXNzaW9uX2lkPTFfTVg0ME56SXhOekE1Tkg1LU1UWXhPVGcxTkRBek1qWXhNbjVaVTIxVFZ6Tm1WbGhwWWpjMVFtSlZkakJPTWxadGVrdC1VSDQmY3JlYXRlX3RpbWU9MTYxOTg1NDA0OCZub25jZT0wLjgzNjU4ODU2MzIwMzMxOTImcm9sZT1wdWJsaXNoZXImZXhwaXJlX3RpbWU9MTYyMjQ0NjA1NyZpbml0aWFsX2xheW91dF9jbGFzc19saXN0PQ==";

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference, storageRef1;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference, ref1;

    private static final int PICK_IMAGE_REQUEST =125 ;
    private Uri uri;
    Date now = new Date();
    private String session_key;

    private RecyclerView recyclerView;
    private CallAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);
        Log.d(TAG, "onCreate");
        mPublisherViewContainer = (RelativeLayout) findViewById(R.id.publisherview);
        bScreenshot = findViewById(R.id.activity_call_bScreenshot);

     //   recyclerView = findViewById(R.id.activity_call_rcView);

        getSupportActionBar().hide();

        firebaseStorage= FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference().child("session");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                session_key = snapshot.getValue().toString();
                if (!session_key.isEmpty())
                storageRef1 = storageReference.child("images").child(session_key);
                ref1 = firebaseDatabase.getReference().child("uploads").child(session_key);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        final Button swapCamera = (Button) findViewById(R.id.swapCamera);
        swapCamera.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mPublisher == null) {
                    return;
                }
                mPublisher.cycleCamera();
            }
        });

        final ToggleButton toggleAudio = (ToggleButton) findViewById(R.id.toggleAudio);
        toggleAudio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mPublisher == null) {
                    return;
                }
                if (isChecked) {
                    mPublisher.setPublishAudio(true);
                } else {
                    mPublisher.setPublishAudio(false);
                }
            }
        });

        final ToggleButton toggleVideo = (ToggleButton) findViewById(R.id.toggleVideo);
        toggleVideo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mPublisher == null) {
                    return;
                }
                if (isChecked) {
                    mPublisher.setPublishVideo(true);
                } else {
                    mPublisher.setPublishVideo(false);
                }
            }
        });

        mSubscribers = new ArrayList<>();
        for (int i = 0; i < MAX_NUM_SUBSCRIBERS; i++) {
            int containerId = getResources().getIdentifier("subscriberview" + (new Integer(i)).toString(),
                    "id", CallActivity.this.getPackageName());
            int toggleAudioId = getResources().getIdentifier("toggleAudioSubscriber" + (new Integer(i)).toString(),
                    "id", CallActivity.this.getPackageName());
            mSubscribers.add(new SubscriberContainer(
                    findViewById(containerId),
                    findViewById(toggleAudioId),
                    null
            ));
        }

        requestPermissions();
        bScreenshot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select an image"),PICK_IMAGE_REQUEST);
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==PICK_IMAGE_REQUEST && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            uri=data.getData();
            if (uri!= null){
                Intent intent = new Intent(CallActivity.this, UploadActivity.class);
                intent.putExtra("session_key", session_key);
                intent.putExtra("uri", uri);
                startActivity(intent);
            }else {
                Toast.makeText(CallActivity.this, "Unable to select Image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class CallAdapter extends RecyclerView.Adapter<CallAdapter.ViewHolder>{

        private Context context;
        private ArrayList<SubscriberContainer> list=new ArrayList<>();

        public CallAdapter(Context context, ArrayList<SubscriberContainer> list) {
            this.context=context;
            this.list=list;
        }

        @NonNull
        @Override
        public CallAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_subscriber, parent, false);
            return new ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull CallAdapter.ViewHolder holder, final int position) {
            switch (list.size()){
                case 1:

                    break;

                case 2:

                    break;

                case 3:

                    break;

                case 4:

                    break;
            }
        }
        @Override
        public int getItemCount() {
            return list.size();
        }
        public class ViewHolder extends RecyclerView.ViewHolder {
            ToggleButton bAudio;
            RelativeLayout layout;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                bAudio = itemView.findViewById(R.id.list_subscriber_bAudio);
                layout = itemView.findViewById(R.id.list_subscriber_layout);

            }
        }
    }


    @Override
    protected void onStart() {
        Log.d(TAG, "onStart");

        super.onStart();
    }

    @Override
    protected void onRestart() {
        Log.d(TAG, "onRestart");

        super.onRestart();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");

        super.onResume();

        if (mSession == null) {
            return;
        }
        mSession.onResume();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");

        super.onPause();

        if (mSession == null) {
            return;
        }
        mSession.onPause();

        if (isFinishing()) {
            disconnectSession();
        }
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onPause");

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");

        disconnectSession();

        super.onDestroy();
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
        Log.d(TAG, "onPermissionsDenied:" + requestCode + ":" + perms.size());

        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this)
                    .setTitle(getString(R.string.title_settings_dialog))
                    .setRationale(getString(R.string.rationale_ask_again))
                    .setPositiveButton(getString(R.string.setting))
                    .setNegativeButton(getString(R.string.cancel))
                    .setRequestCode(RC_SETTINGS_SCREEN_PERM)
                    .build()
                    .show();
        }
    }

    @AfterPermissionGranted(RC_VIDEO_APP_PERM)
    private void requestPermissions() {
        String[] perms = {
                Manifest.permission.INTERNET,
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
        };
        if (EasyPermissions.hasPermissions(this, perms)) {
            mSession = new Session.Builder(CallActivity.this, API_KEY, SESSION_ID).build();
            mSession.setSessionListener(this);
            mSession.connect(TOKEN);
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_video_app), RC_VIDEO_APP_PERM, perms);
        }
    }
    @Override
    public void onConnected(Session session) {
        Log.d(TAG, "onConnected: Connected to session " + session.getSessionId());
        sessionConnected = true;

        mPublisher = new Publisher.Builder(CallActivity.this).name("publisher").build();

        mPublisher.setPublisherListener(this);
        mPublisher.setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE, BaseVideoRenderer.STYLE_VIDEO_FILL);

        mPublisherViewContainer.addView(mPublisher.getView());

        mSession.publish(mPublisher);
    }

    @Override
    public void onDisconnected(Session session) {
        Log.d(TAG, "onDisconnected: disconnected from session " + session.getSessionId());
        sessionConnected = false;
        mSession = null;
    }

    @Override
    public void onError(Session session, OpentokError opentokError) {
        Log.d(TAG, "onError: Error (" + opentokError.getMessage() + ") in session " + session.getSessionId());

        Toast.makeText(this, "Session error. See the logcat please.", Toast.LENGTH_LONG).show();
        finish();
    }

    private SubscriberContainer findFirstEmptyContainer(Subscriber subscriber) {
        for (SubscriberContainer c : mSubscribers) {
            if (c.subscriber == null) {
                return c;
            }
        }
        return null;
    }

    private SubscriberContainer findContainerForStream(Stream stream) {
        for (SubscriberContainer c : mSubscribers) {
            if (c.subscriber.getStream().getStreamId().equals(stream.getStreamId())) {
                return c;
            }
        }
        return null;
    }

    private void addSubscriber(Subscriber subscriber) {
        SubscriberContainer container = findFirstEmptyContainer(subscriber);
        if (container == null) {
            Toast.makeText(this, "New subscriber ignored. MAX_NUM_SUBSCRIBERS limit reached.", Toast.LENGTH_LONG).show();
            return;
        }

        container.subscriber = subscriber;
        container.container.addView(subscriber.getView());
        subscriber.setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE, BaseVideoRenderer.STYLE_VIDEO_FILL);

        container.toggleAudio.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                subscriber.setSubscribeToAudio(true);
            } else {
                subscriber.setSubscribeToAudio(false);
            }
        });
        container.toggleAudio.setVisibility(View.VISIBLE);
    }

    private void removeSubscriberWithStream(Stream stream) {
        SubscriberContainer container = findContainerForStream(stream);
        if (container == null) {
            return;
        }

        container.container.removeView(container.subscriber.getView());
        container.toggleAudio.setOnCheckedChangeListener(null);
        container.toggleAudio.setVisibility(View.INVISIBLE);
        container.subscriber = null;
    }

    @Override
    public void onStreamReceived(Session session, Stream stream) {
        Log.d(TAG, "onStreamReceived: New stream " + stream.getStreamId() + " in session " + session.getSessionId());

        final Subscriber subscriber = new Subscriber.Builder(CallActivity.this, stream).build();
        mSession.subscribe(subscriber);
        addSubscriber(subscriber);
    }

    @Override
    public void onStreamDropped(Session session, Stream stream) {
        Log.d(TAG, "onStreamDropped: Stream " + stream.getStreamId() + " dropped from session " + session.getSessionId());

        removeSubscriberWithStream(stream);
    }

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
        Log.d(TAG, "onError: Error (" + opentokError.getMessage() + ") in publisher");

        Toast.makeText(this, "Session error. See the logcat please.", Toast.LENGTH_LONG).show();
        finish();
    }

    private void disconnectSession() {
        if (mSession == null || !sessionConnected) {
            return;
        }
        sessionConnected = false;

        if (mSubscribers.size() > 0) {
            for (SubscriberContainer c : mSubscribers) {
                if (c.subscriber != null) {
                    mSession.unsubscribe(c.subscriber);
                    c.subscriber.destroy();
                }
            }
        }

        if (mPublisher != null) {
            mPublisherViewContainer.removeView(mPublisher.getView());
            mSession.unpublish(mPublisher);
            mPublisher.destroy();
            mPublisher = null;
        }
        mSession.disconnect();
    }
}
