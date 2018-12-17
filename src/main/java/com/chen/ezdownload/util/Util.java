package com.chen.ezdownload.util;

import com.chen.ezdownload.DownloadConfig;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.Map;

public class Util {

    private Util() {
    }

    public static final String RANGE = "Range";
    private static final String USER_AGENT = "User-Agent";

    public static String md5(String string) {
        byte[] hash = null;
        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ignored) {
        }

        if (hash != null) {
            StringBuilder hex = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                if ((b & 0xFF) < 0x10) hex.append('0');
                hex.append(Integer.toHexString(b & 0xFF));
            }
            return hex.toString();
        }

        return null;
    }


    public static String humanReadableBytes(long bytes) {
        int unit = 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        char pre = ("KMGTPE").charAt(exp - 1);
        return String.format(Locale.ENGLISH, "%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    public static void configConnection(HttpURLConnection connection, DownloadConfig downloadConfig) throws ProtocolException {
        connection.setConnectTimeout(downloadConfig.getTimeOut());
        connection.setReadTimeout(downloadConfig.getReadOut());
        connection.setRequestMethod(downloadConfig.getMethod());
        connection.addRequestProperty(USER_AGENT, "easy-download v1.0.0");
        for (Map.Entry<String, String> entry : downloadConfig.getRequestParams().entrySet()) {
            connection.setRequestProperty(entry.getKey(), entry.getValue());
        }
    }
}
