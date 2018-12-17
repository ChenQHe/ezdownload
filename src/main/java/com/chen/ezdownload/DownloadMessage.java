package com.chen.ezdownload;

/**
 * Created by android studio.
 * company:Xunfei Magic Technology Co., Ltd.
 * author:ChenHe
 * Time:18-7-3 PM:3:14
 * <p>
 * Callback message
 */
class DownloadMessage {
    /**
     * File length
     */
    long length;
    /**
     * Current download length
     */
    long current;
    /**
     * Downloaded file
     */
    DownloadFile file;
    /**
     * download speed
     */
    String speed;
    /**
     * error code
     */
    int code;
    /**
     * Error message
     */
    String error;
}
