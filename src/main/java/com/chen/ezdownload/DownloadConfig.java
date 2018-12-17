package com.chen.ezdownload;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by android studio.
 * company:Xunfei Magic Technology Co., Ltd.
 * author:ChenHe
 * Time:18-8-14 AM:10:50
 * <p>
 * Download configuration file
 */
public class DownloadConfig {

    public final static String GET = "GET";
    public final static String POST = "POST";
    public final static String DELETE = "DELETE";
    public final static String PUT = "PUT";
    /**
     * Request method default get
     */
    private String mMethod = GET;
    /**
     * Connection timeout default 15s
     */
    private int mTimeOut = 15 * 1000;
    /**
     * Read timeout default 15s
     */
    private int mReadOut = 15 * 1000;
    /**
     * Refresh rate defaults every 80 writes to refresh the progress
     */
    private int rate = 100;

    /**
     * Number of threads
     */
    private int threadNum = 2;

    public int getThreadNum() {
        return threadNum;
    }

    public DownloadConfig setThreadNum(int threadNum) {
        this.threadNum = threadNum;
        return this;
    }

    /**
     * Request parameter
     */
    private final Map<String, String> mRequestParams = new HashMap<>();

    public int getRate() {
        return rate;
    }


    public DownloadConfig setRate(int rate) {
        this.rate = rate;
        return this;
    }

    public String getMethod() {
        return mMethod;
    }


    public DownloadConfig setMethod(String method) {
        mMethod = method;
        return this;
    }

    public int getTimeOut() {
        return mTimeOut;
    }


    public DownloadConfig setTimeOut(int timeOut) {
        mTimeOut = timeOut;
        return this;
    }

    public int getReadOut() {
        return mReadOut;
    }

    public DownloadConfig setReadOut(int readOut) {
        mReadOut = readOut;
        return this;
    }

    public Map<String, String> getRequestParams() {
        return mRequestParams;
    }

    public DownloadConfig addRequestParams(String key, String value) {
        mRequestParams.put(key, value);
        return this;
    }
}
