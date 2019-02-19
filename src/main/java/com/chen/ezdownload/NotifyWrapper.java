package com.chen.ezdownload;

import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by android studio.
 * author:ChenHe
 */
class NotifyWrapper {
    private NotificationManagerCompat mManager;
    private Map<String, NotifyBuilderWrapper> mBuilderMap;
    private Context mContext;
    private AtomicInteger mSequenceGenerator;

    private final class NotifyBuilderWrapper {
        int mNotifyId;
        NotificationCompat.Builder mBuilder;

        NotifyBuilderWrapper(int notifyId, NotificationCompat.Builder builder) {
            mNotifyId = notifyId;
            mBuilder = builder;
        }
    }

    NotifyWrapper(Context context) {
        mContext = context;
        mManager = NotificationManagerCompat.from(context);
        mBuilderMap = new ConcurrentHashMap<>();
        mSequenceGenerator = new AtomicInteger();
        destroy = false;
    }

    private boolean destroy;

    void destroy() {
        destroy = true;
        mManager = null;
        mSequenceGenerator = null;
        mBuilderMap.clear();
        mBuilderMap = null;
    }

    void createNotify(String url, INotification iBuilder, String fileName, String message) {
        if (destroy) return;
        if (mBuilderMap.get(url) != null) {
            return;
        }
        NotificationCompat.Builder builder = iBuilder.getBuilder(mContext);
        builder.setContentTitle(fileName);
        builder.setContentText(message);
        int id = mSequenceGenerator.incrementAndGet();
        mManager.notify(id, builder.build());
        builder.setProgress(100, 0, false);
        NotifyBuilderWrapper wrapper = new NotifyBuilderWrapper(id, builder);
        mBuilderMap.put(url, wrapper);
    }

    void showProgress(String url, int progress, String message) {
        if (destroy) return;
        NotifyBuilderWrapper notifyWrapper = mBuilderMap.get(url);
        if (notifyWrapper == null) {
            return;
        }
        notifyWrapper.mBuilder.setContentText(message);
        mManager.notify(notifyWrapper.mNotifyId, notifyWrapper.mBuilder.build());
        notifyWrapper.mBuilder.setProgress(100, progress, false);
    }

    void updateMessage(String url, String message) {
        if (destroy) return;
        NotifyBuilderWrapper notifyWrapper = mBuilderMap.get(url);
        if (notifyWrapper == null) {
            return;
        }
        notifyWrapper.mBuilder.setContentText(message);
        mManager.notify(notifyWrapper.mNotifyId, notifyWrapper.mBuilder.build());
    }
}
