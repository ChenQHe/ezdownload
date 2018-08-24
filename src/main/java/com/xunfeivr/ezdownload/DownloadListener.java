package com.xunfeivr.ezdownload;

/**
 * Created by android studio.
 * company:讯飞幻境科技有限公司
 * author:ChenHe
 * Time:18-8-16 下午5:16
 *
 * 一个下载进度的回调接口
 */
public interface DownloadListener {
    /**
     * 当文件开始下载时回调
     *
     * @param file   文件
     * @param length 文件长度
     */
    void onStart(DownloadFile file, long length);

    /**
     * 下载进度
     *
     * @param speed   下载速度
     * @param current 下载进度
     * @param length  总长度
     */
    void onProgress(String speed, long current, long length);

    /**
     * 下载出错时回调
     *
     * @param code 错误码
     * @param msg  错误信息
     */
    void onError(int code, String msg);

    /**
     * 文件下载成功
     *
     * @param filePath 下载的文件
     */
    void onCompleted(String filePath);

    /**
     * 下载取消
     */
    void onCancel();
}
