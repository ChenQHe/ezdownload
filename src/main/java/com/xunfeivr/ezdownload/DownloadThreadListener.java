package com.xunfeivr.ezdownload;

import com.xunfeivr.ezdownload.db.BreakPointInfo;

import java.util.List;

/**
 * Created by android studio.
 * company:讯飞幻境科技有限公司
 * author:ChenHe
 * Time:18-8-17 下午6:23
 */
interface DownloadThreadListener {
    /**
     * 当文件开始下载时回调
     *
     * @param info 断点
     */
    void onStart(BreakPointInfo info);

    /**
     * 下载进度
     *
     * @param info 断点
     */
    void onProgress(List<BreakPointInfo> infoList, BreakPointInfo info);

    /**
     * 下载出错时回调
     *
     * @param code 错误码
     * @param msg  错误信息
     */
    void onError(BreakPointInfo info, int code, String msg);

    /**
     * 文件下载成功
     *
     * @param info 下载的文件
     */
    void onCompleted(BreakPointInfo info);

    /**
     * 下载取消
     *
     * @param info info
     */
    void onCancel(BreakPointInfo info);

}
