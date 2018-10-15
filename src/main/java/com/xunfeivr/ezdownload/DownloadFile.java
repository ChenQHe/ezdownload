package com.xunfeivr.ezdownload;


/**
 * Created by android studio.
 * company:讯飞幻境科技有限公司
 * author:ChenHe
 * Time:18-7-3 上午11:38
 * <p>
 * 需要下载的实体类
 */
public abstract class DownloadFile {

    /**
     * @return  下载地址
     */
    public abstract String getDownloadUrl();

    /**
     * @return  下载文件名称
     */
    public abstract String getFileName();

    /**
     * @return 下载保存的位置目录
     */
    public abstract String getParent();

    /**
     * @return 新的实例
     */
    public abstract DownloadFile newInstance();
}
