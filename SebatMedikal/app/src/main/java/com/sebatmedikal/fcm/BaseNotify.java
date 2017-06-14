package com.sebatmedikal.fcm;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.sebatmedikal.R;
import com.sebatmedikal.util.LogUtil;
import com.sebatmedikal.util.NullUtil;

/**
 * Created by orhan on 14.06.2017.
 */
public class BaseNotify {
    private int number = 0;
    private int id;
    private Context context;
    private String title;
    private String bigTitle;
    private String text;
    private String bigText;
    private String summaryText;
    private String ticker;
    private PendingIntent pendingIntent;
    private boolean autoClose = true;

    public void setNumber(int number) {
        this.number = number;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setBigTitle(String bigTitle) {
        this.bigTitle = bigTitle;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setBigText(String bigText) {
        this.bigText = bigText;
    }

    public void setSummaryText(String summaryText) {
        this.summaryText = summaryText;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public void setPendingIntent(PendingIntent pendingIntent) {
        this.pendingIntent = pendingIntent;
    }

    public void setAutoClose(boolean autoClose) {
        this.autoClose = autoClose;
    }

    public boolean validate() {
        if (NullUtil.isAnyNull(context, id, title, text, pendingIntent)) {
            LogUtil.logMessage(getClass(), "not validated");
            return false;
        }

        return true;
    }

    public boolean checkStyle() {
        if (NullUtil.isAnyNull(bigTitle, bigText)) {
            LogUtil.logMessage(getClass(), "not checked Style");
            return false;
        }

        if (NullUtil.isNull(summaryText)) {
            summaryText = "";
        }

        return true;
    }

    public void createNotify() {
        if (!validate()) {
            LogUtil.logMessage(getClass(), "baseNotify not validated");
            return;
        }

        final Bitmap picture = BitmapFactory.decodeResource(context.getResources(), R.mipmap.application_icon);

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.drawable.medical)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setLargeIcon(picture)
                .setContentIntent(pendingIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setAutoCancel(autoClose);

        if (NullUtil.isNotNull(ticker)) {
            builder.setTicker(ticker);
        }

        if (number > 0) {
            builder.setNumber(number);
        }

        if (checkStyle()) {
            builder.setStyle(new NotificationCompat.BigTextStyle()
                    .bigText(bigText)
                    .setBigContentTitle(bigTitle)
                    .setSummaryText(summaryText));
        }

        notify(builder.build());
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    private void notify(final Notification notification) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            nm.notify(id + "", 0, notification);
        } else {
            nm.notify((id + "").hashCode(), notification);
        }
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    public void cancel(int id) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            nm.cancel(id + "", 0);
        } else {
            nm.cancel((id + "").hashCode());
        }
    }
}


