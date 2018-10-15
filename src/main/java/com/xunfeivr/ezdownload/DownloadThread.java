package com.xunfeivr.ezdownload;

import com.xunfeivr.ezdownload.db.BreakPointInfo;
import com.xunfeivr.ezdownload.util.LogUtil;
import com.xunfeivr.ezdownload.util.Util;

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
 * company:讯飞幻境科技有限公司
 * author:ChenHe
 * Time:18-8-15 下午4:41
 * <p>
 * 一个断点的下载线程
 */
class DownloadThread extends Thread {

    private static final int DOWNLOAD_STATE_ING = 1;
    private static final int DOWNLOAD_STATE_INIT = 2;
    /**
     * 下载配置文件
     */
    private DownloadConfig mDownloadConfig;
    /**
     * 所有的下载线程的断点信息 回调进度用
     */
    private List<BreakPointInfo> mBreakPointInfoList;
    /**
     * 该线程的断点信息
     */
    private BreakPointInfo mBreakPointInfo;
    /**
     * 线程下载回调
     */
    private DownloadThreadListener mDownloadListener;
    /**
     * 控制停止
     */
    private boolean stop;
    /**
     * 下载的url
     */
    private String mUrl;
    /**
     * 下载保存的文件
     */
    private File mFile;

    private int mDownloadState;

    /**
     * 构造方法
     *
     * @param url              下载地址
     * @param file             下载位置
     * @param id               断点id
     * @param list             所有线程的集合
     * @param downloadConfig   下载配置
     * @param downloadListener 下载回调
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
     * 取消下载
     */
    void cancel() {
        stop = true;
    }

    boolean isRunning() {
        return mDownloadState != DOWNLOAD_STATE_INIT;
    }

    @Override
    public void run() {
        RandomAccessFile accessFile = null;
        BufferedInputStream bis = null;
        HttpURLConnection connection = null;
        try {
            //如果已经下载完 直接回调完成
            if (mBreakPointInfo.offset >= mBreakPointInfo.length) {
                mDownloadListener.onCompleted(mBreakPointInfo);
                return;
            }
            //创建任意访问文件
            accessFile = new RandomAccessFile(mFile, "rwd");
            //将位置移到游标处
            accessFile.seek(mBreakPointInfo.offset);
            //给服务器的请求头  里面表示需要请求哪段数据 ("Range","bytes=offset-length")
            String range = "bytes=" + mBreakPointInfo.offset + "-" + mBreakPointInfo.length;
            LogUtil.e(range);
            //创建连接
            URL url = new URL(mUrl);
            connection = (HttpURLConnection) url.openConnection();
            //配置
            Util.configConnection(connection, mDownloadConfig);
            //添加断点请求
            connection.addRequestProperty(Util.RANGE, range);
            //获取回调频率
            int rate = mDownloadConfig.getRate();
            //连接net
            connection.connect();
            //记录下载状态 正在下载
            mDownloadState = DOWNLOAD_STATE_ING;
            int code = connection.getResponseCode();
            if (code == HttpURLConnection.HTTP_OK || code == HttpURLConnection.HTTP_PARTIAL) {
                //如果服务器有错误 直接返回
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
                //获取输入流
                InputStream is = connection.getInputStream();
                bis = new BufferedInputStream(is);
                mDownloadListener.onStart(mBreakPointInfo);
                int len;
                byte[] buf = new byte[512];
                int count = 0;//记录写入次数
                while ((len = bis.read(buf)) != -1) {
                    accessFile.write(buf, 0, len);//
                    mBreakPointInfo.offset = mBreakPointInfo.offset + len;//移动游标
                    mBreakPointInfo.currentDownloaded = mBreakPointInfo.currentDownloaded + len;//记录当前下载长度
                    count = count + 1;
                    if (count % rate == 0 || mBreakPointInfo.offset >= mBreakPointInfo.length) {
                        //回调进度
                        mDownloadListener.onProgress(mBreakPointInfoList, mBreakPointInfo);
                        mBreakPointInfo.currentDownloaded = 0;
                        if (mBreakPointInfo.offset >= mBreakPointInfo.length) {
                            break;
                        }
                    }

                    if (stop) {//用户取消
                        mDownloadListener.onCancel(mBreakPointInfo);
                        break;
                    }
                }
                if (!stop) {//如果没有取消
                    mDownloadListener.onCompleted(mBreakPointInfo);
                }
            } else {
                mDownloadListener.onError(mBreakPointInfo, code, connection.getResponseMessage());
            }
        } catch (IOException e) {
            mDownloadListener.onError(mBreakPointInfo, HttpError.SERVER_ERROR, e.toString());
        } finally {
            //不管如何 最终都要下载完成 资源释放和文件保存
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
