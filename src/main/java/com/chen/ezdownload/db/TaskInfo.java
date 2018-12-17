package com.chen.ezdownload.db;

import java.util.List;

/**
 * Created by android studio.
 * company:Xunfei Magic Technology Co., Ltd.
 * author:ChenHe
 * Time:18-8-21 AM:9:59
 */
public class TaskInfo {
    /**
     * Task id  Obtained from the database
     */
    private int id;
    /**
     * Download url
     */
    private String url;
    /**
     * Download file name
     */
    private String fileName;
    /**
     * Download saved directory
     */
    private String parent;
    /**
     * Total length of download file
     */
    private long length;
    /**
     * Current download length
     */
    private long current;
    /**
     * All breakpoint information for this task
     */
    private List<BreakPointInfo> breakPointInfoList;

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public List<BreakPointInfo> getBreakPointInfoList() {
        return breakPointInfoList;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public long getLength() {
        return length;
    }

    public void setCurrent(long current) {
        this.current = current;
    }

    public long getCurrent() {
        return current;
    }

    public void setBreakPointInfoList(List<BreakPointInfo> breakPointInfoList) {
        this.breakPointInfoList = breakPointInfoList;
    }

    @Override
    public String toString() {
        return "TaskInfo{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", fileName='" + fileName + '\'' +
                ", parent='" + parent + '\'' +
                ", length=" + length +
                ", current=" + current +
                ", breakPointInfoList=" + breakPointInfoList +
                '}';
    }
}
