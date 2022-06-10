
package com.basis.wapper;

import android.os.Handler;
import android.os.HandlerThread;

import com.basis.utils.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * log 本地缓存 阻塞线程
 * loop: 默认间隔5s 读写1s
 */
public class LogDumper extends HandlerThread {
    private static final String TAG = "LogThread";
    // 默认 轮训间隔5s
    private final static long DEF_LOOP_TIME = 5 * 1000;
    // 单次写入时间
    private final static long DEF_WRITE_TIME = 1000;
    private volatile static LogDumper _dumper;
    private Handler mHandler;
    private Task looperTask;

    private LogDumper() {
        super(TAG);
    }

    @Deprecated
    public void start() {
        super.start();
        mHandler = new Handler(getLooper());
    }

    public static LogDumper get() {
        if (null == _dumper) {
            synchronized (LogDumper.class) {
                if (null == _dumper) {
                    _dumper = new LogDumper();
                    _dumper.start();
                }
            }
        }
        return _dumper;
    }

    public void dumper(int pid, String filter, File logFile) {
        Task task = new Task(pid, filter, DEF_WRITE_TIME);
        task.setLogFile(logFile);
        if (null != mHandler) {
            mHandler.post(task);
        }
    }

    /**
     * @param pid        进程Id
     * @param loop       轮训写入间隔,<0 则使用默认 DEF_LOOP_TIME
     * @param fileFormat 日志文件构建器
     */
    public void startLoop(int pid, long loop, String filtter, LogFileFormat fileFormat) {
        if (null == looperTask) {
            looperTask = new Task(pid, filtter, DEF_WRITE_TIME);
        }
        looperTask.setLogFile(fileFormat.fileFormat());
        looperTask.setOnComplete(new IResultBack<Void>() {
            @Override
            public void onResult(Void unused) {
                // 此处 loopTask 置空即可结束轮训
                if (null != looperTask && null != mHandler) {
                    looperTask.setLogFile(fileFormat.fileFormat());
                    mHandler.postDelayed(looperTask, loop < 0 ? DEF_LOOP_TIME : loop);
                }
            }
        });
        if (null != looperTask && null != mHandler) {
            mHandler.post(looperTask);
        }
    }

    public void stopLoop() {
        looperTask = null;
    }

    public interface LogFileFormat {
        File fileFormat();
    }

    /**
     * 通过cmd 命令 将日志写入到文件中
     */
    private static class Task implements Runnable {
        private final static String TAG = "LogTask";
        private final long maxWriteTime;
        private final String mPid;
        private final String filter;
        private final String cmd;
        private IResultBack<Void> onComplete;
        private FileOutputStream outputStream;

        /**
         * @param pid 进程id
         */
        private Task(int pid, String filter, long writeTime) {
            maxWriteTime = writeTime;
            this.filter = filter;
            mPid = String.valueOf(pid);
            // logcat | grep "^..MyApp\|^..MyActivity"
            //显示当前mPid程序的日志等级  日志等级：*:v , *:d , *:w , *:e , *:f , *:s
            cmd = "logcat *:e *:d | grep \"(" + mPid + ")\"";// 输出d~e
            // cmds = "logcat -s way";//打印标签过滤信息
            // cmds = "logcat *:e *:i | grep \"(" + mPid + ")\"";
            // cmd = "logcat  | grep \"(" + mPid + ")\"";//打印所有日志信息
        }

        public void setOnComplete(IResultBack<Void> onComplete) {
            this.onComplete = onComplete;
        }

        public void setOutputStream(FileOutputStream outputStream) {
            this.outputStream = outputStream;
        }

        public void setLogFile(File logFile) {
            if (null == logFile) {
                Logger.e(TAG, "LogFile is Null");
                return;
            }
            try {
                outputStream = new FileOutputStream(logFile, true);// 追加
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                outputStream = null;
            }
        }

        @Override
        public void run() {
            if (null == outputStream) {
                Logger.e(TAG, "outputStream is null ");
                return;
            }
            Process logcatProc = null;
            BufferedReader mReader = null;
            try {
                long start = System.currentTimeMillis();
                logcatProc = Runtime.getRuntime().exec(cmd);
                InputStream inputStream = logcatProc.getInputStream();
                mReader = new BufferedReader(new InputStreamReader(inputStream), 1024);
                String line;
                // 此处为阻塞是输入,可能导致最大写入时间不准
                while (null != (line = mReader.readLine())) {
                    if (System.currentTimeMillis() - start > maxWriteTime) {
                        break;
                    }
                    if (line.contains(mPid) && line.contains(filter)) {
                        outputStream.write((line + "\n").getBytes());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (logcatProc != null) {
                    logcatProc.destroy();
                }
                if (mReader != null) {
                    try {
                        mReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    outputStream = null;
                }
                if (null != onComplete) onComplete.onResult(null);
            }
        }
    }
}
