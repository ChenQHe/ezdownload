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

    /**
     * 下载任务
     */
    private DownloadTask mDownloadTask;

    /**
     * 下载监听器的集合  这样作可以保证在其他地方刷新进度
     */
    private final List<DownloadListener> mDownloadListenerList = new ArrayList<>();

    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    public class MyBinder extends Binder {
        public DownloadService getService() {
            return DownloadService.this;
        }
    }

    public void config(DownloadConfig config){
        mDownloadTask.config(config);
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
        if (mDownloadTask == null) {
            mDownloadTask = new DownloadTask(this);
        }
    }

    @Override
    public void download(DownloadFile file) {
        if (!mDownloadTask.isDownloading()) {
            mDownloadTask.startDownload(file, this);
        } else {
            LogUtil.e("正在下载中.......");
        }
    }

    @Override
    public void cancel() {
        if (mDownloadTask.isDownloading()) {
            mDownloadTask.cancel();
        } else {
            LogUtil.e("没有下载的任务.......");
        }
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
    public void onProgress(String speed, long current, long total) {
        for (DownloadListener downloadListener : mDownloadListenerList) {
            if (downloadListener != null) {
                downloadListener.onProgress(speed, current, total);
            }
        }
    }

    @Override
    public void onError(int code, String msg) {
        for (DownloadListener downloadListener : mDownloadListenerList) {
            if (downloadListener != null) {
                downloadListener.onError(code, msg);
            }
        }
    }

    @Override
    public void onCompleted(String filePath) {
        for (DownloadListener downloadListener : mDownloadListenerList) {
            if (downloadListener != null) {
                downloadListener.onCompleted(filePath);
            }
        }
    }

    @Override
    public void onCancel() {
        for (DownloadListener downloadListener : mDownloadListenerList) {
            if (downloadListener != null) {
                downloadListener.onCancel();
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
