package com.xunfeivr.ezdownload;

/**
 * Created by android studio.
 * company:讯飞幻境科技有限公司
 * author:ChenHe
 * Time:18-8-17 下午2:51
 * 一个下载的统一接口
 */
interface IDownloadService {

    /**
     * 下载这个文件
     *
     * @param file 需要下载的实体类
     */
    void download(DownloadFile file);


    /**
     * 取消下载 可以当暂停用
     */
    void cancel(String url);

    /**
     * 添加一个监听器 可以在任何地方回调
     *
     * @param downloadListener 下载进度监听器
     */
    void addDownloadListener(DownloadListener downloadListener);

    /**
     * 移除一个监听器
     *
     * @param downloadListener 下载进度监听器
     */
    void removeDownloadListener(DownloadListener downloadListener);
}
