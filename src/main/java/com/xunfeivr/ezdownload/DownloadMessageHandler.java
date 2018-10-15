package com.xunfeivr.ezdownload;

import android.os.Handler;
import android.os.Message;

/**
 * Created by android studio.
 * company:讯飞幻境科技有限公司
 * author:ChenHe
 * Time:18-8-17 上午11:36
 * <p>
 * 将子线程回调到UI线程  使用的是静态代理模式
 */
class DownloadMessageHandler implements DownloadListener, Handler.Callback {

    private static final int START = 0x11;
    private static final int DOWNLOADING = 0x12;
    private static final int ERROR = 0x13;
    private static final int CANCEL = 0x14;
    private static final int COMPLETE = 0x15;

    private DownloadListener mDownloadListener;

    private DownloadMessage mMessage;

    private Handler mHandler;

    DownloadMessageHandler(DownloadListener downloadListener) {
        mHandler = new Handler(this);
        mMessage = new DownloadMessage();
        mDownloadListener = downloadListener;
    }

    @Override
    public void onStart(DownloadFile file, long length) {
        mMessage.length = length;
        sendMessage(file, START);
    }

    @Override
    public void onProgress(DownloadFile file, String speed, long current, long length) {
        mMessage.speed = speed;
        mMessage.current = current;
        mMessage.length = length;
        sendMessage(file, DOWNLOADING);
    }

    @Override
    public void onError(DownloadFile file, int code, String msg) {
        mMessage.code = code;
        mMessage.error = msg;
        sendMessage(file, ERROR);
    }

    @Override
    public void onCompleted(DownloadFile file) {
        sendMessage(file, COMPLETE);
    }

    @Override
    public void onCancel(DownloadFile file) {
        sendMessage(file, CANCEL);
    }

    private void sendMessage(DownloadFile file, int state) {
        Message message = mHandler.obtainMessage();
        message.what = state;
        mMessage.file = file;
        message.obj = mMessage;
        mHandler.sendMessage(message);
    }

    @Override
    public boolean handleMessage(Message msg) {
        DownloadMessage obj = (DownloadMessage) msg.obj;
        switch (msg.what) {
            case START:
                mDownloadListener.onStart(obj.file, obj.length);
                break;
            case DOWNLOADING:
                mDownloadListener.onProgress(obj.file, obj.speed, obj.current, obj.length);
                break;
            case ERROR:
                mDownloadListener.onError(obj.file, obj.code, obj.error);
                break;
            case CANCEL:
                mDownloadListener.onCancel(obj.file);
                break;
            case COMPLETE:
                mDownloadListener.onCompleted(obj.file);
                break;
        }
        mHandler.removeMessages(msg.what, msg.obj);
        return true;
    }

}
