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

class AppearanceViewModel(
    private val settingsRepository: SettingsRepository
): ViewModel() {
    val themeMode: LiveData<Int> = settingsRepository.themeMode.asLiveData()

    private val _selectedMode = MutableLiveData<Int>()
    val selectedMode: LiveData<Int> get() = _selectedMode

    init {
        viewModelScope.launch {
            if (_selectedMode.value == null){
                _selectedMode.value = settingsRepository.themeMode.first()
            }
        }
    }

    fun selectThemeMode(value: Int){
        _selectedMode.value = value
    }

    suspend fun saveThemeMode(){
        val value = _selectedMode.value ?: return
        settingsRepository.setThemeMode(value)
    }
}

class AppearanceViewModelFactory(
    private val settingsRepository: SettingsRepository
): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppearanceViewModel::class.java)){
            return AppearanceViewModel(settingsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}