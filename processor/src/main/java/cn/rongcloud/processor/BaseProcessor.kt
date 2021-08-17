/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.processor

import javax.annotation.processing.Filer
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import javax.tools.Diagnostic

/**
 * @author gusd
 * @Date 2021/07/29
 */
abstract class BaseProcessor(val processingEnv: ProcessingEnvironment) {

    // 文件操作类，我们将通过此类生成kotlin文件
    lateinit var mFiler: Filer

    // 类型工具类，处理Element的类型
    lateinit var mTypeTools: Types

    lateinit var mElementUtils: Elements

    // 生成类的包名
    val mGeneratePackage = "cn.rongcloud.generate"

    init {
        mFiler = processingEnv.filer
        mElementUtils = processingEnv.elementUtils
        mTypeTools = processingEnv.typeUtils
    }

    abstract fun processImpl(
        annotations: MutableSet<out TypeElement>,
        processingEnv: RoundEnvironment,
    ):Boolean

    public fun getPackageAndClassName(classPath: String): Array<String> {
        val index = classPath.lastIndexOf(".")
        val packageName = classPath.subSequence(0, index)
        val className = classPath.subSequence(index + 1, classPath.length)
        return arrayOf(packageName.toString(), className.toString())
    }

    public fun logDebug(message: String?) {
        message?.let {
            processingEnv.messager.printMessage(
                Diagnostic.Kind.NOTE,
                "${this.javaClass.name}: $message\r\n"
            )
        }
    }

    public fun logWarning(message: String?){
        message?.let {
            processingEnv.messager.printMessage(
                Diagnostic.Kind.WARNING,
                "${this.javaClass.name}: $message\r\n"
            )
        }
    }

    /**
     * 调用该方法会导致编译出错，除非真实错误，否则请勿调用
     */
    public fun logError(message: String?) {
        message?.let {
            processingEnv.messager.printMessage(
                Diagnostic.Kind.ERROR,
                "${this.javaClass.name}: $message\r\n"
            )
        }
    }

    // 使用路径的第三级作为默认模块名
    public fun getDefaultModuleName(classPath: String): String {
        val split = classPath.split(".")
        return if (split.size >= 3) {
            split[2]
        } else {
            split[split.size - 1]
        }
    }
}