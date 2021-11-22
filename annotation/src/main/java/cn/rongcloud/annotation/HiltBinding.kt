/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.annotation

import kotlin.reflect.KClass

/**
 * @author gusd
 * @Date 2021/07/27
 * @Description 用于绑定 View 和实现类，实现类必须继承自 Activity，Fragment ，由于 view 的情况较为特殊，暂不支持 view
 * @TODO 后续考虑支持 view 和任意类型的数据注入
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class HiltBinding(val value: KClass<*> = Void::class)
