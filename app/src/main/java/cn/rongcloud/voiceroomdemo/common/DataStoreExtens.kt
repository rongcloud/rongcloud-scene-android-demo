/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.common

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder
import androidx.datastore.rxjava3.RxDataStore
import cn.rongcloud.voiceroomdemo.MyApp
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.properties.ReadOnlyProperty

/**
 * @author GSD
 * @Date 2021/6/7
 */
val Context.dataStore: RxDataStore<Preferences> by lazy {
    RxPreferenceDataStoreBuilder(MyApp.context, "settings").build()
}

// 用于保存默认值
private val defaultValueMap = HashMap<String, Any>()

var <T> Preferences.Key<T>.defaultValue: T
    get() {
        return defaultValueMap[this.name] as T
    }
    set(value) {
        defaultValueMap[this.name] = value!!
    }


fun myStringPreferencesKey(defValue: String = "") =
    ReadOnlyProperty<Any, Preferences.Key<String>> { _, property ->
        return@ReadOnlyProperty stringPreferencesKey(property.name).apply {
            defaultValue = defValue
        }
    }

fun myIntPreferencesKey(defValue: Int = 0) =
    ReadOnlyProperty<Any, Preferences.Key<Int>> { _, property ->
        return@ReadOnlyProperty intPreferencesKey(property.name).apply {
            defaultValue = defValue
        }
    }

fun myFloatPreferencesKey(defValue: Float = 0f) =
    ReadOnlyProperty<Any, Preferences.Key<Float>> { _, property ->
        return@ReadOnlyProperty floatPreferencesKey(property.name).apply {
            defaultValue = defValue
        }
    }

fun myDoublePreferencesKey(defValue: Double = 0.0) =
    ReadOnlyProperty<Any, Preferences.Key<Double>> { _, property ->
        return@ReadOnlyProperty doublePreferencesKey(property.name).apply {
            defaultValue = defValue
        }
    }


fun myBooleanPreferencesKey(defValue: Boolean = false) =
    ReadOnlyProperty<Any, Preferences.Key<Boolean>> { _, property ->
        return@ReadOnlyProperty booleanPreferencesKey(property.name).apply {
            defaultValue = defValue
        }
    }

fun myLongPreferencesKey(defValue: Long = 0L) =
    ReadOnlyProperty<Any, Preferences.Key<Long>> { _, property ->
        return@ReadOnlyProperty longPreferencesKey(property.name).apply {
            defaultValue = defValue
        }
    }

fun myStringSetPreferencesKey(defValue: Set<String> = emptySet()) =
    ReadOnlyProperty<Any, Preferences.Key<Set<String>>> { _, property ->
        return@ReadOnlyProperty stringSetPreferencesKey(property.name).apply {
            defaultValue = defValue
        }
    }


@ExperimentalCoroutinesApi
fun <T> Context.putValue(key: Preferences.Key<T>, value: T) {
    dataStore.updateDataAsync {
        return@updateDataAsync Single
            .just(it.toMutablePreferences()
                .apply {
                    this[key] = value
                })
    }.subscribe()
}

/**
 * 同步获取值，阻塞式，主线程慎用
 */
@ExperimentalCoroutinesApi
fun <T> Context.getValueSync(key: Preferences.Key<T>): T {
    return dataStore.data().map {
        return@map it[key] ?: key.defaultValue
    }.blockingFirst()
}

/**
 * 异步获取值，返回 rxjava 的 Single 对象
 */
@ExperimentalCoroutinesApi
fun <T> Context.getValue(key: Preferences.Key<T>): Single<T> {
    return dataStore.data().map {
        return@map it[key] ?: key.defaultValue
    }.first(key.defaultValue)
}

/**
 * 监听数据变化
 */
@ExperimentalCoroutinesApi
fun <T> Context.obValue(key: Preferences.Key<T>): Observable<T> {
    return dataStore.data().map {
        return@map it[key] ?: key.defaultValue
    }.distinctUntilChanged { oldValue, newValue ->
        return@distinctUntilChanged oldValue?.equals(newValue) ?: false
    }.toObservable()
}