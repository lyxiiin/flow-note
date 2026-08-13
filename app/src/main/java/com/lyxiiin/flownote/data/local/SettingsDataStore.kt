package com.lyxiiin.flownote.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// 顶层委托属性：进程内唯一 DataStore 实例（必须顶层声明，否则多实例并发冲突崩溃）
// internal：允许其他文件（如 FnApplication）访问，但不暴露到模块外
internal val Context.dataStore by preferencesDataStore(name = "settings")

object SettingsKeys {
    val LANGUAGE = stringPreferencesKey("language")         // "zh-CN" / "en-US" / "system"
    val THEME_MODE = intPreferencesKey("theme_mode")       // 0=跟随系统 1=浅色 2=深色
    val NOTIFICATION_ENABLED = booleanPreferencesKey("notification_enabled")
}