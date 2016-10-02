package com.example.jorgegil.closegamealert.GCM;

import com.example.jorgegil.closegamealert.R;
import com.example.jorgegil.closegamealert.View.Activities.MainActivity;
import com.google.android.gms.gcm.GcmListenerService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class GCMMessageHandler extends GcmListenerService {
    private static final String TAG = "GCMMessageHandler";
    public static final int MESSAGE_NOTIFICATION_ID = 435345;

    @Override
    public void onMessageReceived(String from, Bundle data) {

        if (data.getString("comment") != null) {
            sendNewComment(data.getString("comment"));
        }

        //TODO: Clean this up.
        if (data.getString("message") != null) {
            String message = data.getString("message");
            if (message.charAt(0) == '[') {
                Log.d(TAG, "received JSON: " + data.getString("message"));
                sendGameData(data.getString("message"));
            } else {
                Log.d(TAG, "received notification: " + data.getString("message"));
                createNotification(message, "Touch to view more!");
            }
        }
    }

    // Creates notification based on title and body received
    private void createNotification(String title, String body) {
        Context context = getBaseContext();
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.bball_filled)
                .setContentTitle(title)
                .setContentText(body)
                .setColor(getResources().getColor(R.color.colorPrimary));

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        mBuilder.setContentIntent(pendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(MESSAGE_NOTIFICATION_ID, mBuilder.build());
    }

    // Sends new game data to the Main Activity
    private void sendGameData(String message) {
        Log.d("sender", "Broadcasting message");
        Intent intent = new Intent("game-data");
        intent.putExtra("message", message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void sendNewComment(String comment) {
        Log.d("JSON", "received comment: " + comment);
        Intent intent = new Intent("comment-data");
        intent.putExtra("comment", comment);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

}