package com.chen.ezdownload;

/**
 * Created by android studio.
 * author:ChenHe
 */
public class HttpError {
    public static final int SERVER_ERROR = 303;//Server io exception
    public static final int FILE_ERROR = 300; //File could not be created
    public static final int SERVER_NOT_RESPONSE = 404; //can not connect to the server
    public static final int SERVER_NOT_FILE = 405; //The file does not exist on the server

}
