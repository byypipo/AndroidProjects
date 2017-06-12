package com.sebatmedikal.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sebatmedikal.R;
import com.sebatmedikal.activity.LoginActivity;
import com.sebatmedikal.activity.OperationsActivity;
import com.sebatmedikal.mapper.Mapper;
import com.sebatmedikal.remote.domain.Operation;
import com.sebatmedikal.util.CompareUtil;
import com.sebatmedikal.util.LogUtil;
import com.sebatmedikal.util.NullUtil;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by orhan on 10.06.2017.
 */
public class MessagingService extends FirebaseMessagingService {
    private static final String TAG = "MessagingService";
    private AtomicInteger notificationID = new AtomicInteger();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Log.d(TAG, "Message : " + objectMapper.writeValueAsString(remoteMessage));
        } catch (Exception e) {

        }
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        Log.d(TAG, "Message Notification: " + remoteMessage.getNotification());
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            sendNotification(remoteMessage);
        }
    }

    private void sendNotification(RemoteMessage remoteMessage) {
        String data = remoteMessage.getData().get("message");
        String notification = remoteMessage.getNotification().getBody();

        Intent intent = null;
        if (CompareUtil.equal(remoteMessage.getNotification().getTitle(), "SERVER_ONLINE")) {
            intent = new Intent(this, LoginActivity.class);
        } else if (CompareUtil.equal(remoteMessage.getNotification().getTitle(), "NEW_OPERATION")) {
            intent = new Intent(this, OperationsActivity.class);
            intent.putExtra("operation", Mapper.operationMapper(data));
        }

        if (NullUtil.isNull(intent)) {
            LogUtil.logMessage(getClass(), "intent is not generated for NotificationTitle: " + remoteMessage.getNotification().getTitle());
            return;
        }

        intent.putExtra("data", data);
        intent.putExtra("notification", notification);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.sebat_medikal)
                .setContentTitle(remoteMessage.getNotification().getTitle())
                .setContentText(remoteMessage.getNotification().getBody())
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(notificationID.getAndIncrement(), notificationBuilder.build());
    }
}
