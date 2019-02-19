package com.chen.ezdownload;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import com.chen.ezdownload.util.LogUtil;
import com.chen.ezdownload.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by android studio.
 * company:Xunfei Magic Technology Co., Ltd.
 * author:ChenHe
 * Time:18-8-13 PM:5:57
 * <p>
 * Download service
 */
public class DownloadService extends Service implements DownloadListener, IDownloadService {

    private DownloadTaskDispatcher mDownloadTaskDispatcher;

    private NotifyWrapper mNotifyWrapper;
    /**
     * Download the collection of listeners. This will ensure that the progress is refreshed elsewhere.
     */
    private final List<DownloadListener> mDownloadListenerList = new ArrayList<>();

    private INotification mNotification;

    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    class MyBinder extends Binder {
        DownloadService getService() {
            return DownloadService.this;
        }
    }

    public void config(DownloadConfig config) {
        if (config == null) {
            return;
        }
        mDownloadTaskDispatcher.config(config);
    }
    public void noNeedNotification() {
        mNotifyWrapper.destroy();
    }

    public void setNotification(INotification notification) {
        if (mNotification != null) {
            mNotification = null;
        }
        mNotification = notification;
    }

    private INotification getNotification() {
        if (mNotification == null) {
            mNotification = new SimpleNotification();
        }
        return mNotification;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        init();
        return START_STICKY;
    }

    private void init() {
        if (mDownloadTaskDispatcher == null) {
            mDownloadTaskDispatcher = new DownloadTaskDispatcher(this, this);
        }
        if (mNotifyWrapper == null) {
            mNotifyWrapper = new NotifyWrapper(this);
        }
    }

    @Override
    public void download(DownloadFile file) {
        mDownloadTaskDispatcher.addTask(file);
    }

    @Override
    public void cancel(String url) {
        mDownloadTaskDispatcher.cancel(url);
    }

    @Override
    public void addDownloadListener(DownloadListener listener) {
        mDownloadListenerList.add(listener);
    }

    @Override
    public void removeDownloadListener(DownloadListener listener) {
        mDownloadListenerList.remove(listener);
    }


    @Override
    public void onStart(DownloadFile file, long length) {
        mNotifyWrapper.createNotify(file.getDownloadUrl(), getNotification(), file.getFileName(),
                "Download preparation..." + Util.humanReadableBytes(length));
        for (DownloadListener downloadListener : mDownloadListenerList) {
            if (downloadListener != null) {
                downloadListener.onStart(file, length);
            }
        }
    }

    @Override
    public void onProgress(DownloadFile file, String speed, long current, long total) {
        int progress = (int) (1.0f * current / total);
        mNotifyWrapper.showProgress(file.getDownloadUrl(), progress,
                "Downloading:" + Util.humanReadableBytes(current) + "/" + Util.humanReadableBytes(total)
                        + "---" + speed);
        for (DownloadListener downloadListener : mDownloadListenerList) {
            if (downloadListener != null) {
                downloadListener.onProgress(file, speed, current, total);
            }
        }
    }

    @Override
    public void onError(DownloadFile file, int code, String msg) {
        mNotifyWrapper.updateMessage(file.getDownloadUrl(), "Download error：" + msg);
        for (DownloadListener downloadListener : mDownloadListenerList) {
            if (downloadListener != null) {
                downloadListener.onError(file, code, msg);
            }
        }
    }

    @Override
    public void onCompleted(DownloadFile file) {
        mNotifyWrapper.updateMessage(file.getDownloadUrl(), "Download completed");
        for (DownloadListener downloadListener : mDownloadListenerList) {
            if (downloadListener != null) {
                downloadListener.onCompleted(file);
            }
        }
    }

    @Override
    public void onCancel(DownloadFile file) {
        mNotifyWrapper.updateMessage(file.getDownloadUrl(), "Download pause");
        for (DownloadListener downloadListener : mDownloadListenerList) {
            if (downloadListener != null) {
                downloadListener.onCancel(file);
            }
        }
    }


    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        LogUtil.e("Service has been killed！");
        mDownloadTaskDispatcher.shutDown();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        LogUtil.e("Remove all listeners");
        mDownloadListenerList.clear();
        return super.onUnbind(intent);
    }
}
