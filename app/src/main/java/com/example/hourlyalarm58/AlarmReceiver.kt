package com.example.hourlyalarm58

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val serviceIntent = Intent(context, AlarmSoundService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            val scheduler = AlarmScheduler(context)
            if (scheduler.canScheduleExactAlarms()) {
                AppPreferences(context).setNextAlarmTime(scheduler.scheduleNext())
            }
            pendingResult.finish()
        }
    }
}
