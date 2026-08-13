package com.lyxiiin.flownote.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.lyxiiin.flownote.data.local.SettingsKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface SettingsRepository{
    val language: Flow<String>
    val themeMode: Flow<Int>
    val notificationEnabled: Flow<Boolean>

    suspend fun setLanguage(value: String)
    suspend fun setThemeMode(value: Int)
    suspend fun setNotificationEnabled(enabled: Boolean)
}

class SettingsRepositoryImpl(
    private val dataStore: DataStore<Preferences>
): SettingsRepository {
    override val language: Flow<String> = dataStore.data.map {
        it[SettingsKeys.LANGUAGE] ?: "system"
    }

    override val themeMode: Flow<Int> = dataStore.data.map {
        it[SettingsKeys.THEME_MODE] ?: 0
    }

    override val notificationEnabled: Flow<Boolean> = dataStore.data.map {
        it[SettingsKeys.NOTIFICATION_ENABLED] ?: true
    }

    override suspend fun setLanguage(value: String) {
        dataStore.edit {
            it[SettingsKeys.LANGUAGE] = value
        }
    }

    override suspend fun setThemeMode(value: Int) {
        dataStore.edit {
            it[SettingsKeys.THEME_MODE] = value
        }
    }

    override suspend fun setNotificationEnabled(enabled: Boolean) {
        dataStore.edit {
            it[SettingsKeys.NOTIFICATION_ENABLED] = enabled
        }
    }


}