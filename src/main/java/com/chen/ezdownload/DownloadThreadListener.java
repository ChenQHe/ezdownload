package com.chen.ezdownload;

import com.chen.ezdownload.db.BreakPointInfo;

import java.util.List;

/**
 * Created by android studio.
 * company:Xunfei Magic Technology Co., Ltd.
 * author:ChenHe
 * Time:18-8-17 PM:6:23
 */
interface DownloadThreadListener {
    /**
     * Callback when the file starts downloading
     *
     * @param info Breakpoint
     */
    void onStart(BreakPointInfo info);

    /**
     * Download progress
     *
     * @param info Breakpoint
     */
    void onProgress(List<BreakPointInfo> infoList, BreakPointInfo info);

    /**
     * Callback when downloading errors
     *
     * @param code Error code
     * @param msg  Error message
     */
    void onError(BreakPointInfo info, int code, String msg);

    /**
     * File download succeeded
     *
     * @param info Downloaded file
     */
    void onCompleted(BreakPointInfo info);

    /**
     * Download cancellation
     *
     * @param info info
     */
    void onCancel(BreakPointInfo info);

}
