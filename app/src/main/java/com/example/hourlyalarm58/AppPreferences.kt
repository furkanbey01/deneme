package com.example.hourlyalarm58

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore("settings")

class AppPreferences(private val context: Context) {
    val alarmEnabled: Flow<Boolean> = context.dataStore.data.map { it[ALARM_ENABLED] ?: false }
    val nextAlarmTime: Flow<Long> = context.dataStore.data.map { it[NEXT_ALARM_TIME] ?: 0L }
    suspend fun setAlarmEnabled(enabled: Boolean) { context.dataStore.edit { it[ALARM_ENABLED] = enabled } }
    suspend fun setNextAlarmTime(time: Long) { context.dataStore.edit { it[NEXT_ALARM_TIME] = time } }
    companion object {
        private val ALARM_ENABLED = booleanPreferencesKey("alarm_enabled")
        private val NEXT_ALARM_TIME = longPreferencesKey("next_alarm_time")
    }
}
