package com.genius.appdesign2;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.genius.appdesign2.data.DataIssue;
import com.genius.appdesign2.data.DataStep;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;

public class IssueService extends Service {

    private DatabaseReference reference;
    private Date now = new Date();
    private FirebaseAuth firebaseAuth;

    private FirebaseFirestore firestore;


    public IssueService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser()!= null) {

            firestore = FirebaseFirestore.getInstance();

            firestore.collection("issueData")
                    .addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            assert value != null;
                            for (QueryDocumentSnapshot snapshot : value) {
                           //     Log.e("IssueService: ", snapshot.getData().toString());

                                if (snapshot.getBoolean("flag")) {
                                    Toast.makeText(IssueService.this, "Issue Created on Glass", Toast.LENGTH_SHORT).show();
                                    String key = snapshot.getId().toString();
                                    Log.e("IssueService: ", key);
                                    String title = "AppDesign2";
                                    String message = "Issue Created";
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
                                            .setSmallIcon(R.drawable.ic_bell) // notification icon
                                            .setContentTitle(title) // title for notification
                                            .setContentText(message)// message for notification
                                            .setAutoCancel(true); // clear notification after click
                                    Intent intent = new Intent(getApplicationContext(), IssueActivity.class);
                                    intent.putExtra("key", key);
                                    PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                    mBuilder.setContentIntent(pi);
                                    mNotificationManager.notify(0, mBuilder.build());
                                    break;
                                }
                            }

                        }
                    });
        }

     /*   reference = FirebaseDatabase.getInstance().getReference().child("validator");

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                DataIssue dataIssue = snapshot.getValue(DataIssue.class);

                Log.e("IssueService:" , snapshot.child("key").getValue().toString());

                Log.e("IssueService: ", dataIssue.toString());
                if (dataIssue.getFlag()){
                    Toast.makeText(IssueService.this, "Issue Created on Glass", Toast.LENGTH_SHORT).show();
                    String key = dataIssue.getKey().toString();
                    Log.e("IssueService: ", key);
                    String title = "AppDesign2";
                    String message = "Issue Created";
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
                            .setSmallIcon(R.drawable.ic_bell) // notification icon
                            .setContentTitle(title) // title for notification
                            .setContentText(message)// message for notification
                            .setAutoCancel(true); // clear notification after click
                    Intent intent = new Intent(getApplicationContext(), IssueActivity.class);
                    intent.putExtra("key", key);
                    PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(pi);
                    mNotificationManager.notify(0, mBuilder.build());
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


      */
        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public void onDestroy() {
        Intent intent = new Intent(getApplicationContext(), IssueService.class);
        onStartCommand(intent, 0, 0);
    }
}