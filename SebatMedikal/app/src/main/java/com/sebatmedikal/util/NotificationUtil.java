package com.sebatmedikal.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.app.RemoteInput;
import android.support.v4.app.TaskStackBuilder;

import com.sebatmedikal.R;
import com.sebatmedikal.activity.BaseActivity;
import com.sebatmedikal.activity.NotificationActivity;
import com.sebatmedikal.activity.OperationsActivity;

/**
 * Created by orhan on 8.06.2017.
 */
public class NotificationUtil {
    public static void test_SimpleNotification(Context context, int notificationID, String title, String content) {
        LogUtil.logMessage(NotificationUtil.class, "test_SimpleNotification called");
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.envelope)
                        .setContentTitle(title)
                        .setContentText(content);
// Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, NotificationActivity.class);

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(BaseActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(notificationID, mBuilder.build());
    }

    public static void test_ExpandedLayoutNotification(Context context) {
        LogUtil.logMessage(NotificationUtil.class, "test_ExpandedLayoutNotification called");
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.envelope)
                .setContentTitle("Event tracker")
                .setContentText("Events received");
        NotificationCompat.InboxStyle inboxStyle =
                new NotificationCompat.InboxStyle();
        String[] events = new String[6];
// Sets a title for the Inbox in expanded layout
        inboxStyle.setBigContentTitle("Event tracker details:");
// Moves events into the expanded layout
        for (int i = 0; i < events.length; i++) {

            inboxStyle.addLine(events[i]);
        }
// Moves the expanded layout object into the notification object.
        mBuilder.setStyle(inboxStyle);
    }

    public static void test_InlineReplyNotification(Context context, int notificationID) {
        LogUtil.logMessage(NotificationUtil.class, "test_InlineReplyNotification called");

        Intent intent = new Intent(context, NotificationActivity.class);

        PendingIntent replyPendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        // Key for the string that's delivered in the action's intent.
        String KEY_TEXT_REPLY = "key_text_reply";
        String replyLabel = context.getResources().getString(R.string.app_name);
        RemoteInput remoteInput = new RemoteInput.Builder(KEY_TEXT_REPLY)
                .setLabel(replyLabel)
                .build();
        // Create the reply action and add the remote input.
        Notification.Action action =
                new Notification.Action.Builder(R.drawable.envelope,
                        context.getString(R.string.messages), replyPendingIntent)
                        .addRemoteInput(remoteInput)
                        .build();
//
        // Build the notification and add the action.
        Notification newMessageNotification =
                new Notification.Builder(context)
                        .setSmallIcon(R.drawable.envelope)
                        .setContentTitle("setContentTitle")
                        .setContentText("setContentText")
                        .addAction(action).build();

// Issue the notification.
//        NotificationManager notificationManager =
//                NotificationManager.from(context);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(notificationID, newMessageNotification);
    }

    public static void sendNotification(Context context, int NOTIFICATION_ID, String msg) {
        NotificationManager mNotificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, OperationsActivity.class), 0);

        android.support.v4.app.NotificationCompat.Builder mBuilder =
                new android.support.v7.app.NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.envelope)
                        .setContentTitle("PubNub GCM Notification")
                        .setStyle(new android.support.v7.app.NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
