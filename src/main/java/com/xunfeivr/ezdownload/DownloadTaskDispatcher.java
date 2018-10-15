package com.xunfeivr.ezdownload;

import android.content.Context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by android studio.
 * company:讯飞幻境科技有限公司
 * author:ChenHe
 * Time:18-8-17 下午6:54
 * <p>
 * 管理  downloadTask
 */
class DownloadTaskDispatcher {

    private ThreadPoolExecutor mExecutors;
    private Map<String, DownloadTask> mDownloadTaskMap;
    private Context mContext;
    private DownloadListener mDownloadListener;
    private DownloadConfig mDownloadConfig;

    DownloadTaskDispatcher(Context context, DownloadListener downloadListener) {
        mContext = context;
        mExecutors = new ThreadPoolExecutor(2, 5, 2, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());
        mDownloadListener = downloadListener;
        mDownloadTaskMap = new ConcurrentHashMap<>();
    }

    void config(DownloadConfig downloadConfig) {
        mDownloadConfig = downloadConfig;
    }

    void addTask(DownloadFile downloadFile) {
        String url = downloadFile.getDownloadUrl();
        DownloadTask downloadTask = mDownloadTaskMap.get(url);
        if (downloadTask != null) {
            if (downloadTask.isDownloading()){
                downloadTask.cancel();
            }
            mDownloadTaskMap.remove(url);
            mExecutors.remove(downloadTask);
        }
        downloadTask = new DownloadTask(mContext, downloadFile, mDownloadListener);
        if (mDownloadConfig != null) {
            downloadTask.config(mDownloadConfig);
        }
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

    public void shutDown() {
        if (!mExecutors.isShutdown()) {
            mExecutors.shutdown();
            mExecutors = null;
        }
        mDownloadTaskMap.clear();
        mDownloadTaskMap = null;
    }
}
