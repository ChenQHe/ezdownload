package com.chen.ezdownload;

import android.content.Context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by android studio.
 * company:Xunfei Magic Technology Co., Ltd.
 * author:ChenHe
 * Time:18-8-17 PM:6:54
 * <p>
 * manager downloadTask
 */
class DownloadTaskDispatcher {

    private ThreadPoolExecutor mExecutors;
    private Map<String, DownloadTask> mDownloadTaskMap;
    private Context mContext;
    private DownloadListener mDownloadListener;
    private DownloadConfig mDownloadConfig;

    DownloadTaskDispatcher(Context context, DownloadListener downloadListener) {
        mContext = context;
        mExecutors = new ThreadPoolExecutor(3, 3, 3, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());
        mDownloadListener = downloadListener;
        mDownloadTaskMap = new ConcurrentHashMap<>();
    }

    private DownloadConfig getDownloadConfig() {
        if (mDownloadConfig == null) {
            mDownloadConfig = new DownloadConfig();
        }
        return mDownloadConfig;
    }

    void config(DownloadConfig downloadConfig) {
        if (mDownloadConfig != null) {
            mDownloadConfig = null;
        }
        mDownloadConfig = downloadConfig;
    }

    void addTask(DownloadFile downloadFile) {
        String url = downloadFile.getDownloadUrl();
        DownloadTask downloadTask = mDownloadTaskMap.get(url);
        if (downloadTask != null) {
            if (downloadTask.isDownloading()) {
                downloadTask.cancel();
            }
            mDownloadTaskMap.remove(url);
            mExecutors.remove(downloadTask);
        }
        downloadTask = new DownloadTask(mContext, downloadFile, mDownloadListener);
        downloadTask.config(getDownloadConfig());
        mDownloadTaskMap.put(url, downloadTask);
        mExecutors.execute(downloadTask);
    }

    void cancel(String url) {
        DownloadTask downloadTask = mDownloadTaskMap.remove(url);
        if (downloadTask != null) {
            mExecutors.remove(downloadTask);
            downloadTask.cancel();
        }
    }

    void shutDown() {
        if (!mExecutors.isShutdown()) {
            mExecutors.shutdown();
            mExecutors = null;
        }
        mDownloadTaskMap.clear();
        mDownloadTaskMap = null;
    }
}
