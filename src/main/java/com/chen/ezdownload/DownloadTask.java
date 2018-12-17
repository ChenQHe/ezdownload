package com.chen.ezdownload;

import android.content.Context;

import com.chen.ezdownload.db.BreakPointInfo;
import com.chen.ezdownload.db.DBHelper;
import com.chen.ezdownload.db.TaskInfo;
import com.chen.ezdownload.util.LogUtil;
import com.chen.ezdownload.util.Util;

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
 * company:Xunfei Magic Technology Co., Ltd.
 * author:ChenHe
 * Time:18-8-13 PM:6:37
 * <p>
 * Download scheduler, Control the overall download
 */
class DownloadTask implements Runnable, DownloadThreadListener {

    /**
     * Download configuration
     */
    private DownloadConfig mDownloadConfig;
    /**
     * Files that need to be downloaded
     */
    private DownloadFile mDownloadFile;
    /**
     * Download progress callback, Direct callback to main thread
     */
    private DownloadMessageHandler mDownloadListener;
    /**
     * Record the downloaded thread
     */
    private final Map<Integer, DownloadThread> mDownloadThreadMap = new ConcurrentHashMap<>();
    /**
     * Task id is obtained from the database
     */
    private int id;
    /**
     * The total length of this task
     */
    private long mTotalLength;
    /**
     * The time when the last progress returned
     */
    private long mLastTime;
    /**
     * The time when the task is started. This is the reference system. All time is subtracted from this time.
     */
    private long mStartTime;

    /**
     * Database operation class
     */
    private DBHelper mDBHelper;

    DownloadTask(Context context, DownloadFile downloadFile, DownloadListener downloadListener) {
        mDBHelper = DBHelper.getInstance(context);
        mDownloadFile = downloadFile.clone();
        mDownloadListener = new DownloadMessageHandler(downloadListener);
        mDownloadThreadMap.clear();
        mLastTime = 0;
        mStartTime = System.currentTimeMillis();
    }


    void config(DownloadConfig config) {
        mDownloadConfig = config;
    }

    /**
     * Cancel download task
     */
    void cancel() {
        for (DownloadThread downloadThread : mDownloadThreadMap.values()) {
            if (downloadThread != null) {
                downloadThread.cancel();
            }
        }
    }

    boolean isDownloading() {
        for (DownloadThread downloadThread : mDownloadThreadMap.values()) {
            if (downloadThread.isRunning()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void run() {
        try {
            //Create a location where the download file is saved
            File file = createFile(mDownloadFile.getParent(), mDownloadFile.getFileName());
            if (file == null) {
                mDownloadListener.onError(mDownloadFile, HttpError.FILE_ERROR, "File could not be created");
                return;
            }
            //If it has been saved in the database, such a task can continue to download the breakpoint
            if (startOldWork(file)) {
                return;
            }
            //If not in the database, create a new task and insert the database.
            startNewWork(file);
        } catch (IOException e) {
            mDownloadListener.onError(mDownloadFile, HttpError.SERVER_ERROR, e.getMessage());
        }

    }

    /**
     * Open a new task
     *
     * @param file           Saved file
     * @throws IOException Url exception file exception
     */
    private void startNewWork(File file) throws IOException {
        if (file.exists() && file.delete()) {
            LogUtil.e("File re-download...");
        }
        if (!file.createNewFile()) {
            mDownloadListener.onError(mDownloadFile, HttpError.FILE_ERROR, "File could not be created");
            return;
        }
        //Construct URL
        URL url = new URL(mDownloadFile.getDownloadUrl());
        //Hit a urlConnection connection
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        //Configuration connection
        Util.configConnection(connection, mDownloadConfig);
        //Turn on network connection
        connection.connect();
        //Get return code
        int code = connection.getResponseCode();
        //200 success
        if (code == HttpURLConnection.HTTP_OK) {
            //If the server has an error, return directly
            InputStream errorStream = connection.getErrorStream();
            if (errorStream != null) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                int len;
                byte[] buf = new byte[1024];
                while ((len = errorStream.read(buf)) != -1) {
                    byteArrayOutputStream.write(buf, 0, len);
                }
                mDownloadListener.onError(mDownloadFile, HttpError.SERVER_NOT_RESPONSE, byteArrayOutputStream.toString("UTF-8"));
                errorStream.close();
                byteArrayOutputStream.close();
                return;
            }
            try {
                mTotalLength = Long.parseLong(connection.getHeaderField("Content-Length"));
            } catch (NumberFormatException e) {
                mTotalLength = -1;
            }
            //Disconnected, you can assign tasks.
            connection.disconnect();
            if (mTotalLength < 0) {
                mDownloadListener.onError(mDownloadFile, HttpError.SERVER_NOT_FILE, "The server does not have the file to download");
                return;
            }
            //Initialize a RandomAccessFile
            RandomAccessFile accessFile = new RandomAccessFile(file, "rwd");
            accessFile.setLength(mTotalLength);
            accessFile.close();
            //Create task information Save to database
            TaskInfo taskInfo = new TaskInfo();
            taskInfo.setFileName(mDownloadFile.getFileName());
            taskInfo.setParent(mDownloadFile.getParent());
            taskInfo.setUrl(mDownloadFile.getDownloadUrl());
            taskInfo.setLength(mTotalLength);
            //The database returns an id
            id = mDBHelper.saveTaskInfo(taskInfo);
            taskInfo.setId(id);
            //Get the number of download threads
            int threadNum = mDownloadConfig.getThreadNum();
            if (mTotalLength < 1024 * 1024 * 30) {
                threadNum = 1;
            }
            //Block lengths and add 1 to each block to prevent total data loss
            long block = mTotalLength / threadNum;
            mDownloadListener.onStart(mDownloadFile, mTotalLength);
            List<BreakPointInfo> list = new ArrayList<>();
            for (int i = 0; i < threadNum; i++) {
                //Create breakpoint information Save to database
                BreakPointInfo breakPointInfo = new BreakPointInfo();
                //Id is the id of this task. Each breakpoint is saved. It is convenient to find all the breakpoints of this task later.
                breakPointInfo.taskId = id;
                //Set the starting position of the breakpoint
                breakPointInfo.start = block * i;
                //Set the download cursor and move forward with the download progress
                breakPointInfo.offset = breakPointInfo.start;
                //Set the length of the breakpoint
                breakPointInfo.length = breakPointInfo.start + block - 1;
                if (i == threadNum - 1) {
                    breakPointInfo.length = mTotalLength;
                }
                //Save to database
                breakPointInfo.id = mDBHelper.saveBreakPointInfo(breakPointInfo);

                list.add(breakPointInfo);
                //Create task
                DownloadThread downloadThread = new DownloadThread(mDownloadFile.getDownloadUrl(), file,
                        breakPointInfo.id, list, mDownloadConfig, this);
                //Save to memory
                mDownloadThreadMap.put(breakPointInfo.id, downloadThread);
                //Start task
                downloadThread.start();
            }
        } else {
            mDownloadListener.onError(mDownloadFile, code, connection.getResponseMessage());
        }
    }

    /**
     * Open an old task
     *
     * @param file           Saved file
     * @return True, old task has been processed ;false, old task can not be processed
     */
    private boolean startOldWork(File file) {
        //Read the task information saved by this url from the database
        TaskInfo taskInfo = mDBHelper.getTaskInfo(mDownloadFile.getDownloadUrl());
        //If exists
        if (taskInfo != null) {
            if (!file.exists()) {
                mDBHelper.deleteTaskInfo(taskInfo.getId());
                return false;
            }
            id = taskInfo.getId();
            mTotalLength = taskInfo.getLength();
            //Get breakpoint information, Maybe null
            List<BreakPointInfo> list = taskInfo.getBreakPointInfoList();
            if (list != null) {
                mDownloadListener.onStart(mDownloadFile, mTotalLength);
                for (BreakPointInfo breakPointInfo : list) {
                    //Direct assignment of tasks
                    DownloadThread downloadThread = new DownloadThread(taskInfo.getUrl(), file,
                            breakPointInfo.id, list, mDownloadConfig, this);
                    mDownloadThreadMap.put(breakPointInfo.id, downloadThread);
                    downloadThread.start();
                }
                return true;
            }
        }
        return false;
    }


    /**
     * Create a file
     *
     * @param parent directory
     * @param name   fileName
     * @return File
     */
    private File createFile(String parent, String name) {
        File file = new File(parent);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                return null;
            }
        }
        //Open the save file or create a new one
        return new File(parent, name);
    }

    @Override
    public void onStart(BreakPointInfo info) {

    }


    @Override
    public void onProgress(List<BreakPointInfo> infoList, BreakPointInfo info) {
        mDBHelper.updateBreakPointInfoCurrent(info.id, info.offset);
        long totalDownloaded = 0;
        long currentDownloaded = 0;
        for (BreakPointInfo breakPointInfo : infoList) {
            totalDownloaded = breakPointInfo.downloadedSize() + totalDownloaded;
            currentDownloaded = currentDownloaded + breakPointInfo.currentDownloaded;
        }
        long currentTime = System.currentTimeMillis() - mStartTime;
        double dt = (currentTime - mLastTime) / 1000.d;
        mLastTime = currentTime;
        long speed = (long) (currentDownloaded / dt); //Calculation speed
        mDBHelper.updateTaskInfoCurrent(id, totalDownloaded);
        mDownloadListener.onProgress(mDownloadFile, Util.humanReadableBytes(speed) + "/s", totalDownloaded, mTotalLength);
        if (totalDownloaded >= mTotalLength) {
            mDownloadListener.onCompleted(mDownloadFile);
        }
    }

    @Override
    public void onError(BreakPointInfo info, int code, String msg) {
        mDownloadListener.onError(mDownloadFile, code, msg);
    }

    @Override
    public void onCompleted(BreakPointInfo info) {
        mDownloadThreadMap.remove(info.id);
        if (mDownloadThreadMap.isEmpty()) {
            mDownloadListener.onCompleted(mDownloadFile);
        }

    }

    @Override
    public void onCancel(BreakPointInfo info) {
        mDownloadThreadMap.remove(info.id);
        if (mDownloadThreadMap.isEmpty()) {
            mDownloadListener.onCancel(mDownloadFile);
        }
    }
}
