package com.xunfeivr.ezdownload;

import android.content.Context;
import android.os.Build;

import com.xunfeivr.ezdownload.db.BreakPointInfo;
import com.xunfeivr.ezdownload.db.DBHelper;
import com.xunfeivr.ezdownload.db.TaskInfo;
import com.xunfeivr.ezdownload.util.LogUtil;
import com.xunfeivr.ezdownload.util.Util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by android studio.
 * company:讯飞幻境科技有限公司
 * author:ChenHe
 * Time:18-8-13 下午6:37
 * <p>
 * 下载的调度者 控制整体的下载
 */
class DownloadTask implements Runnable, DownloadThreadListener {
    /**
     * 下载配置
     */
    private DownloadConfig mDownloadConfig;
    /**
     * 需要下载的文件
     */
    private DownloadFile mDownloadFile;
    /**
     * 下载进度回调 直接回调到 主线程
     */
    private DownloadMessageHandler mDownloadListener;
    /**
     * 记录下载的线程
     */
    private final Map<Integer, DownloadThread> mDownloadThreadMap = new ConcurrentHashMap<>();
    /**
     * 任务id  从数据库获取
     */
    private int id;
    /**
     * 这个任务的总长度
     */
    private long mTotalLength;
    /**
     * 上一次进度回到的时间点
     */
    private long mLastTime;
    /**
     * 任务启动的时间 这个是参考系  所有的时间都要减去这个时间 进行变换
     */
    private long mStartTime;
    /**
     * 数据库操作类
     */
    private DBHelper mDBHelper;

    DownloadTask(Context context) {
        mDBHelper = DBHelper.getInstance(context);
    }

    /**
     * 启动一个下载任务
     * @param file  下载的文件
     * @param downloadListener 下载回调
     */
    public void startDownload(DownloadFile file, DownloadListener downloadListener) {
        mDownloadFile = file;
        mDownloadListener = new DownloadMessageHandler(downloadListener);
        mDownloadThreadMap.clear();
        mLastTime = 0;
        mStartTime = System.currentTimeMillis();
        new Thread(this).start();
    }

    public void config(DownloadConfig config) {
        mDownloadConfig = config;
    }

    /**
     * @return 返回一个下载配置  防止用户没有设置 给个默认的
     */
    private DownloadConfig getDownloadConfig() {
        if (mDownloadConfig == null) {
            mDownloadConfig = new DownloadConfig();
        }
        return mDownloadConfig;
    }

    /**
     * 取消下载任务
     */
    public void cancel() {
        for (DownloadThread downloadThread : mDownloadThreadMap.values()) {
            if (downloadThread != null) {
                downloadThread.cancel();
            }
        }
    }

    /**
     * @return 是否正在下载
     */
    public boolean isDownloading() {
        boolean isDownloading = false;
        for (DownloadThread downloadThread : mDownloadThreadMap.values()) {
            if (downloadThread != null) {
                if (downloadThread.isDownloading()) {
                    isDownloading = true;
                    break;
                }
            }
        }
        return isDownloading;
    }

    @Override
    public void run() {
        try {
            //创建下载文件保存的位置
            File file = createFile(mDownloadFile.parent, mDownloadFile.fileName);
            if (file == null) {
                mDownloadListener.onError(300, "文件无法创建");
                return;
            }
            //获取一个下载连接的配置文件
            DownloadConfig downloadConfig = getDownloadConfig();
            //如果是已经在数据库保存过的，这样的任务可以继续断点下载
            if (startOldWork(file, downloadConfig)) {
                return;
            }
            //如果数据库中没有 则重头创建一个新任务 并插入数据库
            startNewWork(file, downloadConfig);
        } catch (IOException e) {
            mDownloadListener.onError(303, e.getMessage());
        }

    }

    /**
     * 开启一个新任务
     *
     * @param file           保存的文件
     * @param downloadConfig 下载的配置文件
     * @throws IOException url异常 文件异常
     */
    private void startNewWork(File file, DownloadConfig downloadConfig) throws IOException {
        if (file.exists() && file.delete()) {
            LogUtil.e("文件重新下载...");
        }
        if (!file.createNewFile()) {
            mDownloadListener.onError(300, "文件无法创建");
            return;
        }
        //构造URL
        URL url = new URL(mDownloadFile.url);
        //打一个urlConnection连接
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        //配置连接
        Util.configConnection(connection, downloadConfig);
        //开启网络连接
        connection.connect();
        //获取返回码
        int code = connection.getResponseCode();
        //200成功 206 断点
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
                mDownloadListener.onError(404, byteArrayOutputStream.toString("UTF-8"));
                connection.disconnect();
                return;
            }
            //只获取服务器端文件的长度
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mTotalLength = connection.getContentLengthLong();
            } else {
                mTotalLength = connection.getContentLength();
            }
            //断开连接 可以分配任务了
            connection.disconnect();
            //初始化一个任意访问文件
            RandomAccessFile accessFile = new RandomAccessFile(file, "rw");
            accessFile.setLength(mTotalLength);
            accessFile.close();
            //创建任务信息 保存到数据库
            TaskInfo taskInfo = new TaskInfo();
            taskInfo.setFileName(mDownloadFile.fileName);
            taskInfo.setParent(mDownloadFile.parent);
            taskInfo.setUrl(mDownloadFile.url);
            taskInfo.setLength(mTotalLength);
            //数据库返回一个id
            id = mDBHelper.saveTaskInfo(taskInfo);
            taskInfo.setId(id);
            //获取下载线程的个数
            int threadNum = downloadConfig.getThreadNum();
            //将长度分块 每块加1防止总数据丢失
            long block = mTotalLength / threadNum + 1;
            mDownloadListener.onStart(mDownloadFile, mTotalLength);
            List<BreakPointInfo> list = new ArrayList<>();
            for (int i = 0; i < threadNum; i++) {
                //创建断点信息 保存到数据库
                BreakPointInfo breakPointInfo = new BreakPointInfo();
                //id是本任务的id 每个断点都保存一份 方便以后找到这个任务的所有断点
                breakPointInfo.taskId = id;
                //设置断点的起始位置
                breakPointInfo.start = block * i;
                //设置下载游标 跟着下载进度向前走动
                breakPointInfo.offset = breakPointInfo.start;
                //设置断点的长度
                if (i == threadNum - 1) {
                    breakPointInfo.length = mTotalLength;
                } else {
                    breakPointInfo.length = (i + 1) * block - 1;
                }
                //保存到数据库
                breakPointInfo.id = mDBHelper.saveBreakPointInfo(breakPointInfo);

                list.add(breakPointInfo);
                //创建任务
                DownloadThread downloadThread = new DownloadThread(mDownloadFile.url, file,
                        breakPointInfo.id, list, downloadConfig, this);
                //保存到内存一份
                mDownloadThreadMap.put(breakPointInfo.id, downloadThread);
                //开始任务
                downloadThread.start();
            }
        } else {
            mDownloadListener.onError(code, connection.getResponseMessage());
        }
    }

    /**
     * 开启一个老任务
     *
     * @param file           保存的文件
     * @param downloadConfig 下载的配置文件
     * @return true 老任务已经处理 false 老任务不能处理
     */
    private boolean startOldWork(File file, DownloadConfig downloadConfig) {
        //从数据库中读取这个url保存的任务信息
        TaskInfo taskInfo = mDBHelper.getTaskInfo(mDownloadFile.url);
        //如果有
        if (taskInfo != null) {
            if (!file.exists()) {
                mDBHelper.deleteTaskInfo(taskInfo.getId());
                return false;
            }
            id = taskInfo.getId();
            mTotalLength = taskInfo.getLength();
            //获取断点信息 可能没有
            List<BreakPointInfo> list = taskInfo.getBreakPointInfoList();
            if (list != null) {
                mDownloadListener.onStart(mDownloadFile, mTotalLength);
                for (BreakPointInfo breakPointInfo : list) {
                    //直接分配任务
                    DownloadThread downloadThread = new DownloadThread(taskInfo.getUrl(), file,
                            breakPointInfo.id, list, downloadConfig, this);
                    mDownloadThreadMap.put(breakPointInfo.id, downloadThread);
                    downloadThread.start();
                }
                return true;
            }
        }
        return false;
    }


    /**
     * 创建一个文件
     *
     * @param parent 目录
     * @param name   文件名
     * @return File
     */
    private File createFile(String parent, String name) {
        File file = new File(parent);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                return null;
            }
        }
        //打开保存文件 或者 创建一个新的
        return new File(parent, name);
    }

    @Override
    public void onStart(BreakPointInfo info) {

    }


    @Override
    public void onProgress(List<BreakPointInfo> infoList, BreakPointInfo info) {
        long totalDownloaded = 0;
        long currentDownloaded = 0;
        for (BreakPointInfo breakPointInfo : infoList) {
            totalDownloaded = breakPointInfo.downloadedSize() + totalDownloaded;
            currentDownloaded = currentDownloaded + breakPointInfo.currentDownloaded;
        }
        long currentTime = System.currentTimeMillis() - mStartTime;
        double dt = (currentTime - mLastTime) / 1000.d;
        mLastTime = currentTime;
        long speed = (long) (currentDownloaded / dt); //计算速度
        mDBHelper.updateTaskInfoCurrent(id, totalDownloaded);
        mDownloadListener.onProgress(Util.humanReadableBytes(speed, true) + "/s", totalDownloaded, mTotalLength);
        mDBHelper.updateBreakPointInfoCurrent(info.id, info.offset);
    }

    @Override
    public void onError(BreakPointInfo info, int code, String msg) {
        mDownloadListener.onError(code, msg);
    }

    @Override
    public void onCompleted(BreakPointInfo info) {
        mDownloadThreadMap.remove(info.id);
        if (mDownloadThreadMap.isEmpty()) {
            mDownloadListener.onProgress("0kb/s", mTotalLength, mTotalLength);
            String filePath = mDownloadFile.parent + File.separator + mDownloadFile.fileName;
            mDownloadListener.onCompleted(filePath);
        }

    }

    @Override
    public void onCancel(BreakPointInfo info) {
        mDownloadThreadMap.remove(info.id);
        if (mDownloadThreadMap.isEmpty()) {
            mDownloadListener.onCancel();
        }
    }
}
