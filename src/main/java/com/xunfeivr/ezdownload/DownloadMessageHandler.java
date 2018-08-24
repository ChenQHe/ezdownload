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
        mMessage.file = file;
        mMessage.length = length;
        sendMessage(START);
    }

    @Override
    public void onProgress(String speed, long current, long length) {
        mMessage.speed = speed;
        mMessage.current = current;
        mMessage.length = length;
        sendMessage(DOWNLOADING);
    }

    @Override
    public void onError(int code, String msg) {
        mMessage.code = code;
        mMessage.error = msg;
        sendMessage(ERROR);
    }

    @Override
    public void onCompleted(String filePath) {
        mMessage.filePath = filePath;
        sendMessage(COMPLETE);
    }

    @Override
    public void onCancel() {
        sendMessage(CANCEL);
    }

    private void sendMessage(int state) {
        Message message = mHandler.obtainMessage();
        message.what = state;
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
                mDownloadListener.onProgress(obj.speed, obj.current, obj.length);
                break;
            case ERROR:
                mDownloadListener.onError(obj.code, obj.error);
                break;
            case CANCEL:
                mDownloadListener.onCancel();
                break;
            case COMPLETE:
                mDownloadListener.onCompleted(obj.filePath);
                break;
        }
        mHandler.removeMessages(msg.what, msg.obj);
        return false;
    }

}
