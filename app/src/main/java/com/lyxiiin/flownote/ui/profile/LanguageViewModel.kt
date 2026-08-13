package com.lyxiiin.flownote.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.lyxiiin.flownote.data.repository.SettingsRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class LanguageViewModel(private val settingsRepository: SettingsRepository): ViewModel() {
    /** 已持久化的语言（DataStore 响应式数据源） */
    val language: LiveData<String> = settingsRepository.language.asLiveData()

    /** 页面内临时选中的语言：列表点击只更新这里，点击 Finish 才落盘 */
    private val _selectedLanguage = MutableLiveData<String>()
    val selectedLanguage: LiveData<String> get() = _selectedLanguage

    init {
        // 初始临时选中值跟随已持久化的语言（仅当用户尚未点击选择时，避免覆盖快速操作）
        viewModelScope.launch {
            if (_selectedLanguage.value == null) {
                _selectedLanguage.value = settingsRepository.language.first()
            }
        }
    }

    /** 列表项点击：仅更新临时选中，不写入 DataStore */
    fun selectLanguage(value: String) {
        _selectedLanguage.value = value
    }

    /**
     * 点击 Finish：将临时选中值持久化到 DataStore。
     * 挂起等待写入完成，由调用方在完成后处理页面跳转，
     * 避免 fire-and-forget 导致写入协程随 ViewModel 销毁被取消。
     */
    suspend fun saveLanguage() {
        val value = _selectedLanguage.value ?: return
        settingsRepository.setLanguage(value)
    }
}

class LanguageViewModelFactory(
    private val settingsRepository: SettingsRepository
): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LanguageViewModel::class.java)){
            return LanguageViewModel(settingsRepository) as T
        }
        throw IllegalStateException("Unknown viewmodel class: ${modelClass.name}")
    }
}

