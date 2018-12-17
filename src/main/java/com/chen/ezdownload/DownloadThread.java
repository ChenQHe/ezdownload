package com.chen.ezdownload;

import com.chen.ezdownload.db.BreakPointInfo;
import com.chen.ezdownload.util.LogUtil;
import com.chen.ezdownload.util.Util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by android studio.
 * company:Xunfei Magic Technology Co., Ltd.
 * author:ChenHe
 * Time:18-8-15 PM:4:41
 * <p>
 * a breakpoint download thread
 */
class DownloadThread extends Thread {

    private static final int DOWNLOAD_STATE_ING = 1;
    private static final int DOWNLOAD_STATE_INIT = 2;
    /**
     * Download configuration file
     */
    private DownloadConfig mDownloadConfig;
    /**
     * Breakpoint information for all download threads
     */
    private List<BreakPointInfo> mBreakPointInfoList;
    /**
     * Breakpoint information for this thread
     */
    private BreakPointInfo mBreakPointInfo;
    /**
     * Thread download callback
     */
    private DownloadThreadListener mDownloadListener;
    /**
     * Control stop
     */
    private boolean stop;
    /**
     * Downloaded url
     */
    private String mUrl;
    /**
     * Download saved file
     */
    private File mFile;
    /**
     * Download state
     */
    private int mDownloadState;

    /**
     * Construction method
     *
     * @param url              download link
     * @param file             Download location
     * @param id               Breakpoint id
     * @param list             Collection of all threads
     * @param downloadConfig   Download configuration
     * @param downloadListener Download callback
     */
    DownloadThread(String url, File file, int id, List<BreakPointInfo> list,
                   DownloadConfig downloadConfig, DownloadThreadListener downloadListener) {
        mBreakPointInfoList = list;
        mUrl = url;
        mFile = file;
        mDownloadState = DOWNLOAD_STATE_INIT;
        for (BreakPointInfo breakPointInfo : list) {
            if (breakPointInfo.id == id) {
                mBreakPointInfo = breakPointInfo;
            }
        }
        mDownloadConfig = downloadConfig;
        mDownloadListener = downloadListener;
    }

    /**
     * Cancel download
     */
    void cancel() {
        stop = true;
    }

    public boolean isRunning() {
        return mDownloadState != DOWNLOAD_STATE_INIT;
    }

    @Override
    public void run() {
        RandomAccessFile accessFile = null;
        BufferedInputStream bis = null;
        HttpURLConnection connection = null;
        try {
            //If the download has been completed, the direct callback is completed.
            if (mBreakPointInfo.offset >= mBreakPointInfo.length) {
                mDownloadListener.onCompleted(mBreakPointInfo);
                return;
            }
            //Create any access file
            accessFile = new RandomAccessFile(mFile, "rwd");
            //Move the position to the cursor
            accessFile.seek(mBreakPointInfo.offset);
            //The request header to the server indicates which piece of data needs to be requested ("Range", "bytes=offset-length")
            String range = "bytes=" + mBreakPointInfo.offset + "-" + mBreakPointInfo.length;
            LogUtil.e(range);
            //Create a connection
            URL url = new URL(mUrl);
            connection = (HttpURLConnection) url.openConnection();
            //Configuration
            Util.configConnection(connection, mDownloadConfig);
            //Add a breakpoint request
            connection.addRequestProperty(Util.RANGE, range);
            //Get callback frequency
            int rate = mDownloadConfig.getRate();
            //Connect to network
            connection.connect();
            //Record download status Downloading
            mDownloadState = DOWNLOAD_STATE_ING;
            int code = connection.getResponseCode();
            if (code == HttpURLConnection.HTTP_OK || code == HttpURLConnection.HTTP_PARTIAL) {
                //If the server has an error, return directly
                InputStream errorStream = connection.getErrorStream();
                if (errorStream != null) {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    int len;
                    byte[] buf = new byte[1024];
                    while ((len = errorStream.read(buf)) != -1) {
                        byteArrayOutputStream.write(buf, 0, len);
                    }
                    mDownloadListener.onError(mBreakPointInfo, HttpError.SERVER_NOT_RESPONSE, byteArrayOutputStream.toString("UTF-8"));
                    mDownloadState = DOWNLOAD_STATE_INIT;
                    errorStream.close();
                    byteArrayOutputStream.close();
                    return;
                }
                //Get the input stream
                InputStream is = connection.getInputStream();
                bis = new BufferedInputStream(is);
                mDownloadListener.onStart(mBreakPointInfo);
                int len;
                byte[] buf = new byte[512];
                int count = 0;//Record writes
                while ((len = bis.read(buf)) != -1) {
                    accessFile.write(buf, 0, len);//
                    mBreakPointInfo.offset = mBreakPointInfo.offset + len;//Moving cursor
                    mBreakPointInfo.currentDownloaded = mBreakPointInfo.currentDownloaded + len;//Record the current download length
                    count = count + 1;
                    if (count % rate == 0 || mBreakPointInfo.offset >= mBreakPointInfo.length) {
                        //Callback progress
                        mDownloadListener.onProgress(mBreakPointInfoList, mBreakPointInfo);
                        mBreakPointInfo.currentDownloaded = 0;
                        if (mBreakPointInfo.offset >= mBreakPointInfo.length) {
                            break;
                        }
                    }

                    if (stop) {//User canceled
                        mDownloadListener.onCancel(mBreakPointInfo);
                        break;
                    }
                }
                if (!stop) {//If not canceled
                    mDownloadListener.onCompleted(mBreakPointInfo);
                }
            } else {
                mDownloadListener.onError(mBreakPointInfo, code, connection.getResponseMessage());
            }
        } catch (IOException e) {
            mDownloadListener.onError(mBreakPointInfo, HttpError.SERVER_ERROR, e.toString());
        } finally {
            //No matter how you end up downloading the resource release and file saving
            mDownloadState = DOWNLOAD_STATE_INIT;
            if (accessFile != null) {
                try {
                    accessFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }

    }

}
