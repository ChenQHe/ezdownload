package com.xunfeivr.ezdownload;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by android studio.
 * company:讯飞幻境科技有限公司
 * author:ChenHe
 * Time:18-8-14 上午10:50
 * <p>
 * 下载配置文件
 */
public class DownloadConfig {

    public final static String GET = "GET";
    public final static String POST = "POST";
    public final static String DELETE = "DELETE";
    public final static String PUT = "PUT";
    /**
     * 请求方式  默认get
     */
    private String mMethod = GET;
    /**
     * 连接超时  默认 15s
     */
    private int mTimeOut = 15 * 1000;
    /**
     * 读取超时 默认 15s
     */
    private int mReadOut = 15 * 1000;
    /**
     * 刷新率 默认每 80次写入 刷新一次进度
     */
    private int rate = 80;

    /**
     * 线程个数
     */
    private int threadNum = 3;

    public int getThreadNum() {
        return threadNum;
    }

    public DownloadConfig setThreadNum(int threadNum) {
        this.threadNum = threadNum;
        return this;
    }

    /**
     * 请求参数
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
