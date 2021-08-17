/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.processor

import cn.rongcloud.annotation.AutoInit
import cn.rongcloud.annotation.HiltBinding
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

/**
 * @author gusd
 * @Date 2021/07/28
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions(value = ["isAppModule"])
class CoreProcessor : AbstractProcessor() {

    private val processorImplList = arrayListOf<BaseProcessor>()


    override fun init(processingEnv: ProcessingEnvironment?) {
        super.init(processingEnv)
        if (processingEnv == null) return
        processorImplList.apply {
            add(ModuleInitProcessor(processingEnv))
            add(HiltInjectProcessor(processingEnv))
        }
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return HashSet<String>().apply {
            add(HiltBinding::class.java.canonicalName)
            add(AutoInit::class.java.canonicalName)
        }
    }

    override fun getSupportedOptions(): MutableSet<String> {
        return HashSet<String>().apply {
            add(ProcessorConstant.IS_APP_MODULE)
            add(ProcessorConstant.MODULE_NAME)
        }
    }


    override fun process(
        annotations: MutableSet<out TypeElement>,
        processingEnv: RoundEnvironment
    ): Boolean {
        return try {
            processImpl(annotations, processingEnv)
        } catch (e: Exception) {
            logError(e.message)
            true
        }
    }

    private fun processImpl(
        annotations: MutableSet<out TypeElement>,
        roundEnvironment: RoundEnvironment,
    ): Boolean {
        processorImplList.forEach {
            it.processImpl(annotations, roundEnvironment)
        }
        return true
    }

    public fun logError(message: String?) {
        message?.let {
            processingEnv.messager.printMessage(
                Diagnostic.Kind.ERROR,
                "${this.javaClass.name}: $message\r\n"
            )
        }
    }

}