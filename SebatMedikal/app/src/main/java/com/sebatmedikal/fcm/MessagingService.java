package com.sebatmedikal.fcm;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Region;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sebatmedikal.R;
import com.sebatmedikal.activity.LoginActivity;
import com.sebatmedikal.activity.OperationsActivity;
import com.sebatmedikal.activity.UsersActivity;
import com.sebatmedikal.mapper.Mapper;
import com.sebatmedikal.remote.domain.Operation;
import com.sebatmedikal.remote.domain.User;
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

        BaseNotify baseNotify = new BaseNotify();
        baseNotify.setContext(this);
        baseNotify.setId(notificationID.getAndIncrement());

        Intent intent = null;
        if (CompareUtil.equal(remoteMessage.getNotification().getTitle(), "SERVER_ONLINE")) {
            intent = new Intent(this, LoginActivity.class);
            baseNotify.setTitle(getString(R.string.SERVER_ONLINE));
            baseNotify.setText(remoteMessage.getNotification().getBody());
        } else if (CompareUtil.equal(remoteMessage.getNotification().getTitle(), "NEW_OPERATION")) {
            Operation operation = Mapper.operationMapper(data);
            intent = new Intent(this, OperationsActivity.class);
            intent.putExtra("operation", operation);
            baseNotify.setTitle(getString(R.string.NEW_OPERATION));
            baseNotify.setText(getString(R.string.operation_createdBy) + ": " + operation.getCreatedBy());
            baseNotify.setBigText(getString(R.string.operation_createdBy) + ": " + operation.getCreatedBy());
            baseNotify.setBigTitle(getString(R.string.NEW_OPERATION));
            baseNotify.setSummaryText(getString(R.string.NEW_OPERATION_BY, operation.getCreatedBy(), operation.getOperationType().getOperationTypeName()));
        } else if (CompareUtil.equal(remoteMessage.getNotification().getTitle(), "LOGIN")) {
            User user = Mapper.userMapper(data);
            intent = new Intent(this, UsersActivity.class);
            baseNotify.setTitle(getString(R.string.LOGIN, user.getFirstName() + " " + user.getLastName()));
            baseNotify.setText(remoteMessage.getNotification().getBody());
        } else if (CompareUtil.equal(remoteMessage.getNotification().getTitle(), "LOGOUT")) {
            User user = Mapper.userMapper(data);
            intent = new Intent(this, UsersActivity.class);
            baseNotify.setTitle(getString(R.string.LOGOUT, user.getFirstName() + " " + user.getLastName()));
            baseNotify.setText(remoteMessage.getNotification().getBody());
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

        baseNotify.setPendingIntent(pendingIntent);
        baseNotify.createNotify();
    }
}
