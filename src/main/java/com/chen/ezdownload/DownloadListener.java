package com.chen.ezdownload;

/**
 * Created by android studio.
 * company:Xunfei Magic Technology Co., Ltd.
 * author:ChenHe
 * Time:18-8-16 PM:5:16
 * <p>
 * a callback interface for download progress
 */
public interface DownloadListener {
    /**
     * Callback when the file starts downloading
     *
     * @param file   file
     * @param length fileLength
     */
    void onStart(DownloadFile file, long length);

    /**
     * Download progress
     *
     * @param speed   download speed
     * @param current Download progress
     * @param length  Total length
     */
    void onProgress(DownloadFile file, String speed, long current, long length);

    /**
     * Callback when downloading errors
     *
     * @param code error code
     * @param msg  error message
     */
    void onError(DownloadFile file, int code, String msg);

    /**
     * File download succeeded
     */

    void onCompleted(DownloadFile file);

    /**
     * Download Canceled
     */
    void onCancel(DownloadFile file);
}
