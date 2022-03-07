package com.basis.utils;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author: BaiCQ
 * @ClassName: IOUtil
 * @date: 2018/3/8
 * @Description: IO操作的工具包
 */
public class IOUtil {

    /**
     * 关流
     * @param stream
     */
    public static void closeStream(Closeable stream) {
        if (null == stream) return;
        try {
            stream.close();
            stream = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 安全写入
     * @param out
     * @param outData
     */
    public static void safeWrite(OutputStream out,byte[] outData){
        try {
            out.write(outData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 循环读取并写满整个指定长度的byte数组
     * @param in inputliu
     * @param desArr 目标数组
     * @param offset 写入目标数组的偏移量
     */
    public static void readFullArr(InputStream in, byte[] desArr, int offset) throws IOException{
        int desLen = desArr.length;//填充满素组长度偏移量
        do {
            int readLen = in.read(desArr, offset, desLen - offset);
            if (readLen > 0) offset += readLen;
        } while (offset < desLen);
    }
}
