package com.chen.ezdownload;

import android.content.Context;
import android.support.v4.app.NotificationCompat;

/**
 * Created by android studio.
 * author:ChenHe
 */
public interface INotification {
    NotificationCompat.Builder getBuilder(Context context);
}
