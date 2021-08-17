/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.processor

import cn.rongcloud.annotation.AutoInit
import cn.rongcloud.bean.AutoInitBean
import com.squareup.javapoet.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.util.*
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.inject.Named
import javax.lang.model.element.ElementKind
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.tools.StandardLocation

/**
 * @author gusd
 * @Date 2021/07/28
 */
class ModuleInitProcessor(processingEnv: ProcessingEnvironment) : BaseProcessor(processingEnv) {
    private val moduleInitTempFile = File(".${File.separator}build${File.separator}temp.txt")

    private var isFirst = true
    private var hasInitModuleClass = false
    private var moduleName: String? = null

    override fun processImpl(
        annotations: MutableSet<out TypeElement>,
        roundEnvironment: RoundEnvironment
    ): Boolean {
        val options = processingEnv.options
        val isAppModule = options[ProcessorConstant.IS_APP_MODULE].toBoolean()
        val autoInitElement = roundEnvironment.getElementsAnnotatedWith(AutoInit::class.java)
        if (moduleName.isNullOrEmpty()) {
            var moduleName = options[ProcessorConstant.MODULE_NAME]
            // 从第一个类中获取 module 名
            if (!moduleName.isNullOrEmpty() && isFirst) {
                roundEnvironment.rootElements.firstOrNull()?.let {
                    moduleName = getDefaultModuleName(it.asType().toString())
                }
            }
        }
        if (autoInitElement.isNotEmpty()) {
            hasInitModuleClass = true
        }




        if (roundEnvironment.processingOver()) {
            if (!hasInitModuleClass && getModuleNameFromFile().contains(moduleName)) {
                removeModuleNameFromFile()
            }
        }

        isFirst = false

        if (autoInitElement.isEmpty()) {
            return true
        }


        val elementList = arrayListOf<AutoInitBean>()
        autoInitElement.forEach { element ->
            logDebug("element name = ${element.simpleName}")
            if (element.kind != ElementKind.CLASS) {
                return@forEach
            }
            if (moduleName.isNullOrEmpty()) {
                moduleName = getDefaultModuleName(element.asType().toString())
                logWarning("当前模块未配置 moduleName, 将使用默认的 $moduleName 作为模块名")
            }
            elementList.add(AutoInitBean(element.asType().toString(), element))
        }

        // 将模块名记录到文件临时文件中
        if (!moduleInitTempFile.exists()) {
            moduleInitTempFile.createNewFile()
        }
        if (!moduleName.isNullOrEmpty()) {
            val moduleNameFromFile = getModuleNameFromFile()
            if (!moduleNameFromFile.contains(moduleName)) {
                val fw = FileWriter(moduleInitTempFile.path, true)
                fw.appendLine(moduleName)
                fw.close()
            }
        }

        generateAutoInitFile(moduleName ?: "", elementList)
//        createTestFile(moduleName)
        if (isAppModule) {
            // 生成 APP 层的注入策略
            generateTotalInitFile()
        }

        return true
    }


    private fun createTestFile(moduleName: String?) {
        mFiler.createResource(StandardLocation.CLASS_OUTPUT, "", "META-INFO/moduleInitInfo.properties")
            .openWriter()
            .append("moduleName = $moduleName")
            .close()
    }

    private fun removeModuleNameFromFile() {
        if (!moduleName.isNullOrEmpty()) {
            val moduleNameFromFile = getModuleNameFromFile()
            val set = moduleNameFromFile.toHashSet()
            set.remove(moduleName)
            val fw = FileWriter(moduleInitTempFile.path, false)
            set.forEach {
                fw.appendLine(it)
            }
            fw.close()

        }
    }

    private fun getModuleNameFromFile(): Set<String> {
        val fileReader = FileReader(moduleInitTempFile.path)
        val readLines = fileReader.readLines()
        fileReader.close()
        return readLines.toSet()
    }

    private fun generateTotalInitFile() {

        val moduleSet = getModuleNameFromFile()

        val moduleAndClassName = getPackageAndClassName(ProcessorConstant.INIT_MODULE)
        val moduleInitType = ClassName.get(moduleAndClassName[0], moduleAndClassName[1])
        val arrayList = ClassName.get("java.util", "ArrayList")
        val listOfInitModule = ParameterizedTypeName.get(arrayList, moduleInitType)

        val parameterList = arrayListOf<ParameterSpec>()
        var string = "ArrayList<\$T> list = new ArrayList();"
        moduleSet.forEach { moduleName ->
            logDebug("moduleName = $moduleName")
            ParameterSpec
                .builder(listOfInitModule, "${moduleName}_List")
                .addAnnotation(
                    AnnotationSpec.builder(Named::class.java)
                        .addMember("value", "\"${moduleName}\"")
                        .build()
                ).build().apply {
                    parameterList.add(this)
                }
            string += "list.addAll(${moduleName}_List);"
        }
        val returnCodeString = string + "return list;"


        val methodSpec = MethodSpec
            .methodBuilder("provideModuleItem")
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(Provides::class.java)
            .addAnnotation(
                AnnotationSpec.builder(Named::class.java)
                    .addMember("value", "\"autoInit\"")
                    .build()
            )
            .addParameters(parameterList)
            .addCode(
                CodeBlock.of(
                    returnCodeString,
                    moduleInitType
                )
            )
            .returns(listOfInitModule)
            .build()

        val componentAnnotation = CodeBlock.builder()
            .add(
                "\$T.class",
                ClassName.get("dagger.hilt.components", "SingletonComponent")
            )
            .build()

        val annotationSpec =
            AnnotationSpec.builder(InstallIn::class.java)
                .addMember("value", componentAnnotation)
                .build()

        val type =
            TypeSpec.classBuilder("Generate_ModuleInitComponent")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Module::class.java)
                .addAnnotation(annotationSpec)
                .addMethod(methodSpec)
                .build()
        JavaFile.builder(mGeneratePackage, type).build()
            .writeTo(mFiler)
    }


    private fun generateAutoInitFile(
        moduleName: String = "",
        elementList: ArrayList<AutoInitBean>
    ) {
        logDebug("moduleName = ${moduleName}")

        val parameterList = arrayListOf<ParameterSpec>()
        var string = "ArrayList<\$T> list = new ArrayList();"
        elementList.forEachIndexed { index, bean ->
            val packageAndClassName = getPackageAndClassName(bean.clazz)
            ParameterSpec.builder(
                ClassName.get(packageAndClassName[0], packageAndClassName[1]),
                packageAndClassName[1].toLowerCase()
            ).build().apply {
                parameterList.add(this)
            }
            string += "list.add(${packageAndClassName[1].toLowerCase()});"
        }
        val returnCodeString = string + "return list;"
        val moduleAndClassName = getPackageAndClassName(ProcessorConstant.INIT_MODULE)
        val moduleInitType = ClassName.get(moduleAndClassName[0], moduleAndClassName[1])
        val arrayList = ClassName.get("java.util", "ArrayList")
        val listOfInitModule = ParameterizedTypeName.get(arrayList, moduleInitType)
        val methodSpec =
            MethodSpec.methodBuilder("provideModuleItem")
                .addModifiers(Modifier.PUBLIC)
                .returns(listOfInitModule)
                .addAnnotation(Provides::class.java)
                .addAnnotation(
                    AnnotationSpec.builder(Named::class.java)
                        .addMember("value", "\"${moduleName}\"")
                        .build()
                )
                .addParameters(parameterList)
                .addCode(
                    CodeBlock.of(
                        returnCodeString,
                        moduleInitType
                    )
                )
                .build()

        val componentAnnotation = CodeBlock.builder()
            .add(
                "\$T.class",
                ClassName.get("dagger.hilt.components", "SingletonComponent")
            )
            .build()

        val annotationSpec =
            AnnotationSpec.builder(InstallIn::class.java)
                .addMember("value", componentAnnotation)
                .build()
        val type =
            TypeSpec.classBuilder("${moduleName.toUpperCase()}_Generate_ModuleInit")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Module::class.java)
                .addAnnotation(annotationSpec)
                .addMethod(methodSpec)
                .build()
        JavaFile.builder(mGeneratePackage, type).build()
            .writeTo(mFiler)
    }

}