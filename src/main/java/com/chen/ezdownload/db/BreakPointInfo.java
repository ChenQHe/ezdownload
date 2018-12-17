package com.chen.ezdownload.db;

/**
 * Created by android studio.
 * company:Xunfei Magic Technology Co., Ltd.
 * author:ChenHe
 * Time:18-8-14 AM:10:40
 */
public class BreakPointInfo {
    /**
     * Breakpoint id in the database
     */
    public int id;
    /**
     * Own task id
     */
    public int taskId;
    /**
     * Breakpoint starting point
     */
    public long start;
    /**
     * The position of the cursor of the breakpoint
     */
    public long offset;
    /**
     * Breakpoint length (end point)
     */
    public long length;
    /**
     * This value does not need to be saved in the database. It is used to calculate the download speed.
     */
    public long currentDownloaded;

    /**
     * The length of the download is the cursor position minus the starting point
     * @return Download length
     */
    public long downloadedSize() {
        return offset - start;
    }

    @Override
    public String toString() {
        return "BreakPointInfo{" +
                "id=" + id +
                ", taskId=" + taskId +
                ", start=" + start +
                ", offset=" + offset +
                ", length=" + length +
                '}';
    }
}
