package com.basis.utils;

import android.util.Log;

import com.basis.wapper.LogDumper;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Logger {
    private static SimpleDateFormat logfileFormat = new SimpleDateFormat("yyyy-MM-dd HH");// 日志文件格式
    private final static Map<Integer, String> map = new HashMap(8);
    public final static String Tag = "Logger";
    public final static String PRE_TAG = "RC_";
    public final static int MAX_LEN = 3000;
    private static boolean debug = true;

    static {
        map.put(Log.VERBOSE, "v");
        map.put(Log.DEBUG, "d");
        map.put(Log.INFO, "i");
        map.put(Log.WARN, "w");
        map.put(Log.ERROR, "e");
    }

    public static void setDebug(boolean debug) {
        Logger.debug = debug;
    }

    public static void v(Object obj) {
        log(Log.VERBOSE, Tag, obj);
    }

    public static void v(String tag, Object obj) {
        log(Log.VERBOSE, tag, obj);
    }

    public static void d(Object obj) {
        log(Log.DEBUG, Tag, obj);
    }

    public static void d(String tag, Object obj) {
        log(Log.DEBUG, tag, obj);
    }

    public static void i(Object obj) {
        log(Log.INFO, Tag, obj);
    }

    public static void i(String tag, Object obj) {
        log(Log.INFO, tag, obj);
    }

    public static void w(Object obj) {
        log(Log.WARN, Tag, obj);
    }

    public static void w(String tag, Object obj) {
        log(Log.WARN, tag, obj);
    }

    public static void e(Object obj) {
        log(Log.ERROR, Tag, obj);
    }

    public static void e(String tag, Object obj) {
        log(Log.ERROR, tag, obj);
    }

    private synchronized static void log(int priority, String tag, Object obj) {
        if (null == obj || !debug) return;
        String log = obj.toString();
        //循环写入日志
        int len = log.length();
        int start = 0;
        int end = MAX_LEN;
        do {
            if (end > len) end = len;
            String cur = log.substring(start, end);
            Log.println(priority, PRE_TAG + tag, cur);
            start = end;
            end = start + MAX_LEN;
        } while (start < len);
    }

    private static File getLogFile() {
        String nameFormat = logfileFormat.format(new Date());
        //日志文件存储路径
        String logPath = KPath.getFilesPath() + File.separator + Tag;
        File dirsFile = new File(logPath);
        if (!dirsFile.exists()) {
            dirsFile.mkdirs();
        }
        //创建日志文件
        File file = new File(logPath, nameFormat + "-log.txt");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }


    public static void startLoop() {
        int pid = android.os.Process.myPid();
        startLoop(pid, -1);
    }

    public static void startLoop(int pid, long loop) {
        LogDumper.get().startLoop(pid, loop, PRE_TAG, Logger::getLogFile);
    }

    public static void stopLoop() {
        LogDumper.get().stopLoop();
    }
}
