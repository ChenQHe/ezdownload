package com.xunfeivr.ezdownload;

/**
 * Created by android studio.
 * author:ChenHe
 */
public class HttpError {
    public static final int SERVER_ERROR = 303;//服务器 io异常
    public static final int FILE_ERROR = 300; //文件无法创建
    public static final int SERVER_NOT_RESPONSE = 404; //无法连接服务器
    public static final int SERVER_NOT_FILE = 405; //服务器不存在该文件

}
