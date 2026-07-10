package com.example.hourlyalarm58

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore("settings")

class AppPreferences(private val context: Context) {
    val alarmEnabled: Flow<Boolean> = context.settingsDataStore.data.map { values ->
        values[ALARM_ENABLED] ?: false
    }

    val nextAlarmTime: Flow<Long> = context.settingsDataStore.data.map { values ->
        values[NEXT_ALARM_TIME] ?: 0L
    }

    suspend fun setAlarmEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { values -> values[ALARM_ENABLED] = enabled }
    }

    suspend fun setNextAlarmTime(epochMillis: Long) {
        context.settingsDataStore.edit { values -> values[NEXT_ALARM_TIME] = epochMillis }
    }

    companion object {
        private val ALARM_ENABLED = booleanPreferencesKey("alarm_enabled")
        private val NEXT_ALARM_TIME = longPreferencesKey("next_alarm_time")
    }
}
