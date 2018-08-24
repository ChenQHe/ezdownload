package com.xunfeivr.ezdownload.db;

/**
 * Created by android studio.
 * company:讯飞幻境科技有限公司
 * author:ChenHe
 * Time:18-8-14 上午10:40
 */
public class BreakPointInfo {
    /**
     * 断点 id 数据库中获取
     */
    public int id;
    /**
     * 所属的任务 id
     */
    public int taskId;
    /**
     * 断点起点
     */
    public long start;
    /**
     * 断点的游标的位置
     */
    public long offset;
    /**
     * 断点长度 （结束点）
     */
    public long length;
    /**
     * 这个值不需要保存数据库中 它是用于计算下载速度的
     */
    public long currentDownloaded;

    /**
     * 下载的长度 为 游标位置 减去 起点
     * @return 下载长度
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
