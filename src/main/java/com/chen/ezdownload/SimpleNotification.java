package com.chen.ezdownload;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.v4.app.NotificationCompat;

/**
 * Created by android studio.
 * author:ChenHe
 */
public class SimpleNotification implements INotification {

    public String getChannelId() {
        return "easy-download";
    }

    public String getChannelName() {
        return "下载服务通知";
    }

    public String getChannelDescription() {
        return "简单的下载任务";
    }

    public @DrawableRes int getSmallerIcon() {
        return android.R.mipmap.sym_def_app_icon;
    }

    public String getTicker() {
        return "下载任务开始...";
    }

    public PendingIntent getIntent(Context context) {
        return null;
    }

    @Override
    public NotificationCompat.Builder getBuilder(Context context) {
        String channelId = getChannelId();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (nm != null) {
                NotificationChannel channel = new NotificationChannel(channelId, getChannelName(),
                        NotificationManager.IMPORTANCE_HIGH);
                channel.enableLights(false);
                channel.enableVibration(false);
                channel.setDescription(getChannelDescription());
                nm.createNotificationChannel(channel);
            }
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId);
        builder.setWhen(System.currentTimeMillis());
        builder.setTicker(getTicker());
        PendingIntent intent = getIntent(context);
        if (intent != null) {
            builder.setContentIntent(intent);
        }
        builder.setSmallIcon(getSmallerIcon());
        return builder;
    }
}
