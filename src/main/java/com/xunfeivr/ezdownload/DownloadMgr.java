package com.xunfeivr.ezdownload;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.xunfeivr.ezdownload.util.LogUtil;

/**
 * Created by android studio.
 * company:讯飞幻境科技有限公司
 * author:ChenHe
 * Time:18-7-3 上午11:17
 * <p>
 * 管理整个下载服务  使用的是静态代理模式
 */
public class DownloadMgr implements IDownloadService {

    /**
     * 后台下载服务
     */
    private DownloadService mDownloadService;

    private ServiceConnection mServiceConnection;
    /**
     * 监听器
     */
    private DownloadListener mDownloadListener;

    private DownloadMgr() {
        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                LogUtil.e("服务启动成功!");
                mDownloadService = ((DownloadService.MyBinder) service).getService();
                addDownloadListener(mDownloadListener);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                LogUtil.e("服务启动失败!");
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
     * 这个应该在 MainActivity 的onCreate()中调用
     *
     * @param context          上下文
     * @param downloadListener 监听器
     */
    public void bindService(Activity context, DownloadListener downloadListener) {
        if (context == null) {
            return;
        }
        mDownloadListener = downloadListener;
        Intent intent = new Intent(context, DownloadService.class);
        context.startService(intent);
        context.bindService(intent, mServiceConnection, Context.BIND_WAIVE_PRIORITY);
    }

    /**
     * 这个应该在 MainActivity 的onDestroy()中调用
     *
     * @param context 上下文
     */
    public void unbindService(Activity context) {
        if (context != null) {
            context.unbindService(mServiceConnection);
            removeDownloadListener(mDownloadListener);
        }
    }

    public void config(DownloadConfig config) {
        if (mDownloadService != null) {
            mDownloadService.config(config);
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
            LogUtil.e("添加一个监听器");
            mDownloadService.addDownloadListener(downloadListener);
        }
    }

    @Override
    public void removeDownloadListener(DownloadListener downloadListener) {
        if (mDownloadService != null && downloadListener != null) {
            LogUtil.e("移除一个监听器");
            mDownloadService.removeDownloadListener(downloadListener);
        }
    }
}
