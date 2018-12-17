package com.chen.ezdownload;


/**
 * Created by android studio.
 * company:Xunfei Magic Technology Co., Ltd.
 * author:ChenHe
 * Time:18-7-3 Am:11:38
 * <p>
 * Entity class that needs to be downloaded
 */
public abstract class DownloadFile{

    /**
     * @return  download link
     */
    public abstract String getDownloadUrl();

    /**
     * @return  Download file name
     */
    public abstract String getFileName();

    /**
     * @return Download saved location directory
     */
    public abstract String getParent();

    /**
     * @return New instance
     */
    public abstract DownloadFile clone();
}
