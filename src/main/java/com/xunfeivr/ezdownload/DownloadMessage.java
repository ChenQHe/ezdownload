package com.xunfeivr.ezdownload;

/**
 * Created by android studio.
 * company:讯飞幻境科技有限公司
 * author:ChenHe
 * Time:18-7-3 下午3:14
 * <p>
 * 回调的消息
 */
class DownloadMessage {
    /**
     * 文件长度
     */
    public long length;
    /**
     * 当前下载的长度
     */
    public long current;
    /**
     * 下载的文件
     */
    public DownloadFile file;
    /**
     * 下载速度
     */
    public String speed;
    /**
     * 错误码
     */
    public int code;
    /**
     * 错误信息
     */
    public String error;
}
