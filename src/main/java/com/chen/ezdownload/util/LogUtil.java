package com.chen.ezdownload.util;

import android.util.Log;

/**
 * Created by android studio.
 * company:Xunfei Magic Technology Co., Ltd.
 * author:ChenHe
 * Time:18-6-29 PM:3:27
 */
public class LogUtil {

    private static final boolean isDebug = true;

    private static final String TAG = "LogUtil";

    private LogUtil(){}

    public static void d(Object object) {
        if (isDebug) {
            Log.d(TAG, object.toString());
        }
    }

    public static void d(String tag, Object object) {
        if (isDebug) {
            Log.d(tag, object.toString());
        }
    }

    public static void i(Object object) {
        if (isDebug) {
            Log.i(TAG, object.toString());
        }
    }

    public static void i(String tag, Object object) {
        if (isDebug) {
            Log.i(tag, object.toString());
        }
    }

    public static void e(Object object) {
        if (isDebug) {
            Log.e(TAG, object.toString());
        }
    }

    public static void e(String tag, Object object) {
        if (isDebug) {
            Log.e(tag, object.toString());
        }
    }

    public static void w(Object object) {
        if (isDebug) {
            Log.v(TAG, object.toString());
        }
    }

    public static void w(String tag, Object object) {
        if (isDebug) {
            Log.v(tag, object.toString());
        }
    }
}
