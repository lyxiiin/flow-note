package com.lyxiiin.flownote

import android.app.Application
import android.app.LocaleManager
import android.app.UiModeManager
import android.os.LocaleList
import com.lyxiiin.flownote.data.local.AppDatabase
import com.lyxiiin.flownote.data.local.dataStore
import com.lyxiiin.flownote.data.repository.NoteCategoryRepository
import com.lyxiiin.flownote.data.repository.NoteCategoryRepositoryImpl
import com.lyxiiin.flownote.data.repository.NoteRepository
import com.lyxiiin.flownote.data.repository.NoteRepositoryImpl
import com.lyxiiin.flownote.data.repository.SettingsRepository
import com.lyxiiin.flownote.data.repository.SettingsRepositoryImpl
import com.lyxiiin.flownote.data.repository.TodoRepository
import com.lyxiiin.flownote.data.repository.TodoRepositoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class FnApplication: Application() {

    /** Application 级协程作用域：跟随进程存活，用于全局后台任务 */
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    override fun onCreate() {
        super.onCreate()
        // 观察语言偏好变化，同步到系统"按应用语言"设置（LocaleManager），Activity 会自动重建以应用新语言
        // 注意：DataStore 的 data 是永不完成的流，每个观察必须独立 launch，串行 collect 会导致后续观察永远不执行
        applicationScope.launch {
            settingsRepository.language
                .distinctUntilChanged()
                .collect { language ->
                    // "system" 表示跟随系统语言，无需干预
                    if (language != "system") {
                        applyApplicationLocales(language)
                    }
                }
        }
        // 观察主题模式变化，应用全局深色/浅色模式
        applicationScope.launch {
            settingsRepository.themeMode
                .distinctUntilChanged()
                .collect { themeMode ->
                    // 无条件应用：0=跟随系统 1=浅色 2=深色，直接作为 setApplicationNightMode 入参
                    applyApplicationAppearance(themeMode)
                }
        }
    }

    /** 将语言标签（BCP-47 格式）写入系统 LocaleManager */
    private fun applyApplicationLocales(language: String) {
        val localeManager = getSystemService(LocaleManager::class.java)
        localeManager.applicationLocales = LocaleList.forLanguageTags(language)
    }

    private fun applyApplicationAppearance(themeMode: Int){
        val uiModeManager = getSystemService(UiModeManager::class.java)
        uiModeManager.setApplicationNightMode(themeMode)
    }

    val database: AppDatabase by lazy {
        AppDatabase.getInstance(this)
    }
    val noteCategoryRepository: NoteCategoryRepository by lazy {
        NoteCategoryRepositoryImpl(database.noteCategoriesDao())
    }

    val noteRepository: NoteRepository by lazy {
        NoteRepositoryImpl(database.noteDao())
    }

    val todoRepository: TodoRepository by lazy {
        TodoRepositoryImpl(database.todoDao())
    }

    val settingsRepository: SettingsRepository by lazy {
        SettingsRepositoryImpl(this.dataStore)
    }
}