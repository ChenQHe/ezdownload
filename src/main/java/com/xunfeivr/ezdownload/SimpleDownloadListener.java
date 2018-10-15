package com.xunfeivr.ezdownload;

/**
 * Created by android studio.
 * author:ChenHe
 */
public abstract class SimpleDownloadListener implements DownloadListener {
    @Override
    public void onStart(DownloadFile file, long length) {

    }

    @Override
    public void onProgress(DownloadFile file, String speed, long current, long length) {

    }

    @Override
    public void onCancel(DownloadFile file) {

    }
}
