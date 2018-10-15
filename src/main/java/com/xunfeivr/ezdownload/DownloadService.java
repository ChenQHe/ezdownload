package com.xunfeivr.ezdownload;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.xunfeivr.ezdownload.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by android studio.
 * company:讯飞幻境科技有限公司
 * author:ChenHe
 * Time:18-8-13 下午5:57
 * <p>
 * 下载服务
 */
public class DownloadService extends Service implements DownloadListener, IDownloadService {

    private DownloadTaskDispatcher mDownloadTaskDispatcher;

    /**
     * 下载监听器的集合  这样作可以保证在其他地方刷新进度
     */
    private final List<DownloadListener> mDownloadListenerList = new ArrayList<>();

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
        mDownloadTaskDispatcher.config(config);
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
        for (DownloadListener downloadListener : mDownloadListenerList) {
            if (downloadListener != null) {
                downloadListener.onStart(file, length);
            }
        }
    }

    @Override
    public void onProgress(DownloadFile file, String speed, long current, long total) {
        for (DownloadListener downloadListener : mDownloadListenerList) {
            if (downloadListener != null) {
                downloadListener.onProgress(file, speed, current, total);
            }
        }
    }

    @Override
    public void onError(DownloadFile file, int code, String msg) {
        for (DownloadListener downloadListener : mDownloadListenerList) {
            if (downloadListener != null) {
                downloadListener.onError(file, code, msg);
            }
        }
    }

    @Override
    public void onCompleted(DownloadFile file) {
        for (DownloadListener downloadListener : mDownloadListenerList) {
            if (downloadListener != null) {
                downloadListener.onCompleted(file);
            }
        }
    }

    @Override
    public void onCancel(DownloadFile file) {
        for (DownloadListener downloadListener : mDownloadListenerList) {
            if (downloadListener != null) {
                downloadListener.onCancel(file);
            }
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        LogUtil.e("移除所有监听者");
        mDownloadListenerList.clear();
        return super.onUnbind(intent);
    }
}
