package com.chen.ezdownload;

/**
 * Created by android studio.
 * company:Xunfei Magic Technology Co., Ltd.
 * author:ChenHe
 * Time:18-8-17 PM:2:51
 * a unified interface for download
 */
interface IDownloadService {

    /**
     * Download this file
     *
     * @param file Entity class that needs to be downloaded
     */
    void download(DownloadFile file);


    /**
     * Cancel download can be used for suspension
     */
    void cancel(String url);

    /**
     * Add a listener to call back anywhere
     *
     * @param downloadListener Download progress listener
     */
    void addDownloadListener(DownloadListener downloadListener);

    /**
     * Remove a listener
     *
     * @param downloadListener Download progress listener
     */
    void removeDownloadListener(DownloadListener downloadListener);
}
