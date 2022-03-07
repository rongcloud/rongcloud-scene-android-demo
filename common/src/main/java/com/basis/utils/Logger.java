package com.basis.utils;

import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Logger {
    private static SimpleDateFormat logInfoFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 日志的输出格式
    private static SimpleDateFormat logfileFormat = new SimpleDateFormat("yyyy-MM-dd");// 日志文件格式
    private final static Map<Integer, String> map = new HashMap(8);
    private final static int MAX_CACHE = 32;
    private final static ArrayList<String> cacheLogs = new ArrayList<>(MAX_CACHE * 2);
    public final static String Tag = "Logger";
    public final static int MAX_LEN = 3000;

    private static boolean debug = true;
    private static boolean toLocal = false;

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
            Log.println(priority, tag, cur);
            start = end;
            end = start + MAX_LEN;
        } while (start < len);
        //写入local
        if (toLocal) {
            String formatInfo = logInfoFormat.format(new Date()) + " " + tag + " " + map.get(priority) + " " + log;
            cacheLogs.add(formatInfo);
            if (cacheLogs.size() >= MAX_CACHE) {
                brush();
            }
        }
    }

    /**
     * 将缓存的日志全部刷入local
     */
    public static void brush() {
        int size = cacheLogs.size();
        Log.e(Tag, "brush to local size = " + size);
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < size; i++) {
            buffer.append(cacheLogs.get(i));
            buffer.append("\n");
        }
        cacheLogs.clear();
        writeToLocal(buffer.toString());
    }

    /**
     * 打开日志文件并写入日志
     *
     * @param data
     */
    private static void writeToLocal(String data) {// 新建或打开日志文件
        File logFile = getLogFile();
        if (null == logFile || !logFile.exists()) {
            Log.e(Tag, "log file create fail !");
            return;
        }
        Log.e(Tag, "path = " + logFile.getAbsolutePath());
        try {
            // 后面这个参数代表是不是要接上文件中原来的数据，不进行覆盖
            FileWriter filerWriter = new FileWriter(logFile, true);
            BufferedWriter bufWriter = new BufferedWriter(filerWriter);
            bufWriter.write(data);
            bufWriter.newLine();
            bufWriter.close();
            filerWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            }
        }
        return file;
    }
}
