package com.xunfeivr.ezdownload.db;

import java.util.List;

/**
 * Created by android studio.
 * company:讯飞幻境科技有限公司
 * author:ChenHe
 * Time:18-8-21 上午9:59
 */
public class TaskInfo {
    /**
     * 任务id  数据库中获取
     */
    private int id;
    /**
     * 下载url
     */
    private String url;
    /**
     * 下载文件名
     */
    private String fileName;
    /**
     * 下载保存的目录
     */
    private String parent;
    /**
     * 下载文件总长度
     */
    private long length;
    /**
     * 当前下载的长度
     */
    private long current;
    /**
     * 这个任务的所有断点信息
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
