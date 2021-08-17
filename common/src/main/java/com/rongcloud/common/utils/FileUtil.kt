/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package com.rongcloud.common.utils

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.FileUtils
import android.webkit.MimeTypeMap
import androidx.annotation.RequiresApi
import java.io.*
import java.util.*


/**
 * @author gusd
 * @Date 2021/07/09
 */
object FileUtil {
    private const val BUFFER_SIZE = 8094


    @RequiresApi(Build.VERSION_CODES.Q)
    private fun uriToFileQ(context: Context, uri: Uri): File? =
        if (uri.scheme == ContentResolver.SCHEME_FILE)
            File(requireNotNull(uri.path))
        else if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            //把文件保存到沙盒
            val contentResolver = context.contentResolver
            val displayName = "${System.currentTimeMillis()}${Random().nextInt(9999)}.${
                MimeTypeMap.getSingleton()
                    .getExtensionFromMimeType(contentResolver.getType(uri))
            }"
            val ios = contentResolver.openInputStream(uri)
            if (ios != null) {
                File("${context.cacheDir.absolutePath}/$displayName")
                    .apply {
                        val fos = FileOutputStream(this)
                        FileUtils.copy(ios, fos)
                        fos.close()
                        ios.close()
                    }
            } else null
        } else null


    private fun isFileDownload(): Boolean {
//        MyApp.context.getExternalFilesDir()
        return false
    }


    fun writeFile(
        inputStream: InputStream,
        path: String,
        onProgress: (done: Long) -> Unit
    ): Boolean {
        lateinit var outputFile: File
        lateinit var outputStream: OutputStream
        try {
            outputFile = File("${path}.temp")
            if (outputFile.exists()) {
                outputFile.delete()
            }
            outputFile.parentFile.mkdirs()
            val fileReader = ByteArray(BUFFER_SIZE)
            outputStream = FileOutputStream(outputFile)
            var done = 0L
            var isReading = true
            while (isReading) {
                val read = inputStream.read(fileReader)
                if (read == -1) {
                    done = -1
                    isReading = false
                } else {
                    done += read
                    outputStream.write(fileReader, 0, read)
                }
                onProgress(done)
            }
            outputStream.flush()
            outputFile.renameTo(File(path))
            return true
        } catch (e: IOException) {
            if (outputFile.exists()) {
                outputFile.delete()
            }
        } finally {
            inputStream.close()
            outputStream.close()
        }
        return false
    }

    fun exists(path: String) = File(path).exists()

    fun copyFile(srcFile: File, destFile: File) {

        val fis = FileInputStream(srcFile);
        val fos = FileOutputStream(destFile)
        val bis = BufferedInputStream(fis)
        val bos = BufferedOutputStream(fos)
        val buf = ByteArray(1024)

        var len = 0;
        while (true) {
            len = bis.read(buf)
            if (len == -1) break;
            bos.write(buf, 0, len)
        }
        fis.close()
        fos.close()

    }

}