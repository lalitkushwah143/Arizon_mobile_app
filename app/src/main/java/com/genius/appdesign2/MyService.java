package com.genius.appdesign2;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

public class MyService extends Service {


    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;
    private String CHANNEL_ID = "AppDesign2";
    private Date now = new Date();
    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.

        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference().child("session");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null && !snapshot.getValue().toString().equals("")){

                    Toast.makeText(MyService.this, "Notification", Toast.LENGTH_SHORT).show();

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
                            .setSmallIcon(R.drawable.ic_bell) // notification icon
                            .setContentTitle(title) // title for notification
                            .setContentText(message)// message for notification
                            .setAutoCancel(true); // clear notification after click
                    Intent intent = new Intent(getApplicationContext(), CallActivity.class);
                    PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(pi);
                    mNotificationManager.notify(0, mBuilder.build());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Intent intent = new Intent(getApplicationContext(), MyService.class);
        onStartCommand(intent, 0, 0);
    }
}