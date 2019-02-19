package com.chen.ezdownload;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.chen.ezdownload.util.LogUtil;

/**
 * Created by android studio.
 * company:Xunfei Magic Technology Co., Ltd.
 * author:ChenHe
 * Time:18-7-3 AM:11:17
 * <p>
 * Manage the entire download service using static proxy mode
 */
public class DownloadMgr implements IDownloadService {
    /**
     * Background download service
     */
    private DownloadService mDownloadService;

    private ServiceConnection mServiceConnection;
    /**
     * Listener for link service completion or not
     */
    private IBindListener mIBindListener;

    private DownloadMgr() {
        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                LogUtil.e("Service started successfully!");
                mDownloadService = ((DownloadService.MyBinder) service).getService();
                if (mIBindListener != null) {
                    mIBindListener.onConnectedServer();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                LogUtil.e("Service startup failed!");
            }
        };
    }

    public static DownloadMgr getInstance() {
        return SingleHolder.downloadMgr;
    }

    private final static class SingleHolder {
        private static final DownloadMgr downloadMgr = new DownloadMgr();
    }

    /**
     * This should be called in the onCreate() of MainActivity
     *
     * @param context       context
     * @param iBindListener listener
     */
    public void bindService(Activity context, IBindListener iBindListener) {
        if (context == null) {
            return;
        }
        mIBindListener = iBindListener;
        Intent intent = new Intent(context, DownloadService.class);
        context.startService(intent);
        context.bindService(intent, mServiceConnection, Context.BIND_WAIVE_PRIORITY);
    }

    /**
     * This should be called in the OnDestroy() of MainActivity
     *
     * @param context context
     */
    public void unbindService(Activity context) {
        if (context != null) {
            context.unbindService(mServiceConnection);
        }
    }

    public void config(DownloadConfig config) {
        if (mDownloadService != null) {
            mDownloadService.config(config);
        }
    }

    public void setNotification(INotification notification) {
        if (mDownloadService != null) {
            mDownloadService.setNotification(notification);
        }
    }
    public void noNeedNotification() {
        if (mDownloadService != null) {
            mDownloadService.noNeedNotification();
        }
    }
    @Override
    public void download(DownloadFile file) {
        if (mDownloadService != null && file != null) {
            mDownloadService.download(file);
        }
    }

    @Override
    public void cancel(String url) {
        if (mDownloadService != null) {
            mDownloadService.cancel(url);
        }
    }

    @Override
    public void addDownloadListener(DownloadListener downloadListener) {
        if (mDownloadService != null && downloadListener != null) {
            LogUtil.e("Add a listener");
            mDownloadService.addDownloadListener(downloadListener);
        }
    }

    @Override
    public void removeDownloadListener(DownloadListener downloadListener) {
        if (mDownloadService != null && downloadListener != null) {
            LogUtil.e("Remove a listener");
            mDownloadService.removeDownloadListener(downloadListener);
        }
    }
}
