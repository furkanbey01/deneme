package com.example.hourlyalarm58

import android.content.*
import android.os.Build
import kotlinx.coroutines.*

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        NotificationHelper.ensureChannel(context)
        val serviceIntent = Intent(context, AlarmSoundService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) context.startForegroundService(serviceIntent) else context.startService(serviceIntent)
        val pending = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            val prefs = AppPreferences(context)
            prefs.setAlarmEnabled(true)
            val scheduler = AlarmScheduler(context)
            if (scheduler.canScheduleExactAlarms()) {
                val next = scheduler.scheduleNext()
                prefs.setNextAlarmTime(next)
            }
            pending.finish()
        }
    }
}
