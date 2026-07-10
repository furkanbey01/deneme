package com.example.hourlyalarm58

import android.content.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != Intent.ACTION_BOOT_COMPLETED) return
        val pending = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            val prefs = AppPreferences(context)
            if (prefs.alarmEnabled.first() && AlarmScheduler(context).canScheduleExactAlarms()) {
                prefs.setNextAlarmTime(AlarmScheduler(context).scheduleNext())
            }
            pending.finish()
        }
    }
}
