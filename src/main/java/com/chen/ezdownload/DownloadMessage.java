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
    public long length;
    /**
     * Current download length
     */
    public long current;
    /**
     * Downloaded file
     */
    public DownloadFile file;
    /**
     * download speed
     */
    public String speed;
    /**
     * error code
     */
    public int code;
    /**
     * Error message
     */
    public String error;
}
